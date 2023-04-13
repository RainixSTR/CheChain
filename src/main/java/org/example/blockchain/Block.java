package org.example.blockchain;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

public class Block implements Serializable {

    public static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    static {
        OBJECT_MAPPER.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
    }

    @JsonProperty("index")
    private int index;

    @JsonProperty("previousHash")
    private String prevHash;

    @JsonProperty("data")
    private String data;
    @JsonProperty("nonce")
    private int nonce;
    @JsonProperty("hash")
    private String hash;

    public Block() {
    }

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

    public void setIndex(int index) {
        this.index = index;
    }

    public void setPrevHash(String prevHash) {
        this.prevHash = prevHash;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setNonce(int nonce) {
        this.nonce = nonce;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    private String calculateHash() {
        String calculatedHash;
        do {
            String input = index + prevHash + data + nonce;
            calculatedHash = DigestUtils.sha256Hex(input);
            nonce++;
        } while (!isValid(calculatedHash));
        return calculatedHash;
    }

    public static Block fromJson(InputStream stream) {
        try {
            return OBJECT_MAPPER.readValue(stream, Block.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String toJson() {
        try {
            return OBJECT_MAPPER.writeValueAsString(this);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean isValid(String curHash) {
        return curHash.endsWith("0000");
    }

}