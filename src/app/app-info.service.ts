import {Injectable} from '@angular/core';
import {HttpClient} from "@angular/common/http";
import {Observable} from "rxjs";

export interface AppInfo {
    app: AppDetails;
}

export interface AppDetails {
    name: string;
    version: string;
}

@Injectable({
    providedIn: 'root'
})
export class AppInfoService {

    constructor(private http: HttpClient) {
    }

    getAppInfo(): Observable<AppInfo> {
        return this.http.get<AppInfo>('actuator/info');
    }
}
