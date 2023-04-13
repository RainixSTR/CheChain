package org.example.blockchain;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Block {

    private static final MessageDigest md;

    static {
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    private final int index;
    private final String prevHash;
    private final String data;
    private int nonce;
    private final String hash;

    public Block(int index, String prevHash, String data) {
        this.index = index;
        this.prevHash = prevHash;
        this.data = data;
        this.nonce = 0;
        this.hash = calculateHash();
    }

    public Block(int index, String prevHash, String data, int nonce, String hash) {
        this.index = index;
        this.prevHash = prevHash;
        this.data = data;
        this.nonce = nonce;
        this.hash = hash;
    }

    public int getIndex() {
        return index;
    }

    public String getPreviousHash() {
        return prevHash;
    }

    public String getHash() {
        return hash;
    }

    public String getData() {
        return data;
    }

    public int getNonce() {
        return nonce;
    }

    private String calculateHash() {
        HexFormat hex = HexFormat.of();
        byte[] hashBytes;
        String calculatedHash;
        do {
            String input = index + prevHash + data + nonce;
            hashBytes = md.digest(input.getBytes());
            calculatedHash = hex.formatHex(hashBytes);
            nonce++;
        } while (!isValid(calculatedHash));
        return calculatedHash;
    }

    public static boolean isValid(String curHash) {
        return curHash.endsWith("0000");
    }
}