import { AuthConfig } from 'angular-oauth2-oidc';

export const authConfig: AuthConfig = {
    issuer: 'https://accounts.google.com',
    redirectUri: typeof window !== 'undefined' ? window.location.origin : '',
    clientId: '38423991825-burmdic6706js9cmarbugkj6dr12pcjk.apps.googleusercontent.com',
    scope: 'openid profile email',
    strictDiscoveryDocumentValidation: false,
};
