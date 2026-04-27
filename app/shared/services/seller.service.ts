import { Injectable } from "@angular/core";
import { HttpClient } from "@angular/common/http";
import { Observable } from "rxjs";

@Injectable({ providedIn: "root" })
export class SellerService {
  private base = "http://localhost:8080/api/seller";

  constructor(private http: HttpClient) {}

  uploadImage(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(
      'http://localhost:8080/api/seller/images',
      formData,
      { responseType: 'text' }
    );
  }

  uploadCertificate(file: File): Observable<string> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post(
      'http://localhost:8080/api/seller/certificates',
      formData,
      { responseType: 'text' }
    );
  }


  getAllCollectibles(): Observable<any[]> {
    return this.http.get<any[]>(`${this.base}/collectibles`);
  }
  getCollectibleById(id: number): Observable<any> {
    return this.http.get<any>(`${this.base}/collectibles/${id}`);
  }
  createCollectible(payload: any): Observable<any> {
    return this.http.post<any>(`${this.base}/collectibles`, payload);
  }
  updateCollectible(id: number, payload: any): Observable<any> {
    return this.http.put<any>(`${this.base}/collectibles/${id}`, payload);
  }
  deleteCollectible(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/collectibles/${id}`);
  }
}
