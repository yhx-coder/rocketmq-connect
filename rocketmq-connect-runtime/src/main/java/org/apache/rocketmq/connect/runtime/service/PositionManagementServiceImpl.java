/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package org.apache.rocketmq.connect.runtime.service;

import io.netty.util.internal.ConcurrentSet;
import io.openmessaging.connector.api.data.RecordOffset;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.rocketmq.common.TopicConfig;
import org.apache.rocketmq.connect.runtime.common.LoggerName;
import org.apache.rocketmq.connect.runtime.config.ConnectConfig;
import org.apache.rocketmq.connect.runtime.converter.JsonConverter;
import org.apache.rocketmq.connect.runtime.converter.RecordOffsetConverter;
import org.apache.rocketmq.connect.runtime.converter.RecordPartitionConverter;
import org.apache.rocketmq.connect.runtime.converter.RecordPositionMapConverter;
import org.apache.rocketmq.connect.runtime.store.ExtendRecordPartition;
import org.apache.rocketmq.connect.runtime.store.FileBaseKeyValueStore;
import org.apache.rocketmq.connect.runtime.store.KeyValueStore;
import org.apache.rocketmq.connect.runtime.utils.ConnectUtil;
import org.apache.rocketmq.connect.runtime.utils.FilePathConfigUtil;
import org.apache.rocketmq.connect.runtime.utils.datasync.BrokerBasedLog;
import org.apache.rocketmq.connect.runtime.utils.datasync.DataSynchronizer;
import org.apache.rocketmq.connect.runtime.utils.datasync.DataSynchronizerCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PositionManagementServiceImpl implements PositionManagementService {
    private static final Logger log = LoggerFactory.getLogger(LoggerName.ROCKETMQ_RUNTIME);

    /**
     * Current position info in store.
     */
    private KeyValueStore<ExtendRecordPartition, RecordOffset> positionStore;

    /**
     * The updated partition of the task in the current instance.
     */
    private Set<ExtendRecordPartition> needSyncPartition;

    /**
     * Synchronize data with other workers.
     */
    private DataSynchronizer<String, Map<ExtendRecordPartition, RecordOffset>> dataSynchronizer;

    /**
     * Listeners.
     */
    private Set<PositionUpdateListener> positionUpdateListener;

    private final String positionManagePrefix = "PositionManage";

    public PositionManagementServiceImpl() {
    }

    /**
     * Preparation before startup
     *
     * @param connectConfig
     */
    private void prepare(ConnectConfig connectConfig) {
        String positionStoreTopic = connectConfig.getPositionStoreTopic();
        if (!ConnectUtil.isTopicExist(connectConfig, positionStoreTopic)) {
            log.info("try to create position store topic: {}!", positionStoreTopic);
            TopicConfig topicConfig = new TopicConfig(positionStoreTopic, 1, 1, 6);
            ConnectUtil.createTopic(connectConfig, topicConfig);
        }
    }

    @Override
    public void start() {

        positionStore.load();
        dataSynchronizer.start();
        sendOnlinePositionInfo();
    }

    @Override
    public void stop() {

        sendNeedSynchronizePosition();
        positionStore.persist();
        dataSynchronizer.stop();
    }

    @Override
    public void persist() {

        positionStore.persist();
    }

    @Override
    public void load() {
        positionStore.load();
    }

    @Override
    public void synchronize() {

        sendNeedSynchronizePosition();
    }

    @Override
    public Map<ExtendRecordPartition, RecordOffset> getPositionTable() {

        return positionStore.getKVMap();
    }

    @Override
    public RecordOffset getPosition(ExtendRecordPartition partition) {

        return positionStore.get(partition);
    }

    @Override
    public void putPosition(Map<ExtendRecordPartition, RecordOffset> positions) {

        positionStore.putAll(positions);
        needSyncPartition.addAll(positions.keySet());
    }

    @Override
    public void putPosition(ExtendRecordPartition partition, RecordOffset position) {

        positionStore.put(partition, position);
        needSyncPartition.add(partition);
    }

    @Override
    public void removePosition(List<ExtendRecordPartition> partitions) {

        if (null == partitions) {
            return;
        }

        for (ExtendRecordPartition partition : partitions) {
            needSyncPartition.remove(partition);
            positionStore.remove(partition);
        }
    }

    @Override
    public void registerListener(PositionUpdateListener listener) {

        this.positionUpdateListener.add(listener);
    }

    @Override public void initialize(ConnectConfig connectConfig) {
        this.positionStore = new FileBaseKeyValueStore<>(FilePathConfigUtil.getPositionPath(connectConfig.getStorePathRootDir()),
            new RecordPartitionConverter(),
            new RecordOffsetConverter());
        this.dataSynchronizer = new BrokerBasedLog(connectConfig,
            connectConfig.getPositionStoreTopic(),
            ConnectUtil.createGroupName(positionManagePrefix, connectConfig.getWorkerId()),
            new PositionChangeCallback(),
            new JsonConverter(),
            new RecordPositionMapConverter());
        this.positionUpdateListener = new HashSet<>();
        this.needSyncPartition = new ConcurrentSet<>();
        this.prepare(connectConfig);
    }

    @Override public StagingMode getStagingMode() {
        return StagingMode.DISTRIBUTED;
    }

    private void sendOnlinePositionInfo() {

        dataSynchronizer.send(PositionChangeEnum.ONLINE_KEY.name(), positionStore.getKVMap());
    }


    private void sendNeedSynchronizePosition() {

        Set<ExtendRecordPartition> needSyncPartitionTmp = needSyncPartition;
        needSyncPartition = new ConcurrentSet<>();
        Map<ExtendRecordPartition, RecordOffset> needSyncPosition = positionStore.getKVMap().entrySet().stream()
                .filter(entry -> needSyncPartitionTmp.contains(entry.getKey()))
                .collect(Collectors.toMap(entry -> entry.getKey(), entry -> entry.getValue()));

        dataSynchronizer.send(PositionChangeEnum.POSITION_CHANG_KEY.name(), needSyncPosition);
    }

    private void sendSynchronizePosition() {

        dataSynchronizer.send(PositionChangeEnum.POSITION_CHANG_KEY.name(), positionStore.getKVMap());
    }

    private class PositionChangeCallback implements DataSynchronizerCallback<String, Map<ExtendRecordPartition, RecordOffset>> {

        @Override
        public void onCompletion(Throwable error, String key, Map<ExtendRecordPartition, RecordOffset> result) {

            boolean changed = false;
            switch (PositionChangeEnum.valueOf(key)) {
                case ONLINE_KEY:
                    changed = true;
                    sendSynchronizePosition();
                    break;
                case POSITION_CHANG_KEY:
                    changed = mergePositionInfo(result);
                    break;
                default:
                    break;
            }
            if (changed) {
                triggerListener();
            }

        }
    }

    private void triggerListener() {
        for (PositionUpdateListener positionUpdateListener : positionUpdateListener) {
            positionUpdateListener.onPositionUpdate();
        }
    }

    /**
     * Merge new received position info with local store.
     *
     * @param result
     * @return
     */
    private boolean mergePositionInfo(Map<ExtendRecordPartition, RecordOffset> result) {

        boolean changed = false;
        if (null == result || 0 == result.size()) {
            return changed;
        }

        for (Map.Entry<ExtendRecordPartition, RecordOffset> newEntry : result.entrySet()) {
            boolean find = false;
            for (Map.Entry<ExtendRecordPartition, RecordOffset> existedEntry : positionStore.getKVMap().entrySet()) {
                if (newEntry.getKey().equals(existedEntry.getKey())) {
                    find = true;
                    if (!newEntry.getValue().equals(existedEntry.getValue())) {
                        changed = true;
                        existedEntry.setValue(newEntry.getValue());
                    }
                    break;
                }
            }
            if (!find) {
                positionStore.put(newEntry.getKey(), newEntry.getValue());
            }
        }
        return changed;
    }

    private enum PositionChangeEnum {

        /**
         * Insert or update position info.
         */
        POSITION_CHANG_KEY,

        /**
         * A worker online.
         */
        ONLINE_KEY
    }
}

