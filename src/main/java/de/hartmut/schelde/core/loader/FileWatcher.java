/*
 * Copyright 2018 Hartmut Lang
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

package de.hartmut.schelde.core.loader;

import de.hartmut.schelde.core.config.ScheldeConfig;
import org.apache.commons.io.monitor.FileAlterationListener;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

/**
 * hartmut on 09.02.18.
 */
@Component
public class FileWatcher implements FileAlterationListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcher.class);

    private final ScheldeConfig config;
    private final FileService fileService;
    private FileAlterationMonitor fileAlterationMonitor;


    public FileWatcher(ScheldeConfig config, FileService fileService) {
        this.config = config;
        this.fileService = fileService;
    }

    @PostConstruct
    public void init() {
        LOGGER.debug("init()");
        initDirs();
        startWatcher();
    }

    @PreDestroy
    public void shutdown() throws Exception {
        LOGGER.debug("shutdown()");
        fileAlterationMonitor.stop();
    }

    private void initDirs() {
        for (String scanPath : config.getScanPaths()) {
            LOGGER.trace("initDirs(): {}", scanPath);
            Path dirPath = Paths.get(scanPath);
            try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dirPath)) {
                for (Path path : directoryStream) {
                    LOGGER.trace("file: {}", path);
                    fileService.addOrUpdate(path);
                }
            } catch (IOException ex) {
                LOGGER.warn("initDirs(): failed!", ex);
            }
        }
    }

    private void startWatcher() {
        fileAlterationMonitor = new FileAlterationMonitor(
            Duration.ofSeconds(config.getScanPeriodSeconds()).toMillis());
        for (String scanPath : config.getScanPaths()) {
            LOGGER.debug("startWatcher(): for {}", scanPath);
            FileAlterationObserver fileAlterationObserver = new FileAlterationObserver(scanPath);
            fileAlterationMonitor.addObserver(fileAlterationObserver);
            fileAlterationObserver.addListener(this);
        }
        try {
            fileAlterationMonitor.start();
        } catch (Exception ex) {
            LOGGER.error("startWatcher(): failed!", ex);
        }
    }

    @Override
    public void onStart(FileAlterationObserver fileAlterationObserver) {
        LOGGER.trace("onStart()");
    }

    @Override
    public void onDirectoryCreate(File file) {
        LOGGER.trace("onDirectoryCreate(): {}", file);
    }

    @Override
    public void onDirectoryChange(File file) {
        LOGGER.trace("onDirectoryChange(): {}", file);

    }

    @Override
    public void onDirectoryDelete(File file) {
        LOGGER.trace("onDirectoryDelete(): {}", file);
    }

    @Override
    public void onFileCreate(File file) {
        LOGGER.debug("onFileCreate(): {}", file);
        fileService.addOrUpdate(file.toPath());
    }

    @Override
    public void onFileChange(File file) {
        LOGGER.debug("onFileChange(): {}", file);
        fileService.addOrUpdate(file.toPath());
    }

    @Override
    public void onFileDelete(File file) {
        LOGGER.debug("onFileDelete(): {}", file);
        fileService.removeFile(file.toPath());
    }

    @Override
    public void onStop(FileAlterationObserver fileAlterationObserver) {
        LOGGER.trace("onStop()");

    }
}
