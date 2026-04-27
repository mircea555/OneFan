import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({ providedIn: 'root' })
export class AdminService {
  private api = 'http://localhost:8080/api/admin';

  constructor(private http: HttpClient) {}

  // 1. View all users
  getAllUsers(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/users`);
  }

  // 2. View all collectibles/listings
  getAllCollectibles(): Observable<any[]> {
    return this.http.get<any[]>(`${this.api}/collectibles`);
  }

  // 3. Approve collectible
  approveCollectible(id: number): Observable<any> {
    return this.http.put(`${this.api}/collectibles/${id}/approve`, {});
  }

  // 4. Reject collectible
  rejectCollectible(id: number): Observable<any> {
    return this.http.put(`${this.api}/collectibles/${id}/reject`, {});
  }

  // 5. Get blockchain log
  getBlockchainLog(): Observable<string[]> {
    return this.http.get<string[]>(`${this.api}/blockchain/log`);
  }
}
