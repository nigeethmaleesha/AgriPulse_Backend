package com.agripulse.repository;

import com.agripulse.model.entity.Road;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoadRepository extends JpaRepository<Road, Long> {

    List<Road> findByIsOpenTrue();

    List<Road> findByFromPointId(String fromPointId);
}
