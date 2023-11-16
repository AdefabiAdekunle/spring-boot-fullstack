import {Component, EventEmitter, Input, Output} from '@angular/core';
import {CustomerDTO} from "../../models/customer-dto";
import {CustomerComponent} from "../customer/customer.component";

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



  get customerImage(): string {
    const gender = this.customerData.gender === 'MALE' ? 'men' : 'women';
    return `https://randomuser.me/api/portraits/${gender}/${this.customerIndex}.jpg`;
  }

  onDelete() {
    this.delete.emit(this.customerData)
  }

  onUpdate() {
    this.update.emit(this.customerData)
  }
}
