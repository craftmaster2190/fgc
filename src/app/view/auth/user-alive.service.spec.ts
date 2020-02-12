import { TestBed } from '@angular/core/testing';

import { UserAliveService } from './user-alive.service';

describe('UserAliveService', () => {
  let service: UserAliveService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(UserAliveService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
