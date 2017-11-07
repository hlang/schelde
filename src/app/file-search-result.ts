import {FileInfo} from "./file-info";

export interface FileSearchResult {
    fileInfos?: Array<FileInfo>;
    size?: number;
  totalElements: number;
    totalPages?: number;
    pageNumber?: number
}
