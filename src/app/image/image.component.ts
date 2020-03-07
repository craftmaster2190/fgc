import { Component, OnInit, Input, OnChanges } from "@angular/core";
import { ImagesCacheService } from "./images-cache.service";
import { Observable } from "rxjs";

@Component({
  selector: "app-image",
  templateUrl: "./image.component.html",
  styleUrls: ["./image.component.scss"]
})
export class ImageComponent implements OnChanges {
  @Input() target: string;
  src: Observable<string>;

  constructor(private readonly imagesCache: ImagesCacheService) {}

  ngOnChanges(): void {
    this.src = this.imagesCache.get(this.target);
  }
}
