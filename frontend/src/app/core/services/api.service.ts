import { Injectable } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class ApiService {
  /** Gateway via le même origin : proxy ng serve (proxy.conf.json) en dev, nginx en Docker. */
  readonly baseUrl = '/gw';

  url(path: string): string {
    return `${this.baseUrl}${path.startsWith('/') ? path : '/' + path}`;
  }
}
