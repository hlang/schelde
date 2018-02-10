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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static java.nio.file.StandardWatchEventKinds.*;

/**
 * hartmut on 09.02.18.
 */
@Component
public class FileWatcher implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileWatcher.class);

    private final ScheldeConfig config;
    private final FileService fileService;

    private Map<WatchKey, Path> watchKeys = new HashMap<>();
    private WatchService watchService;
    private ExecutorService executorService = Executors.newSingleThreadExecutor();

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
    public void shutdown() throws IOException {
        LOGGER.debug("shutdown()");
        executorService.shutdown();
        watchService.close();
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
        try {
            watchService = FileSystems.getDefault().newWatchService();
            for (String scanPath : config.getScanPaths()) {
                LOGGER.debug("startWatcher(): for {}", scanPath);
                Path path = Paths.get(scanPath);
                WatchKey watchKey = path.register(watchService, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
                watchKeys.put(watchKey, path);
            }
            executorService.submit(this);
        } catch (IOException ex) {
            LOGGER.warn("init(): failed!", ex);
        }
    }

    @Override
    public void run() {
        LOGGER.debug("run()");
        for (; ; ) {

            // wait for key to be signalled
            WatchKey key;
            try {
                key = watchService.take();
            } catch (InterruptedException x) {
                return;
            }

            Path path = watchKeys.get(key);
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind kind = event.kind();

                // TBD - provide example of how OVERFLOW event is handled
                if (kind == OVERFLOW) {
                    LOGGER.warn("run(): overflow detected!");
                    continue;
                }

                // Context for directory entry event is the file name of entry
                WatchEvent<Path> ev = (WatchEvent<Path>) event;
                WatchEvent.Kind<Path> pathKind = ev.kind();
                Path eventPath = path.resolve(ev.context());
                LOGGER.debug("run(): event {} for {}", pathKind, eventPath);
                if (pathKind.equals(StandardWatchEventKinds.ENTRY_DELETE)) {
                    fileService.removeFile(eventPath);
                } else {
                    fileService.addOrUpdate(eventPath);
                }
            }

            // reset key and remove from set if directory no longer accessible
            boolean valid = key.reset();
            if (!valid) {
                LOGGER.warn("run(): key reset is invalid!");
                break;
            }
        }
    }
}
