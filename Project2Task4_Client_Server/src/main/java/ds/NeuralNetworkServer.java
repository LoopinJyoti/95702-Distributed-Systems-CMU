/*
 * CLASS LEVEL DOCUMENTATION
 *
 * Package: ds
 *
 * AUTHOR: JYOTI KHANCHANDANI
 *
 * This code was developed with initial assistance and input from a generative AI tool named Perplexity.
 *
 * The `NeuralNetworkServer` class is a UDP-based server application designed to simulate a simple feed-forward neural network.
 * It communicates with clients to perform various operations, such as displaying the current truth table, updating truth table inputs,
 * performing single and multiple training steps, and testing inputs with the neural network. The server accepts client requests
 * formatted as JSON objects and responds accordingly based on the requested operation.
 *
 * This server program uses a two-layer neural network (hidden layer and output layer) and employs basic backpropagation techniques
 * to train the network on user-provided truth tables. The server is capable of handling operations like displaying the truth table,
 * updating inputs, performing training steps, and testing inputs. It uses the `Request` and `Response` classes to facilitate structured
 * communication between the client and server.
 *
 * Citation: Initial development of this code was influenced by generative AI guidance from Perplexity.
 */

package ds; // Specify the package for this class.

import com.google.gson.Gson; // Import for Gson library to handle JSON serialization and deserialization.
import java.io.*; // Import for handling I/O operations.
import java.net.*; // Import for networking classes.
import java.util.*; // Import for using List, ArrayList, and other utilities.

/**
 * The `NeuralNetworkServer` class represents a UDP server that simulates a neural network.
 * It can learn a truth table, train on inputs, and test user-provided values through client requests.
 */
public class NeuralNetworkServer { // Class definition for `NeuralNetworkServer`.
    private NeuralNetwork neuralNetwork; // Neural network object to perform training and testing.
    private ArrayList<Double[][]> userTrainingSets; // ArrayList to store training sets representing the truth table.
    private Random rand; // Random object used for selecting training sets randomly.
    private DatagramSocket socket; // Socket to handle UDP communication.
    private Gson gson; // Gson object to handle JSON conversions.

    /**
     * Constructor to initialize the Neural Network server.
     *
     * @param port The port number on which the server listens.
     * @throws IOException If there is an error creating the socket.
     */
    public NeuralNetworkServer(int port) throws IOException {
        initialization(); // Initialize neural network and training sets.
        this.socket = new DatagramSocket(port); // Create a socket bound to the specified port.
        this.gson = new Gson(); // Initialize Gson object for handling JSON serialization/deserialization.
    }

    /**
     * Initializes the neural network and training sets.
     */
    private void initialization() {
        this.neuralNetwork = new NeuralNetwork(2, 5, 1, null, null, null, null); // Initialize the neural network with a 2-5-1 structure.
        // Initialize the default truth table training set.
        this.userTrainingSets = new ArrayList<>(Arrays.asList(
                new Double[][]{{0.0, 0.0}, {0.0}}, // Row 1: Inputs [0, 0] -> Output [0]
                new Double[][]{{0.0, 1.0}, {1.0}}, // Row 2: Inputs [0, 1] -> Output [0]
                new Double[][]{{1.0, 0.0}, {1.0}}, // Row 3: Inputs [1, 0] -> Output [0]
                new Double[][]{{1.0, 1.0}, {0.0}}  // Row 4: Inputs [1, 1] -> Output [0]
        ));
        this.rand = new Random(); // Initialize the random object for random training selection.
    }

    /**
     * Starts the server to listen for incoming client requests.
     *
     * @throws IOException If there is an error receiving or sending packets.
     */
    public void start() throws IOException {
        byte[] receiveData = new byte[1024]; // Buffer to store incoming data.
        System.out.println("Server is running on port " + socket.getLocalPort()); // Notify that the server is running.

        while (true) { // Continuous loop to listen for client requests.
            DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length); // Prepare a packet to receive data.
            socket.receive(receivePacket); // Receive a request packet from a client.

            String jsonRequest = new String(receivePacket.getData(), 0, receivePacket.getLength()); // Convert received data to a string.
            System.out.println("Received JSON request: " + jsonRequest); // Print the received request.

            String jsonResponse = handleRequest(jsonRequest); // Handle the request and get the response as a JSON string.
            System.out.println("Sending JSON response: " + jsonResponse); // Print the response to be sent.

            byte[] sendData = jsonResponse.getBytes(); // Convert the response string to a byte array.
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, receivePacket.getAddress(), receivePacket.getPort()); // Create a packet to send.
            socket.send(sendPacket); // Send the response packet back to the client.

            if (jsonResponse.contains("Server shutting down...")) { // Check for server shutdown condition.
                break; // Exit the loop if the shutdown condition is met.
            }
        }
    }

    /**
     * Handles the client request by parsing the JSON request and performing the appropriate operation.
     *
     * @param jsonRequest The request in JSON format.
     * @return The response in JSON format.
     */
    private String handleRequest(String jsonRequest) {
        Request req = gson.fromJson(jsonRequest, Request.class); // Convert the JSON string to a `Request` object.
        int operation = req.getOption(); // Get the operation code from the request.
        Response resp = new Response("OK"); // Create a default response with status "OK".

        switch (operation) { // Switch statement to handle different operations.
            case 0: // DISPLAY_TRUTH_TABLE operation.
                resp.setTruthTable(getCurrentTruthTable()); // Set the current truth table in the response.
                break;
            case 1: // PROVIDE_INPUTS operation.
                resp = provideTruthTableInputs(req.getValues()); // Handle providing new truth table inputs.
                break;
            case 2: // SINGLE_TRAINING_STEP operation.
                resp.setError(performSingleTrainingStep()); // Perform a single training step and set the error.
                break;
            case 3: // MULTIPLE_TRAINING_STEPS operation.
                resp.setError(performMultipleTrainingSteps(req.getIterations())); // Perform multiple training steps and set the error.
                break;
            case 4: // TEST_INPUTS operation.
                resp.setError(testWithInputPair(req.getValues().get(0), req.getValues().get(1))); // Test with a specific input pair and set the output.
                break;
            default: // Handle invalid commands.
                resp.setStatus("ERROR"); // Set status to "ERROR".
                resp.setMessage("Invalid command"); // Set an error message.
        }

        return gson.toJson(resp); // Convert the response to a JSON string and return.
    }

    /**
     * Retrieves the current truth table as a list of lists.
     *
     * @return A 2D list representing the truth table.
     */
    private List<List<Integer>> getCurrentTruthTable() {
        List<List<Integer>> truthTable = new ArrayList<>(); // Create a new list to hold the truth table.

        for (Double[][] set : userTrainingSets) { // Iterate over the training sets.
            List<Integer> row = new ArrayList<>(); // Create a new list for each row.
            row.add((int) Math.round(set[0][0])); // Add the first input value (rounded).
            row.add((int) Math.round(set[0][1])); // Add the second input value (rounded).
            row.add((int) Math.round(set[1][0])); // Add the output value (rounded).
            truthTable.add(row); // Add the row to the truth table.
        }
        return truthTable; // Return the complete truth table.
    }

    /**
     * Updates the truth table with new user-provided inputs.
     *
     * @param inputs A list of four integers representing the truth table outputs.
     * @return A response indicating success or error.
     */
    private Response provideTruthTableInputs(List<Integer> inputs) {
        if (inputs != null && inputs.size() == 4) { // Validate input size.
            for (int i = 0; i < 4; i++) { // Update each row of the truth table.
                userTrainingSets.get(i)[1][0] = inputs.get(i).doubleValue(); // Update the output value for each row.
            }
            neuralNetwork = new NeuralNetwork(2, 5, 1, null, null, null, null); // Reinitialize the neural network with new inputs.
            Response resp = new Response("OK"); // Create a success response.
            resp.setMessage("Inputs provided successfully"); // Set a success message.
            return resp; // Return the response.
        } else {
            Response resp = new Response("ERROR"); // Create an error response.
            resp.setMessage("Invalid input format"); // Set an error message.
            return resp; // Return the response.
        }
    }

    /**
     * Performs a single training step by randomly selecting a training set.
     *
     * @return The updated training error after the step.
     */
    private double performSingleTrainingStep() {
        int random_choice = rand.nextInt(4); // Randomly select a training set.
        List<Double> userTrainingInputs = Arrays.asList(userTrainingSets.get(random_choice)[0]); // Retrieve the inputs.
        List<Double> userTrainingOutputs = Arrays.asList(userTrainingSets.get(random_choice)[1]); // Retrieve the outputs.
        neuralNetwork.train(userTrainingInputs, userTrainingOutputs); // Train the network with the selected inputs and outputs.
        return neuralNetwork.calculateTotalError(userTrainingSets); // Calculate and return the total training error.
    }

    /**
     * Performs multiple training steps for the neural network.
     *
     * @param n The number of training steps to perform.
     * @return The updated training error after `n` steps.
     */
    private double performMultipleTrainingSteps(int n) {
        for (int i = 0; i < n; i++) { // Loop through the number of steps.
            int random_choice = rand.nextInt(4); // Randomly select a training set.
            List<Double> userTrainingInputs = Arrays.asList(userTrainingSets.get(random_choice)[0]); // Retrieve the inputs.
            List<Double> userTrainingOutputs = Arrays.asList(userTrainingSets.get(random_choice)[1]); // Retrieve the outputs.
            neuralNetwork.train(userTrainingInputs, userTrainingOutputs); // Train the network.
        }
        return neuralNetwork.calculateTotalError(userTrainingSets); // Calculate and return the total training error.
    }

    /**
     * Tests the neural network with a specific pair of inputs.
     *
     * @param input0 The first input value.
     * @param input1 The second input value.
     * @return The output value from the neural network for the given inputs.
     */
    private double testWithInputPair(double input0, double input1) {
        List<Double> testUserInputs = Arrays.asList(input0, input1); // Create a list of inputs.
        List<Double> userOutput = neuralNetwork.feedForward(testUserInputs); // Feed the inputs into the network.
        return userOutput.get(0); // Return the output value.
    }

    /**
     * Main method to start the Neural Network server.
     *
     * @param args Command-line arguments (not used).
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in); // Scanner for reading user input.
        System.out.print("Enter the port number to listen on: "); // Prompt the user to enter a port number.
        int port = scanner.nextInt(); // Read and store the port number.

        try { // Try block to start the server.
            NeuralNetworkServer server = new NeuralNetworkServer(port); // Create a new server instance.
            server.start(); // Start the server.
        } catch (IOException e) { // Catch block for handling exceptions.
            e.printStackTrace(); // Print the stack trace for any exceptions.
        }
    }
}


// This program is based upon an excellent blog post from Matt Mazur.
// See: https://mattmazur.com/2015/03/17/a-step-by-step-backpropagation-example/
// Matt's solution is in Python and this program is a translation of the same logic into Java.
// The mathematics underlying this neural network are explained well in the blog post.
// To fully understand this program, you would need to study the math in the blog post.

/*The idea is to train a neural network to learn a truth table.
Here is a truth table for the AND operation:
0   0   0
0   1   0
1   0   0
1   1   1

And here is a truth table for the XOR operation:
0  0  0
0  1  1
1  0  1
1  1  0

The user is asked to provide the rightmost column of the table. For example, if the user wants to
train the table for the XOR operation, the user will provide the following inputs:  0  1  1  0.

10,000 steps are typically used to train the network. If the error is close to 0, for example, 0.053298, the network
will perform well.

If the output of a test is close to 1, for example, .9759876, we will call that a 1.
If the output of a test is close to 0, for example, .0348712, we will call that a 0.

The program is not written to handle input errors. We assume that the user behaves well.
 */
// Each Neuron has a bias, a list of weights, and a list of inputs.
// Each neuron will produce a single real number as an output.
class Neuron {
    private double bias;
    public List<Double> weights;
    public List<Double> inputs;
    double output;
    // Construct a neuron with a bias and reserve memory for its weights.
    public Neuron(double bias) {
        this.bias = bias;
        weights = new ArrayList<Double>();
    }
    //Calculate the output by using the inputs and weights already provided.
    //Squash the result so the output is between 0 and 1.
    public double calculateOutput(List<Double> inputs) {

        this.inputs = inputs;

        output = squash(calculateTotalNetInput());
        return output;
    }
    // Compute the total net input from the input, weights, and bias.
    public double calculateTotalNetInput() {

        double total = 0.0;
        for (int i = 0; i < inputs.size(); i++) {
            total += inputs.get(i) * weights.get(i);
        }
        return total + bias;
    }

    // This is the activation function, returning a value between 0 and 1.
    public double squash(double totalNetInput) {
        double v = 1.0 / (1.0 + Math.exp(-1.0 * totalNetInput));
        return v;
    }
    // Compute the partial derivative of the error with respect to the total net input.
    public Double calculatePDErrorWRTTotalNetInput(double targetOutput) {
        return calculatePDErrorWRTOutput(targetOutput) * calculatePDTotalNetInputWRTInput();
    }
    // Calculate error. How different are we from the target?
    public Double calculate_error(Double targetOutput) {
        double theError = 0.5 * Math.pow(targetOutput - output, 2.0);
        return theError;
    }
    // Compute the partial derivative of the error with respect to the output.
    public Double calculatePDErrorWRTOutput(double targetOutput) {
        return (-1) * ( targetOutput - output);
    }
    // Compute the partial derivative of the total net input with respect to the input.
    public Double  calculatePDTotalNetInputWRTInput() {
        return output * ( 1.0 - output);

    }
    // Calculate the partial derivative of the total net input with respect to the weight.
    public Double calculatePDTotalNetInputWRTWeight(int index) {
        return inputs.get(index);
    }
}

// The Neuron layer represents a collection of neurons.
// All neurons in the same layer have the same bias.
// We include in each layer the number of neurons and the list of neurons.
class NeuronLayer {
    private double bias;
    private int numNeurons;

    public List<Neuron> neurons;

    // Construct by specifying the number of neurons and the bias that applies to all the neurons in this layer.
    // If the bias is not provided, choose a random bias.
    // Create neurons for this layer and set the bias in each neuron.
    public NeuronLayer(int numNeurons, Double bias) {
        if(bias == null) {

            this.bias = new Random().nextDouble();
        }
        else {
            this.bias = bias;
        }
        this.numNeurons = numNeurons;
        this.neurons  = new ArrayList<Neuron>();
        for(int i = 0; i < numNeurons; i++) {
            this.neurons.add(new Neuron(this.bias));
        }
    }
    // Display the neuron layer by displaying each neuron.
    public String toString() {
        String s = "";
        s = s + "Neurons: " + neurons.size() + "\n";
        for(int n = 0; n < neurons.size(); n++) {
            s = s + "Neuron " + n + "\n";
            for (int w = 0; w < neurons.get(n).weights.size(); w++) {
                s = s + "\tWeight: " + neurons.get(n).weights.get(w) + "\n";
            }
            s = s + "\tBias " + bias + "\n";
        }

        return s;
    }

    // Feed the input data into the neural network and produce some output in the output layer.
    // Return a list of outputs. There may be a single output in the output list.
    List<Double> feedForward(List<Double> inputs) {

        List<Double> outputs = new ArrayList<Double>();

        for(Neuron neuron : neurons ) {

            outputs.add(neuron.calculateOutput(inputs));
        }

        return outputs;
    }
    // Return a list of outputs from this layer.
    // We do this by gathering the output of each neuron in the layer.
    // This is returned as a list of Doubles.
    // This is not used in this program.
    List<Double> getOutputs() {
        List<Double> outputs = new ArrayList<Double>();
        for(Neuron neuron : neurons ) {
            outputs.add(neuron.output);
        }
        return outputs;
    }
}

// The NeuralNetwork class represents two layers of neurons - a hidden layer and an output layer.
// We also include the number of inputs and the learning rate.
// The learning rate determines the step size by which the network’s weights are
// updated during each iteration of training. This is typically chosen experimentally.

 class NeuralNetwork {

     // The learning rate is chosen experimentally. Typically, it is set between 0 and 1.
     private double LEARNING_RATE = 0.5;
     // This truth table example will have two inputs.
     private int numInputs;

     // This neural network will be built from two layers of neurons.
     private NeuronLayer hiddenLayer;
     private NeuronLayer outputLayer;

     // The neural network is constructed by specifying the number of inputs, the number of neurons in the hidden layer,
     // the number of neurons in the output layer, the hidden layer weights, the hidden layer bias,
     // the output layer weights and output layer bias.
     public NeuralNetwork(int numInputs, int numHidden, int numOutputs, List<Double> hiddenLayerWeights, Double hiddenLayerBias,
                          List<Double> outputLayerWeights, Double outputLayerBias) {
         // How many inputs to this neural network
         this.numInputs = numInputs;

         // Create two layers, one hidden layer and one output layer.
         hiddenLayer = new NeuronLayer(numHidden, hiddenLayerBias);
         outputLayer = new NeuronLayer(numOutputs, outputLayerBias);

         initWeightsFromInputsToHiddenLayerNeurons(hiddenLayerWeights);

         initWeightsFromHiddenLayerNeuronsToOutputLayerNeurons(outputLayerWeights);
     }

     // The hidden layer neurons have weights that are assigned here. If the actual weights are not
     // provided, random weights are generated.
     public void initWeightsFromInputsToHiddenLayerNeurons(List<Double> hiddenLayerWeights) {

         int weightNum = 0;
         for (int h = 0; h < hiddenLayer.neurons.size(); h++) {
             for (int i = 0; i < numInputs; i++) {
                 if (hiddenLayerWeights == null) {
                     hiddenLayer.neurons.get(h).weights.add((new Random()).nextDouble());
                 } else {
                     hiddenLayer.neurons.get(h).weights.add(hiddenLayerWeights.get(weightNum));
                 }
                 weightNum = weightNum + 1;
             }
         }
     }

     // The output layer neurons have weights that are assigned here. If the actual weights are not
     // provided, random weights are generated.
     public void initWeightsFromHiddenLayerNeuronsToOutputLayerNeurons(List<Double> outputLayerWeights) {
         int weightNum = 0;
         for (int o = 0; o < outputLayer.neurons.size(); o++) {
             for (int h = 0; h < hiddenLayer.neurons.size(); h++) {
                 if (outputLayerWeights == null) {
                     outputLayer.neurons.get(o).weights.add((new Random()).nextDouble());
                 } else {
                     outputLayer.neurons.get(o).weights.add(outputLayerWeights.get(weightNum));
                 }
                 weightNum = weightNum + 1;
             }
         }
     }

     // Display a NeuralNetwork object by calling the toString on each layer.
     public String toString() {
         String s = "";
         s = s + "-----\n";
         s = s + "* Inputs: " + numInputs + "\n";
         s = s + "-----\n";

         s = s + "Hidden Layer\n";
         s = s + hiddenLayer.toString();
         s = s + "----";
         s = s + "* Output layer\n";
         s = s + outputLayer.toString();
         s = s + "-----";
         return s;
     }

     // Feed the inputs provided into the network and get outputs.
     // The inputs are provided to the hidden layer. The hidden layer's outputs
     // are provided as inputs the output layer. The outputs of the output layer
     // are returned to the caller as a list of outputs. That number of outputs may be one.
     // The feedForward method is called on each layer.
     public List<Double> feedForward(List<Double> inputs) {

         List<Double> hiddenLayerOutputs = hiddenLayer.feedForward(inputs);
         return outputLayer.feedForward(hiddenLayerOutputs);
     }

     // Training means to feed the data forward - forward propagation. Compare the result with the target(s), and
     // use backpropagation to update the weights. See the blog post to review the math.
     public void train(List<Double> trainingInputs, List<Double> trainingOutputs) {

         // Update state of neural network and ignore the return value
         feedForward(trainingInputs);
         // Perform backpropagation
         List<Double> pdErrorsWRTOutputNeuronTotalNetInput =
                 new ArrayList<Double>(Collections.nCopies(outputLayer.neurons.size(), 0.0));
         for (int o = 0; o < outputLayer.neurons.size(); o++) {
             pdErrorsWRTOutputNeuronTotalNetInput.set(o, outputLayer.neurons.get(o).calculatePDErrorWRTTotalNetInput(trainingOutputs.get(o)));
         }
         List<Double> pdErrorsWRTHiddenNeuronTotalNetInput =
                 new ArrayList<Double>(Collections.nCopies(hiddenLayer.neurons.size(), 0.0));
         for (int h = 0; h < hiddenLayer.neurons.size(); h++) {
             double dErrorWRTHiddenNeuronOutput = 0;
             for (int o = 0; o < outputLayer.neurons.size(); o++) {
                 dErrorWRTHiddenNeuronOutput +=
                         pdErrorsWRTOutputNeuronTotalNetInput.get(o) * outputLayer.neurons.get(o).weights.get(h);
                 pdErrorsWRTHiddenNeuronTotalNetInput.set(h, dErrorWRTHiddenNeuronOutput *
                         hiddenLayer.neurons.get(h).calculatePDTotalNetInputWRTInput());
             }
         }
         for (int o = 0; o < outputLayer.neurons.size(); o++) {
             for (int wHo = 0; wHo < outputLayer.neurons.get(o).weights.size(); wHo++) {
                 double pdErrorWRTWeight =
                         pdErrorsWRTOutputNeuronTotalNetInput.get(o) *
                                 outputLayer.neurons.get(o).calculatePDTotalNetInputWRTWeight(wHo);
                 outputLayer.neurons.get(o).weights.set(wHo, outputLayer.neurons.get(o).weights.get(wHo) - LEARNING_RATE * pdErrorWRTWeight);
             }
         }
         for (int h = 0; h < hiddenLayer.neurons.size(); h++) {
             for (int wIh = 0; wIh < hiddenLayer.neurons.get(h).weights.size(); wIh++) {
                 double pdErrorWRTWeight =
                         pdErrorsWRTHiddenNeuronTotalNetInput.get(h) *
                                 hiddenLayer.neurons.get(h).calculatePDTotalNetInputWRTWeight(wIh);
                 hiddenLayer.neurons.get(h).weights.set(wIh, hiddenLayer.neurons.get(h).weights.get(wIh) - LEARNING_RATE * pdErrorWRTWeight);
             }
         }
     }

     // Perform a feed forward for each training row and total the error.
     public double calculateTotalError(ArrayList<Double[][]> trainingSets) {

         double totalError = 0.0;

         for (int t = 0; t < trainingSets.size(); t++) {
             List<Double> trainingInputs = Arrays.asList(trainingSets.get(t)[0]);
             List<Double> trainingOutputs = Arrays.asList(trainingSets.get(t)[1]);
             feedForward(trainingInputs);
             for (int o = 0; o < trainingOutputs.size(); o++) {
                 totalError += outputLayer.neurons.get(o).calculate_error(trainingOutputs.get(o));
             }
         }
         return totalError;
     }
 }


