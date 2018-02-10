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

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Date;

/**
 * hartmut on 10.02.18.
 */
@Service
public class FileService {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileService.class);

    private final FileInfoRepository fileInfoRepository;

    public FileService(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
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
    }

    public void removeFile(Path path) {
        LOGGER.debug("removeFile(): {}", path);

        FileInfo fileInfo = fileInfoRepository.findByFileName(path.getFileName().toString());
        if (fileInfo != null) {
            fileInfoRepository.delete(fileInfo);
        }
    }
}
