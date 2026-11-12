export interface Product {
  id?: number;
  name: string;
  description?: string;
  price: number;
  stock: number;
  categoryId?: number;
  categoryName?: string;
  shopId?: number;
  shopName?: string;
  imageUrl?: string;
  active?: boolean;
  createdAt?: string;
}

export interface ProductRequest {
  name: string;
  description?: string;
  price: number;
  stock: number;
  categoryId?: number;
}