import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, of, Subject } from "rxjs";
import { switchMap, tap } from "rxjs/operators";
import { Optional } from "../util/optional";

const KEY = "IMAGES_";

@Injectable({
  providedIn: "root"
})
export class ImagesCacheService {
  constructor(private readonly http: HttpClient) {}

  get(target: string): Observable<string> {
    return Optional.of(sessionStorage.getItem(KEY + target))
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
          tap(dataUrl => sessionStorage.setItem(KEY + target, dataUrl))
        )
      );
  }

  invalidate(target: string) {
    sessionStorage.removeItem(KEY + target);
  }
}
