package com.jswone.orchestrator.repository;

import com.jswone.orchestrator.entity.WorkflowReferenceEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface WorkFlowReferenceRepository extends JpaRepository<WorkflowReferenceEntity, Long> {

  @Query(
      "SELECT w FROM WorkflowReferenceEntity w WHERE w.referenceNo = :referenceNo AND w.type ="
          + " :type")
  Optional<WorkflowReferenceEntity> fetchWorkflowDetails(
      @Param("referenceNo") String referenceNo, @Param("type") String type);
}
