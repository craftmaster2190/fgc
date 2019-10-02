import { TestBed } from '@angular/core/testing';

import { ChatBusService } from './chat-bus.service';

describe('ChatBusService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ChatBusService = TestBed.get(ChatBusService);
    expect(service).toBeTruthy();
  });
});
