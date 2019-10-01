import { Component, OnInit, Input } from '@angular/core';
import { timingSafeEqual } from 'crypto';
import { AuthService } from '../auth/auth.service';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.sass']
})
export class RegisterComponent implements OnInit {

  private password: string;
  private confirmPassword: string;
  private username: string;
  private regFailed: boolean;
  private nameFailed: boolean;
  constructor(private auth: AuthService) { }

  ngOnInit(): void {
    this.regFailed = false;
    this.nameFailed = false;
  }

  register(): void {
    if (this.hasValidPassword() && this.hasValidUsername()) {
      this.auth.register(this.username, this.password);
      this.regFailed = false;
      this.nameFailed = false;
    } else {
      if (!this.hasValidPassword()) {
        this.regFailed = true;
      }
      if (!this.hasValidUsername()) {
        this.nameFailed = false;
      }
    }
  }
  setPassword(password: string) {
    this.regFailed = false;
    this.password = password;
  }
  setConfirmPassword(confirmPassword: string) {
    this.regFailed = false;
    this.confirmPassword = confirmPassword;
  }
  setUsername(username: string) {
    this.nameFailed = false;
    this.username = username;
  }
  hasValidPassword(): boolean {
    return this.password
      && this.confirmPassword
      && this.password === this.confirmPassword;
  }
  hasValidUsername(): boolean {
    return this.username && this.username.length > 0;
  }
}
