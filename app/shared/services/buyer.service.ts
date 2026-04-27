import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Collectible {
  id: number;
  name: string;
  description: string;
  price: number;
  sport: string;
  player: string;
  category: string;
  imageUrl: string;
  certificateUrl: string;
  approved: boolean | null;
  authenticated: boolean;
  sold: boolean;
  sellerId: number;
  ownerId: number;
  imageLoadError?: boolean;
}

@Injectable({ providedIn: 'root' })
export class BuyerService {
  private readonly BASE = 'http://localhost:8080/api';

  constructor(private http: HttpClient) {}

  /** List all collectibles (approved, rejected, pending) for buyer browse */
  getAllCollectibles(): Observable<Collectible[]> {
    return this.http.get<Collectible[]>(`${this.BASE}/collectibles/all`);
  }

  /** Re-fetch a single collectible to “Verify” authenticity */
  verifyCollectible(id: number): Observable<Collectible> {
    return this.http.get<Collectible>(`${this.BASE}/collectibles/public/${id}`);
  }

  /** Buy a collectible */
  buyCollectible(id: number): Observable<any> {
    return this.http.post(`${this.BASE}/buyer/collectibles/${id}/buy`, {});
  }

  /** List everything this buyer owns */
  getOwnedCollectibles(): Observable<Collectible[]> {
    return this.http.get<Collectible[]>(`${this.BASE}/buyer/collectibles`);
  }
}
