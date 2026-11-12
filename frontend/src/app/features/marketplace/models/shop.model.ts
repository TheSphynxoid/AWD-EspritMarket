export interface Shop {
  id?: number;
  name: string;
  description?: string;
  logoUrl?: string;
  ownerId: number;
  ownerEmail: string;
  status?: 'PENDING' | 'ACTIVE' | 'SUSPENDED';
  createdAt?: string;
}

export interface ShopRequest {
  name: string;
  description?: string;
  logoUrl?: string;
  ownerId: number;
  ownerEmail: string;
}