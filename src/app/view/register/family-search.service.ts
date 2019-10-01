import { Family } from '../auth/family';
import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';

@Injectable()
export class FamilySearchService {
  constructor(private readonly httpClient: HttpClient) {}

  search(partialFamilyName: string) {
    return this.httpClient.get<Array<Family>>(
      "/register/family?search=" + partialFamilyName
    );
  }
}
