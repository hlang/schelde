import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {MatTableModule} from "@angular/material/table";
import {MatInputModule} from "@angular/material/input";
import {MatButtonModule} from "@angular/material/button";
import {MatPaginatorModule} from "@angular/material/paginator";
import {MatFormFieldModule} from "@angular/material/form-field";
import {MatTooltipModule} from "@angular/material/tooltip";
import {MatSortModule} from "@angular/material/sort";
import {BrowserAnimationsModule} from "@angular/platform-browser/animations";
import {HttpClientModule} from "@angular/common/http";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {NgModule} from "@angular/core";
import {BrowserModule} from "@angular/platform-browser";
import {FileTableComponent} from './file-table/file-table.component';
import {FileDetailsComponent} from './file-details/file-details.component';
import {MatCardModule} from "@angular/material/card";
import {ClipboardModule} from "@angular/cdk/clipboard";

@NgModule({
    declarations: [
        AppComponent,
        FileTableComponent,
        FileDetailsComponent
    ],
    imports: [
        BrowserModule,
        FormsModule,
        ReactiveFormsModule,
        HttpClientModule,
        BrowserAnimationsModule,
        AppRoutingModule,
        MatTableModule,
        MatPaginatorModule,
        MatButtonModule,
        MatInputModule,
        MatFormFieldModule,
        MatTooltipModule,
        MatSortModule,
        MatCardModule,
        ClipboardModule
    ],
    providers: [],
    bootstrap: [AppComponent]
})
export class AppModule {
}
