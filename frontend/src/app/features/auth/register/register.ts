import { Component, inject, signal } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-register',
  imports: [FormsModule, RouterLink],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-slate-100">
      <form (ngSubmit)="submit()" class="bg-white p-8 rounded-xl shadow-md w-full max-w-sm space-y-4">
        <h1 class="text-2xl font-bold text-center">Register</h1>
        <input [(ngModel)]="username" name="username" placeholder="Username" required class="w-full border rounded px-3 py-2" />
        <input [(ngModel)]="email" name="email" type="email" placeholder="Email" required class="w-full border rounded px-3 py-2" />
        <input [(ngModel)]="password" name="password" type="password" placeholder="Password" required class="w-full border rounded px-3 py-2" />
        @if (error()) { <p class="text-red-600 text-sm">{{ error() }}</p> }
        @if (success()) { <p class="text-green-600 text-sm">{{ success() }}</p> }
        <button type="submit" class="w-full bg-blue-600 text-white py-2 rounded hover:bg-blue-500">Create account</button>
        <p class="text-center text-sm"><a routerLink="/login" class="text-blue-600">Already have an account?</a></p>
      </form>
    </div>
  `,
})
export class RegisterComponent {
  private auth = inject(AuthService);
  private router = inject(Router);

  username = '';
  email = '';
  password = '';
  error = signal<string | null>(null);
  success = signal<string | null>(null);

  submit(): void {
    this.error.set(null);
    this.success.set(null);
    this.auth.register(this.username, this.email, this.password).subscribe({
      next: () => {
        this.success.set('Account created. You can log in.');
        setTimeout(() => this.router.navigate(['/login']), 1000);
      },
      error: () => this.error.set('Registration failed'),
    });
  }
}
