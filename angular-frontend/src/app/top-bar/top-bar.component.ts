import { Component } from '@angular/core';
import {AuthService} from "../authorization/auth.service";

@Component({
  selector: 'app-top-bar',
  templateUrl: './top-bar.component.html',
  styleUrls: ['./top-bar.component.css']
})
export class TopBarComponent {
  public isLoggedIn = false;

  constructor(private _service: AuthService) { }

  ngOnInit() {
    this.isLoggedIn = this._service.isSignedIn();
    let i = window.location.href.indexOf('code');
    if(!this.isLoggedIn && i != -1) {
      this._service.retrieveToken(window.location.href.substring(i + 5));
    }
  }

  login() {
    window.location.href =
      'http://localhost:8083/auth/realms/baeldung/protocol/openid-connect/auth?response_type=code&scope=openid%20write%20read&client_id=' +
    this._service.clientId + '&redirect_uri='+ this._service.redirectUri;
  }

  logout() {
    this._service.logout();
  }
}
