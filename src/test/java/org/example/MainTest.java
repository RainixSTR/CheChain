package org.example;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class MainTest {

    @Test
    void getPortFromAddressWithInvalidPortFormatThrowsException() {
        String addressWithInvalidPort = "http://localhost:invalid_port";
        assertThrows(
                NumberFormatException.class, () -> Main.getPortFromAddress(addressWithInvalidPort));
    }

    @Test
    void getPortFromAddressWithoutProtocolPrefix() {
        String address = "localhost:8080";
        int expectedPort = 8080;

        int actualPort = Main.getPortFromAddress(address);

        assertEquals(expectedPort, actualPort, "The port number should be 8080");
    }

    @Test
    void getPortFromAddressWithProtocolPrefix() {
        String addressWithProtocolPrefix = "http://localhost:8080";
        int expectedPort = 8080;

        int actualPort = Main.getPortFromAddress(addressWithProtocolPrefix);

        assertEquals(
                expectedPort,
                actualPort,
                "The port number should be extracted correctly from the address with protocol prefix");
    }

    @Test
    void getNodeNameWhenNameArgumentIsProvidedWithoutValueThenThrowException() {
        String[] args = {"-name"};

        assertThrows(
                IllegalArgumentException.class, () -> Main.getNodeName(args), "Specify node name");
    }

    @Test
    void getNodeNameWhenNameArgumentIsNotProvidedThenThrowException() {
        String[] args = {"-port", "8080"};

        assertThrows(
                IllegalArgumentException.class, () -> Main.getNodeName(args), "Specify node name");
    }

    @Test
    void getNodeNameWhenNameArgumentIsProvided() {
        String[] args = {"-name", "node_2"};

        String nodeName = Main.getNodeName(args);

        assertEquals("node_2", nodeName);
    }
}