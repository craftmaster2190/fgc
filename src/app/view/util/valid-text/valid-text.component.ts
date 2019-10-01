import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: "app-valid-text",
  templateUrl: "./valid-text.component.html",
  styleUrls: ["./valid-text.component.sass"]
})
export class ValidTextComponent implements OnInit {
  @Input() text: string;
  @Input() valid: boolean;

  constructor() {}

  ngOnInit() {}
}
