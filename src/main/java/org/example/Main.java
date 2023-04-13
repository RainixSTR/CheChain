package org.example;

import org.example.blockchain.BlockGenerator;
import org.example.blockchain.Blockchain;
import org.example.http.BlockServer;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        List<String> peers_1 = Arrays.asList("http://localhost:8082", "http://localhost:8083");
        startNode(peers_1, 8081, true);
        Thread.sleep(2000);
        List<String> peers_2 = Arrays.asList("http://localhost:8081", "http://localhost:8083");
        startNode(peers_2, 8082,false);
        Thread.sleep(2000);
        List<String> peers_3 = Arrays.asList("http://localhost:8081", "http://localhost:8082");
        startNode(peers_3, 8083, false);
        Thread.sleep(2000);
    }

    private static void startNode(List<String> peers, int port, boolean generateGenesis) throws IOException {
        Blockchain blockchain = new Blockchain(port, peers, generateGenesis);
        BlockGenerator blockGenerator = new BlockGenerator(blockchain);
        BlockServer blockServer = new BlockServer(blockchain, port);
        blockServer.start();
        blockGenerator.startGenerating();
    }
}