package org.example.blockchain;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Blockchain {
    private final int port;
    private final List<Block> chain;
    private final List<String> peerUrls;

    public Blockchain(int port, List<String> peerUrls, boolean genesisBlock) {
        this.port = port;
        this.chain = new ArrayList<>();
        if (genesisBlock) {
            this.chain.add(createGenesisBlock());
        }
        this.peerUrls = peerUrls;
        syncWithPeers();
    }

    private Block createGenesisBlock() {
        return new Block(0, "0", "Genesis Block");
    }

    public List<Block> getChain() {
        return this.chain;
    }

    public Block getLatestBlock() {
        if (chain.isEmpty()) {
            return null;
        }
        return this.chain.get(this.chain.size() - 1);
    }

    public List<Block> getChainFrom(int index) {
        if (index < 0 || index > chain.size() - 1) {
            return Collections.emptyList();
        }

        return chain.subList(index, chain.size());
    }

    public boolean addBlock(Block newBlock, boolean sendToCluster) {
        if (newBlock == null) {
            return false;
        }
        if (!Block.isValid(newBlock.getHash())) {
            return false;
        }
        String addMsg = port + " | Block added " + newBlock.getHash();
        if (sendToCluster) {
            for (String serverUrl : peerUrls) {
                try {
                    HttpClient client = HttpClient.newHttpClient();
                    HttpRequest request = HttpRequest.newBuilder()
                            .uri(URI.create(serverUrl + "/addblock"))
                            .header("Content-Type", "application/json")
                            .POST(HttpRequest.BodyPublishers.ofString(newBlock.toJson()))
                            .build();

                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    if (response.statusCode() != 200) {
                        System.out.println(port + " | Failed to add block to server " + serverUrl);
                    } else {
                        addMsg = port + " | " + response.body();
                    }
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                    System.out.println(port + " | Failed to connect to server " + serverUrl);
                }
            }
        }
        this.chain.add(newBlock);
        System.out.println(addMsg);

        isChainValid();

        return true;
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

    public void syncWithPeers() {
        for (String peerUrl : peerUrls) {
            try {
                int idx = getLatestBlock() == null ? 0 : getLatestBlock().getIndex();
                URL url = new URL(peerUrl + "/blocks?after=" + idx);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");

                if (con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String response = in.readLine();
                    in.close();

                    JSONArray blockArray = new JSONArray(response);
                    for (int i = 0; i < blockArray.length(); i++) {
                        JSONObject blockObj = blockArray.getJSONObject(i);
                        int index = blockObj.getInt("index");
                        String previousHash = blockObj.getString("previousHash");
                        String data = blockObj.getString("data");
                        int nonce = blockObj.getInt("nonce");
                        String hash = blockObj.getString("hash");

                        Block block = new Block(index, previousHash, data,nonce, hash);

                        if (addBlock(block, false)) {
                            System.out.println("Added block from peer: " + peerUrl + ", hash: " + block.getHash());
                        }
                    }
                } else {
                    System.out.println("Failed to fetch blocks from peer: " + peerUrl);
                }
            } catch (Exception e) {
                System.out.println("Failed to sync with peer: " + peerUrl);
                e.printStackTrace();
            }
        }
    }
}
