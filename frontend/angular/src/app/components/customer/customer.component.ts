import {Component, OnInit} from '@angular/core';
import {CustomerDTO} from "../../models/customer-dto";
import {CustomerService} from "../../services/customer/customer.service";
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {first} from "rxjs";
import {ConfirmationService, MessageService} from "primeng/api";
import {AuthenticationResponse} from "../../models/authentication-response";

@Component({
  selector: 'app-customer',
  templateUrl: './customer.component.html',
  styleUrls: ['./customer.component.scss']
})
export class CustomerComponent implements OnInit {

  operation: 'create' | 'update' = 'create';
  display: boolean = false;
  customers: CustomerDTO[] = [];
  customer: CustomerRegistrationRequest = {};

  authResponse: AuthenticationResponse = {};

  customerImageUrl: string = "";
  constructor(
    private customerService: CustomerService,
    private messageService: MessageService,
    private confirmationService: ConfirmationService
  ) {

  }

  ngOnInit(): void {
    this.findAllCustomers();
    const storedUser = localStorage.getItem('user');
    if (storedUser) {
      this.authResponse = JSON.parse(storedUser);
    }
  }

  private findAllCustomers() {
    this.customerService.findAll().pipe(first())
      .subscribe({
        next: (data)=>{
          this.customers = data;
        }
      })
  }
  save(customer: CustomerRegistrationRequest) {
    if(customer) {
      if(this.operation === 'create') {
        this.customerService.registerCustomer(customer)
          .subscribe({
            next: ()=>{
              this.findAllCustomers();
              this.display = false;
              this.customer = {};
              this.messageService.add(
                {severity: 'success',
                  summary: 'Customer saved',
                  detail: `customer ${customer.name} was successfully saved`}
              );
            }
          });
      }
      else if (this.operation === 'update') {
        this.customerService.updateCustomer(customer.id, {...customer})
          .subscribe({
            next: () => {
              this.findAllCustomers();
              this.display = false;
              this.customer = {};
              this.messageService.add(
                {severity: 'success',
                  summary: 'Customer updated',
                  detail: `customer ${customer.name} was successfully updated`}
              );
            }
          })
      }
    }
  }

  deleteCustomer(customer: CustomerDTO) {
    this.confirmationService.confirm({
      header: 'Delete customer',
      message: `Are you sure you want to delete ${customer.name}? You can\'t undo this action afterwords`,
      accept: () => {
        this.customerService.deleteCustomer(customer.id)
          .subscribe({
            next: () => {
              this.findAllCustomers();
              this.messageService.add(
                {severity: 'success',
                  summary: 'Customer deleted',
                  detail: `customer ${customer.name} was successfully deleted`}
              );
            }
          })
      }
    })
  }

  updateCustomer(customerDTO: CustomerDTO) {
    this.operation = "update";
    this.display = true
    this.customer = {...customerDTO};
  }

  createCustomer() {
    this.display = true;
    this.customer = {};
    this.operation = 'create';
  }

  cancel() {
    this.display = false;
    this.customer = {};
    this.operation = 'create';
  }

  onUploadImage(formData: FormData) {
    this.customerService.uploadCustomerProfileImage(this.customer.id, formData)
      .subscribe({
        next: () => {
          this.display = false
          this.messageService.add(
            {severity: 'success',
              summary: 'Customer Profile picture uploaded',
              detail: `customer ${this.customer.name} profile image was successfully uploaded`}
          );
          this.findAllCustomers();
          this.customerImageUrl = this.customerService.getCustomerProfileImageUrl(this.customer.id);
        }
      })
  }


}
