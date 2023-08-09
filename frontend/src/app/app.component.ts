import {Component, OnInit} from '@angular/core';
import {AuthService} from './auth.service';
import {User} from './user';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent implements OnInit {
  title = 'heroes-app';
  user?: User;

  constructor(private authService: AuthService) {
  }

  ngOnInit(): void {
    this.authService.getUser()
      .subscribe(user => this.user = user);
    // console.log(this.user?.name)
  }

  logout() {
    this.authService.logout();
    this.user = undefined;
  }
}
