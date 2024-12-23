/**

Author: JYOTI GIRDHARI KHANCHANDANI (JKHANCHA)
        Last Modified: October 31, 2024
        This program implements the server side of a blockchain simulator using TCP.
        It listens for client connections, processes blockchain-related requests,
        and sends responses back to the client.
        Key features:
        Listens on port 7777 for client connections
        Uses a RealBlockchainService instance to perform actual blockchain operations
        Processes JSON-formatted request messages
        Sends JSON-formatted response messages back to clients
        Supports multiple client connections (one at a time in the current implementation)
        Measures and includes execution time for each operation in the response
        The server demonstrates proper error handling and uses try-with-resources
        for managing network resources. It acts as the real subject in the Proxy pattern,
        performing the actual blockchain operations requested by clients through the proxy.
        */


package ds.server;

import java.net.*;
import java.io.*;

import ds.client.BlockchainService;
import ds.message.RequestMessage;
import ds.message.ResponseMessage;



public class ServerTCP {
    private static final int PORT = 7777;
    private static BlockchainService blockchainService;

    public static void main(String[] args) {
        blockchainService = new RealBlockchainService();

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Blockchain server running on port " + PORT);

            while (true) {
                try (Socket clientSocket = serverSocket.accept();
                     PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
                     BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()))) {

                    System.out.println("We have a visitor");
                    String requestJson;
                    while ((requestJson = in.readLine()) != null) {
                        System.out.println("THE JSON REQUEST MESSAGE IS: " + requestJson);
                        RequestMessage request = RequestMessage.fromJson(requestJson);
                        ResponseMessage response = processRequest(request);
                        String responseJson = response.toJson();
                        System.out.println("THE JSON RESPONSE MESSAGE IS: " + responseJson);
                        out.println(responseJson);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static ResponseMessage processRequest(RequestMessage request) {
        long startTime = System.currentTimeMillis();
        String status = "SUCCESS";
        String data;

        switch (request.getOperation()) {
            case 0:
                data = blockchainService.getBlockchainStatus();
                break;
            case 1:
                data = blockchainService.addTransaction(request.getData(), Integer.parseInt(request.getDifficulty()));
                break;
            case 2:
                data = blockchainService.verifyBlockchain();
                break;
            case 3:
                data = blockchainService.viewBlockchain();
                break;
            case 4:
                String[] corruptData = request.getData().split(",");
                int blockId = Integer.parseInt(corruptData[0]);
                String newData = corruptData[1];
                data = blockchainService.corruptChain(blockId, newData);
                break;
            case 5:
                data = blockchainService.repairChain();
                break;
            default:
                status = "ERROR";
                data = "Invalid operation";
        }

        long executionTime = System.currentTimeMillis() - startTime;
        return new ResponseMessage(status, data, executionTime);
    }
}
