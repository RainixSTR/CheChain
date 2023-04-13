package org.example.http;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import org.example.blockchain.Block;
import org.example.blockchain.Blockchain;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.List;

public class BlockServer {
    private final Blockchain blockchain;
    private final int port;

    public BlockServer(Blockchain blockchain, int port) {
        this.blockchain = blockchain;
        this.port = port;
    }

    public void start() throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/blocks", new BlocksHandler());
        server.createContext("/addblock", new AddBlockHandler());
        server.start();
    }

    private class BlocksHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";
            ObjectMapper objectMapper = new ObjectMapper();

            if ("GET".equals(exchange.getRequestMethod())) {
                List<Block> blocks;
                String afterParam = exchange.getRequestURI().getQuery();
                if (afterParam == null) {
                    blocks = blockchain.getChain();
                } else {
                    int afterIndex = Integer.parseInt(afterParam.substring(6)); // ?after
                    blocks = blockchain.getChainAfter(afterIndex);
                }
                response = objectMapper.writeValueAsString(blocks);
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    private class AddBlockHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String response = "";

            if ("POST".equals(exchange.getRequestMethod())) {
                Block newBlock = Block.fromJson(exchange.getRequestBody());
                if (blockchain.addBlock(newBlock, false)) {
                    response = "Received Block added: " + newBlock.getHash();
                } else {
                    response = "Received Block not added";
                }
            }

            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }
}