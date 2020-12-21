import {AfterViewInit, Component, OnDestroy, OnInit, ViewChild} from '@angular/core';
import {debounceTime, distinctUntilChanged} from "rxjs/operators";
import {MatSort} from "@angular/material/sort";
import {BehaviorSubject, Subscription} from "rxjs";
import {FormControl} from "@angular/forms";
import {FileInfo} from "../file-info";
import {FileInfoService} from "../file-info.service";
import {MatPaginator} from "@angular/material/paginator";

@Component({
  selector: 'app-file-table',
  templateUrl: './file-table.component.html',
  styleUrls: ['./file-table.component.css']
})
export class FileTableComponent implements OnInit, OnDestroy, AfterViewInit {

    pageSize = 20;
    displayedColumns = ['fileName', 'time', 'size', 'download'];
    fileNameControl = new FormControl('');

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild(MatSort) sort: MatSort;

    fileInfoSubject = new BehaviorSubject<FileInfo[]>([]);

    private streamSubscription: Subscription;

    constructor(private fileInfoService: FileInfoService) {
    }

    ngOnInit(): void {
        this.fileNameControl.valueChanges
            .pipe(debounceTime(200),
                distinctUntilChanged())
            .subscribe(() => {
                this.resetPage();
                this.loadData();
            })
        this.streamSubscription = this.fileInfoService.getFileInfoStream()
            .subscribe(() => this.loadData());
    }


    ngAfterViewInit(): void {
        this.loadData();
    }

    ngOnDestroy(): void {
        this.streamSubscription?.unsubscribe();
    }


    sortEvent() {
        this.resetPage();
        this.loadData();
    }

    pageEvent() {
        this.loadData();
    }

    private loadData() {
        const page = this.paginator.pageIndex;
        const filename = this.fileNameControl.value;
        const sortColumn = this.getSortField(this.sort.active);
        const sortDirection = this.sort.direction;
        this.fileInfoService.getFileInfos(page, filename, sortColumn, sortDirection)
            .subscribe(searchResult => {
                this.paginator.length = searchResult.totalElements;
                this.fileInfoSubject.next(searchResult.fileInfos);
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
