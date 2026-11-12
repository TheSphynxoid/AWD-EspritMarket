// Aligné sur PanierResponse / LignePanierResponse du service marketplace
export interface CartItem {
  itemId?: number;
  productId: number;
  productName?: string;
  unitPrice: number;
  quantity: number;
  subtotal?: number;
}

export interface Cart {
  panierId?: number;
  userId: string; // identifiant Keycloak (sub)
  lignes: CartItem[];
  totalAmount: number;
}
