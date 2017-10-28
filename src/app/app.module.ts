import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';
import {FormsModule} from '@angular/forms';
import {HttpModule} from '@angular/http';

import {AppComponent} from './app.component';
import {FileInfoService} from "./file-info.service";
import {MatButtonModule, MatPaginatorModule, MatTableModule} from "@angular/material";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";

@NgModule({
    declarations: [
        AppComponent,
    ],
    imports: [
        BrowserModule,
        FormsModule,
        HttpModule,
        BrowserAnimationsModule,
        MatTableModule,
        MatPaginatorModule,
        MatButtonModule
    ],
    providers: [
        FileInfoService
    ],
    bootstrap: [AppComponent]
})
export class AppModule {
}
