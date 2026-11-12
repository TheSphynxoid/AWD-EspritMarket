export interface CartItem {
  id?: number;
  productId: number;
  productName?: string;
  unitPrice: number;
  quantity: number;
  subtotal?: number;
}

export interface Cart {
  id?: number;
  userId: number;
  items: CartItem[];
  total: number;
}