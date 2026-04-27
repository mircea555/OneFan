import { Component, OnInit } from '@angular/core';
import { BuyerService, Collectible } from 'src/app/shared/services/buyer.service';

@Component({
  selector: 'app-buyer-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css']
})
export class BuyerDashboardComponent implements OnInit {
  allPublic: Collectible[] = [];
  filterPlayer = '';
  filterSport = '';
  filterAuthenticatedOnly = false;
  filteredList: Collectible[] = [];

  myCollection: Collectible[] = [];
  showOwnedTab = false;

  loadingAll = false;
  loadingOwned = false;
  errorAll: string | null = null;
  errorOwned: string | null = null;

  constructor(private buyerSvc: BuyerService) {}

  ngOnInit(): void {
    this.loadAllPublic();
  }

  loadAllPublic(): void {
    this.loadingAll = true;
    this.errorAll = null;
    this.buyerSvc.getAllCollectibles().subscribe({
      next: (data: Collectible[]) => {
        this.allPublic = data.map(c => ({ ...c, imageLoadError: false }));
        this.applyFilters();
        this.loadingAll = false;
      },
      error: () => {
        this.errorAll = 'Could not load collectibles.';
        this.loadingAll = false;
      }
    });
  }

  applyFilters(): void {
    this.filteredList = this.allPublic.filter(c => {
      if (this.filterPlayer && !c.player?.toLowerCase().includes(this.filterPlayer.toLowerCase())) {
        return false;
      }
      if (this.filterSport && !c.sport?.toLowerCase().includes(this.filterSport.toLowerCase())) {
        return false;
      }
      if (this.filterAuthenticatedOnly && !c.authenticated) {
        return false;
      }
      return true;
    });
  }

  resetFilters(): void {
    this.filterPlayer = '';
    this.filterSport = '';
    this.filterAuthenticatedOnly = false;
    this.applyFilters();
  }

  switchToBrowse(): void {
    this.showOwnedTab = false;
    this.applyFilters();
  }

  switchToOwned(): void {
    this.showOwnedTab = true;
    this.loadOwned();
  }

  loadOwned(): void {
    this.loadingOwned = true;
    this.errorOwned = null;
    this.buyerSvc.getOwnedCollectibles().subscribe({
      next: (data: Collectible[]) => {
        this.myCollection = data.map(c => ({ ...c, imageLoadError: false }));
        this.loadingOwned = false;
      },
      error: () => {
        this.errorOwned = 'Could not load your collectibles.';
        this.loadingOwned = false;
      }
    });
  }

  onVerify(c: Collectible): void {
    this.buyerSvc.verifyCollectible(c.id).subscribe({
      next: (updated: Collectible) => {
        c.authenticated = updated.authenticated;
        this.applyFilters();
      },
      error: () => {
        alert('Could not verify authenticity. Please try again.');
      }
    });
  }

  onBuy(c: Collectible): void {
    let msg = `Buy "${c.name}" for \$${c.price}?`;
    if (c.approved === null) {
      msg = `⚠️ This collectible is *pending approval* and may not be authentic.\n\n` + msg;
    }
    if (c.approved === false) {
      msg = `❌ This collectible was *rejected* by admin and may not be authentic!\n\n` + msg;
    }
    if (!confirm(msg)) {
      return;
    }
    this.buyerSvc.buyCollectible(c.id).subscribe({
      next: () => {
        // Remove from “Browse All”
        this.allPublic = this.allPublic.filter(x => x.id !== c.id);
        this.applyFilters();
        // Refresh “My Collection” if currently showing
        if (this.showOwnedTab) {
          this.loadOwned();
        }
      },
      error: () => {
        alert('Purchase failed. It may have been sold already.');
      }
    });
  }
}
