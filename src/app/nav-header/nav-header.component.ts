import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { DeviceUsersService } from "../auth/device-users.service";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { Observable } from "rxjs";
import {
  debounceTime,
  distinctUntilChanged,
  filter,
  tap,
  switchMap,
  map
} from "rxjs/operators";
import { Family } from "../family/family";

@Component({
  selector: "app-nav-header",
  templateUrl: "./nav-header.component.html",
  styleUrls: ["./nav-header.component.scss"]
})
export class NavHeaderComponent implements OnInit {
  name: string;
  family: string;

  constructor(
    public readonly authService: DeviceUsersService,
    private modalService: NgbModal
  ) {}

  ngOnInit() {}

  openModal(content) {
    this.name = this.getUsername();
    this.family = this.getFamilyName();
    this.modalService.open(content);
  }

  getUsername() {
    return this.authService.getCurrentUser()?.name || "";
  }

  getFamilyName() {
    return this.authService.getCurrentUser()?.family?.name || "";
  }

  logout(modal) {
    modal.dismiss("Logout");
    this.authService.logoutUser();
  }

  searchingFamilies: boolean;

  searchFamilies = (text$: Observable<string>) => {
    return text$.pipe(
      debounceTime(200),
      distinctUntilChanged(),
      filter(term => term && term.length >= 2),
      tap(() => (this.searchingFamilies = true)),
      switchMap(term =>
        this.authService
          .searchFamilies(term)
          .pipe(map(family => family.map(f => f.name)))
      ),
      tap(() => (this.searchingFamilies = false))
    );
  };

  nameValid() {
    return (this.name?.length || 0) >= 4;
  }

  familyValid() {
    return (this.family?.length || 0) >= 4;
  }
}
