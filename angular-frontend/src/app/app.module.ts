import {HTTP_INTERCEPTORS, HttpClientModule} from '@angular/common/http';
import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { MatToolbarModule } from '@angular/material/toolbar';
import { MatButtonModule } from "@angular/material/button";
import { AppRoutingModule } from './app-routing.module';
import { HomeComponent } from './home.component';
import { TopBarComponent } from "./top-bar/top-bar.component";
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import {CustomerProfileModule} from "./customer-profile/customer-profile.module";
import {AuthInterceptor} from "./authorization/auth.intercepter";
import {CookieService} from "./authorization/cookie.service";

@NgModule({
  declarations: [
    HomeComponent,
    TopBarComponent,
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    BrowserAnimationsModule,
    CustomerProfileModule,
    MatToolbarModule,
    MatButtonModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: AuthInterceptor,
      multi: true
    },
    CookieService
  ],
  bootstrap: [HomeComponent]
})
export class AppModule {
}
