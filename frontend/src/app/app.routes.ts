import { Routes } from '@angular/router';
import { RegisterComponent } from './pages/register/register';
import { LoginComponent } from './pages/login/login';
import { IndexComponent } from './pages/index/index';
import { LandingComponent } from './pages/landing/landing';
import { AuthGuard } from './guards/auth-guard';
import { NoAuthGuard } from './guards/no-auth-guard';
import { SolicitarComponent } from './pages/solicitude/solicitude';
import { ChatComponent } from './pages/chat/chat';
import { MisSolicitudesComponent } from './pages/myorders/myorders';
import { SoporteComponent } from './pages/helper/helper';
import { RoleGuard } from './guards/role-guard';
import { ChatAtencionComponent } from './administracion/chat/chat';
import { DashboardComponent } from './administracion/dashboard/dashboard';
import { StaffComponent } from './administracion/staff/staff';
import { PanelComponent } from './administracion/panel/panel';
import { PaymentsComponent } from './administracion/payments/payments';
import { InventoryComponent } from './administracion/inventory/inventory';

export const routes: Routes = [
  //autoredireccionamiento
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  
  //todos pero sin sesion iniciada obligatio
  { path: 'login', component: LoginComponent, canActivate: [NoAuthGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [NoAuthGuard] },

  //solo para clientela
  { path: 'index', component: IndexComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'solicitar', component: SolicitarComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'chat', component: ChatComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'mis-solicitudes', component: MisSolicitudesComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'soporte', component: SoporteComponent, canActivate: [AuthGuard, RoleGuard] },

  //todos
  { path: 'landing', component: LandingComponent },

  //solo para empleados
  { path: 'panel', component: PanelComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'empleados', component: StaffComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'chat-atencion', component: ChatAtencionComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'pagos', component: PaymentsComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'inventario', component: InventoryComponent, canActivate: [AuthGuard, RoleGuard] },
  { path: 'dashboard', component: DashboardComponent, canActivate: [AuthGuard, RoleGuard] },

];


/* para cuando se implementen mas de dos roles 
estado: beta

export const routes: Routes = [
  { path: '', redirectTo: '/login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent, canActivate: [NoAuthGuard] },
  { path: 'register', component: RegisterComponent, canActivate: [NoAuthGuard]},
  { path: 'index', component: IndexComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['cliente'] } },
  { path: 'solicitar', component: SolicitarComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['cliente'] } },
  { path: 'chat', component: ChatComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['cliente'] } },
  { path: 'mis-solicitudes', component: MisSolicitudesComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['cliente'] } },
  { path: 'soporte', component: SoporteComponent, canActivate: [AuthGuard, RoleGuard], data: { roles: ['cliente'] } },
  { path: 'landing', component: LandingComponent },
];

*/