import {Component, EventEmitter, Input, Output, ViewChild} from '@angular/core';
import {CustomerRegistrationRequest} from "../../models/customer-registration-request";
import {MessageService} from "primeng/api";
import {FileUpload} from "primeng/fileupload";

@Component({
  selector: 'app-manage-customer',
  templateUrl: './manage-customer.component.html',
  styleUrls: ['./manage-customer.component.scss']
})
export class ManageCustomerComponent {

  @ViewChild('fileUpload') fileUpload: FileUpload | undefined;
  @Input()
  operation: 'create' | 'update' = 'create';

  @Input()
  customer: CustomerRegistrationRequest = {};

  @Output()
  submit: EventEmitter<CustomerRegistrationRequest> = new EventEmitter<CustomerRegistrationRequest>();

  @Output()
  cancel: EventEmitter<void> = new EventEmitter<void>();

  @Output()
  uploadImage: EventEmitter<FormData> = new EventEmitter<FormData>();

  constructor(private messageService: MessageService) {
  }
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

  onUpload(event:any) {
    const formData = new FormData();
    formData.append('file', event.files[0]);
    this.uploadImage.emit(formData);
    this.fileUpload?.clear();
  }

}
