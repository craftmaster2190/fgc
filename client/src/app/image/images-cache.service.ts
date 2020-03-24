import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, of, Subject, from, interval } from "rxjs";
import { switchMap, tap, debounce } from "rxjs/operators";
import { Optional } from "../util/optional";

const KEY = "IMAGES_";

@Injectable({
  providedIn: "root"
})
export class ImagesCacheService {
  private readonly invalidateDebounce = {};
  private readonly fetchSemaphores: {
    [target: string]: Promise<string>;
  } = {};

  constructor(private readonly http: HttpClient) {}

  get(target: string): Observable<string> {
    return Optional.of(sessionStorage.getItem(KEY + target))
      .map(imageData => {
        return of(imageData);
      })
      .orElseGet(() => {
        const fetchPromise = Optional.of(
          this.fetchSemaphores[target]
        ).orElseGet(() => {
          return this.http
            .get(target, { responseType: "blob" })
            .pipe(
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
            .toPromise();
        });
        this.fetchSemaphores[target] = fetchPromise;
        return from(fetchPromise);
      });
  }

  has(target: string) {
    return !!sessionStorage.getItem(KEY + target);
  }

  invalidate(target: string) {
    sessionStorage.removeItem(KEY + target);
    if (!this.invalidateDebounce[target]) {
      this.invalidateDebounce[target] = new Subject<void>();
      this.invalidateDebounce[target]
        .pipe(debounce(() => interval(1000)))
        .subscribe(() => delete this.fetchSemaphores[target]);
    }
    this.invalidateDebounce[target].next();
  }
}
