import {Component, OnInit} from '@angular/core';
import {ActivatedRoute, Router} from "@angular/router";
import {FileInfoService} from "../file-info.service";
import {FileInfo} from "../file-info";

@Component({
    selector: 'app-file-details',
    templateUrl: './file-details.component.html',
    styleUrls: ['./file-details.component.css']
})
export class FileDetailsComponent implements OnInit {

    fileInfo: FileInfo;

    constructor(private route: ActivatedRoute,
                private router: Router,
                private fileInfoService: FileInfoService) {
    }

    ngOnInit(): void {
        this.route.paramMap.subscribe(
            params => this.loadFileInfo(params.get('digest'))
        );
    }

    getCurrentUrl(): string {
        return window.location.href;
    }

    private loadFileInfo(digest: string) {
        if(digest) {
            this.fileInfoService.getFileInfoByDigest(digest)
                .subscribe(fileInfo => this.fileInfo = fileInfo);
        }
    }
}
