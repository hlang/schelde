/*
 * Copyright 2017 Hartmut Lang
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.hartmut.schelde.core.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * hartmut on 14.10.17.
 */
@Component
@ConfigurationProperties(prefix = "schelde")
public class ScheldeConfig {
    private Long scanPeriodSeconds = 120L;
    private List<String> scanPaths;

    public Long getScanPeriodSeconds() {
        return scanPeriodSeconds;
    }

    public void setScanPeriodSeconds(Long scanPeriodSeconds) {
        this.scanPeriodSeconds = scanPeriodSeconds;
    }

    public List<String> getScanPaths() {
        return scanPaths;
    }

    public void setScanPaths(List<String> scanPaths) {
        this.scanPaths = scanPaths;
    }
}