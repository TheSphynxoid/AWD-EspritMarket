import { Routes } from '@angular/router';
import { DeliveryListComponent } from './delivery-list/delivery-list';
import { DeliveryFormComponent } from './delivery-form/delivery-form';
import { DeliveryDetailComponent } from './delivery-detail/delivery-detail';

export const DELIVERY_ROUTES: Routes = [
  { path: '', component: DeliveryListComponent },
  { path: 'new', component: DeliveryFormComponent },
  { path: ':id', component: DeliveryDetailComponent },
  { path: ':id/edit', component: DeliveryFormComponent },
];
