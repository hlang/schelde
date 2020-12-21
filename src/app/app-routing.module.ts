import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import {FileTableComponent} from "./file-table/file-table.component";
import {FileDetailsComponent} from "./file-details/file-details.component";

const routes: Routes = [
    { path: 'files', component: FileTableComponent },
    { path: 'file/:digest', component: FileDetailsComponent },
    { path: '',   redirectTo: '/files', pathMatch: 'full' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
