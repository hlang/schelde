import {inject, TestBed} from '@angular/core/testing';

import {FileInfoService} from './file-info.service';

describe('FileInfoService', () => {
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [FileInfoService]
    });
  });

  it('should ...', inject([FileInfoService], (service: FileInfoService) => {
    expect(service).toBeTruthy();
  }));
});
