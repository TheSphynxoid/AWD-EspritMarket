import { CanActivateFn, Router } from '@angular/router';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';

export const authGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isAuthenticated()) return true;
  router.navigate(['/login']);
  return false;
};

export const ADMIN_DELIVERY_ROLE = 'ADMIN_DELIVERY';

export const adminDeliveryGuard: CanActivateFn = () => {
  const auth = inject(AuthService);
  const router = inject(Router);
  if (auth.isAuthenticated() && auth.hasRole(ADMIN_DELIVERY_ROLE)) return true;
  router.navigate([auth.isAuthenticated() ? '/delivery' : '/login']);
  return false;
};
