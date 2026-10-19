import { TestBed } from '@angular/core/testing';
import { CanActivateFn } from '@angular/router';
import { Router } from '@angular/router';
import { AuthService } from '../services/auth';
import { RoleGuard } from './role-guard';

describe('RoleGuard', () => {
  let authService: AuthService;
  let router: Router;
  let roleGuard: RoleGuard;
  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [RoleGuard, AuthService, Router]
    });

    authService = TestBed.inject(AuthService);
    router = TestBed.inject(Router);

    roleGuard = TestBed.inject(RoleGuard);
  });

  it('should be created', () => {
    expect(roleGuard).toBeTruthy();
  });

});
