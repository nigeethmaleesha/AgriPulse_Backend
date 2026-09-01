package com.agripulse.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.agripulse.exception.FertilizerAllocationException;
import com.agripulse.model.Farm;
import com.agripulse.repository.FarmRepository;

import org.springframework.web.bind.annotation.CrossOrigin;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/api/farms")
public class FarmController {

    private final FarmRepository farmRepository;

    @Autowired
    public FarmController(FarmRepository farmRepository) {
        this.farmRepository = farmRepository;
    }

    // Register a new farm
    @PostMapping
    public ResponseEntity<Farm> registerFarm(@RequestBody Farm farm) {
        if (farmRepository.existsByContactNumber(farm.getContactNumber())) {
            throw new FertilizerAllocationException(
                    "A farm with contact number " + farm.getContactNumber() + " is already registered.");
        }
        Farm saved = farmRepository.save(farm);
        return ResponseEntity.ok(saved);
    }

    // List all registered farms
    @GetMapping
    public ResponseEntity<List<Farm>> getAllFarms() {
        return ResponseEntity.ok(farmRepository.findAll());
    }

    // Get one farm by ID
    @GetMapping("/{id}")
    public ResponseEntity<Farm> getFarmById(@PathVariable Long id) {
        Farm farm = farmRepository.findById(id)
                .orElseThrow(() -> new FertilizerAllocationException("Farm not found with id: " + id));
        return ResponseEntity.ok(farm);
    }
}