import { Component, OnInit } from "@angular/core";
import { Router } from "@angular/router";
import { SellerService } from "src/app/shared/services/seller.service";

@Component({
  selector: "app-seller-dashboard",
  templateUrl: "./dashboard.component.html",
  styleUrls: ["./dashboard.component.css"],
})
export class SellerDashboardComponent implements OnInit {
  collectibles: any[] = [];
  loading = false;
  error: string | null = null;

  constructor(
    private sellerSvc: SellerService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCollectibles();
  }

  loadCollectibles(): void {
    this.loading = true;
    this.error = null;
    this.sellerSvc.getAllCollectibles().subscribe({
      next: (data: any[]) => {
        this.collectibles = data;
        this.loading = false;
      },
      error: (err: any) => {
        this.error = "Could not load collectibles.";
        this.loading = false;
      },
    });
  }

  onAddNew(): void {
    this.router.navigate(["/seller/collectibles/new"]);
  }

  onEdit(id: number): void {
    // ⬇️ Make sure the array segments are in the same order your routes expect:
    this.router.navigate(['/seller/collectibles', id, 'edit']);
  }

  onDelete(id: number): void {
    if (!confirm("Are you sure you want to delete this collectible?")) {
      return;
    }
    this.loading = true;
    this.sellerSvc.deleteCollectible(id).subscribe({
      next: () => {
        this.loadCollectibles();
      },
      error: (err: any) => {
        this.error = "Delete operation failed.";
        this.loading = false;
      },
    });
  }
}
