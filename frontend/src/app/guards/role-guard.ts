import { Injectable } from '@angular/core';
import { CanActivate, ActivatedRouteSnapshot, RouterStateSnapshot, Router } from '@angular/router';
import { AuthService } from '../services/auth';

@Injectable({
  providedIn: 'root'
})
export class RoleGuard implements CanActivate {
  constructor(private authService: AuthService, private router: Router) {}
  canActivate(next: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean {
    const usuario = JSON.parse(localStorage.getItem('usuario') || '{}');
    const route = state.url;
    if (usuario && usuario.rol === 'cliente') {
      if (route === '/index' || route === '/solicitar' || route === '/chat' || route === '/mis-solicitudes' || route === '/soporte') {
        return true;
      } else {
        this.router.navigate(['/index']);
        return false;
      }
    }
    else if (usuario && usuario.rol === 'administrador') {
      const rutasAdministrador = [
        '/panel',
        '/empleados',
        '/chat-atencion',
        '/pagos',
        '/inventario',
        '/dashboard'
      ];
      if (rutasAdministrador.includes(route)) {
        return true; 
      } else {
        this.router.navigate(['/panel']);
        return false;
      }
    } else {
      this.router.navigate(['/login'], { queryParams: { error: 'no-auth' } });
      alert('No tienes permisos para acceder a esta área.');
      return false;
    }
  }
}
