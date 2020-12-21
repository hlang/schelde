import {FileSearchResult} from "./file-search-result";
import {Observable} from "rxjs";
import {HttpClient, HttpParams} from "@angular/common/http";
import {map} from "rxjs/operators";
import {Injectable} from "@angular/core";
import {FileInfo} from "./file-info";

@Injectable({
  providedIn: 'root'
})
export class FileInfoService {

    constructor(private http: HttpClient) {
    }

    private fileInfoUrl = 'fileInfoes/search/findByFileNameContainingIgnoreCase';

    getFileInfos(pageNum: number, fileName = "", sortField = "fileName", sortDirection = "asc"): Observable<FileSearchResult> {
        let params = new HttpParams()
            .set('page', pageNum.toString())
            .set('name', fileName)
            .set('sort', `${sortField},${sortDirection}`);

        return this.http.get(this.fileInfoUrl, { params})
          .pipe(map(res => this.extractData(res)));

    }

    getFileInfoByDigest(digest: string): Observable<FileInfo> {
        let params = new HttpParams()
            .set('digest', digest);

        return this.http.get<FileInfo>('fileInfoes/search/findByDigest', {params});
    }

    getFileInfoStream(): Observable<Object> {
        return new Observable<Object>(obs => {
            const es = new EventSource('/download/file/events');
            es.addEventListener('message', (evt: any) => {
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
