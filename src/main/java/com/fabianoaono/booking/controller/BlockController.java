package com.fabianoaono.booking.controller;

import com.fabianoaono.booking.entity.Block;
import com.fabianoaono.booking.exception.BlockNotFoundException;
import com.fabianoaono.booking.service.BlockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blocks")
public class BlockController {

    private final BlockService blockService;

    @Autowired
    public BlockController(BlockService blockService) {

        this.blockService = blockService;
    }

    @GetMapping
    public List<Block> getAllBlocks() {
        return blockService.getAllBlocks();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Block> getBlockById(@PathVariable Long id) {

        return blockService.getBlockById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Object> createBlock(@RequestBody Block block) {

        if (block == null || block.getPropertyId() == null ||
                block.getStartDate() == null || block.getEndDate() == null) {
            return ResponseEntity.badRequest().body("Block data is invalid.");
        }

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(blockService.createBlock(block));

    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateBlock(@PathVariable Long id, @RequestBody Block block) {

        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(blockService.updateBlock(id, block));
        } catch (BlockNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Block not found: " + e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteBlock(@PathVariable Long id) {

        try {
            blockService.deleteBlock(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        } catch (BlockNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Block not found: " + e.getMessage());
        }
    }
}
