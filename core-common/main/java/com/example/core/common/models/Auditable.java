package com.example.core.common.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@MappedSuperclass
@Getter
@Setter
@ToString
@EntityListeners({AuditingEntityListener.class, BaseEntityListener.class})
public abstract class Auditable {

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "created_program")
  private String createdProgram;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "created_by")
  private Integer createdBy;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "created_at", nullable = false, updatable = false)
  @CreatedDate
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime createdAt;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "updated_by")
  private Integer updatedBy;

  @JsonProperty(access = JsonProperty.Access.READ_ONLY)
  @Column(name = "updated_at")
  @LastModifiedDate
  @Temporal(TemporalType.TIMESTAMP)
  private LocalDateTime updatedAt;
}
