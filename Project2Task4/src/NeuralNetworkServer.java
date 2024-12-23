import com.google.gson.Gson;
import java.io.*;
import java.net.*;
import java.util.*;

public class NeuralNetworkServer {
    private NeuralNetwork neuralNetwork;
    private ArrayList<Double[][]> userTrainingSets;
    private Gson gson;

    public NeuralNetworkServer() {
        this.neuralNetwork = new NeuralNetwork(2, 5, 1, null, null, null, null);
        this.userTrainingSets = new ArrayList<>(Arrays.asList(
                new Double[][]{{0.0, 0.0}, {0.0}},
                new Double[][]{{0.0, 1.0}, {0.0}},
                new Double[][]{{1.0, 0.0}, {0.0}},
                new Double[][]{{1.0, 1.0}, {0.0}}
        ));
        this.gson = new Gson();
    }

    public String handleRequest(String jsonRequest) {
        Map<String, Object> request = gson.fromJson(jsonRequest, Map.class);
        String requestType = (String) request.get("request");

        switch (requestType) {
            case "getCurrentRange":
                return getCurrentRange();
            case "setCurrentRange":
                return setCurrentRange(request);
            case "train":
                return train(request);
            case "test":
                return test(request);
            default:
                return "{\"response\":\"error\", \"message\":\"Invalid request\"}";
        }
    }

    private String getCurrentRange() {
        Map<String, Object> response = new HashMap<>();
        response.put("response", "getCurrentRange");
        response.put("status", "OK");
        for (int i = 0; i < 4; i++) {
            response.put("val" + (i+1), userTrainingSets.get(i)[1][0]);
        }
        return gson.toJson(response);
    }

    private String setCurrentRange(Map<String, Object> request) {
        for (int i = 0; i < 4; i++) {
            userTrainingSets.get(i)[1][0] = ((Number) request.get("val" + (i+1))).doubleValue();
        }
        neuralNetwork = new NeuralNetwork(2, 5, 1, null, null, null, null);
        return "{\"response\":\"setCurrentRange\", \"status\":\"OK\"}";
    }

    private String train(Map<String, Object> request) {
        int iterations = ((Number) request.get("iterations")).intValue();
        Random rand = new Random();
        for (int i = 0; i < iterations; i++) {
            int random_choice = rand.nextInt(4);
            List<Double> userTrainingInputs = Arrays.asList(userTrainingSets.get(random_choice)[0]);
            List<Double> userTrainingOutputs = Arrays.asList(userTrainingSets.get(random_choice)[1]);
            neuralNetwork.train(userTrainingInputs, userTrainingOutputs);
        }
        double totalError = neuralNetwork.calculateTotalError(userTrainingSets);
        return String.format("{\"response\":\"train\", \"status\":\"OK\", \"val1\":%f}", totalError);
    }

    private String test(Map<String, Object> request) {
        double input0 = ((Number) request.get("val1")).doubleValue();
        double input1 = ((Number) request.get("val2")).doubleValue();
        List<Double> testUserInputs = Arrays.asList(input0, input1);
        List<Double> userOutput = neuralNetwork.feedForward(testUserInputs);
        return String.format("{\"response\":\"test\", \"status\":\"OK\", \"val1\":%f}", userOutput.get(0));
    }

    public static void main(String[] args) throws IOException {
        NeuralNetworkServer server = new NeuralNetworkServer();
        ServerSocket serverSocket = new ServerSocket(8000);
        System.out.println("Server is running on port 8000");

        while (true) {
            Socket clientSocket = serverSocket.accept();
            new Thread(() -> handleClient(clientSocket, server)).start();
        }
    }

    private static void handleClient(Socket clientSocket, NeuralNetworkServer server) {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                String response = server.handleRequest(inputLine);
                out.println(response);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port 8000 or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}

