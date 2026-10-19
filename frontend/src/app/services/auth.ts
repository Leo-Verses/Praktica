import { Injectable } from '@angular/core';
import { Router } from '@angular/router';
import { AuthGoogleService } from './auth-google';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  constructor(
    private router: Router,
    private authGoogle: AuthGoogleService
  ) {}

  isLoggedIn(): boolean {
    return typeof window !== 'undefined' && !!localStorage.getItem('usuario');
  }

  getUsuario(): any {
    if (typeof window === 'undefined') return null;
    const user = localStorage.getItem('usuario');
    return user ? JSON.parse(user) : null;
  }

  async logout(): Promise<void> {
    if (typeof window === 'undefined') return;

    localStorage.removeItem('usuario');

    try {
      await this.authGoogle.logout();
    } catch (e) {
      console.warn('Error al cerrar sesión de Google o no estaba activa:', e);
    }

    this.router.navigate(['/login']).then(() => {
    });
  }
}
