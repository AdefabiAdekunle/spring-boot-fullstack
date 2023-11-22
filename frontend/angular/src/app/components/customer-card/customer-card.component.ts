import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CustomerDTO} from "../../models/customer-dto";
import {CustomerComponent} from "../customer/customer.component";
import {CustomerService} from "../../services/customer/customer.service";

@Component({
  selector: 'app-customer-card',
  templateUrl: './customer-card.component.html',
  styleUrls: ['./customer-card.component.scss']
})
export class CustomerCardComponent {
  @Input()
  customerData!: CustomerDTO;

  @Input()
  customerIndex = 0;

  @Output()
  delete: EventEmitter<CustomerDTO> = new EventEmitter<CustomerDTO>();

  @Output()
  update: EventEmitter<CustomerDTO> = new EventEmitter<CustomerDTO>();

  @Input()
  customerImageUrl: string = "";

  constructor(private customerService: CustomerService) {
  }
  get customerImage(): string {
    // const gender = this.customerData.gender === 'MALE' ? 'men' : 'women';
    // return `https://randomuser.me/api/portraits/${gender}/${this.customerIndex}.jpg`;

    if(this.customerData.profileImageId === null){
      console.log("AAAAAAAA")
      return ""
    }
    return this.customerService.getCustomerProfileImageUrl(this.customerData.id);
  }

  onDelete() {
    this.delete.emit(this.customerData)
  }

  onUpdate() {
    this.update.emit(this.customerData)
  }
}
