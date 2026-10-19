import { Injectable, inject, signal } from '@angular/core';
import { Router } from '@angular/router';
import { OAuthService } from 'angular-oauth2-oidc';
import { authConfig } from '../auth-config';

@Injectable({
  providedIn: 'root',
})
export class AuthGoogleService {
  private oAuthService = inject(OAuthService);
  private router = inject(Router);
  profile = signal<any>(null);

  constructor() {
    this.initConfiguration();
  }

  initConfiguration() {
    if (typeof window === 'undefined') return;

    const configWithRedirect = {
      ...authConfig,
      redirectUri: window.location.origin + '/login',
      prompt: 'select_account',
    };

    this.oAuthService.configure(configWithRedirect);
    this.oAuthService.setupAutomaticSilentRefresh();
    this.oAuthService.loadDiscoveryDocumentAndTryLogin().then(() => {
      if (this.oAuthService.hasValidIdToken()) {
        this.profile.set(this.oAuthService.getIdentityClaims());
      }
    });
  }

  login() {
    this.oAuthService.initImplicitFlow();
  }

  logout(): Promise<void> {
    return new Promise((resolve, reject) => {
      try {
        this.oAuthService.revokeTokenAndLogout();
        this.oAuthService.logOut();
        this.profile.set(null);
        resolve();
      } catch (error) {
        reject(error);
      }
    });
  }

  getProfile() {
    return this.profile();
  }

  getIdToken(): string | null {
    if (this.oAuthService.hasValidIdToken()) {
      return this.oAuthService.getIdToken();
    }
    return null;
  }
}
