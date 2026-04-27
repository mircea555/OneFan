import { Component } from '@angular/core';
import { AuthService } from '../../shared/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent {
  form = {
    username: '',
    password: ''
  };
  error: string | null = null;
  message: string | null = null;
  loading = false;

  constructor(private auth: AuthService, private router: Router) {}

  onSubmit() {
    this.error = null;
    this.loading = true;
    this.auth.login(this.form).subscribe({
      next: (res: any) => {
        this.loading = false;
        this.message = 'Login successful!';
        console.log('LOGIN RESPONSE:', res);
        const roles = res.roles || this.auth.getRoles();
        console.log('ROLES:', roles);
        if (roles && roles.includes('ROLE_ADMIN')) {
          console.log('Navigating to admin dashboard');
          this.router.navigate(['/admin/dashboard']);
        } else if (roles && roles.includes('ROLE_SELLER')) {
          console.log('Navigating to seller dashboard');
          this.router.navigate(['/seller/dashboard']);
        } else if (roles && roles.includes('ROLE_BUYER')) {
          console.log('Navigating to buyer dashboard');
          this.router.navigate(['/buyer/dashboard']);
        } else {
          console.log('Navigating to home');
          this.router.navigate(['/']);
        }
      },
      error: err => {
        this.loading = false;
        this.error = err.error?.message || 'Login failed!';
      }
    });
  }


}
