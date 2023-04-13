package org.example.blockchain;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class BlockGenerator {
    private final Blockchain blockchain;
    private final ThreadPoolExecutor executor;

    public BlockGenerator(Blockchain blockchain) {
        this.blockchain = blockchain;
        this.executor = new ThreadPoolExecutor(1, 1,
                0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<>());
    }

    public void startGenerating() {
        executor.execute(() -> {
            while (true) {
                try {
                    Block previousBlock = blockchain.getLatestBlock();
                    int nextIndex = previousBlock == null ? 0 : previousBlock.getIndex() + 1;
                    String previousHash = previousBlock == null ? "stub" : previousBlock.getHash();
                    String data = generateData();
                    Block newBlock = new Block(nextIndex, previousHash, data);
                    blockchain.addBlock(newBlock, true);
                    Thread.sleep(3000);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private String generateData() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 256; i++) {
            char c = (char) ((Math.random() * 26) + 'a');
            sb.append(c);
        }
        return sb.toString();
    }
}
