import {DataSource} from "@angular/cdk/collections";
import {FileInfoService} from "./file-info.service";
import {FileInfo} from "./file-info";
import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {MatPaginator} from "@angular/material";
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
                private filter: ElementRef) {
        super();
    }

    connect(): Observable<FileInfo[]> {
        this.subject = new BehaviorSubject<FileInfo[]>([]);
        this.getFileInfos(0, "");
        let filterObservable = Observable.fromEvent(this.filter.nativeElement, 'keyup')
            .debounceTime(200)
            .distinctUntilChanged()
            .do(() => this.paginator.pageIndex = 0);
        Observable.merge(this.paginator.page, filterObservable)
            .subscribe(() =>
                this.getFileInfos(this.paginator.pageIndex, this.filter.nativeElement.value));

        return this.subject;
    }

    disconnect() {
    }

    private getFileInfos(pageIndex: number, fileName: string) {
        this.fileInfoService.getFileInfos(pageIndex, fileName)
            .subscribe(searchResult => {
                this.subject.next(searchResult.fileInfos);
                this.paginator.length = searchResult.totalElements;
            })
    }
}
