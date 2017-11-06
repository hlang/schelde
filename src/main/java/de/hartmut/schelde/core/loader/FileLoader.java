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

package de.hartmut.schelde.core.loader;

import de.hartmut.schelde.core.config.ScheldeConfig;
import de.hartmut.schelde.core.db.FileInfo;
import de.hartmut.schelde.core.db.FileInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.stream.Collectors;

import static java.util.concurrent.TimeUnit.SECONDS;

/**
 * hartmut on 14.10.17.
 */

@Component
public class FileLoader implements Runnable {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileLoader.class);

    private final ScheldeConfig config;
    private final FileInfoRepository fileInfoRepository;
    private final ScheduledExecutorService executorService;

    public FileLoader(ScheldeConfig config, FileInfoRepository fileInfoRepository) {
        this.config = config;
        this.fileInfoRepository = fileInfoRepository;
        this.executorService = Executors.newScheduledThreadPool(1);
    }

    @PostConstruct
    public void init() {
        executorService.scheduleWithFixedDelay(this,
                5, config.getScanPeriodSeconds(), SECONDS);
    }

    @Override
    public void run() {
        LOGGER.debug("run scan");
        Set<FileInfo> fileInfos = new HashSet<>();
        for (String dir : config.getScanPaths()) {
            try {
                fileInfos.addAll(scanDir(Paths.get(dir)));
            } catch (IOException ex) {
                LOGGER.warn("scan failed!", ex);
            }
        }


        fileInfos.forEach(fileInfo ->
          LOGGER.debug("{} {} {} {}",
                        fileInfo.getFileName(),
            fileInfo.getFileSize(),
                        fileInfo.getModTime(),
                        fileInfo.getPath())
        );
        processFileInfo(fileInfos);
    }

    private Set<FileInfo> scanDir(Path dir) throws IOException {
        LOGGER.trace("scanDir(): {}", dir);
        Set<FileInfo> fileInfos = new HashSet<>();
        try (DirectoryStream<Path> directoryStream = Files.newDirectoryStream(dir)) {
            for (Path path : directoryStream) {
                if (Files.isDirectory(path)) {
                    fileInfos.addAll(scanDir(path));
                }
                if (Files.isRegularFile(path)) {
                    LOGGER.trace("file: {}", path);
                    FileInfo fileInfo = new FileInfo();
                    fileInfo.setFileName(path.getFileName().toString());
                  fileInfo.setFileSize(Files.size(path));
                    fileInfo.setModTime(
                            Timestamp.from(Files.getLastModifiedTime(path).toInstant()));
                    fileInfo.setPath(path.toAbsolutePath().toString());
                    fileInfos.add(fileInfo);
                }
            }
        }
        return fileInfos;
    }

    private void processFileInfo(Set<FileInfo> fileInfos) {
        try {
            fileInfos.forEach(this::addOrUpdate);
        } catch (DataAccessException ex) {
            LOGGER.debug("Error:", ex);
        }

        Set<String> fileNames = fileInfos.stream()
                .map(FileInfo::getFileName)
                .collect(Collectors.toSet());
        fileInfoRepository.findAll()
                .forEach(fileInfo -> removeNotExiting(fileInfo, fileNames));

        fileInfoRepository.findAll()
                .forEach(fileInfo ->
                        LOGGER.debug("DB: {} {} {}",
                                fileInfo.getId(),
                                fileInfo.getFileName(),
                                fileInfo.getModTime())
                );

    }

    private void addOrUpdate(FileInfo fileInfo) {
        Optional<FileInfo> fileInfoOpt = fileInfoRepository.findByFileName(fileInfo.getFileName());
        if (fileInfoOpt.isPresent()) {
            FileInfo fileInfoExisting = fileInfoOpt.get();
            if (!fileInfoExisting.equals(fileInfo)) {
                LOGGER.debug("addOrUpdate(): update {}", fileInfo.getFileName());
                fileInfoExisting.setModTime(fileInfo.getModTime());
                fileInfoRepository.save(fileInfoExisting);
            }
        } else {
            LOGGER.debug("addOrUpdate(): add {}", fileInfo.getFileName());
            fileInfoRepository.save(fileInfo);
        }
    }

    private void removeNotExiting(FileInfo fileInfo, final Set<String> fileNames) {
        if (!fileNames.contains(fileInfo.getFileName())) {
            LOGGER.debug("removeNotExiting(): remove {}", fileInfo.getFileName());
            fileInfoRepository.delete(fileInfo);
        }
    }

}
