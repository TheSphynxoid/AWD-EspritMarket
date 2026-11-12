import { Routes } from '@angular/router';
import { authGuard } from './core/guards/auth.guard';
import { LoginComponent } from './features/auth/login/login';
import { RegisterComponent } from './features/auth/register/register';
import { ProfileComponent } from './features/profile/profile';
import { ServiceListComponent } from './features/services/service-list/service-list';
import { ServiceDetailComponent } from './features/services/service-detail/service-detail';
import { ServiceNewComponent } from './features/services/service-new/service-new';
import { RoleSelectComponent } from './features/role-select/role-select';
import { EventListComponent } from './features/events/event-list/event-list';
import { EventDetailComponent } from './features/events/event-detail/event-detail';
import { EventFormComponent } from './features/events/event-form/event-form';
import { TicketsComponent } from './features/tickets/tickets';
import { ShopListComponent } from './features/marketplace/shop-list/shop-list.component';
import { ShopDetailComponent } from './features/marketplace/shop-detail/shop-detail.component';
import { CartComponent } from './features/marketplace/cart/cart.component';
import { AdminDashboardComponent } from './features/marketplace/admin-dashboard/admin-dashboard.component';
import { ShopCreateComponent } from './features/marketplace/shop-create/shop-create.component';
import { ProductListComponent } from './features/marketplace/product-list/product-list.component';
import { DeliveryTestComponent } from './features/marketplace/delivery-test/delivery-test.component';
export const routes: Routes = [
  { path: '', redirectTo: 'services', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'role-select', component: RoleSelectComponent, canActivate: [authGuard] },
  { path: 'profile', component: ProfileComponent, canActivate: [authGuard] },
  { path: 'services', component: ServiceListComponent },
  { path: 'services/new', component: ServiceNewComponent, canActivate: [authGuard] },
  { path: 'services/:id', component: ServiceDetailComponent },
  { path: 'events', component: EventListComponent, canActivate: [authGuard] },
  { path: 'events/new', component: EventFormComponent, canActivate: [authGuard] },
  { path: 'events/:id', component: EventDetailComponent, canActivate: [authGuard] },
  { path: 'events/:id/edit', component: EventFormComponent, canActivate: [authGuard] },
  { path: 'tickets', component: TicketsComponent, canActivate: [authGuard] },
  { path: 'marketplace', component: ShopListComponent },
  { path: 'marketplace/products', component: ProductListComponent },
  { path: 'marketplace/shops/new', component: ShopCreateComponent },
  { path: 'marketplace/shops/:id', component: ShopDetailComponent },
  { path: 'marketplace/cart', component: CartComponent },
  { path: 'marketplace/admin', component: AdminDashboardComponent, canActivate: [authGuard] },
  { path: 'marketplace/delivery-test', component: DeliveryTestComponent },
];
