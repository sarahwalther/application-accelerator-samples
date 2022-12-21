import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders, HttpStatusCode} from "@angular/common/http";
import {CookieService} from "./cookie.service";
import {AuthToken} from "./auth-types";

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  public clientId = 'newClient';
  public redirectUri = 'http://localhost:8089/';

  constructor(private _http: HttpClient, private _cookieService: CookieService) { }

  retrieveToken(code: any) {
    let params = new URLSearchParams();
    params.append('grant_type','authorization_code');
    params.append('client_id', this.clientId);
    params.append('redirect_uri', this.redirectUri);
    params.append('code',code);

    let headers =
      new HttpHeaders({'Content-type': 'application/x-www-form-urlencoded; charset=utf-8'});

    this._http.post('http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/token',
      params.toString(), { headers: headers })
      .subscribe(
        data => this.saveToken(data as AuthToken),
        err => alert('Invalid Credentials'));
  }

  saveToken(token: AuthToken) {
    let expireDate = new Date().getTime() + (1000 * token.expires_in);
    this._cookieService.setCookie("access_token", token.access_token, expireDate)
    // Cookie.set("access_token", token.access_token, expireDate);
    console.log('Obtained Access token');
    window.location.href = 'http://localhost:8089';
  }

  isSignedIn(): boolean {
    return this._cookieService.getCookie('access_token') !== '';
  }

  logout() {
    this._cookieService.deleteCookie('access_token');
    window.location.reload();
  }
}
