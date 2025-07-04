package com.jswone.orchestrator.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

@Entity
@Table(name = "workflow_reference")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class WorkflowReferenceEntity implements Serializable {

  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Id
  @Column(name = "id", nullable = false)
  private Long id;

  @Column(name = "type")
  private String type;

  @Column(name = "workflow_id")
  private String workflowId;

  @Column(name = "reference_no")
  private String referenceNo;

  @Column(name = "initiated_by")
  private String initiatedBy;

  @Column(name = "metadata")
  @JdbcTypeCode(SqlTypes.JSON)
  private Map<String, Object> metadata;
}
