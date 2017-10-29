import {Injectable} from '@angular/core';
import {Http, RequestOptions, Response, ResponseContentType, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {FileSearchResult} from "./file-search-result";
import "rxjs/add/operator/map";
import {FileInfo} from "./file-info";


@Injectable()
export class FileInfoService {

  constructor(private http: Http) {
  }

    private fileInfoUrl = 'fileInfoes/search/findByFileNameContainingIgnoreCase';
    private downloadUrl = 'download/file/';

    getFileInfos(pageNum: number, fileName = ""): Observable<FileSearchResult> {
    let params: URLSearchParams = new URLSearchParams();
    params.set('sort', 'fileName');
    params.set('page', String(pageNum));
        params.set('name', '%' + fileName + '%');
    let options = new RequestOptions();
    options.params = params;

    return this.http.get(this.fileInfoUrl, options)
      .map(res => this.extractData(res));

  }

    downloadFile(file: FileInfo): Observable<Blob> {
        return this.http.get(this.downloadUrl + file.id, {responseType: ResponseContentType.Blob})
            .map(res => res.blob());
    }

  private extractData(res: Response): FileSearchResult {
    let body = res.json();
    let searchResult = new FileSearchResult();
    if (body._embedded) {
      searchResult.fileInfos = body._embedded.fileInfoes;
      searchResult.totalElements = body.page.totalElements;
      searchResult.size = body.page.size;
      searchResult.totalPages = body.page.totalPages;
      searchResult.pageNumber = body.page.number;
    } else {
      searchResult.totalElements = 0;
    }

    return searchResult;
  }
}
