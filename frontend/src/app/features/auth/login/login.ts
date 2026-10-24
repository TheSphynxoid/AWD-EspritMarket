import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-login',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-slate-100">
      <form (ngSubmit)="submit()" class="bg-white p-8 rounded-xl shadow-md w-full max-w-sm space-y-4">
        <h1 class="text-2xl font-bold text-center">Login</h1>
        <input [(ngModel)]="username" name="username" placeholder="Username" required
               class="w-full border rounded px-3 py-2" />
        <input [(ngModel)]="password" name="password" type="password" placeholder="Password" required
               class="w-full border rounded px-3 py-2" />
        @if (error()) {
          <p class="text-red-600 text-sm">{{ error() }}</p>
        }
        <button type="submit" class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-500">Sign in</button>
        <p class="text-center text-sm">
          <a routerLink="/" class="text-blue-600">Back home</a>
        </p>
      </form>
    </div>
  `,
})
export class LoginComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  username = '';
  password = '';
  error = signal<string | null>(null);

  submit(): void {
    this.error.set(null);
    this.auth.login(this.username, this.password).subscribe({
      next: () => this.router.navigate(['/services']),
      error: () => this.error.set('Login failed'),
    });
  }
}
