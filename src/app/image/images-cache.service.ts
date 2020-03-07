import { Injectable } from "@angular/core";
import { of, Observable, Subject } from "rxjs";
import { HttpClient } from "@angular/common/http";
import { Optional } from "../util/optional";
import { map, tap, switchMap } from "rxjs/operators";

const KEY = "IMAGES_";

@Injectable({
  providedIn: "root"
})
export class ImagesCacheService {
  constructor(private readonly http: HttpClient) {}

  get(target: string): Observable<string> {
    return Optional.of(localStorage.getItem(KEY + target))
      .map(imageData => of(imageData))
      .orElseGet(() =>
        this.http.get(target, { responseType: "blob" }).pipe(
          switchMap(blob => {
            const fileReader = new FileReader();
            const resultSubject = new Subject<string>();
            fileReader.onload = () =>
              resultSubject.next(fileReader.result as string);
            fileReader.onerror = event => resultSubject.error(event);
            fileReader.readAsDataURL(blob);
            return resultSubject;
          }),
          tap(dataUrl => console.log("DataUrl", dataUrl))
        )
      );
  }

  postProfileImage;
}
