import {DataSource} from "@angular/cdk/collections";
import {FileInfoService} from "./file-info.service";
import {FileInfo} from "./file-info";
import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {MatPaginator, MatSort} from "@angular/material";
import {ElementRef} from "@angular/core";
import {Observable} from "rxjs/Observable";
import "rxjs/add/observable/of";
import "rxjs/add/observable/merge";
import 'rxjs/add/operator/debounceTime';
import 'rxjs/add/operator/distinctUntilChanged';
import 'rxjs/add/operator/do';
import 'rxjs/add/observable/fromEvent';
import "rxjs/add/operator/map";

export class FileInfoDataSource extends DataSource<FileInfo> {
    subject: BehaviorSubject<FileInfo[]>;

    constructor(private fileInfoService: FileInfoService,
                private paginator: MatPaginator,
                private filter: ElementRef,
                private sort: MatSort) {
        super();
    }

    connect(): Observable<FileInfo[]> {
        this.subject = new BehaviorSubject<FileInfo[]>([]);
        this.getFileInfos(0, "", this.sort);
        let filterObservable = Observable.fromEvent(this.filter.nativeElement, 'keyup')
            .debounceTime(200)
            .distinctUntilChanged()
            .do(() => this.resetPage());
        let pageObservable = this.paginator.page;
        let sortObservable = this.sort.sortChange
            .do(() => this.resetPage());
        let fileEventObservable = this.fileInfoService.getFileInfoStream()
            .debounceTime(200);
        Observable.merge(pageObservable, filterObservable, sortObservable, fileEventObservable)
            .subscribe(() =>
                this.getFileInfos(this.paginator.pageIndex,
                    this.filter.nativeElement.value,
                    this.sort));

        return this.subject;
    }

    disconnect() {
    }

    private getFileInfos(pageIndex: number, fileName: string, sort: MatSort) {
        this.fileInfoService.getFileInfos(pageIndex, fileName,
            this.getSortField(sort.active), sort.direction)
            .subscribe(searchResult => {
                this.subject.next(searchResult.fileInfos);
                this.paginator.length = searchResult.totalElements;
            })
    }

    private resetPage() {
        this.paginator.pageIndex = 0;
    }

    private getSortField(activeSort: string): string {
        switch (activeSort) {
            case "time":
                return "modTime";

            case "size":
                return "fileSize";

            case "filename":
            default:
                return "fileName";
        }
    }
}
