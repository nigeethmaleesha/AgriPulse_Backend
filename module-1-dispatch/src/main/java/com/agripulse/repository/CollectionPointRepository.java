package com.agripulse.repository;

import com.agripulse.model.entity.CollectionPoint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CollectionPointRepository extends JpaRepository<CollectionPoint, String> {
}
