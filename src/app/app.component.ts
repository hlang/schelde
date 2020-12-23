import {Component, OnInit} from '@angular/core';
import {AppInfo, AppInfoService} from "./app-info.service";

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
    title = 'Schelde!';
    appInfo: AppInfo;

    constructor(private appInfoServer: AppInfoService) {
    }

    ngOnInit(): void {
        this.appInfoServer.getAppInfo()
            .subscribe(appInfo => this.appInfo = appInfo)
    }
}
