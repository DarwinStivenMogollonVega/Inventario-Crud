package com.pragma.Inventario.shared.audit;

public interface AuditService {
    void record(String entityType, Long entityId, String action, String details);
}
