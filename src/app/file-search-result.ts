import {FileInfo} from "./file-info";

export class FileSearchResult {
  fileInfos: Array<FileInfo>;
  size: number;
  totalElements: number;
  totalPages: number;
  pageNumber: number
}
