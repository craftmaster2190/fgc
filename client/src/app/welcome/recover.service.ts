import { HttpClient } from "@angular/common/http";
import { Injectable } from "@angular/core";
import { Observable, of } from "rxjs";
import { map, catchError } from "rxjs/operators";
import RecoveryRequest from "./recovery-request";

function is2XX(status: number) {
  status = status | 0;
  return status >= 200 && status < 300;
}

export interface RecoverySuccess {
  recoverySuccess: boolean;
}

@Injectable({
  providedIn: "root"
})
export class RecoverService {
  constructor(private readonly http: HttpClient) {}

  generateRecoveryCode(): Observable<string> {
    return this.http.post("/api/recovery/generate", null, {
      responseType: "text"
    });
  }

  recoverViaCode(recoveryCode: string, name: string, family: string) {
    return this.http.put<void>("/api/recovery", { recoveryCode, name, family });
  }

  tryRecoverMe(name: string, family: string): Observable<RecoverySuccess> {
    return this.http
      .post(
        "/api/recovery",
        { name, family },
        {
          observe: "response",
          responseType: "text"
        }
      )
      .pipe(
        map(response => {
          if (response.status === 200) {
            return { recoverySuccess: true };
          }
          if (is2XX(response.status)) {
            return { recoverySuccess: false };
          }
          throw new Error("Invalid response: " + JSON.stringify(response));
        }),
        catchError(err => {
          console.log(err);
          return of({ recoverySuccess: false });
        })
      );
  }

  applyUserCommentToRecoveryRequest(
    name: string,
    family: string,
    userComment: string
  ) {
    return this.http.patch<void>("/api/recovery/request", {
      name,
      family,
      userComment
    });
  }

  adminGetRecoveryRequests(): Observable<Array<RecoveryRequest>> {
    return this.http.get<Array<RecoveryRequest>>("/api/recovery/request");
  }

  adminApproveRecoveryRequest(recoveryRequest: RecoveryRequest): void {
    this.http.put("/api/recovery/request", recoveryRequest);
  }
}
