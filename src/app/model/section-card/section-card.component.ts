import { Component, Input, OnInit } from '@angular/core';

@Component({
  selector: "app-section-card",
  templateUrl: "./section-card.component.html",
  styleUrls: ["./section-card.component.sass"]
})
export class SectionCardComponent implements OnInit {
  @Input() openPanels: Record<string, boolean> = {};
  constructor() {}

  ngOnInit() {}
}
