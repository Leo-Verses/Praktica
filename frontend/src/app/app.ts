import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { SocialLoginModule, SocialAuthServiceConfig, GoogleLoginProvider } from '@abacritt/angularx-social-login';
import { OAuthService } from 'angular-oauth2-oidc';
import { AuthGoogleService } from './services/auth-google';
import { NgChartsModule } from 'ng2-charts';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, FormsModule, SocialLoginModule, NgChartsModule, ],
  providers: [
    {
      provide: 'SocialAuthServiceConfig',
      useValue: {
        autoLogin: false,
        providers: [
          {
            id: GoogleLoginProvider.PROVIDER_ID,
            provider: new GoogleLoginProvider('38423991825-burmdic6706js9cmarbugkj6dr12pcjk.apps.googleusercontent.com')
          }
        ],
        onError: (err) => console.error('Error con la autenticación social:', err)
      } as SocialAuthServiceConfig
    }
  ],
  templateUrl: './app.html'
})
export class App {
  constructor(
    private authGoogleService: AuthGoogleService,
    private oauthService: OAuthService
  ) {}

  ngOnInit() {
    if (this.oauthService.hasValidIdToken()) {
      const profile = this.oauthService.getIdentityClaims();
      console.log('Usuario ya autenticado:', profile);
    } else {
      console.log('#1 fail token');
    }
  }

  loginGoogle() {
    this.authGoogleService.login();
  }

  logoutGoogle() {
    this.authGoogleService.logout();
  }
}
