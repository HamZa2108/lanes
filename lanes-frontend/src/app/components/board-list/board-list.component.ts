import { Component, OnInit, inject, signal } from '@angular/core';
import { DatePipe } from '@angular/common';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { ApiService } from '../../services/api.service';
import { BoardSummary } from '../../models/api.types';

@Component({
  selector: 'app-board-list',
  standalone: true,
  imports: [ReactiveFormsModule, RouterLink, DatePipe],
  templateUrl: './board-list.component.html',
})
export class BoardListComponent implements OnInit {
  private api = inject(ApiService);
  private fb = inject(FormBuilder);

  boards = signal<BoardSummary[] | null>(null);
  error = signal<string | null>(null);
  creating = signal(false);
  editingId = signal<number | null>(null);
  deletingId = signal<number | null>(null);

  createForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(120)]],
  });

  editForm = this.fb.nonNullable.group({
    name: ['', [Validators.required, Validators.maxLength(120)]],
  });

  ngOnInit(): void {
    this.loadBoards();
  }

  loadBoards(): void {
    this.error.set(null);
    this.api.listBoards().subscribe({
      next: (data) => this.boards.set(data),
      error: () => this.error.set('Could not load boards. Is the API awake?'),
    });
  }

  onCreate(): void {
    if (this.createForm.invalid || this.creating()) return;
    const name = this.createForm.controls.name.value.trim();
    if (!name) return;

    this.creating.set(true);
    this.error.set(null);
    this.api.createBoard({ name }).subscribe({
      next: (board) => {
        this.boards.update((current) => [board, ...(current ?? [])]);
        this.createForm.reset({ name: '' });
        this.creating.set(false);
      },
      error: () => {
        this.error.set('Could not create board.');
        this.creating.set(false);
      },
    });
  }

  startEdit(board: BoardSummary): void {
    this.editingId.set(board.id);
    this.editForm.controls.name.setValue(board.name);
  }

  cancelEdit(): void {
    this.editingId.set(null);
  }

  saveEdit(id: number): void {
    if (this.editForm.invalid) return;
    const name = this.editForm.controls.name.value.trim();
    if (!name) return;

    this.api.updateBoard(id, { name }).subscribe({
      next: (updated) => {
        this.boards.update(
          (current) =>
            current?.map((b) => (b.id === id ? { ...b, name: updated.name } : b)) ?? null,
        );
        this.editingId.set(null);
      },
      error: () => this.error.set('Could not rename board.'),
    });
  }

  askDelete(id: number): void {
    this.deletingId.set(id);
  }

  cancelDelete(): void {
    this.deletingId.set(null);
  }

  confirmDelete(id: number): void {
    this.api.deleteBoard(id).subscribe({
      next: () => {
        this.boards.update((current) => current?.filter((b) => b.id !== id) ?? null);
        this.deletingId.set(null);
      },
      error: () => {
        this.error.set('Could not delete board.');
        this.deletingId.set(null);
      },
    });
  }
}
