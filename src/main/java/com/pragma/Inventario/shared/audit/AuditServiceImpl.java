package com.pragma.Inventario.shared.audit;

import java.time.OffsetDateTime;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class AuditServiceImpl implements AuditService {

    private final AuditRepository auditRepository;

    public AuditServiceImpl(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @Override
    public void record(String entityType, Long entityId, String action, String details) {
        String username = "anonymous";
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getName() != null) {
            username = auth.getName();
        }

        String payload = details == null ? "" : details;

        AuditEntry entry = new AuditEntry(entityType, entityId, action, username, OffsetDateTime.now(), payload);
        auditRepository.save(entry);
    }
}
