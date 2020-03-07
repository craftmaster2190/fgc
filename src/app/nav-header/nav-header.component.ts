import { Component, OnInit, OnDestroy } from "@angular/core";
import { Router } from "@angular/router";
import { DeviceUsersService } from "../auth/device-users.service";
import { NgbModal } from "@ng-bootstrap/ng-bootstrap";
import { Observable, Subject, interval, Subscription } from "rxjs";
import {
  debounceTime,
  distinctUntilChanged,
  filter,
  tap,
  switchMap,
  map,
  debounce
} from "rxjs/operators";
import { Family } from "../family/family";
import ResizeImage from "image-resize";

@Component({
  selector: "app-nav-header",
  templateUrl: "./nav-header.component.html",
  styleUrls: ["./nav-header.component.scss"]
})
export class NavHeaderComponent implements OnInit, OnDestroy {
  name: string;
  family: string;
  private updateUserSubject = new Subject();
  private subscription: Subscription;

  constructor(
    public readonly authService: DeviceUsersService,
    private readonly modalService: NgbModal
  ) {}

  ngOnInit() {
    this.subscription = this.updateUserSubject
      .pipe(debounce(() => interval(300)))
      .subscribe(() =>
        this.authService.updateUser({ name: this.name, family: this.family })
      );
  }

  ngOnDestroy() {
    this.subscription.unsubscribe();
  }

  openModal(content) {
    if (this.authService.getCurrentUser()) {
      this.name = this.getUsername();
      this.family = this.getFamilyName();
      this.modalService.open(content);
    }
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

  onFileSelected(event) {
    const fileInputNode: HTMLInputElement = event.target;
    const resizeImage = new ResizeImage({
      format: "png",
      width: 1000
    });
    return resizeImage.play(fileInputNode);
  }
}
