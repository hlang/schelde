import {Component, ElementRef, OnInit, ViewChild} from '@angular/core';
import {MatPaginator, MatSort} from '@angular/material';
import {FileInfoService} from "./file-info.service";
import {FileSearchResult} from "./file-search-result";
import {FileInfoDataSource} from "./file-info-data-source";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
    title = 'Schelde!';
    searchResult: FileSearchResult | null;
    displayedColumns = ['fileName', 'time', 'size', 'download'];
    fileInfoDs: FileInfoDataSource | null;

    constructor(private fileInfoService: FileInfoService) {
    }

    @ViewChild(MatPaginator) paginator: MatPaginator;
    @ViewChild('filter') filter: ElementRef;
    @ViewChild(MatSort) sort: MatSort;

    ngOnInit() {
        this.sort.disableClear = true;
        this.sort.direction = "asc";
        this.sort.active = "fileName";
        this.fileInfoDs =
            new FileInfoDataSource(
                this.fileInfoService,
                this.paginator,
                this.filter,
                this.sort);
    }


}
