package com.fabianoaono.booking.service;

import com.fabianoaono.booking.entity.Block;
import com.fabianoaono.booking.exception.BlockNotFoundException;
import com.fabianoaono.booking.repository.BlockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BlockService {

    private final BlockRepository blockRepository;

    @Autowired
    public BlockService(BlockRepository blockRepository) {

        this.blockRepository = blockRepository;
    }

    public List<Block> getAllBlocks() {
        return blockRepository.findAll();
    }

    public Optional<Block> getBlockById(Long id) {
        return blockRepository.findById(id);
    }

    public Block createBlock(Block block) {

        return blockRepository.save(block);
    }

    public Block updateBlock(Long id, Block block) throws BlockNotFoundException {

        if (!blockRepository.existsById(id)) {
            throw new BlockNotFoundException("Block with id " + id + " does not exist");
        }

        block.setId(id);
        return blockRepository.save(block);
    }

    public void deleteBlock(Long id) throws BlockNotFoundException {

        if (!blockRepository.existsById(id)) {
            throw new BlockNotFoundException("Block with id " + id + " does not exist");
        }

        blockRepository.deleteById(id);
    }
}
