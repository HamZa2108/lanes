import { Routes } from '@angular/router';

export const routes: Routes = [
  {
    path: '',
    loadComponent: () =>
      import('./components/board-list/board-list.component').then((m) => m.BoardListComponent),
  },
  {
    path: 'boards/:id',
    loadComponent: () =>
      import('./components/board-detail/board-detail.component').then(
        (m) => m.BoardDetailComponent,
      ),
  },
  { path: '**', redirectTo: '' },
];
