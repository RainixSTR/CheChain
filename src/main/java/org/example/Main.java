package org.example;

import org.example.blockchain.BlockGenerator;
import org.example.blockchain.Blockchain;

public class Main {
    public static void main(String[] args) {
        BlockGenerator blockGenerator = new BlockGenerator(new Blockchain());
        blockGenerator.startGenerating();
    }
}