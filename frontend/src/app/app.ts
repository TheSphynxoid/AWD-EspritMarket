import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { NavbarComponent } from './shared/components/navbar/navbar';

@Component({
  selector: 'app-root',
  imports: [RouterOutlet, NavbarComponent],
  template: `
    <app-navbar />
    <main class="min-h-screen bg-slate-50">
      <router-outlet />
    </main>
  `,
})
export class App {}
