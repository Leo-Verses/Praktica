import { Injectable } from '@angular/core';
import {
  HttpInterceptor,
  HttpRequest,
  HttpHandler,
  HttpEvent
} from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable()
export class TokenInterceptor implements HttpInterceptor {

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const usuario = localStorage.getItem('usuario');
    let token: string | null = null;

    if (usuario) {
      try {
        const userData = JSON.parse(usuario);
        token = userData?.token;
      } catch (e) {
        console.error('#3 Error parseando usuario del localStorage', e);
      }
    }

    if (!token) {
      return next.handle(req);
    }

    const cloned = req.clone({
      setHeaders: {
        Authorization: `Bearer ${token}`
      }
    });
    return next.handle(cloned);
  }

}
