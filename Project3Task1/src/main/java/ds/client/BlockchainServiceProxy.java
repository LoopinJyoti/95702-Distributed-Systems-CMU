/**
 * Author: JYOTI GIRDHARI KHANCHANDANI (JKHANCHA)
 * Last Modified: October 31, 2024
 *
 * This class implements the BlockchainService interface and acts as a proxy
 * for the remote blockchain service. It handles network communication with the server,
 * encapsulating the complexity of network operations from the client.
 *
 * NOTE : This code is generated with assisstance from Perplexity AI.
 */
package ds.client;

import ds.message.RequestMessage;
import ds.message.ResponseMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class BlockchainServiceProxy implements BlockchainService, AutoCloseable {
    // Constants for server connection
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 7777;

    // Network communication objects
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    /**
     * Constructor: Initializes the socket connection and I/O streams
     * @throws IOException if connection fails
     */
    public BlockchainServiceProxy() throws IOException {
        this.socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
        this.out = new PrintWriter(socket.getOutputStream(), true);
        this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    /**
     * Sends a request to the server and returns the response
     * @param operation The operation code
     * @param data The data for the operation
     * @param difficulty The difficulty level (if applicable)
     * @return The server's response as a String
     * @throws IOException if communication fails
     */
    private String sendRequest(int operation, String data, String difficulty) throws IOException {
        // Create and send request
        RequestMessage request = new RequestMessage(operation, data, difficulty);
        out.println(request.toJson());

        // Receive and parse response
        String responseJson = in.readLine();
        ResponseMessage response = ResponseMessage.fromJson(responseJson);
        return response.getData();
    }

    /**
     * Retrieves the current status of the blockchain
     * @return Status string or error message
     */
    @Override
    public String getBlockchainStatus() {
        try {
            return sendRequest(0, "", "");
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Adds a new transaction to the blockchain
     * @param data The transaction data
     * @param difficulty The mining difficulty
     * @return Confirmation message or error
     */
    @Override
    public String addTransaction(String data, int difficulty) {
        try {
            return sendRequest(1, data, String.valueOf(difficulty));
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Verifies the integrity of the blockchain
     * @return Verification result or error message
     */
    @Override
    public String verifyBlockchain() {
        try {
            return sendRequest(2, "", "");
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Retrieves the entire blockchain for viewing
     * @return Blockchain data or error message
     */
    @Override
    public String viewBlockchain() {
        try {
            return sendRequest(3, "", "");
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Corrupts a specific block in the chain (for testing)
     * @param blockId The ID of the block to corrupt
     * @param newData The new data to replace in the block
     * @return Confirmation message or error
     */
    @Override
    public String corruptChain(int blockId, String newData) {
        try {
            return sendRequest(4, blockId + "," + newData, "");
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Attempts to repair the blockchain after corruption
     * @return Repair result or error message
     */
    @Override
    public String repairChain() {
        try {
            return sendRequest(5, "", "");
        } catch (IOException e) {
            return "Error: " + e.getMessage();
        }
    }

    /**
     * Closes all network resources
     * @throws IOException if closing resources fails
     */
    @Override
    public void close() throws IOException {
        if (in != null) in.close();
        if (out != null) out.close();
        if (socket != null && !socket.isClosed()) socket.close();
    }
}
