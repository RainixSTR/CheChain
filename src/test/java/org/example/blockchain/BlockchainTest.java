package org.example.blockchain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BlockchainTest {
    private Blockchain blockchain;

    @BeforeEach
    void setUp() {
        blockchain = new Blockchain(8080, Collections.emptyList(), true);
    }

    @Test
    void getChainFromWhenIndexOutOfBounds() {
        int indexOutOfBounds = blockchain.getChain().size();
        assertTrue(
                blockchain.getChainFrom(indexOutOfBounds).isEmpty(),
                "Expected an empty list when index is out of bounds");
    }

    @Test
    void getChainFromWhenIndexIsValid() { // Add blocks to the blockchain
        Block block1 = new Block(1, blockchain.getLatestBlock().getHash(), "Block 1");
        blockchain.addBlock(block1, false);
        Block block2 = new Block(2, blockchain.getLatestBlock().getHash(), "Block 2");
        blockchain.addBlock(block2, false);

        // Test getChainAfter with valid index
        int index = 2;
        List<Block> chainAfterIndex = blockchain.getChainFrom(index);
        assertEquals(1, chainAfterIndex.size());
        assertEquals(block2, chainAfterIndex.get(0));
    }

    @Test
    void addInvalidBlockReturnsFalse() {
        Block invalidBlock = new Block(1, "0", "Invalid Block", 0, "invalidhash");

        assertFalse(
                blockchain.addBlock(invalidBlock, false),
                "Adding an invalid block should return false");
        assertEquals(
                1,
                blockchain.getChain().size(),
                "Chain size should not change after adding an invalid block");
    }

    @Test
    void addValidBlockReturnsTrue() {
        Block previousBlock = blockchain.getLatestBlock();
        Block newBlock =
                new Block(previousBlock.getIndex() + 1, previousBlock.getHash(), "Test data");

        boolean result = blockchain.addBlock(newBlock, false);

        assertTrue(result);
        assertEquals(newBlock, blockchain.getLatestBlock());
    }

    @Test
    void addBlockWithValidBlockAndSendToCluster() {
        Block newBlock = new Block(1, blockchain.getLatestBlock().getHash(), "Test Data");
        boolean result = blockchain.addBlock(newBlock, true);

        assertTrue(result, "The block should be added to the chain");
        assertEquals(2, blockchain.getChain().size(), "The chain should have 2 blocks");
        assertEquals(
                newBlock, blockchain.getLatestBlock(), "The latest block should be the new block");
    }
}