import java.io.*;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.Scanner;

public class NeuralNetworkClient {
    private static Scanner scanner = new Scanner(System.in);
    private static InetAddress serverAddress;
    private static int serverPort;
    private static DatagramSocket aSocket = null;

    // Operation codes
    private static final int DISPLAY_TRUTH_TABLE = 0;
    private static final int PROVIDE_INPUTS = 1;
    private static final int SINGLE_TRAINING_STEP = 2;
    private static final int MULTIPLE_TRAINING_STEPS = 3;
    private static final int TEST_INPUTS = 4;
    private static final int EXIT = 5;

    public static void main(String[] args) {
        try {
            System.out.println("The Neural Network client is running.");

            serverAddress = InetAddress.getByName("localhost");

            System.out.print("Please enter server port: ");
            serverPort = scanner.nextInt();

            int userSelection = menu();

            while (userSelection != EXIT) {
                String result;
                switch (userSelection) {
                    case DISPLAY_TRUTH_TABLE:
                        result = sendRequest(DISPLAY_TRUTH_TABLE, new double[0]);
                        System.out.println(result);
                        break;
                    case PROVIDE_INPUTS:
                        System.out.println("Enter the four results of a 4 by 2 truth table. Each value should be 0 or 1.");
                        double[] inputs = new double[4];
                        for (int i = 0; i < 4; i++) {
                            inputs[i] = scanner.nextDouble();
                        }
                        result = sendRequest(PROVIDE_INPUTS, inputs);
                        System.out.println(result);
                        break;
                    case SINGLE_TRAINING_STEP:
                        result = sendRequest(SINGLE_TRAINING_STEP, new double[0]);
                        System.out.println(result);
                        break;
                    case MULTIPLE_TRAINING_STEPS:
                        System.out.println("Enter the number of training steps:");
                        int n = scanner.nextInt();
                        result = sendRequest(MULTIPLE_TRAINING_STEPS, new double[]{n});
                        System.out.println(result);
                        break;
                    case TEST_INPUTS:
                        System.out.println("Enter a pair of doubles from a row of the truth table. These are domain values.");
                        double input0 = scanner.nextDouble();
                        double input1 = scanner.nextDouble();
                        result = sendRequest(TEST_INPUTS, new double[]{input0, input1});
                        System.out.println(result);
                        break;
                    default:
                        System.out.println("Error in input. Please choose an integer from the main menu.");
                        break;
                }
                userSelection = menu();
            }

            sendRequest(EXIT, new double[0]);
            System.out.println("Client side quitting. The Neural Network server is still running.");

        } catch (IOException e) {
            System.out.println("IO Exception: " + e.getMessage());
        } finally {
            if (aSocket != null) aSocket.close();
        }
    }

    private static String sendRequest(int operation, double[] values) throws IOException {
        if (aSocket == null) {
            aSocket = new DatagramSocket();
        }

        ByteBuffer buffer = ByteBuffer.allocate(4 + 8 * values.length);
        buffer.putInt(operation);
        for (double value : values) {
            buffer.putDouble(value);
        }

        byte[] message = buffer.array();

        DatagramPacket request = new DatagramPacket(message, message.length, serverAddress, serverPort);
        aSocket.send(request);

        byte[] receiveBuffer = new byte[1000];
        DatagramPacket reply = new DatagramPacket(receiveBuffer, receiveBuffer.length);
        aSocket.receive(reply);

        return new String(reply.getData(), 0, reply.getLength()).trim();
    }

    public static int menu() {
        System.out.println("\nUsing a neural network to learn a truth table.\nMain Menu");
        System.out.println("0. Display the current truth table.");
        System.out.println("1. Provide four inputs for the range of the two input truth table and build a new neural network. To test XOR, enter 0  1  1  0.");
        System.out.println("2. Perform a single training step.");
        System.out.println("3. Perform n training steps. 10000 is a typical value for n.");
        System.out.println("4. Test with a pair of inputs.");
        System.out.println("5. Exit program.");
        return scanner.nextInt();
    }
}
