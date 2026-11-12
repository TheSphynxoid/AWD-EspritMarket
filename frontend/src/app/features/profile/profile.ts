import { Component, OnInit, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { JsonPipe } from '@angular/common';
import { AuthService } from '../../core/services/auth.service';

@Component({
  selector: 'app-profile',
  imports: [JsonPipe],
  template: `
    <div class="max-w-2xl mx-auto p-6">
      <h1 class="text-2xl font-bold mb-4">Profile</h1>
      <pre class="bg-white rounded-xl shadow p-4 overflow-auto text-sm">{{ data | json }}</pre>
    </div>
  `,
})
export class ProfileComponent implements OnInit {
  private http = inject(HttpClient);
  private auth = inject(AuthService);
  data: any = null;

  ngOnInit(): void {
    this.http.get('http://localhost:8088/user/me').subscribe({ next: (d) => (this.data = d) });
  }
}
