import {Component, Input, Output} from '@angular/core';
import {MenuItem} from "primeng/api";
import {AuthenticationResponse} from "../../models/authentication-response";
import {Router} from "@angular/router";

@Component({
  selector: 'app-header-bar',
  templateUrl: './header-bar.component.html',
  styleUrls: ['./header-bar.component.scss']
})
export class HeaderBarComponent {
  constructor(
    private router: Router
  ) {
  }
  @Input()
  authResponse: AuthenticationResponse = {};

  items: Array<MenuItem> = [
    {
      label: 'Profile',
      icon: 'pi pi-user'
    },
    {
      label: 'Setting',
      icon: 'pi pi-cog'
    },
    {
      separator: true
    },
    {
      label: 'Sign out',
      icon: 'pi pi-sign-out',
      command: () => {
        localStorage.clear();
        this.router.navigate(['login'])
      }
    }
  ];

  get username(): string {
    if(this.authResponse.customerDTO?.username){
      return this.authResponse.customerDTO?.username;
    }
    return '---';
  }

  get userRole(): string {
    if(this.authResponse.customerDTO?.roles){
      return this.authResponse.customerDTO?.roles[0];
    }
    return '---';
  }

}
