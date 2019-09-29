import { TestBed } from '@angular/core/testing';

import { AnswerBusService } from './answer-bus.service';

describe('AnswerBusService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: AnswerBusService = TestBed.get(AnswerBusService);
    expect(service).toBeTruthy();
  });
});
