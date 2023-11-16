import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";

@Component({
  selector: 'app-manage-customer',
  templateUrl: './manage-customer.component.html',
  styleUrls: ['./manage-customer.component.scss']
})
export class ManageCustomerComponent {

  @Input()
  operation: 'create' | 'update' = 'create';

  @Input()
  customer: CustomerRegistrationRequest = {};

  @Output()
  submit: EventEmitter<CustomerRegistrationRequest> = new EventEmitter<CustomerRegistrationRequest>();

  @Output()
  cancel: EventEmitter<void> = new EventEmitter<void>();
  get isCustomerValid(): boolean {
    return this.hasLength(this.customer.name) &&
      this.hasLength(this.customer.email) &&
      (
        this.operation === 'update' ||
        this.hasLength(this.customer.password) &&
      this.hasLength(this.customer.gender)
      )
        &&
      this.customer.age !== undefined && this.customer.age >0;
  }
  private hasLength(input: string | undefined) : boolean {
    return input !== null && input !== undefined &&
      input.length > 0;
  }

  onSubmit() {
    this.submit.emit(this.customer);
  }

  protected readonly console = console;

  onCancel() {
    this.cancel.emit();
  }
}
