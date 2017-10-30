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

package de.hartmut.schelde.core.rest;

import de.hartmut.schelde.core.db.FileInfo;
import de.hartmut.schelde.core.db.FileInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * hartmut on 27.10.17.
 */
@RestController
@RequestMapping("/download")
public class FileInfoController {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileInfoController.class);

    private final FileInfoRepository fileInfoRepository;

    @Autowired
    public FileInfoController(FileInfoRepository fileInfoRepository) {
        this.fileInfoRepository = fileInfoRepository;
    }

    @GetMapping("/file/{fileId}")
    public ResponseEntity<Resource> downloadById(@PathVariable("fileId") Long fileId) throws IOException {
        FileInfo fileInfo = fileInfoRepository.findOne(fileId);
        if (fileInfo == null) {
            ResponseEntity.notFound();
        }

        LOGGER.info("download: {}, {}", fileInfo.getId(), fileInfo.getFileName());
        Path filePath = Paths.get(fileInfo.getPath());

        InputStreamResource resource = new InputStreamResource(Files.newInputStream(filePath));

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment;filename=" + "\"" + fileInfo.getFileName() + "\"");

        return ResponseEntity.ok()
            .headers(headers)
            .contentLength(fileInfo.getFileSize())
            .contentType(MediaType.parseMediaType("application/octet-stream"))
            .body(resource);
    }
}
