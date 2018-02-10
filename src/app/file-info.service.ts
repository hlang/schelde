import {Injectable} from '@angular/core';
import {Observable} from "rxjs";
import {FileSearchResult} from "./file-search-result";
import "rxjs/add/operator/map";
import {HttpClient, HttpParams} from "@angular/common/http";

declare var EventSource;

@Injectable()
export class FileInfoService {

    constructor(private http: HttpClient) {
    }

    private fileInfoUrl = 'fileInfoes/search/findByFileNameContainingIgnoreCase';

    getFileInfos(pageNum: number, fileName = "", sortField = "fileName", sortDirection = "asc"): Observable<FileSearchResult> {
        let params = new HttpParams()
            .set('sort', 'fileName')
            .set('page', String(pageNum))
            .set('name', fileName)
            .set('sort', sortField + ',' + sortDirection);

        return this.http.get(this.fileInfoUrl, {
            params: params
        }).map(res => this.extractData(res));

    }

    getFileInfoStream(): Observable<Object> {
        return new Observable<Object>(obs => {
            const es = new EventSource('/download/file/events');
            es.addEventListener('message', (evt) => {
                // console.log(evt.data);
                obs.next(evt.data);
            });
            return () => es.close();
        });
    }

    private extractData(response: Object): FileSearchResult {
        let searchResult: FileSearchResult;
        if (response['_embedded']) {
            searchResult = {
                fileInfos: response['_embedded'].fileInfoes,
                totalElements: response['page'].totalElements,
                size: response['page'].size,
                totalPages: response['page'].totalPages,
                pageNumber: response['page'].number
            }
        } else {
            searchResult = {
                totalElements: 0
            }
        }

        return searchResult;
    }
}
