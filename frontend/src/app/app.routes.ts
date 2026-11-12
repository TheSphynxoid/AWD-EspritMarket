import { Routes } from '@angular/router';
import { adminDeliveryGuard, authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { ProfileComponent } from './features/profile/profile';
import { ServiceListComponent } from './features/services/service-list/service-list';
import { ServiceDetailComponent } from './features/services/service-detail/service-detail';
import { RoleSelectComponent } from './features/role-select/role-select';
import { EventListComponent } from './features/events/event-list/event-list';
import { EventDetailComponent } from './features/events/event-detail/event-detail';
import { EventFormComponent } from './features/events/event-form/event-form';
import { TicketsComponent } from './features/tickets/tickets';
import { CompanyListComponent } from './features/partnership/company-list/company-list';
import { JobOfferListComponent } from './features/partnership/job-offer-list/job-offer-list';
import { JobOfferDetailComponent } from './features/partnership/job-offer-detail/job-offer-detail';
import { ApplicationListComponent } from './features/partnership/application-list/application-list';
import { InterviewListComponent } from './features/partnership/interview-list/interview-list';
import { ShopListComponent } from './features/marketplace/shop-list/shop-list.component';
import { ShopDetailComponent } from './features/marketplace/shop-detail/shop-detail.component';
import { ShopCreateComponent } from './features/marketplace/shop-create/shop-create.component';
import { ProductListComponent } from './features/marketplace/product-list/product-list.component';
import { CartComponent } from './features/marketplace/cart/cart.component';
import { AdminDashboardComponent } from './features/marketplace/admin-dashboard/admin-dashboard.component';
import { DeliveryTestComponent } from './features/marketplace/delivery-test/delivery-test.component';

export const routes: Routes = [
  { path: '', redirectTo: 'services', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'role-select', component: RoleSelectComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'services', component: ServiceListComponent },
  { path: 'services/new', loadComponent: () => import('./features/services/service-new/service-new').then((m) => m.ServiceNewComponent), canActivate: [authGuard] },
  { path: 'services/mine', loadComponent: () => import('./features/services/my-services/my-services').then((m) => m.MyServicesComponent), canActivate: [authGuard] },
  { path: 'services/bookings', loadComponent: () => import('./features/services/my-bookings/my-bookings').then((m) => m.MyBookingsComponent), canActivate: [authGuard] },
  { path: 'services/received', loadComponent: () => import('./features/services/received-bookings/received-bookings').then((m) => m.ReceivedBookingsComponent), canActivate: [authGuard] },
  { path: 'services/:id/edit', loadComponent: () => import('./features/services/service-new/service-new').then((m) => m.ServiceNewComponent), canActivate: [authGuard] },
  { path: 'services/:id', component: ServiceDetailComponent },
  { path: 'events', component: EventListComponent, canActivate: [authGuard] },
  { path: 'events/new', component: EventFormComponent, canActivate: [authGuard] },
  { path: 'events/:id', component: EventDetailComponent, canActivate: [authGuard] },
  { path: 'events/:id/edit', component: EventFormComponent, canActivate: [authGuard] },
  { path: 'tickets', component: TicketsComponent, canActivate: [authGuard] },
  {
    path: 'delivery',
    canActivate: [authGuard],
    loadChildren: () => import('./features/delivery/delivery.routes').then((m) => m.DELIVERY_ROUTES),
  },
  {
    path: 'admin/delivery',
    canActivate: [adminDeliveryGuard],
    loadChildren: () =>
      import('./features/admin-delivery/admin-delivery.routes').then((m) => m.ADMIN_DELIVERY_ROUTES),
  },

  // ── Partnership routes (companies / job offers en lecture publique ; le reste exige un JWT) ──
  { path: 'partnership', redirectTo: 'partnership/companies', pathMatch: 'full' },
  { path: 'partnership/companies', component: CompanyListComponent },
  { path: 'partnership/job-offers', component: JobOfferListComponent },
  { path: 'partnership/job-offers/:id', component: JobOfferDetailComponent },
  { path: 'partnership/applications', component: ApplicationListComponent, canActivate: [authGuard] },
  { path: 'partnership/interviews', component: InterviewListComponent, canActivate: [authGuard] },

  // ── Marketplace (boutiques / produits en lecture publique ; le reste exige un JWT) ──
  { path: 'marketplace', component: ShopListComponent },
  { path: 'marketplace/products', component: ProductListComponent },
  { path: 'marketplace/shops/new', component: ShopCreateComponent, canActivate: [authGuard] },
  { path: 'marketplace/shops/:id', component: ShopDetailComponent },
  { path: 'marketplace/cart', component: CartComponent, canActivate: [authGuard] },
  { path: 'marketplace/admin', component: AdminDashboardComponent, canActivate: [authGuard] },
  { path: 'marketplace/delivery-test', component: DeliveryTestComponent, canActivate: [authGuard] },
];
