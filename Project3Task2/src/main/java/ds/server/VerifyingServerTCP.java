/**
 * Author: JYOTI GIRDHARI KHANCHANDANI (JKHANCHA)
 * Last Modified: October 31, 2024
 *
 *This server class acts as a TCP server that verifies client requests using RSA signatures before
 * * processing them. It uses the `RealBlockchainServer` as a backend for blockchain operations.
 * * This server ensures that only valid, signed requests are accepted, thereby enhancing the security
 * * of blockchain interactions in a TCP-based environment.
 *
 *NOTE : This code is generated with assisstance from Perplexity AI.
 *
 */

package ds.server;

import ds.message.RequestMessage;
import ds.message.ResponseMessage;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class VerifyingServerTCP implements IBlockchainServer {

    private RealBlockchainServer realServer;
    private RSASignatureUtils rsaVerifier;
    /**
     * Constructor initializes the RealBlockchainServer and RSASignatureUtils.
     */
    public VerifyingServerTCP() {
        this.realServer = new RealBlockchainServer();
        this.rsaVerifier = new RSASignatureUtils();
    }
    /**
     * Processes a client request by verifying the RSA signature first.
     * If verification is successful, it forwards the request to the real server.
     *
     * @param request The request message containing the operation and signature.
     * @return ResponseMessage with the operation result, or an error message if verification fails.
     * @throws Exception if signature verification or request processing encounters issues.
     */
    @Override
    public ResponseMessage processRequest(RequestMessage request) throws Exception {
        if (verifyRequest(request)) {
            return realServer.processRequest(request);
        } else {
            return new ResponseMessage("ERROR", "Invalid request signature", 0);
        }
    }

    /**
     * Verifies the RSA signature on the request.
     *
     * @param request The request message with data, signature, and public key components.
     * @return true if the signature is valid, false otherwise.
     * @throws Exception if an error occurs in verification.
     */
    private boolean verifyRequest(RequestMessage request) throws Exception {
        // Verify using received keys and signature
        return rsaVerifier.verify(
                request.getData() + request.getDifficulty(),
                request.getSignature(),
                request.getPublicKeyE(),
                request.getPublicKeyN()
        );
    }

    private static final int PORT = 7777;
/**
        * Main method to start the TCP server, which listens for client connections on a predefined port.
            * It verifies and processes incoming requests in a continuous loop.
            *
            * @param args Not used.
    */
    public static void main(String[] args) {
        IBlockchainServer serverProxy = new VerifyingServerTCP();

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

                        // Deserialize JSON into RequestMessage object
                        RequestMessage request = RequestMessage.fromJson(requestJson);

                        // Check if we received all fields correctly
                        System.out.println("Received Public Key E: " + request.getPublicKeyE());
                        System.out.println("Received Public Key N: " + request.getPublicKeyN());

                        ResponseMessage response;

                        try {
                            response = serverProxy.processRequest(request);
                        } catch (Exception e) {
                            response = new ResponseMessage("ERROR", "An error occurred: " + e.getMessage(), 0);
                        }

                        // Send response back to client
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
}
