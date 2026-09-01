package com.agripulse.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.agripulse.model.Farm;

public interface FarmRepository extends JpaRepository<Farm, Long> {

    Optional<Farm> findByContactNumber(String contactNumber);

    boolean existsByContactNumber(String contactNumber);
}