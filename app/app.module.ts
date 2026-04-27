// angular imports
import { BrowserModule } from '@angular/platform-browser';
import { NgModule }      from '@angular/core';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { HttpClientModule, HTTP_INTERCEPTORS } from '@angular/common/http';
import { FormsModule, ReactiveFormsModule }    from '@angular/forms';

// your components
import { AppComponent }         from './app.component';
import { NavbarComponent }      from './navbar/navbar.component';
import { LoginComponent }       from './auth/login/login.component';
import { RegisterComponent }    from './auth/register/register.component';
import { BuyerDashboardComponent }  from './buyer/dashboard/dashboard.component';
import { SellerDashboardComponent } from './seller/dashboard/dashboard.component';
import { AdminDashboardComponent }  from './admin/dashboard/dashboard.component';
import { AdminUsersComponent }      from './admin/users/users.component';
import { CollectibleFormComponent } from './seller/collectible-form/collectible-form.component';

// Angular Material modules
import { MatToolbarModule }  from '@angular/material/toolbar';
import { MatButtonModule }   from '@angular/material/button';
import { MatIconModule }     from '@angular/material/icon';
import { MatCardModule }     from '@angular/material/card';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatFormFieldModule }from '@angular/material/form-field';
import { MatInputModule }    from '@angular/material/input';
import { MatChipsModule }    from '@angular/material/chips';

import { JwtInterceptor }    from './shared/interceptors/jwt.interceptor';
import { AppRoutingModule }  from './app-routing.module';

@NgModule({
  declarations: [
    AppComponent,
    NavbarComponent,
    LoginComponent,
    RegisterComponent,
    BuyerDashboardComponent,
    SellerDashboardComponent,
    AdminDashboardComponent,
    AdminUsersComponent,
    CollectibleFormComponent
  ],
  imports: [
    BrowserModule,
    BrowserAnimationsModule, // required by Material
    AppRoutingModule,
    HttpClientModule,
    FormsModule,
    ReactiveFormsModule,

    // --- Angular Material ---
    MatToolbarModule,
    MatButtonModule,
    MatIconModule,
    MatCardModule,
    MatCheckboxModule,
    MatFormFieldModule,
    MatInputModule,
    MatChipsModule
  ],
  providers: [
    { provide: HTTP_INTERCEPTORS, useClass: JwtInterceptor, multi: true }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
