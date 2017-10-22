import {Component, OnInit, ViewChild} from '@angular/core';
import {MatPaginator} from '@angular/material';
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
  displayedColumns = ['fileName', 'time', 'size', 'Download'];
  fileInfoDs: FileInfoDataSource | null;

  constructor(private fileInfoService: FileInfoService) {
  }

  @ViewChild(MatPaginator) paginator: MatPaginator;

  ngOnInit() {
    this.getFileInfos();
    this.fileInfoDs = new FileInfoDataSource(this.fileInfoService, this.paginator);
  }


  private getFileInfos(): void {
    this.fileInfoService.getFileInfos(0)
      .subscribe(searchResult => {
          this.searchResult = searchResult;
          this.paginator.length = searchResult.totalElements;
          this.paginator.pageSize = searchResult.size;
          this.paginator.pageIndex = searchResult.pageNumber;

        },
        response => {
        }
      );
  }

}
