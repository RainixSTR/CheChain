package org.example;

import org.example.blockchain.BlockGenerator;
import org.example.blockchain.Blockchain;
import org.example.http.BlockServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class Main {

    private static final String GENESIS_NODE_NAME = "node_1";

    public static final String PROTOCOL_PREFIX = "http://";

    public static void main(String[] args) throws IOException {
        String nodeName = getNodeName(args);

        Properties properties = new Properties();
        InputStream resourceAsStream = Main.class.getClassLoader().getResourceAsStream("config.properties");
        properties.load(resourceAsStream);

        String address = properties.getProperty(nodeName);
        if (address == null || address.isBlank()) {
            throw new IllegalStateException("Specify node hostnames");
        }

        int port = getPortFromAddress(address);

        List<String> peers = new ArrayList<>();
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            String propertyNodeName = (String) entry.getKey();
            String propertyNodeAddress = (String) entry.getValue();
            if (nodeName.equals(propertyNodeName)) {
                continue;
            }
            peers.add(propertyNodeAddress);
        }

        startNode(peers, port, nodeName.equals(GENESIS_NODE_NAME));
    }

    public static String getNodeName(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String arg = args[i];
            if (arg.equals("-name")) {
                if (i + 1 >= args.length) {
                    throw new IllegalArgumentException("Specify node name");
                }
                return args[i + 1];
            }
        }
        throw new IllegalArgumentException("Specify node name");
    }

    public static int getPortFromAddress(String address) {
        String normalizedAddress = address;
        if (address.startsWith(PROTOCOL_PREFIX)) {
            normalizedAddress = address.replace(PROTOCOL_PREFIX, "");
        }
        String[] split = normalizedAddress.split(":");
        return Integer.parseInt(split[1]);
    }


    private static void startNode(List<String> peers, int port, boolean generateGenesis) throws IOException {
        Blockchain blockchain = new Blockchain(port, peers, generateGenesis);
        BlockGenerator blockGenerator = new BlockGenerator(blockchain);
        BlockServer blockServer = new BlockServer(blockchain, port);
        blockServer.start();
        blockGenerator.startGenerating();
    }
}