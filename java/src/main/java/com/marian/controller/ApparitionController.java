package com.marian.controller;

import com.marian.Apparition;
import com.marian.ApparitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apparitions")
@CrossOrigin(origins = "*")
public class ApparitionController {

    @Autowired
    private ApparitionRepository repository;

    @GetMapping
    public List<Apparition> getAllApparitions() {
        System.out.println("Fetching all apparitions...");
        return repository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Apparition> getApparitionById(@PathVariable Long id) {
        return repository.findById(id)
                .map(apparition -> ResponseEntity.ok(apparition))
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Apparition addApparition(@RequestBody Apparition apparition) {
        return repository.save(apparition);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Apparition> updateApparition(@PathVariable Long id, @RequestBody Apparition updatedApparition) {
        return repository.findById(id)
                .map(existingApparition -> {
                    existingApparition.setName(updatedApparition.getName());
                    existingApparition.setLocation(updatedApparition.getLocation());
                    existingApparition.setDate(updatedApparition.getDate());
                    existingApparition.setApprovalStatus(updatedApparition.getApprovalStatus());
                    existingApparition.setDescription(updatedApparition.getDescription());
                    Apparition savedApparition = repository.save(existingApparition);
                    return ResponseEntity.ok(savedApparition);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteApparition(@PathVariable Long id) {
        if (repository.existsById(id)) {
            repository.deleteById(id);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }
}