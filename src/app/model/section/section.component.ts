import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: "app-section",
  templateUrl: "./section.component.html",
  styleUrls: ["./section.component.sass"]
})
export class SectionComponent implements OnInit {
  @Input() title: string;
  @Input() openPanels: Record<string, boolean> = {};
  constructor() {}

  ngOnInit() {}
}
