package com.example.core.common.interfaces;

import com.example.core.common.models.Auditable;

public interface IAuditableListeners {
  void beforeAnyUpdate(Auditable auditable);

  void afterAnyUpdate(Auditable auditable);

  void afterLoad(Auditable auditable);

  void beforeRemove(Auditable auditable);

  void afterRemove(Auditable auditable);
}
