import { Routes } from '@angular/router';
import { AdminLayoutComponent } from './admin-layout/admin-layout';
import { AdminDeliveriesComponent } from './deliveries/admin-deliveries';
import { AdminDeliveryDashboardComponent } from './dashboard/dashboard';
import { DelivererListComponent } from './deliverer-list/deliverer-list';
import { VehicleListComponent } from './vehicle-list/vehicle-list';
import { AdminFeesComponent } from './fees/admin-fees';

export const ADMIN_DELIVERY_ROUTES: Routes = [
  {
    path: '',
    component: AdminLayoutComponent,
    children: [
      { path: '', redirectTo: 'deliveries', pathMatch: 'full' },
      { path: 'deliveries', component: AdminDeliveriesComponent },
      { path: 'stats', component: AdminDeliveryDashboardComponent },
      { path: 'deliverers', component: DelivererListComponent },
      { path: 'vehicles', component: VehicleListComponent },
      { path: 'fees', component: AdminFeesComponent },
    ],
  },
];
