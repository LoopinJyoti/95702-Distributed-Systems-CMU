/*
 * CLASS LEVEL DOCUMENTATION
 *
 * Package: ds
 *
 * AUTHOR: JYOTI KHANCHANDANI
 *
 * This code was developed with initial assistance and input from a generative AI tool named Perplexity.
 *
 * The `NeuralNetworkClient` class is a client application that communicates with a remote neural network server using UDP.
 * This client program provides an interactive menu that allows the user to perform various operations on the neural network
 * hosted on the server. The operations include displaying the current truth table, providing new inputs for the truth table,
 * performing a single training step, performing multiple training steps, testing inputs, and exiting the program.
 *
 * The client uses a structured format for communication by creating instances of the `Request` class, which are then serialized
 * into JSON using the Gson library. The server responds back with `Response` objects, which the client deserializes and displays.
 * Each menu option corresponds to an operation code that the server can interpret. The client and server interaction illustrates
 * a lightweight communication mechanism using UDP datagrams, making this a practical example of how distributed systems can
 * handle neural network training and testing over a network.
 *
 * Citation: Initial development of this code was influenced by generative AI guidance from Perplexity.
 */

package ds; // Specify the package for this class.

import com.google.gson.Gson; // Import for Gson library to handle JSON serialization and deserialization.
import java.io.*; // Import for handling I/O operations.
import java.net.*; // Import for networking classes.
import java.util.*; // Import for using List, ArrayList, and Scanner utilities.

/**
 * The `NeuralNetworkClient` class is a menu-driven client application that interacts with a neural network server using UDP.
 * It provides options for managing a truth table, training a neural network, and testing inputs using a lightweight UDP-based protocol.
 */
public class NeuralNetworkClient { // Class definition for `NeuralNetworkClient`.
    private static Scanner scanner = new Scanner(System.in); // Scanner for reading user input from the console.
    private static InetAddress serverAddress; // Variable to store the server's IP address.
    private static int serverPort; // Variable to store the server's port number.
    private static DatagramSocket aSocket = null; // Socket to handle UDP communication.
    private static Gson gson = new Gson(); // Gson object to handle JSON conversions.

    // Operation codes used to identify different client requests.
    private static final int DISPLAY_TRUTH_TABLE = 0; // Operation code for displaying the truth table.
    private static final int PROVIDE_INPUTS = 1; // Operation code for providing new truth table inputs.
    private static final int SINGLE_TRAINING_STEP = 2; // Operation code for a single training step.
    private static final int MULTIPLE_TRAINING_STEPS = 3; // Operation code for multiple training steps.
    private static final int TEST_INPUTS = 4; // Operation code for testing specific inputs.
    private static final int EXIT = 5; // Operation code to exit the program.

    /**
     * Main method for the Neural Network client application.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        try { // Start of try block to handle I/O exceptions.
            System.out.println("The Neural Network client is running."); // Print a message indicating that the client is running.

            serverAddress = InetAddress.getByName("localhost"); // Resolve the server's IP address as "localhost".

            System.out.print("Please enter server port: "); // Prompt the user to enter the server's port number.
            serverPort = scanner.nextInt(); // Read and store the server port from user input.

            int userSelection = menu(); // Display the menu and get the user's selection.

            while (userSelection != EXIT) { // Continue processing until the user selects to exit.
                switch (userSelection) { // Handle the user's selection.
                    case DISPLAY_TRUTH_TABLE: // Option to display the current truth table.
                        displayTruthTable();
                        break;
                    case PROVIDE_INPUTS: // Option to provide new truth table inputs.
                        provideTruthTableInputs();
                        break;
                    case SINGLE_TRAINING_STEP: // Option to perform a single training step.
                        performSingleTrainingStep();
                        break;
                    case MULTIPLE_TRAINING_STEPS: // Option to perform multiple training steps.
                        performMultipleTrainingSteps();
                        break;
                    case TEST_INPUTS: // Option to test specific inputs.
                        testInputs();
                        break;
                    default: // Handle invalid inputs.
                        System.out.println("Error in input. Please choose an integer from the main menu.");
                        break;
                }
                userSelection = menu(); // Redisplay the menu for the next operation.
            }

            exitProgram(); // Notify the server of program exit.
            System.out.println("Client side quitting. The Neural Network server is still running."); // Inform the user of client exit.

        } catch (IOException e) { // Catch block for I/O exceptions.
            System.out.println("IO Exception: " + e.getMessage()); // Print the exception message.
        } finally { // Ensure the following block is executed whether or not an exception occurs.
            if (aSocket != null) aSocket.close(); // Close the socket if it was opened.
        }
    }

    /**
     * Displays the current truth table by sending a request to the server.
     *
     * @throws IOException If there is an error in communication.
     */
    private static void displayTruthTable() throws IOException {
        Request req = new Request(DISPLAY_TRUTH_TABLE); // Create a request for displaying the truth table.
        Response resp = sendRequest(req); // Send the request and get the response.

        if ("OK".equals(resp.getStatus())) { // Check if the status is OK.
            System.out.println("Current truth table:"); // Print a header for the truth table.
            List<List<Integer>> truthTable = resp.getTruthTable(); // Retrieve the truth table from the response.
            for (List<Integer> row : truthTable) { // Iterate over the rows of the truth table.
                System.out.printf("%d %d %d\n", row.get(0), row.get(1), row.get(2)); // Print each row in a formatted manner.
            }
        } else {
            System.out.println("Error: " + resp.getMessage()); // Print any error message from the server.
        }
    }

    /**
     * Allows the user to provide inputs for a new truth table.
     *
     * @throws IOException If there is an error in communication.
     */
    private static void provideTruthTableInputs() throws IOException {
        System.out.println("Enter the four results of a 4 by 2 truth table. Each value should be 0 or 1."); // Instruction for the user.
        List<Integer> inputs = new ArrayList<>(); // List to store the inputs.
        for (int i = 0; i < 4; i++) { // Loop to read four inputs.
            inputs.add(scanner.nextInt()); // Add each input to the list.
        }
        Request req = new Request(PROVIDE_INPUTS, inputs, 0); // Create a request with the provided inputs.
        Response resp = sendRequest(req); // Send the request and get the response.
        System.out.println(resp.getMessage()); // Print the server's response message.
    }

    /**
     * Performs a single training step for the neural network.
     *
     * @throws IOException If there is an error in communication.
     */
    private static void performSingleTrainingStep() throws IOException {
        Request req = new Request(SINGLE_TRAINING_STEP); // Create a request for a single training step.
        Response resp = sendRequest(req); // Send the request and get the response.
        System.out.println("Training error: " + resp.getError()); // Print the training error.
    }

    /**
     * Performs multiple training steps for the neural network.
     *
     * @throws IOException If there is an error in communication.
     */
    private static void performMultipleTrainingSteps() throws IOException {
        System.out.println("Enter the number of training steps:"); // Prompt for the number of steps.
        int n = scanner.nextInt(); // Read the number of training steps.
        Request req = new Request(MULTIPLE_TRAINING_STEPS, null, n); // Create a request with the number of steps.
        Response resp = sendRequest(req); // Send the request and get the response.
        System.out.println("Training error after " + n + " steps: " + resp.getError()); // Print the final training error.
    }

    /**
     * Tests the neural network with a pair of inputs from the user.
     *
     * @throws IOException If there is an error in communication.
     */
    private static void testInputs() throws IOException {
        System.out.println("Enter a pair of doubles from a row of the truth table. These are domain values."); // Instruction for the user.
        List<Integer> testInputs = new ArrayList<>(); // List to store the test inputs.
        testInputs.add(scanner.nextInt()); // Read and add the first input.
        testInputs.add(scanner.nextInt()); // Read and add the second input.
        Request req = new Request(TEST_INPUTS, testInputs, 0); // Create a request with the test inputs.
        Response resp = sendRequest(req); // Send the request and get the response.
        System.out.println("Output: " + resp.getError()); // Print the output of the test.
    }

    /**
     * Exits the client program by notifying the server.
     *
     * @throws IOException If there is an error in communication.
     */
    private static void exitProgram() throws IOException {
        Request req = new Request(EXIT); // Create an exit request.
        sendRequest(req); // Send the exit request to the server.
    }

    /**
     * Sends a request to the server and receives the response.
     *
     * @param request The request object to send.
     * @return The response received from the server.
     * @throws IOException If there is an I/O error during communication.
     */
    private static Response sendRequest(Request request) throws IOException {
        if (aSocket == null) { // If the socket is not created yet.
            aSocket = new DatagramSocket(); // Create a new DatagramSocket for communication.
        }

        String jsonRequest = gson.toJson(request); // Convert the request object to a JSON string.
        byte[] message = jsonRequest.getBytes(); // Convert the JSON string to a byte array.

        DatagramPacket requestPacket = new DatagramPacket(message, message.length, serverAddress, serverPort); // Create a request packet.
        aSocket.send(requestPacket); // Send the request packet to the server.

        byte[] receiveBuffer = new byte[1000]; // Buffer to store the server's response.
        DatagramPacket reply = new DatagramPacket(receiveBuffer, receiveBuffer.length); // Prepare a packet to receive the reply.
        aSocket.receive(reply); // Receive the reply from the server.

        String jsonResponse = new String(reply.getData(), 0, reply.getLength()).trim(); // Convert the reply data to a string.
        return gson.fromJson(jsonResponse, Response.class); // Convert the JSON string back to a Response object and return.
    }

    /**
     * Displays the menu options and returns the user's selection.
     *
     * @return The user's menu selection.
     */
    public static int menu() {
        System.out.println("\nUsing a neural network to learn a truth table.\nMain Menu"); // Print menu header.
        System.out.println("0. Display the current truth table."); // Option 0: Display the truth table.
        System.out.println("1. Provide four inputs for the range of the two input truth table and build a new neural network. To test XOR, enter 0  1  1  0."); // Option 1: Provide inputs.
        System.out.println("2. Perform a single training step."); // Option 2: Single training step.
        System.out.println("3. Perform n training steps. 10000 is a typical value for n."); // Option 3: Multiple training steps.
        System.out.println("4. Test with a pair of inputs."); // Option 4: Test inputs.
        System.out.println("5. Exit program."); // Option 5: Exit program.
        return scanner.nextInt(); // Read and return the user's selection.
    }
}
