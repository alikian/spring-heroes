import {Injectable} from '@angular/core';
import {Observable, of} from 'rxjs';
import {User} from './user'
import {MessageService} from './message.service';
import {HttpClient, HttpHeaders} from '@angular/common/http';
import {catchError, map, tap} from 'rxjs/operators';
import {Router} from "@angular/router";
import { environment } from '../environments/environment';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private userUrl = '/api/user';
  constructor(
    private http: HttpClient,
    private messageService: MessageService,
    private router: Router) {
  }

  private log(message: string) {
    this.messageService.add(`HeroService: ${message}`);
  }

  getUser(): Observable<User> {
    return this.http.get<User>(environment.apiUrl+this.userUrl).pipe(
      tap(_ => this.log(`fetched user `)),
      catchError(this.handleError<User>(`getUser `))
    );
  }

  logout() {
    this.http.post('/logout', {}).pipe(
      tap(_ =>{
        this.router.navigateByUrl("/");
      }),catchError(this.handleError<Object>(`logout `))).subscribe();

  }
  private handleError<T>(operation = 'operation', result?: T) {
    return (error: any): Observable<T> => {

      // TODO: send the error to remote logging infrastructure
      // console.error(error); // log to console instead

      // TODO: better job of transforming error for user consumption
      this.log(`${operation} failed: ${error.message}`);

      // Let the app keep running by returning an empty result.
      return of(result as T);
    };
  }
}
