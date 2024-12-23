package ds.client;

import ds.message.RequestMessage;
import ds.message.ResponseMessage;
import java.util.Scanner;

public interface IBlockchainClient {
    void displayKeys();  // This is the method that needs to be implemented
    void displayMenu();
    RequestMessage createRequest(int choice, Scanner scanner) throws Exception;
    void processResponse(ResponseMessage response);
    void run() throws Exception;
}
