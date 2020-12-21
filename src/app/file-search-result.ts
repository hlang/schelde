import {FileInfo} from "./file-info";

export interface FileSearchResult {
    fileInfos?: FileInfo[];
    size?: number;
  totalElements: number;
    totalPages?: number;
    pageNumber?: number
}
