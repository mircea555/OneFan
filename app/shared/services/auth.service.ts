import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';

const TOKEN_KEY = 'jwt_token';
const USER_KEY = 'user_data';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = 'http://localhost:8080/api/auth';
  private loggedIn$ = new BehaviorSubject<boolean>(this.hasToken());

  constructor(private http: HttpClient, private router: Router) {}

  // REGISTER
  register(form: { username: string, email: string, password: string, role: string[] }) {
    return this.http.post(`${this.apiUrl}/signup`, form);
  }

  // LOGIN
  login(credentials: { username: string, password: string }): Observable<any> {
    return this.http.post(`${this.apiUrl}/signin`, credentials).pipe(
      tap((res: any) => {
        if (res.token || res.accessToken) {
          // Store token and user info
          localStorage.setItem(TOKEN_KEY, res.token || res.accessToken);
          localStorage.setItem(USER_KEY, JSON.stringify(res));
          this.loggedIn$.next(true);
        }
      })
    );
  }

  // LOGOUT
  logout(): void {
    localStorage.removeItem(TOKEN_KEY);
    localStorage.removeItem(USER_KEY);
    this.loggedIn$.next(false);
    this.router.navigate(['/auth/login']);
  }

  // LOGIN STATE
  isLoggedIn(): boolean {
    return !!localStorage.getItem(TOKEN_KEY);
  }

  // Observable login state for components to subscribe to
  loginState(): Observable<boolean> {
    return this.loggedIn$.asObservable();
  }

  // GET JWT
  getToken(): string | null {
    return localStorage.getItem(TOKEN_KEY);
  }

  // USER ROLES (array of strings: ['ROLE_SELLER'], etc)
  getRoles(): string[] {
    const user = this.getUser();
    return user?.roles ?? [];
  }

  hasRole(role: string): boolean {
    return this.getRoles().includes(role);
  }

  // CURRENT USER DATA
  getUser(): any {
    const raw = localStorage.getItem(USER_KEY);
    if (raw) {
      try {
        return JSON.parse(raw);
      } catch {
        return null;
      }
    }
    return null;
  }

  // Just for completeness: remove all (useful for admin reset etc)
  clear(): void {
    localStorage.clear();
    this.loggedIn$.next(false);
  }

  // Utility
  private hasToken(): boolean {
    return !!localStorage.getItem(TOKEN_KEY);
  }
}
