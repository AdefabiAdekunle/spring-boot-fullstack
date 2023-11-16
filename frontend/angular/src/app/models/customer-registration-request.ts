export interface CustomerRegistrationRequest {
  id?: number;
  name?: string;
  email?: string;
  age?: number;
  gender?: 'MALE' | 'FEMALE';
  password?: string;
}
