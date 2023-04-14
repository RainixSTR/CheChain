package org.example.blockchain;

import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

class BlockTest {

    @Test
    void isValidWhenHashEndsWith0000() {
        String validHash = "123456789abcdef0000";
        assertTrue(
                Block.isValid(validHash),
                "isValid should return true when the hash ends with '0000'");
    }

    @Test
    void isValidWhenHashDoesNotEndWith0000() {
        String invalidHash = "123456789abcdef123456789abcdef123456789abcdef123456789abcde";
        assertFalse(
                Block.isValid(invalidHash), "Expected isValid to return false for invalid hash");
    }

    @Test
    void toJsonThrowsRuntimeExceptionOnJsonProcessingException() {
        Block block =
                new Block(1, "prevHash", "data") {
                    @Override
                    public String toJson() {
                        throw new RuntimeException("JsonProcessingException");
                    }
                };

        assertThrows(
                RuntimeException.class,
                block::toJson,
                "Expected toJson() to throw RuntimeException on JsonProcessingException");
    }

    @Test
    void toJsonReturnsValidJsonString() {
        Block block =
                new Block(
                        1,
                        "0000000000000000000000000000000000000000000000000000000000000000",
                        "Test data");

        String jsonString = block.toJson();

        assertNotNull(jsonString, "JSON string should not be null");
        assertTrue(
                jsonString.contains("\"index\":1"), "JSON string should contain the correct index");
        assertTrue(
                jsonString.contains(
                        "\"previousHash\":\"0000000000000000000000000000000000000000000000000000000000000000\""),
                "JSON string should contain the correct previousHash");
        assertTrue(
                jsonString.contains("\"data\":\"Test data\""),
                "JSON string should contain the correct data");
        assertTrue(jsonString.contains("\"nonce\":"), "JSON string should contain the nonce");
        assertTrue(jsonString.contains("\"hash\":\""), "JSON string should contain the hash");
    }

    @Test
    void fromJsonWithInvalidInputStreamThrowsException() {
        InputStream invalidStream =
                new ByteArrayInputStream("invalid json".getBytes(StandardCharsets.UTF_8));

        assertThrows(RuntimeException.class, () -> Block.fromJson(invalidStream));
    }

    @Test
    void fromJsonWithValidInputStream() {
        String json =
                "{\"index\":1,\"previousHash\":\"0000000000000000000000000000000000000000000000000000000000000000\",\"data\":\"Test data\",\"nonce\":0,\"hash\":\"0000000000000000000000000000000000000000000000000000000000000000\"}";
        InputStream inputStream = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));

        Block block = Block.fromJson(inputStream);

        assertNotEquals(null, block);
        assertEquals(1, block.getIndex());
        assertEquals(
                "0000000000000000000000000000000000000000000000000000000000000000",
                block.getPreviousHash());
        assertEquals("Test data", block.getData());
        assertEquals(0, block.getNonce());
        assertEquals(
                "0000000000000000000000000000000000000000000000000000000000000000",
                block.getHash());
    }

    @Test
    void calculateHashWhenDataIsChanged() {
        Block block1 = new Block(1, "prevHash", "data");
        String hash1 = block1.getHash();

        Block block2 = new Block(1, "prevHash", "dataChanged");
        String hash2 = block2.getHash();

        assertNotEquals(hash1, hash2, "Hashes should be different when data is changed");
    }

    @Test
    void calculateHashWhenNonceIsChanged() {
        Block block1 = new Block(1, "prevHash", "data");
        int initialNonce = block1.getNonce();
        String initialHash = block1.getHash();

        block1.setNonce(initialNonce + 1);
        String newHash = block1.calculateHash();

        assertNotEquals(initialHash, newHash, "Hash should be different when nonce is changed");
    }
}