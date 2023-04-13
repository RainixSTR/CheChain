package org.example.blockchain;

import java.util.ArrayList;
import java.util.List;

public class Blockchain {
    private final List<Block> chain;

    public Blockchain() {
        this.chain = new ArrayList<>();
        this.chain.add(createGenesisBlock());
    }

    private Block createGenesisBlock() {
        return new Block(0, "0", "Genesis Block");
    }

    public List<Block> getChain() {
        return this.chain;
    }

    public Block getLatestBlock() {
        return this.chain.get(this.chain.size() - 1);
    }

    public boolean addBlock(Block newBlock) {
        if (Block.isValid(newBlock.getHash()) && newBlock.getIndex() == getLatestBlock().getIndex() + 1) {
            this.chain.add(newBlock);
            return true;
        }
        return false;
    }

    public boolean isChainValid() {
        for (int i = 1; i < this.chain.size(); i++) {
            Block currentBlock = this.chain.get(i);
            Block previousBlock = this.chain.get(i - 1);

            if (!Block.isValid(currentBlock.getHash()) || !currentBlock.getPreviousHash().equals(previousBlock.getHash())) {
                return false;
            }
        }
        return true;
    }
}
