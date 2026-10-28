import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class RoleService {
  private readonly key = 'selected_role';
  readonly role = signal<string | null>(
    typeof localStorage !== 'undefined' ? localStorage.getItem(this.key) : null
  );

  setRole(role: string): void {
    if (typeof localStorage !== 'undefined') localStorage.setItem(this.key, role);
    this.role.set(role);
  }

  clear(): void {
    if (typeof localStorage !== 'undefined') localStorage.removeItem(this.key);
    this.role.set(null);
  }

  isAdmin(): boolean {
    return this.role() === 'admin';
  }
}