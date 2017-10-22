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

package de.hartmut.schelde.core.db;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Timestamp;
import java.util.Objects;

/**
 * hartmut on 14.10.17.
 */

@Entity
public class FileInfo {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private long id;
  private String fileName;
  private Long fileSize;
  private Timestamp modTime;
  private String path;

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getFileName() {
    return fileName;
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public Timestamp getModTime() {
    return modTime;
  }

  public void setModTime(Timestamp modTime) {
    this.modTime = modTime;
  }

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public Long getFileSize() {
    return fileSize;
  }

  public void setFileSize(Long fileSize) {
    this.fileSize = fileSize;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    FileInfo fileInfo = (FileInfo) o;
    return Objects.equals(fileName, fileInfo.fileName) &&
      Objects.equals(fileSize, fileInfo.fileSize) &&
      Objects.equals(modTime, fileInfo.modTime) &&
      Objects.equals(path, fileInfo.path);
  }

  @Override
  public int hashCode() {
    return Objects.hash(fileName, fileSize, modTime, path);
  }
}

