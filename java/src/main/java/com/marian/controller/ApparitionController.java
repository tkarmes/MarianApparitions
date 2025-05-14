package com.marian;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}