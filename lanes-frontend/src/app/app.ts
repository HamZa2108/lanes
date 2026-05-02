import { Component, inject } from '@angular/core';
import { RouterLink, RouterOutlet } from '@angular/router';
import { ThemeService } from './services/theme.service';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterOutlet, RouterLink],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {
  private themeService = inject(ThemeService);
  theme = this.themeService.theme;

  toggleTheme(): void {
    this.themeService.toggle();
  }

  clearSession(): void {
    const confirmed = confirm(
      'Clear all your boards and start a fresh session?\n\nThis cannot be undone.',
    );
    if (!confirmed) return;

    localStorage.removeItem('lanes.sessionId');
    window.location.href = '/';
  }
}
