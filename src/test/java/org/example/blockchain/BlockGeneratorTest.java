package org.example.blockchain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class BlockGeneratorTest {
    private Blockchain blockchain;

    @BeforeEach
    void setUp() {
        blockchain = new Blockchain(8080, Collections.emptyList(), false);
    }

    @Test
    void generateDataReturnsRandomStringOf256Characters() {
        BlockGenerator blockGenerator = new BlockGenerator(blockchain);

        String generatedData = blockGenerator.generateData();

        assertNotNull(generatedData, "Generated data should not be null");
        assertEquals(
                256,
                generatedData.length(),
                "Generated data should have a length of 256 characters");

        for (char c : generatedData.toCharArray()) {
            assertTrue(
                    Character.isLowerCase(c),
                    "Generated data should only contain lowercase characters");
        }
    }
}