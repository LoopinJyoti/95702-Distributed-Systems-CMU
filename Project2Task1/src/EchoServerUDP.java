import java.net.*;
import java.io.*;
public class EchoServerUDP{
    public static void main(String args[]){
        DatagramSocket aSocket = null;
        byte[] buffer = new byte[1000];
        try{
            System.out.println("The UDP server  is running");
            System.out.print("Enter server port number to listen to ");
            //read the value typed by the user
            BufferedReader portListen = new BufferedReader(new InputStreamReader(System.in));
            //set it as the serverPort
            int serverPort = Integer.parseInt(portListen.readLine());
            //creates a UDP socket on port 6789.
            aSocket = new DatagramSocket(serverPort);
            //creates a packet to receive data and wait for incoming packets.
            DatagramPacket request = new DatagramPacket(buffer, buffer.length);
            while (true) {
                aSocket.receive(request);

                // create an array with the exact length of the received data
                byte[] receivedData = new byte[request.getLength()];
                System.arraycopy(request.getData(), 0, receivedData, 0, request.getLength());

                // convert to string using only the received data
                String requestString = new String(receivedData);
                System.out.println("Echoing: " + requestString);

                // Create a reply packet with the correct data length
                DatagramPacket reply = new DatagramPacket(receivedData, receivedData.length, request.getAddress(), request.getPort());
                aSocket.send(reply);

                //check if the message sent by client was "halt!" and exit
                if ("halt!".equals(requestString)) {
                    System.out.println("UDP Server side quitting");
                    break;
                }
            }
        }catch (SocketException e){System.out.println("Socket: " + e.getMessage());
        }catch (IOException e) {System.out.println("IO: " + e.getMessage());
        }finally {if(aSocket != null) aSocket.close();}
    }
}

