package com.hamzazine.lanes.session;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

import java.util.UUID;

@Component
@RequestScope
@Getter
@Setter
public class SessionContext {
    private UUID sessionId;
}