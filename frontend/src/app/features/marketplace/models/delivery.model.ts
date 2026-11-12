export interface Delivery {
  id?: number;
  recipientName: string;
  address: string;
  city: string;
  postalCode: string;
  phone: string;
  deliveryFee: number;
  deliveryType: 'STANDARD' | 'EXPRESS' | 'SAME_DAY';
  productId: number; // produit marketplace (vérifié par delivery)
  status?: 'PENDING' | 'IN_PROGRESS' | 'DELIVERED' | 'CANCELLED';
  createdAt?: string;
  delivererId?: number;
}