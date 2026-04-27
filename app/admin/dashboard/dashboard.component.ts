// src/app/admin/dashboard/dashboard.component.ts

import { Component, OnInit } from '@angular/core';
import { AdminService } from 'src/app/shared/services/admin.service';

@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.css'],
})
export class AdminDashboardComponent implements OnInit {
  // Arrays to hold data
  users: any[] = [];
  collectibles: any[] = [];
  blockchainLog: string[] = [];

  // Loading & error states
  loadingUsers = true;
  loadingCollectibles = true;
  loadingLog = false;

  error: string | null = null;
  errorCollectibles: string | null = null;
  errorLog: string | null = null;

  // Toggle flags for showing/hiding sections
  showUsers = true;
  showCollectibles = true;
  showLog = true;

  constructor(private adminService: AdminService) {}

  ngOnInit(): void {
    this.loadUsers();
    this.loadCollectibles();
    this.loadBlockchainLog();
  }

  // ------------ Load All Users ------------
  loadUsers() {
    this.loadingUsers = true;
    this.error = null;

    this.adminService.getAllUsers().subscribe({
      next: (users) => {
        this.users = users;
        this.loadingUsers = false;
      },
      error: () => {
        this.error = 'Could not load users.';
        this.loadingUsers = false;
      },
    });
  }

  // ------------ Load All Collectibles ------------
  loadCollectibles() {
    this.loadingCollectibles = true;
    this.errorCollectibles = null;

    this.adminService.getAllCollectibles().subscribe({
      next: (collectibles) => {
        this.collectibles = collectibles;
        this.loadingCollectibles = false;
      },
      error: () => {
        this.errorCollectibles = 'Could not load collectibles.';
        this.loadingCollectibles = false;
      },
    });
  }

  // ------------ Approve a Collectible ------------
  approve(id: number) {
    this.adminService.approveCollectible(id).subscribe({
      next: () => {
        // Refresh the collectibles table and the blockchain log
        this.loadCollectibles();
        this.loadBlockchainLog();
      },
      error: (err) => {
        alert('Approve failed: ' + (err.error?.message || err.message));
      },
    });
  }

  // ------------ Reject a Collectible ------------
  reject(id: number) {
    this.adminService.rejectCollectible(id).subscribe({
      next: () => {
        // Refresh only the collectibles table (no need to refresh log on rejection)
        this.loadCollectibles();
      },
      error: (err) => {
        alert('Reject failed: ' + (err.error?.message || err.message));
      },
    });
  }

  // ------------ Load Blockchain Log ------------
  loadBlockchainLog() {
    this.loadingLog = true;
    this.errorLog = null;

    this.adminService.getBlockchainLog().subscribe({
      next: (log) => {
        this.blockchainLog = log;
        this.loadingLog = false;
      },
      error: () => {
        this.errorLog = 'Could not load blockchain log.';
        this.loadingLog = false;
      },
    });
  }
}
