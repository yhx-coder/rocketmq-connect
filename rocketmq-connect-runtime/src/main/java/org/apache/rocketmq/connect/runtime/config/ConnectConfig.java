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

package org.apache.rocketmq.connect.runtime.config;

import java.io.File;
import org.apache.rocketmq.common.MixAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.rocketmq.connect.runtime.common.LoggerName.ROCKETMQ_RUNTIME;

/**
 * Configurations for runtime.
 */
public class ConnectConfig {

    public static final String CONNECT_HOME_PROPERTY = "connect.home.dir";

    public static final String CONNECT_HOME_ENV = "CONNECT_HOME";

    private static final Logger log = LoggerFactory.getLogger(ROCKETMQ_RUNTIME);

    public static final String COMMA = ",";

    public static final String SEMICOLON = ";";

    /**
     * The unique ID of each worker instance in the cluster
     */
    private String workerId = "DefaultWorker";

    /**
     * Storage directory for file store.
     */
    private String storePathRootDir = System.getProperty("user.home") + File.separator + "connectorStore";

    private String connectHome = System.getProperty(CONNECT_HOME_PROPERTY, System.getenv(CONNECT_HOME_ENV));

    private String namesrvAddr = System.getProperty(MixAll.NAMESRV_ADDR_PROPERTY, System.getenv(MixAll.NAMESRV_ADDR_ENV));

    private String rmqProducerGroup = "connector-producer-group";

    private int maxMessageSize;

    private int operationTimeout = 3000;

    private String rmqConsumerGroup = "connector-consumer-group";

    private int rmqMaxRedeliveryTimes;

    private int rmqMessageConsumeTimeout = 3000;

    private int rmqMaxConsumeThreadNums = 32;

    private int rmqMinConsumeThreadNums = 1;

    public int getBrokerSuspendMaxTimeMillis() {
        return brokerSuspendMaxTimeMillis;
    }

    public void setBrokerSuspendMaxTimeMillis(int brokerSuspendMaxTimeMillis) {
        this.brokerSuspendMaxTimeMillis = brokerSuspendMaxTimeMillis;
    }

    private int brokerSuspendMaxTimeMillis = 300;

    /**
     * Default topic to send/consume online or offline message.
     */
    private String clusterStoreTopic = "connector-cluster-topic";

    /**
     * Default topic to send/consume config change message.
     */
    private String configStoreTopic = "connector-config-topic";

    /**
     * Default topic to send/consume position change message.
     */
    private String positionStoreTopic = "connector-position-topic";

    /**
     * Default topic to send/consume offset change message.
     */
    private String offsetStoreTopic = "connector-offset-topic";

    /**
     * Http port for REST API.
     */
    private int httpPort = 8081;

    /**
     * Source task position persistence interval.
     */
    private int positionPersistInterval = 20 * 1000;

    /**
     * Sink task offset persistence interval.
     */
    private int offsetPersistInterval = 20 * 1000;

    /**
     * Connector configuration persistence interval.
     */
    private int configPersistInterval = 20 * 1000;

    private String pluginPaths;

    private String connectClusterId = "DefaultConnectCluster";

    private String allocTaskStrategy = "org.apache.rocketmq.connect.runtime.service.strategy.DefaultAllocateConnAndTaskStrategy";

    private boolean aclEnable = false;

    private String accessKey;

    private String secretKey;

    private boolean autoCreateGroupEnable = false;

    private String clusterName;

    private String adminExtGroup = "connector-admin-group";

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public String getNamesrvAddr() {
        return namesrvAddr;
    }

    public void setNamesrvAddr(String namesrvAddr) {
        this.namesrvAddr = namesrvAddr;
    }

    public String getRmqProducerGroup() {
        return rmqProducerGroup;
    }

    public void setRmqProducerGroup(String rmqProducerGroup) {
        this.rmqProducerGroup = rmqProducerGroup;
    }

    public int getMaxMessageSize() {
        return maxMessageSize;
    }

    public void setMaxMessageSize(int maxMessageSize) {
        this.maxMessageSize = maxMessageSize;
    }

    public int getOperationTimeout() {
        return operationTimeout;
    }

    public void setOperationTimeout(int operationTimeout) {
        this.operationTimeout = operationTimeout;
    }

    public String getRmqConsumerGroup() {
        return rmqConsumerGroup;
    }

    public void setRmqConsumerGroup(String rmqConsumerGroup) {
        this.rmqConsumerGroup = rmqConsumerGroup;
    }

    public int getRmqMaxRedeliveryTimes() {
        return rmqMaxRedeliveryTimes;
    }

    public void setRmqMaxRedeliveryTimes(int rmqMaxRedeliveryTimes) {
        this.rmqMaxRedeliveryTimes = rmqMaxRedeliveryTimes;
    }

    public int getRmqMessageConsumeTimeout() {
        return rmqMessageConsumeTimeout;
    }

    public void setRmqMessageConsumeTimeout(int rmqMessageConsumeTimeout) {
        this.rmqMessageConsumeTimeout = rmqMessageConsumeTimeout;
    }

    public int getRmqMaxConsumeThreadNums() {
        return rmqMaxConsumeThreadNums;
    }

    public void setRmqMaxConsumeThreadNums(int rmqMaxConsumeThreadNums) {
        this.rmqMaxConsumeThreadNums = rmqMaxConsumeThreadNums;
    }

    public int getRmqMinConsumeThreadNums() {
        return rmqMinConsumeThreadNums;
    }

    public void setRmqMinConsumeThreadNums(int rmqMinConsumeThreadNums) {
        this.rmqMinConsumeThreadNums = rmqMinConsumeThreadNums;
    }

    public String getStorePathRootDir() {
        return storePathRootDir;
    }

    public void setStorePathRootDir(String storePathRootDir) {
        this.storePathRootDir = storePathRootDir;
    }

    public int getHttpPort() {
        return httpPort;
    }

    public void setHttpPort(int httpPort) {
        this.httpPort = httpPort;
    }

    public int getPositionPersistInterval() {
        return positionPersistInterval;
    }

    public void setPositionPersistInterval(int positionPersistInterval) {
        this.positionPersistInterval = positionPersistInterval;
    }

    public int getOffsetPersistInterval() {
        return offsetPersistInterval;
    }

    public void setOffsetPersistInterval(int offsetPersistInterval) {
        this.offsetPersistInterval = offsetPersistInterval;
    }

    public int getConfigPersistInterval() {
        return configPersistInterval;
    }

    public void setConfigPersistInterval(int configPersistInterval) {
        this.configPersistInterval = configPersistInterval;
    }

    public String getPluginPaths() {
        return pluginPaths;
    }

    public void setPluginPaths(String pluginPaths) {
        this.pluginPaths = pluginPaths;
    }

    public String getClusterStoreTopic() {
        return clusterStoreTopic;
    }

    public void setClusterStoreTopic(String clusterStoreTopic) {
        this.clusterStoreTopic = clusterStoreTopic;
    }

    public String getConfigStoreTopic() {
        return configStoreTopic;
    }

    public void setConfigStoreTopic(String configStoreTopic) {
        this.configStoreTopic = configStoreTopic;
    }

    public String getPositionStoreTopic() {
        return positionStoreTopic;
    }

    public void setPositionStoreTopic(String positionStoreTopic) {
        this.positionStoreTopic = positionStoreTopic;
    }

    public String getOffsetStoreTopic() {
        return offsetStoreTopic;
    }

    public void setOffsetStoreTopic(String offsetStoreTopic) {
        this.offsetStoreTopic = offsetStoreTopic;
    }

    public String getConnectClusterId() {
        return connectClusterId;
    }

    public void setConnectClusterId(String connectClusterId) {
        this.connectClusterId = connectClusterId;
    }

    public void setAllocTaskStrategy(String allocTaskStrategy) {
        this.allocTaskStrategy = allocTaskStrategy;
    }

    public String getAllocTaskStrategy() {
        return this.allocTaskStrategy;
    }

    public boolean getAclEnable() {
        return aclEnable;
    }

    public void setAclEnable(boolean aclEnable) {
        this.aclEnable = aclEnable;
    }

    public String getAccessKey() {
        return accessKey;
    }

    public void setAccessKey(String accessKey) {
        this.accessKey = accessKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public void setSecretKey(String secretKey) {
        this.secretKey = secretKey;
    }

    public boolean isAutoCreateGroupEnable() {
        return autoCreateGroupEnable;
    }

    public void setAutoCreateGroupEnable(boolean autoCreateGroupEnable) {
        this.autoCreateGroupEnable = autoCreateGroupEnable;
    }

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getAdminExtGroup() {
        return adminExtGroup;
    }

    public void setAdminExtGroup(String adminExtGroup) {
        this.adminExtGroup = adminExtGroup;
    }

    public String getConnectHome() {
        return connectHome;
    }

    public void setConnectHome(String connectHome) {
        this.connectHome = connectHome;
    }

    @Override public String toString() {
        return "ConnectConfig{" +
            "workerId='" + workerId + '\'' +
            ", storePathRootDir='" + storePathRootDir + '\'' +
            ", connectHome='" + connectHome + '\'' +
            ", namesrvAddr='" + namesrvAddr + '\'' +
            ", rmqProducerGroup='" + rmqProducerGroup + '\'' +
            ", maxMessageSize=" + maxMessageSize +
            ", operationTimeout=" + operationTimeout +
            ", rmqConsumerGroup='" + rmqConsumerGroup + '\'' +
            ", rmqMaxRedeliveryTimes=" + rmqMaxRedeliveryTimes +
            ", rmqMessageConsumeTimeout=" + rmqMessageConsumeTimeout +
            ", rmqMaxConsumeThreadNums=" + rmqMaxConsumeThreadNums +
            ", rmqMinConsumeThreadNums=" + rmqMinConsumeThreadNums +
            ", brokerSuspendMaxTimeMillis=" + brokerSuspendMaxTimeMillis +
            ", clusterStoreTopic='" + clusterStoreTopic + '\'' +
            ", configStoreTopic='" + configStoreTopic + '\'' +
            ", positionStoreTopic='" + positionStoreTopic + '\'' +
            ", offsetStoreTopic='" + offsetStoreTopic + '\'' +
            ", httpPort=" + httpPort +
            ", positionPersistInterval=" + positionPersistInterval +
            ", offsetPersistInterval=" + offsetPersistInterval +
            ", configPersistInterval=" + configPersistInterval +
            ", pluginPaths='" + pluginPaths + '\'' +
            ", connectClusterId='" + connectClusterId + '\'' +
            ", allocTaskStrategy='" + allocTaskStrategy + '\'' +
            ", aclEnable=" + aclEnable +
            ", accessKey='" + accessKey + '\'' +
            ", secretKey='" + secretKey + '\'' +
            ", autoCreateGroupEnable=" + autoCreateGroupEnable +
            ", clusterName='" + clusterName + '\'' +
            ", adminExtGroup='" + adminExtGroup + '\'' +
            '}';
    }
}
