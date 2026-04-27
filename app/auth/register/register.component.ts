import { Component } from '@angular/core';
import { AuthService } from '../../shared/services/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent {
  form = {
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
    role: ''      // start empty so user must pick
  };
  error: string | null = null;
  message: string | null = null;
  loading = false;

  private emailDomainPattern = /^[a-zA-Z0-9._%+-]+@(gmail\.com|yahoo\.com|hotmail\.com|outlook\.com|live\.com)$/;

  constructor(
    private auth: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    // extra guard, shouldn't fire if form invalid
    if (
      !this.form.username ||
      !this.form.email ||
      !this.form.password ||
      this.form.password !== this.form.confirmPassword ||
      !this.form.role
    ) {
      this.error = 'Please fix the errors above before submitting.';
      return;
    }

    if (!this.emailDomainPattern.test(this.form.email)) {
      this.error = 'Email must end with @gmail.com, @yahoo.com, @hotmail.com, @outlook.com or @live.com.';
      return;
    }

    this.error = null;
    this.loading = true;

    const payload = {
      username: this.form.username,
      email: this.form.email,
      password: this.form.password,
      role: [ this.form.role ]  // wrap as array for backend
    };

    this.auth.register(payload).subscribe({
      next: () => {
        this.loading = false;
        this.message = 'Registration successful! Redirecting to login…';
        setTimeout(() => this.router.navigate(['/auth/login']), 1500);
      },
      error: err => {
        this.loading = false;
        this.error = err.error?.message || 'Registration failed!';
      }
    });
  }
}
