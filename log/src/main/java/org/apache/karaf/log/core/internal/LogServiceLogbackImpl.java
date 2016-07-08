/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.log.core.internal;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import org.apache.karaf.log.core.Level;
import org.ops4j.pax.logging.PaxLogger;
import org.ops4j.pax.logging.PaxLoggingManager;

public class LogServiceLogbackImpl implements LogServiceInternal {

    static final String ROOT_LOGGER_LEVEL = "log4j2.rootLogger.level";
    static final String LOGGERS = "log4j2.loggers";
    static final String LOGGER_PREFIX = "log4j2.logger.";
    static final String NAME_SUFFIX = ".name";
    static final String LEVEL_SUFFIX = ".level";

    private final Dictionary<String, Object> config;
    private final PaxLoggingManager paxLoggingManager;

    public LogServiceLogbackImpl(Dictionary<String, Object> config, PaxLoggingManager paxLoggingManager) {
        this.config = config;
        this.paxLoggingManager=paxLoggingManager;
    }

    public Map<String, String> getLevel(String logger) {
        // 1 - read file location from config and get log level = pain in the ass
        // 2 - get PaxLoggingService and get Loglevel ... will it work for ALL ? NOT SURE!
        // Go for two as it seems less work :-) BUT will we be able to inject service? Not sure.
        Map<String, String> loggers = new TreeMap<>();
        if(ALL_LOGGER.equals(logger)){
            for (PaxLogger paxLogger : paxLoggingManager.getLoggers()) {
                loggers.put(paxLogger.getName(),getLevel(paxLogger.getLogLevel()));                
            }
            return loggers;
        }

        final PaxLogger paxLogger = paxLoggingManager.getLogger(logger, null);
        loggers.put(logger,getLevel(paxLogger.getLogLevel()));
        return loggers;
    }

    public void setLevel(String logger, String level) {
        paxLoggingManager.setLogLevel(logger,level);
    }

    private String name(String logger) {
        return LOGGER_PREFIX + logger + NAME_SUFFIX;
    }

    private String level(String logger) {
        return LOGGER_PREFIX + logger + LEVEL_SUFFIX;
    }
    
    private String getLevel(int level){
        switch(level){
            case PaxLogger.LEVEL_TRACE:
                return "TRACE";
            case PaxLogger.LEVEL_DEBUG:
                return "DEBUG";
            case PaxLogger.LEVEL_INFO:
                return "INFO";
            case PaxLogger.LEVEL_WARNING:
                return "WARN";
            case PaxLogger.LEVEL_ERROR:
                return "ERROR";
            default:
                return "UNKNOWN";
        }
    }

}
