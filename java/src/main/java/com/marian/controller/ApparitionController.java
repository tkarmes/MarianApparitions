package com.marian.controller;

import com.marian.Apparition;
import com.marian.ApparitionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/apparitions")
public class ApparitionController {

    @Autowired
    private ApparitionRepository repository;

    @GetMapping
    public List<Apparition> getAllApparitions() {
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
}