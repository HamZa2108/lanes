import { Component, OnInit, computed, inject, input, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import {
  CdkDrag,
  CdkDragDrop,
  CdkDragHandle,
  CdkDropList,
  moveItemInArray,
  transferArrayItem,
} from '@angular/cdk/drag-drop';
import { ApiService } from '../../services/api.service';
import { BoardDetail, CardDetail, LaneDetail } from '../../models/api.types';

@Component({
  selector: 'app-board-detail',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, CdkDropList, CdkDrag, CdkDragHandle],
  templateUrl: './board-detail.component.html',
})
export class BoardDetailComponent implements OnInit {
  id = input.required<string>();

  private api = inject(ApiService);
  private fb = inject(FormBuilder);

  board = signal<BoardDetail | null>(null);
  loading = signal(true);
  error = signal<string | null>(null);

  laneIds = computed(() => (this.board()?.lanes ?? []).map((l) => `lane-${l.id}`));

  addingLane = signal(false);
  newLaneForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(120)]],
  });

  editingLaneId = signal<number | null>(null);
  editLaneForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(120)]],
  });

  deletingLaneId = signal<number | null>(null);

  addingCardInLaneId = signal<number | null>(null);
  newCardForm = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(200)]],
  });

  editingCardId = signal<number | null>(null);
  editCardForm = this.fb.nonNullable.group({
    title: ['', [Validators.required, Validators.maxLength(200)]],
    description: [''],
  });

  deletingCardId = signal<number | null>(null);

  ngOnInit(): void {
    const boardId = parseInt(this.id(), 10);
    if (isNaN(boardId)) {
      this.error.set('Invalid board ID.');
      this.loading.set(false);
      return;
    }

    this.api.getBoard(boardId).subscribe({
      next: (b) => {
        this.board.set(b);
        this.loading.set(false);
      },
      error: (err) => {
        this.error.set(err.status === 404 ? 'Board not found.' : 'Could not load board.');
        this.loading.set(false);
      },
    });
  }

  // ==================== LANE DRAG ====================

  onLaneDrop(event: CdkDragDrop<LaneDetail[]>): void {
    if (event.previousIndex === event.currentIndex) return;
    const current = this.board();
    if (!current) return;

    const snapshot = structuredClone(current);
    const lanes = [...current.lanes];
    moveItemInArray(lanes, event.previousIndex, event.currentIndex);
    lanes.forEach((l, i) => (l.position = i));
    this.board.set({ ...current, lanes });

    const moved = lanes[event.currentIndex];
    this.api.updateLane(moved.id, { position: event.currentIndex }).subscribe({
      error: () => {
        this.board.set(snapshot);
        this.error.set('Could not reorder lane. Reverted.');
      },
    });
  }

  // ==================== CARD DRAG ====================

  onCardDrop(event: CdkDragDrop<CardDetail[]>): void {
    const current = this.board();
    if (!current) return;
    if (event.previousContainer === event.container && event.previousIndex === event.currentIndex) {
      return;
    }

    const sourceLaneId = this.extractLaneId(event.previousContainer.id);
    const targetLaneId = this.extractLaneId(event.container.id);
    if (!sourceLaneId || !targetLaneId) return;

    const snapshot = structuredClone(current);
    const newLanes = current.lanes.map((l) => ({ ...l, cards: [...l.cards] }));
    const sourceLane = newLanes.find((l) => l.id === sourceLaneId)!;
    const targetLane = newLanes.find((l) => l.id === targetLaneId)!;

    if (sourceLane === targetLane) {
      moveItemInArray(sourceLane.cards, event.previousIndex, event.currentIndex);
    } else {
      transferArrayItem(
        sourceLane.cards,
        targetLane.cards,
        event.previousIndex,
        event.currentIndex,
      );
    }

    sourceLane.cards.forEach((c, i) => (c.position = i));
    if (sourceLane !== targetLane) {
      targetLane.cards.forEach((c, i) => (c.position = i));
    }

    this.board.set({ ...current, lanes: newLanes });

    const moved = targetLane.cards[event.currentIndex];
    this.api
      .updateCard(moved.id, { laneId: targetLaneId, position: event.currentIndex })
      .subscribe({
        error: () => {
          this.board.set(snapshot);
          this.error.set('Could not move card. Reverted.');
        },
      });
  }

  private extractLaneId(containerId: string): number | null {
    const match = containerId.match(/^lane-(\d+)$/);
    return match ? parseInt(match[1], 10) : null;
  }

  // ==================== LANE CRUD ====================

  startAddLane(): void {
    this.addingLane.set(true);
    this.newLaneForm.reset({ name: '' });
  }

  cancelAddLane(): void {
    this.addingLane.set(false);
  }

  saveNewLane(): void {
    const b = this.board();
    if (!b || this.newLaneForm.invalid) return;
    const name = this.newLaneForm.controls.name.value.trim();
    if (!name) return;

    this.api.createLane(b.id, { name }).subscribe({
      next: (lane) => {
        this.board.update((current) =>
          !current ? null : { ...current, lanes: [...current.lanes, lane] },
        );
        this.addingLane.set(false);
        this.newLaneForm.reset({ name: '' });
      },
      error: () => this.error.set('Could not create lane.'),
    });
  }

  startEditLane(lane: LaneDetail): void {
    this.editingLaneId.set(lane.id);
    this.editLaneForm.controls.name.setValue(lane.name);
  }

  cancelEditLane(): void {
    this.editingLaneId.set(null);
  }

  saveEditLane(id: number): void {
    if (this.editLaneForm.invalid) return;
    const name = this.editLaneForm.controls.name.value.trim();
    if (!name) return;

    this.api.updateLane(id, { name }).subscribe({
      next: (updated) => {
        this.board.update((current) =>
          !current
            ? null
            : {
                ...current,
                lanes: current.lanes.map((l) => (l.id === id ? { ...l, name: updated.name } : l)),
              },
        );
        this.editingLaneId.set(null);
      },
      error: () => this.error.set('Could not rename lane.'),
    });
  }

  askDeleteLane(id: number): void {
    this.deletingLaneId.set(id);
  }

  cancelDeleteLane(): void {
    this.deletingLaneId.set(null);
  }

  confirmDeleteLane(id: number): void {
    this.api.deleteLane(id).subscribe({
      next: () => {
        this.board.update((current) =>
          !current ? null : { ...current, lanes: current.lanes.filter((l) => l.id !== id) },
        );
        this.deletingLaneId.set(null);
      },
      error: () => {
        this.error.set('Could not delete lane.');
        this.deletingLaneId.set(null);
      },
    });
  }

  // ==================== CARD CRUD ====================

  startAddCard(laneId: number): void {
    this.addingCardInLaneId.set(laneId);
    this.newCardForm.reset({ title: '' });
  }

  cancelAddCard(): void {
    this.addingCardInLaneId.set(null);
  }

  saveNewCard(laneId: number): void {
    if (this.newCardForm.invalid) return;
    const title = this.newCardForm.controls.title.value.trim();
    if (!title) return;

    this.api.createCard(laneId, { title }).subscribe({
      next: (card) => {
        this.board.update((current) =>
          !current
            ? null
            : {
                ...current,
                lanes: current.lanes.map((l) =>
                  l.id === laneId ? { ...l, cards: [...l.cards, card] } : l,
                ),
              },
        );
        this.addingCardInLaneId.set(null);
        this.newCardForm.reset({ title: '' });
      },
      error: () => this.error.set('Could not create card.'),
    });
  }

  startEditCard(card: CardDetail): void {
    this.editingCardId.set(card.id);
    this.editCardForm.reset({
      title: card.title,
      description: card.description ?? '',
    });
  }

  cancelEditCard(): void {
    this.editingCardId.set(null);
  }

  saveEditCard(id: number): void {
    if (this.editCardForm.invalid) return;
    const title = this.editCardForm.controls.title.value.trim();
    const description = this.editCardForm.controls.description.value?.trim() ?? '';
    if (!title) return;

    this.api.updateCard(id, { title, description }).subscribe({
      next: (updated) => {
        this.board.update((current) =>
          !current
            ? null
            : {
                ...current,
                lanes: current.lanes.map((l) => ({
                  ...l,
                  cards: l.cards.map((c) =>
                    c.id === id
                      ? { ...c, title: updated.title, description: updated.description }
                      : c,
                  ),
                })),
              },
        );
        this.editingCardId.set(null);
      },
      error: () => this.error.set('Could not save card.'),
    });
  }

  askDeleteCard(id: number): void {
    this.deletingCardId.set(id);
  }

  cancelDeleteCard(): void {
    this.deletingCardId.set(null);
  }

  confirmDeleteCard(id: number): void {
    this.api.deleteCard(id).subscribe({
      next: () => {
        this.board.update((current) =>
          !current
            ? null
            : {
                ...current,
                lanes: current.lanes.map((l) => ({
                  ...l,
                  cards: l.cards.filter((c) => c.id !== id),
                })),
              },
        );
        this.deletingCardId.set(null);
      },
      error: () => {
        this.error.set('Could not delete card.');
        this.deletingCardId.set(null);
      },
    });
  }
}
