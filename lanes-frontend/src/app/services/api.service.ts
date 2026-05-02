import { inject, Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';
import {
  BoardDetail,
  BoardSummary,
  CardDetail,
  CreateBoardRequest,
  CreateCardRequest,
  CreateLaneRequest,
  LaneDetail,
  UpdateBoardRequest,
  UpdateCardRequest,
  UpdateLaneRequest,
} from '../models/api.types';

@Injectable({ providedIn: 'root' })
export class ApiService {
  private http = inject(HttpClient);
  private baseUrl = `${environment.apiUrl}/api`;

  listBoards(): Observable<BoardSummary[]> {
    return this.http.get<BoardSummary[]>(`${this.baseUrl}/boards`);
  }

  getBoard(id: number): Observable<BoardDetail> {
    return this.http.get<BoardDetail>(`${this.baseUrl}/boards/${id}`);
  }

  createBoard(body: CreateBoardRequest): Observable<BoardSummary> {
    return this.http.post<BoardSummary>(`${this.baseUrl}/boards`, body);
  }

  updateBoard(id: number, body: UpdateBoardRequest): Observable<BoardSummary> {
    return this.http.patch<BoardSummary>(`${this.baseUrl}/boards/${id}`, body);
  }

  deleteBoard(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/boards/${id}`);
  }

  createLane(boardId: number, body: CreateLaneRequest): Observable<LaneDetail> {
    return this.http.post<LaneDetail>(`${this.baseUrl}/boards/${boardId}/lanes`, body);
  }

  updateLane(id: number, body: UpdateLaneRequest): Observable<LaneDetail> {
    return this.http.patch<LaneDetail>(`${this.baseUrl}/lanes/${id}`, body);
  }

  deleteLane(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/lanes/${id}`);
  }

  createCard(laneId: number, body: CreateCardRequest): Observable<CardDetail> {
    return this.http.post<CardDetail>(`${this.baseUrl}/lanes/${laneId}/cards`, body);
  }

  updateCard(id: number, body: UpdateCardRequest): Observable<CardDetail> {
    return this.http.patch<CardDetail>(`${this.baseUrl}/cards/${id}`, body);
  }

  deleteCard(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/cards/${id}`);
  }
}
