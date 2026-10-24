import { Routes } from '@angular/router';
import { adminDeliveryGuard, authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { ProfileComponent } from './features/profile/profile';
import { ServiceListComponent } from './features/services/service-list/service-list';
import { ServiceDetailComponent } from './features/services/service-detail/service-detail';
import { ServiceNewComponent } from './features/services/service-new/service-new';

export const routes: Routes = [
  { path: '', redirectTo: 'services', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'services', component: ServiceListComponent },
  { path: 'services/new', component: ServiceNewComponent, canActivate: [authGuard] },
  { path: 'services/:id', component: ServiceDetailComponent },
  {
    path: 'delivery',
    loadChildren: () => import('./features/delivery/delivery.routes').then((m) => m.DELIVERY_ROUTES),
  },
  {
    path: 'admin/delivery',
    canActivate: [adminDeliveryGuard],
    loadChildren: () =>
      import('./features/admin-delivery/admin-delivery.routes').then((m) => m.ADMIN_DELIVERY_ROUTES),
  },
];
