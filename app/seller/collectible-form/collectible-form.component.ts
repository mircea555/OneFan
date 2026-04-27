import { Component, OnInit } from "@angular/core";
import { FormBuilder, FormGroup, Validators } from "@angular/forms";
import { SellerService } from "src/app/shared/services/seller.service";
import { ActivatedRoute, Router } from "@angular/router";
import { firstValueFrom } from 'rxjs';

@Component({
  selector: "app-collectible-form",
  templateUrl: "./collectible-form.component.html",
  styleUrls: ["./collectible-form.component.css"],
})
export class CollectibleFormComponent implements OnInit {
  form: FormGroup;
  imageFile: File | null = null;
  certFile: File | null = null;

  isEdit = false;
  collectibleId!: number;

  loading = false;
  error: string | null = null;

  constructor(
    private fb: FormBuilder,
    private sellerSvc: SellerService,
    private route: ActivatedRoute,
    private router: Router
  ) {
    this.form = this.fb.group({
      name: ["", Validators.required],
      description: [""],
      price: [0, [Validators.required, Validators.min(0.01)]],
      sport: ["", Validators.required],
      player: ["", Validators.required],
      category: ["", Validators.required],
      imageUrl: [""],
      certificateUrl: [""],
    });
  }

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      if (params["id"]) {
        this.isEdit = true;
        this.collectibleId = +params["id"];
        this.loadExistingCollectible(this.collectibleId);
      }
    });
  }

  private loadExistingCollectible(id: number): void {
    this.sellerSvc.getCollectibleById(id).subscribe({
      next: (col: any) => {
        this.form.patchValue({
          name: col.name,
          description: col.description,
          price: col.price,
          sport: col.sport,
          player: col.player,
          category: col.category,
          imageUrl: col.imageUrl,
          certificateUrl: col.certificateUrl,
        });
        this.form.get("name")?.disable();
        this.form.get("description")?.disable();
        this.form.get("sport")?.disable();
        this.form.get("player")?.disable();
        this.form.get("category")?.disable();
        this.form.get("imageUrl")?.disable();
        this.form.get("certificateUrl")?.disable();
      },
      error: () => {
        this.error = "Could not load collectible for editing.";
      },
    });
  }

  onImageSelected(event: any): void {
    this.imageFile = event.target.files[0] || null;
  }
  onCertSelected(event: any): void {
    this.certFile = event.target.files[0] || null;
  }

  private async uploadAndGetUrl(file: File, isCert: boolean): Promise<string> {
    if (isCert) {
      // uploadCertificate(...) returns an Observable<string>
      // firstValueFrom(…) turns it into a Promise<string>
      return await firstValueFrom(this.sellerSvc.uploadCertificate(file));
    } else {
      return await firstValueFrom(this.sellerSvc.uploadImage(file));
    }
  }

  async onSubmit(): Promise<void> {
    this.error = null;
    if (this.form.invalid) {
      this.error = "Please fill in all required fields.";
      return;
    }
    this.loading = true;

    try {
      let payload: any;
      if (this.isEdit) {
        payload = this.form.getRawValue();
      } else {
        payload = this.form.value;
      }

      if (!this.isEdit && this.imageFile) {
        const imageUrl = await this.uploadAndGetUrl(this.imageFile, false);
        payload.imageUrl = imageUrl;
      }
      if (!this.isEdit && this.certFile) {
        const certUrl = await this.uploadAndGetUrl(this.certFile, true);
        payload.certificateUrl = certUrl;
      }

      if (this.isEdit) {
        await this.sellerSvc
          .updateCollectible(this.collectibleId, payload)
          .toPromise();
      } else {
        await this.sellerSvc.createCollectible(payload).toPromise();
      }

      this.router.navigate(["/seller/dashboard"]);
    } catch (e: any) {
      this.error = "Operation failed: " + (e.error?.message || e.message || "");
    } finally {
      this.loading = false;
    }
  }
}
