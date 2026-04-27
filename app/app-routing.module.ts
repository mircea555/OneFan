import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './auth/login/login.component';
import { RegisterComponent } from './auth/register/register.component';
import { BuyerDashboardComponent } from './buyer/dashboard/dashboard.component';
import { SellerDashboardComponent } from './seller/dashboard/dashboard.component';
import { AdminDashboardComponent } from './admin/dashboard/dashboard.component';
import { CollectibleFormComponent } from './seller/collectible-form/collectible-form.component';
import { RoleGuard } from './shared/guards/role.guard';

const routes: Routes = [
  { path: '', redirectTo: '/auth/login', pathMatch: 'full' },
  { path: 'auth/login', component: LoginComponent },
  { path: 'auth/register', component: RegisterComponent },

  {
    path: 'buyer/dashboard',
    component: BuyerDashboardComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ROLE_BUYER'] }
  },
  {
    path: 'seller/dashboard',
    component: SellerDashboardComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ROLE_SELLER'] }
  },
  {
    path: 'seller/collectibles/new',
    component: CollectibleFormComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ROLE_SELLER'] }
  },
  {
    path: 'seller/collectibles/:id/edit',
    component: CollectibleFormComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ROLE_SELLER'] }
  },
  {
    path: 'admin/dashboard',
    component: AdminDashboardComponent,
    canActivate: [RoleGuard],
    data: { roles: ['ROLE_ADMIN'] }
  },

  // Always last: wildcard route
  { path: '**', redirectTo: '/auth/login' }
];


@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
