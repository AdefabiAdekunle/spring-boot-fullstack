import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {CustomerDTO} from "../../models/customer-dto";
import {environment} from "../../../environments/environment";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {CustomerUpdateRequest} from "../../models/customer-update-request";

@Injectable({
  providedIn: 'root'
})
export class CustomerService {

  private readonly customerUrl = `${environment.api.baseUrl}${environment.api.customerUrl}`
  constructor(
    private http: HttpClient
  ) { }

  findAll(): Observable<CustomerDTO[]>{
    // without using interceptor we add this {headers} after url
    // let headers: HttpHeaders = new HttpHeaders();
    // headers = headers.set("Authorization", 'Bearer eyJhbGciOiJIUzI1NiJ9.eyJzY29wZXMiOlsiUk9MRV9VU0VSIl0sInN1YiI6ImFkZWZhYml5dXN1ZkBnbWFpbC5jb20iLCJpc3MiOiJodHRwczovL2FkZWt1bmxlLmNvbSIsImlhdCI6MTY5OTk4MTc0NywiZXhwIjoxNzAxMjc3NzQ3fQ.ItIVntUUGhBeTKGsKppF3plpXMAyyMyJQaj_JE_Q3sw')
    return this.http.get<CustomerDTO[]>(
      this.customerUrl);
  }
  registerCustomer(customer: CustomerRegistrationRequest): Observable<void> {
    return this.http.post<void>(this.customerUrl, customer);
  }

  deleteCustomer(id: number | undefined): Observable<void> {
    return this.http.delete<void>(`${this.customerUrl}/${id}`);
  }

  updateCustomer(id: number | undefined, customer: CustomerUpdateRequest): Observable<void> {
    return this.http.put<void>(`${this.customerUrl}/${id}`, customer);
  }
  uploadCustomerProfileImage(id: number | undefined, formData:FormData): Observable<void> {
    const headers = new HttpHeaders();
    headers.append('Content-Type', 'multipart/form-data')
    return this.http.post<void>(
      `${this.customerUrl}/${id}/profile-image` ,
      formData,
      {headers}
    )
  }

  getCustomerProfileImageUrl(id: number | undefined) : string {
    return `${this.customerUrl}/${id}/profile-image`;
  }

}
