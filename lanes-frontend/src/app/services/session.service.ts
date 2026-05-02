import { Injectable } from '@angular/core';

const STORAGE_KEY = 'lanes.sessionId';
const UUID_REGEX = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

@Injectable({ providedIn: 'root' })
export class SessionService {
  private readonly sessionId: string;

  constructor() {
    this.sessionId = this.loadOrCreate();
  }

  getSessionId(): string {
    return this.sessionId;
  }

  private loadOrCreate(): string {
    try {
      const existing = localStorage.getItem(STORAGE_KEY);
      if (existing && UUID_REGEX.test(existing)) {
        return existing;
      }
    } catch {
      // localStorage may be unavailable (SSR, private mode)
    }

    const fresh = crypto.randomUUID();
    try {
      localStorage.setItem(STORAGE_KEY, fresh);
    } catch {
      // ignore — session still works for this page load
    }
    return fresh;
  }
}
