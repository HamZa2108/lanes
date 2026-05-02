export interface BoardSummary {
  id: number;
  name: string;
  createdAt: string;
}

export interface BoardDetail {
  id: number;
  name: string;
  createdAt: string;
  lanes: LaneDetail[];
}

export interface LaneDetail {
  id: number;
  name: string;
  position: number;
  cards: CardDetail[];
}

export interface CardDetail {
  id: number;
  title: string;
  description: string | null;
  position: number;
  createdAt: string;
}

export interface CreateBoardRequest {
  name: string;
}

export interface UpdateBoardRequest {
  name: string;
}

export interface CreateLaneRequest {
  name: string;
}

export interface UpdateLaneRequest {
  name?: string;
  position?: number;
}

export interface CreateCardRequest {
  title: string;
  description?: string;
}

export interface UpdateCardRequest {
  title?: string;
  description?: string;
  laneId?: number;
  position?: number;
}
