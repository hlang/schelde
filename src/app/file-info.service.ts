import {Injectable} from '@angular/core';
import {Http, RequestOptions, Response, URLSearchParams} from "@angular/http";
import {Observable} from "rxjs";
import {FileSearchResult} from "./file-search-result";
import "rxjs/add/operator/map";

@Injectable()
export class FileInfoService {

    constructor(private http: Http) {
    }

    private fileInfoUrl = 'fileInfoes/search/findByFileNameContainingIgnoreCase';
    private downloadUrl = 'download/file/';

    getFileInfos(pageNum: number, fileName = "", sortField = "fileName", sortDirection = "asc"): Observable<FileSearchResult> {
        let params: URLSearchParams = new URLSearchParams();
        params.set('sort', 'fileName');
        params.set('page', String(pageNum));
        params.set('name', '%' + fileName + '%');
        params.set('sort', sortField + ',' + sortDirection);
        let options = new RequestOptions();
        options.params = params;

        return this.http.get(this.fileInfoUrl, options)
            .map(res => this.extractData(res));

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
