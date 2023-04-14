package org.example.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import org.example.blockchain.Block;
import org.example.blockchain.Blockchain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.*;

class BlocksHandlerTest {

    private Blockchain blockchain;

    private BlockServer.BlocksHandler blocksHandler;
    private String blockchainJson;
    private String response;

    @BeforeEach
    void setUp() throws JsonProcessingException {
        this.blockchain = spy(new Blockchain(8080, Collections.emptyList(), false));

        blocksHandler = new BlockServer.BlocksHandler(blockchain);

        Block block1 = new Block(1, "stub", "data1", 0, "hash1");
        Block block2 = new Block(2, "hash1", "data2", 0, "hash2");
        List<Block> blocks = Arrays.asList(block1, block2);

        when(blockchain.getChain()).thenReturn(blocks);

        ObjectMapper objectMapper = new ObjectMapper();
        blockchainJson = objectMapper.writeValueAsString(blocks);

        List<Block> blocksFromIndex = Collections.singletonList(block2);
        response = objectMapper.writeValueAsString(blocksFromIndex);

        when(blockchain.getChainFrom(1)).thenReturn(blocksFromIndex);
    }

    @Test
    void handleReturns200StatusCodeAndCorrectResponseLength() throws IOException {
        HttpExchange exchange = mock(HttpExchange.class);
        when(exchange.getRequestMethod()).thenReturn("GET");
        when(exchange.getRequestURI()).thenReturn(URI.create("/blocks"));
        OutputStream outputStream = new ByteArrayOutputStream();
        when(exchange.getResponseBody()).thenReturn(outputStream);

        blocksHandler.handle(exchange);

        verify(exchange).sendResponseHeaders(200, blockchainJson.length());
        assertEquals(blockchainJson, outputStream.toString());
    }

    @Test
    void handleWhenAfterQueryParamReturnsBlockchainFromIndex() {
        try {
            HttpExchange exchange = mock(HttpExchange.class);
            when(exchange.getRequestMethod()).thenReturn("GET");
            when(exchange.getRequestURI())
                    .thenReturn(new URI("http://localhost:8080/blocks?after=1"));

            OutputStream outputStream = new ByteArrayOutputStream();
            when(exchange.getResponseBody()).thenReturn(outputStream);

            blocksHandler.handle(exchange);

            verify(exchange).sendResponseHeaders(200, response.length());
            assertEquals(response, outputStream.toString());
        } catch (IOException | URISyntaxException e) {
            fail("Exception occurred during test execution: " + e.getMessage());
        }
    }

    @Test
    void handleWhenNoQueryParamReturnsEntireBlockchain() {
        try {
            HttpExchange exchange = mock(HttpExchange.class);
            when(exchange.getRequestMethod()).thenReturn("GET");
            when(exchange.getRequestURI()).thenReturn(new URI("/blocks"));
            OutputStream os = new ByteArrayOutputStream();
            when(exchange.getResponseBody()).thenReturn(os);

            blocksHandler.handle(exchange);

            verify(exchange).sendResponseHeaders(200, blockchainJson.length());
            assertEquals(blockchainJson, os.toString());
        } catch (IOException | URISyntaxException e) {
            fail("Exception occurred during test execution: " + e.getMessage());
        }
    }
}
