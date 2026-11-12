import { Component, inject } from '@angular/core';
import { Router, RouterLink } from '@angular/router';
import { RoleService } from '../../core/services/role.service';

@Component({
  selector: 'app-role-select',
  imports: [RouterLink],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-slate-100">
      <div class="bg-white p-8 rounded-xl shadow-md w-full max-w-md space-y-4 text-center">
        <h1 class="text-2xl font-bold">Choisir un rôle</h1>
        <p class="text-slate-600">Sélectionnez votre rôle pour accéder à l'interface Event.</p>
        <div class="flex gap-4 justify-center">
          <button (click)="choose('admin')" class="bg-blue-600 text-white px-6 py-2 rounded hover:bg-blue-500">Admin</button>
          <button (click)="choose('user')" class="bg-slate-600 text-white px-6 py-2 rounded hover:bg-slate-500">Utilisateur</button>
        </div>
        <a routerLink="/services" class="text-blue-600 text-sm">Aller aux services</a>
      </div>
    </div>
  `,
})
export class RoleSelectComponent {
  private roleService = inject(RoleService);
  private router = inject(Router);

  choose(role: string): void {
    this.roleService.setRole(role);
    this.router.navigate(['/events']);
  }
}