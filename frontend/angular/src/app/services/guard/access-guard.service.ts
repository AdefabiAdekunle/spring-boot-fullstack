import {inject, Injectable} from '@angular/core';
import {ActivatedRouteSnapshot, CanActivateFn, Router, RouterStateSnapshot, UrlTree} from "@angular/router";
import {AuthenticationResponse} from "../../models/authentication-response";
import { JwtHelperService } from '@auth0/angular-jwt';
import {Observable} from "rxjs";

@Injectable({
  providedIn: 'root'
})
class PermissionsService {

  constructor(private router: Router) {}

  canActivate(next: ActivatedRouteSnapshot
              , state: RouterStateSnapshot
  ): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree{
    //your logic goes here
    const storedUser = localStorage.getItem('user');
        if (storedUser) {
          const authResponse: AuthenticationResponse = JSON.parse(storedUser);
          const token = authResponse.token;
          if (token) {
            const jwtHelper = new JwtHelperService();
            const isTokenNonExpired = !jwtHelper.isTokenExpired(token);
            if (isTokenNonExpired) {
              return true;
            }
          }
        }
        this.router.navigate(['login']);
        return false;
  }

}

export const AuthGuard: CanActivateFn = (next: ActivatedRouteSnapshot,
                                         state: RouterStateSnapshot
): Observable<boolean | UrlTree> | Promise<boolean | UrlTree> | boolean | UrlTree => {
  return inject(PermissionsService).canActivate(next, state);
}
