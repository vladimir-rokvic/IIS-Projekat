package com.iis.projekat.controller;

import com.iis.projekat.dto.DonationCreateDTO;
import com.iis.projekat.dto.DonationDTO;
import com.iis.projekat.dto.ReturnDocumentCreateDTO;
import com.iis.projekat.dto.ReturnDocumentDTO;
import com.iis.projekat.service.DonationService;
import com.iis.projekat.service.ReturnDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/return-documents")
public class ReturnDocumentController {

    @Autowired
    private ReturnDocumentService returnDocumentService;

    @PostMapping
    public ResponseEntity<ReturnDocumentDTO> create(@RequestBody ReturnDocumentCreateDTO dto) {
        ReturnDocumentDTO res = returnDocumentService.createReturnDocument(dto);
        return ResponseEntity.ok(res);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody ReturnDocumentCreateDTO dto) {
        boolean ok = returnDocumentService.updateReturnDocument(id, dto);
        if(!ok) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ReturnDocumentDTO> get(@PathVariable Long id) {
        ReturnDocumentDTO dto = returnDocumentService.getReturnDocumentById(id);
        return ResponseEntity.ok(dto);
    }

    @GetMapping
    public ResponseEntity<List<ReturnDocumentDTO>> list() {
        return ResponseEntity.ok(returnDocumentService.listReturnDocuments());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        returnDocumentService.deleteReturnDocument(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/send")
    public ResponseEntity<?> send(@PathVariable Long id) {
        boolean ok = returnDocumentService.sendReturnDocument(id);
        if(!ok) return ResponseEntity.badRequest().build();
        return ResponseEntity.ok().build();
    }

}
