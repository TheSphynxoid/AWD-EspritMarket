import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { ApiService } from './api.service';

// ── Partner Company ──
export interface PartnerCompany {
  id?: number;
  name: string;
  sector?: string;
  contactEmail?: string;
  partnershipStatus?: string;
  website?: string;
  phone?: string;
}

// ── Job Offer (enriched by Feign with event data) ──
export interface JobOffer {
  id?: number;
  title: string;
  description?: string;
  type?: string;
  location?: string;
  status?: string;
  companyId: number;
  eventId?: number | null;
  // Feign-enriched fields (read-only, populated by backend)
  eventName?: string | null;
  eventDate?: string | null;
  eventLocation?: string | null;
}

// ── Application ──
export interface JobApplication {
  id?: number;
  candidateName: string;
  candidateEmail?: string;
  resumeUrl?: string;
  status?: string;
  date?: string;
  userId?: number;
  jobOfferId: number;
}

// ── Interview ──
export interface Interview {
  id?: number;
  date?: string;
  location?: string;
  type?: string;
  status?: string;
  notes?: string;
  applicationId: number;
}

@Injectable({ providedIn: 'root' })
export class PartnershipService {
  constructor(private http: HttpClient, private api: ApiService) {}

  // ── Partner Companies ──

  listCompanies(): Observable<PartnerCompany[]> {
    return this.http.get<PartnerCompany[]>(this.api.url('/patnership/api/partnership/companies'));
  }

  getCompany(id: number): Observable<PartnerCompany> {
    return this.http.get<PartnerCompany>(this.api.url(`/patnership/api/partnership/companies/${id}`));
  }

  createCompany(company: PartnerCompany): Observable<PartnerCompany> {
    return this.http.post<PartnerCompany>(this.api.url('/patnership/api/partnership/companies'), company);
  }

  updateCompany(id: number, company: PartnerCompany): Observable<PartnerCompany> {
    return this.http.put<PartnerCompany>(this.api.url(`/patnership/api/partnership/companies/${id}`), company);
  }

  deleteCompany(id: number): Observable<void> {
    return this.http.delete<void>(this.api.url(`/patnership/api/partnership/companies/${id}`));
  }

  // ── Job Offers ──

  listJobOffers(): Observable<JobOffer[]> {
    return this.http.get<JobOffer[]>(this.api.url('/patnership/api/partnership/job-offers'));
  }

  getJobOffer(id: number): Observable<JobOffer> {
    return this.http.get<JobOffer>(this.api.url(`/patnership/api/partnership/job-offers/${id}`));
  }

  listJobOffersByCompany(companyId: number): Observable<JobOffer[]> {
    return this.http.get<JobOffer[]>(this.api.url(`/patnership/api/partnership/job-offers/company/${companyId}`));
  }

  createJobOffer(offer: JobOffer): Observable<JobOffer> {
    return this.http.post<JobOffer>(this.api.url('/patnership/api/partnership/job-offers'), offer);
  }

  updateJobOffer(id: number, offer: JobOffer): Observable<JobOffer> {
    return this.http.put<JobOffer>(this.api.url(`/patnership/api/partnership/job-offers/${id}`), offer);
  }

  deleteJobOffer(id: number): Observable<void> {
    return this.http.delete<void>(this.api.url(`/patnership/api/partnership/job-offers/${id}`));
  }

  // ── Applications ──

  listApplications(): Observable<JobApplication[]> {
    return this.http.get<JobApplication[]>(this.api.url('/patnership/api/partnership/applications'));
  }

  getApplication(id: number): Observable<JobApplication> {
    return this.http.get<JobApplication>(this.api.url(`/patnership/api/partnership/applications/${id}`));
  }

  listApplicationsByJobOffer(jobOfferId: number): Observable<JobApplication[]> {
    return this.http.get<JobApplication[]>(this.api.url(`/patnership/api/partnership/applications/job-offer/${jobOfferId}`));
  }

  createApplication(app: JobApplication): Observable<JobApplication> {
    return this.http.post<JobApplication>(this.api.url('/patnership/api/partnership/applications'), app);
  }

  updateApplication(id: number, app: JobApplication): Observable<JobApplication> {
    return this.http.put<JobApplication>(this.api.url(`/patnership/api/partnership/applications/${id}`), app);
  }

  deleteApplication(id: number): Observable<void> {
    return this.http.delete<void>(this.api.url(`/patnership/api/partnership/applications/${id}`));
  }

  // ── Interviews ──

  listInterviews(): Observable<Interview[]> {
    return this.http.get<Interview[]>(this.api.url('/patnership/api/partnership/interviews'));
  }

  getInterview(id: number): Observable<Interview> {
    return this.http.get<Interview>(this.api.url(`/patnership/api/partnership/interviews/${id}`));
  }

  listInterviewsByApplication(applicationId: number): Observable<Interview[]> {
    return this.http.get<Interview[]>(this.api.url(`/patnership/api/partnership/interviews/application/${applicationId}`));
  }

  createInterview(interview: Interview): Observable<Interview> {
    return this.http.post<Interview>(this.api.url('/patnership/api/partnership/interviews'), interview);
  }

  updateInterview(id: number, interview: Interview): Observable<Interview> {
    return this.http.put<Interview>(this.api.url(`/patnership/api/partnership/interviews/${id}`), interview);
  }

  deleteInterview(id: number): Observable<void> {
    return this.http.delete<void>(this.api.url(`/patnership/api/partnership/interviews/${id}`));
  }
}

