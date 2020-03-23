import { Component, OnInit, Input, OnChanges } from "@angular/core";
import { ImagesCacheService } from "./images-cache.service";
import { Observable } from "rxjs";

@Component({
  selector: "app-image",
  templateUrl: "./image.component.html",
  styleUrls: ["./image.component.scss"]
})
export class ImageComponent {
  @Input() target: string;
  @Input() imgStyle: string;

  constructor(private readonly imagesCache: ImagesCacheService) {}

  getSrc() {
    return this.imagesCache.get(this.target);
  }
}
