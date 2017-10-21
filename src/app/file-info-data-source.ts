import {DataSource} from "@angular/cdk/collections";
import {FileInfoService} from "./file-info.service";
import {FileInfo} from "./file-info";
import {Observable} from "rxjs/Observable";
import "rxjs/add/observable/of";
import "rxjs/add/observable/merge";
import "rxjs/add/operator/map";
import {BehaviorSubject} from "rxjs/BehaviorSubject";
import {MatPaginator} from "@angular/material";


export class FileInfoDataSource extends DataSource<FileInfo> {
  subject: BehaviorSubject<FileInfo[]>;

  constructor(private fileInfoService: FileInfoService,
              private paginator: MatPaginator) {
    super();
  }

  /** Connect function called by the table to retrieve one stream containing the data to render. */
  connect(): Observable<FileInfo[]> {
    this.subject = new BehaviorSubject<FileInfo[]>([]);
    this.getFileinfos(0);
    this.paginator.page
      .subscribe(event => this.getFileinfos(event.pageIndex));
    return this.subject;
  }

  disconnect() {
  }

  private getFileinfos(pageIndex: number) {
    this.fileInfoService.getFileInfos(pageIndex)
      .map(searchResult => searchResult.fileInfos)
      .subscribe(fileInfos => this.subject.next(fileInfos))
  }
}
