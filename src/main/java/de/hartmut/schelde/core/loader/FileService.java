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

import de.hartmut.schelde.core.db.FileInfo;
import de.hartmut.schelde.core.db.FileInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * hartmut on 10.02.18.
 */
@Service
public class FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    private final FileInfoRepository fileInfoRepository;
    private Flux<FileEvent> eventFlux;
    private List<FileEventListener> listeners = new ArrayList<>();

    public FileService(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    @PostConstruct
    public void init() {
        LOGGER.debug("init()");
        eventFlux = Flux.push(sink -> {
            registerListener(new FileEventListener() {

                @Override
                public void onDataChunk(List<FileEvent> chunk) {
                    chunk.forEach(event -> sink.next(event));
                }

                @Override
                public void processComplete() {
                    sink.complete();
                }
            });
        });
    }

    private void registerListener(FileEventListener listener) {
        listeners.add(listener);
    }

    public void addOrUpdate(Path path) {
        LOGGER.debug("addOrUpdate(): {}", path);
        try {
            if (Files.isRegularFile(path) && !Files.isHidden(path)) {
                FileInfo fileInfo = new FileInfo();
                fileInfo.setFileName(path.getFileName().toString());
                fileInfo.setFileSize(Files.size(path));
                fileInfo.setModTime(
                    Date.from(Files.getLastModifiedTime(path).toInstant()));
                fileInfo.setPath(path.toAbsolutePath().toString());
                addOrUpdate(fileInfo);
            }
        } catch (IOException ex) {
            LOGGER.warn("addOrUpdate(): failed!", ex);
        }
    }

    private void addOrUpdate(FileInfo fileInfo) {
        FileInfo fileInfoExisting = fileInfoRepository.findByFileName(fileInfo.getFileName());
        if (fileInfoExisting != null) {
            if (!fileInfoExisting.equals(fileInfo)) {
                LOGGER.debug("addOrUpdate(): update {}", fileInfo.getFileName());
                fileInfoExisting.setModTime(fileInfo.getModTime());
                fileInfoExisting.setFileSize(fileInfo.getFileSize());
                fileInfoRepository.save(fileInfoExisting);
            }
        } else {
            LOGGER.debug("addOrUpdate(): add {}", fileInfo.getFileName());
            fileInfoRepository.save(fileInfo);
        }
        publishEvent(fileInfo.getFileName());
    }

    public void removeFile(Path path) {
        LOGGER.debug("removeFile(): {}", path);

        String filename = path.getFileName().toString();
        FileInfo fileInfo = fileInfoRepository.findByFileName(filename);
        if (fileInfo != null) {
            fileInfoRepository.delete(fileInfo);
            publishEvent(filename);
        }
    }

    private void publishEvent(String fileName) {
        LOGGER.trace("publishEvent(): for {}", fileName);
        FileEvent fileEvent = new FileEvent();
        fileEvent.setFilename(fileName);
        listeners.forEach(listener -> listener.onDataChunk(Collections.singletonList(fileEvent)));
    }

    public Flux<FileEvent> getEventFlux() {
        return eventFlux;
    }

    private interface FileEventListener {
        void onDataChunk(List<FileEvent> chunk);

        void processComplete();
    }

}
