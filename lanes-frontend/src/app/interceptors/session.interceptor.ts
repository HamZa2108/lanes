import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { SessionService } from '../services/session.service';
import { environment } from '../../environments/environment';

export const sessionInterceptor: HttpInterceptorFn = (req, next) => {
  if (!req.url.startsWith(environment.apiUrl)) {
    return next(req);
  }

  const sessionService = inject(SessionService);
  const cloned = req.clone({
    setHeaders: {
      'X-Session-Id': sessionService.getSessionId(),
    },
  });

  return next(cloned);
};
