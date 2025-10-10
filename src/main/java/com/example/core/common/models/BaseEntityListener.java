package com.example.core.common.models;

import com.example.core.common.interfaces.IAuditableListeners;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreRemove;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class BaseEntityListener {

  IAuditableListeners auditableListeners;

  private Optional<String> getCurrentAuditor() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (Objects.isNull(authentication)
        || !authentication.isAuthenticated()
        || "anonymousUser".equals(authentication.getPrincipal())) {
      return Optional.of(("0"));
    }
    return Optional.of(authentication.getName());
  }

  @PrePersist
  private void beforeAnyInsert(Auditable auditable) {
    Optional<String> currentAuditor = getCurrentAuditor();
    auditable.setCreatedBy(Integer.parseInt(currentAuditor.get()));
    auditable.setUpdatedBy(Integer.parseInt(currentAuditor.get()));
    if (auditable.getCreatedAt() == null) {
      auditable.setCreatedAt(LocalDateTime.now());
    }
    if (auditable.getUpdatedAt() == null) {
      auditable.setUpdatedAt(LocalDateTime.now());
    }

    if (SecurityContextHolder.getContext().getAuthentication() != null) {
      auditable.setCreatedProgram(
          SecurityContextHolder.getContext().getAuthentication().getDetails().toString());
    } else auditable.setCreatedProgram("System");

    if (Objects.nonNull(auditableListeners)) {
      auditableListeners.beforeAnyUpdate(auditable);
    }
  }

  @PreRemove
  private void beforeAnyRemove(Auditable auditable) {
    if (Objects.nonNull(auditableListeners)) {
      auditableListeners.beforeAnyUpdate(auditable);
    }
  }

  @PostPersist
  @PostUpdate
  @PostRemove
  private void afterAnyUpdate(Auditable auditable) {
    if (Objects.nonNull(auditableListeners)) {
      auditableListeners.afterAnyUpdate(auditable);
    }
  }

  @PostLoad
  private void afterLoad(Auditable auditable) {
    if (Objects.nonNull(auditableListeners)) {
      auditableListeners.afterLoad(auditable);
    }
  }
}
