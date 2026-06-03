package com.pragma.Inventario.shared.audit.infrastructure.adapters.in.web;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.pragma.Inventario.shared.audit.AuditEntry;
import com.pragma.Inventario.shared.audit.AuditRepository;

@Controller
@RequestMapping("/admin/audit")
public class AuditController {

    private final AuditRepository auditRepository;

    public AuditController(AuditRepository auditRepository) {
        this.auditRepository = auditRepository;
    }

    @GetMapping
    public String list(Model model) {
        List<AuditEntry> entries = auditRepository.findAll(Sort.by(Sort.Direction.DESC, "timestamp"));
        model.addAttribute("entries", entries);
        return "admin/audit/list";
    }
}
