import java.net.*;
import java.io.*;
public class EchoClientUDP{
    public static void main(String args[]){
        // args give message contents and server hostname
        DatagramSocket aSocket = null;
        try {
            System.out.println("The UDP client is running");
            //Instruction 1
            //set up the client's connection to the server.
            InetAddress aHost = InetAddress.getByName("localhost");
            //int serverPort = 6789;
            //ask the user to enter the server's port number.
            System.out.print("Enter server port number: ");
            //read the value typed by the user
            BufferedReader portInput = new BufferedReader(new InputStreamReader(System.in));
            //set it as the serverPort
            int serverPort = Integer.parseInt(portInput.readLine());
            //set up a socket to communicate with server
            aSocket = new DatagramSocket();
            String nextLine;
            System.out.println("Enter the message to send and send halt! if you wish to exit");
            BufferedReader typed = new BufferedReader(new InputStreamReader(System.in));
            // loop to read user input from the console.
            while ((nextLine = typed.readLine()) != null) {
                //converts the user's input (a string) into a byte array.
                byte[] m = nextLine.getBytes();
                //creates a UDP packet to send to the server.
                DatagramPacket request = new DatagramPacket(m, m.length, aHost, serverPort);
                //sends the constructed DatagramPacket to the server via the socket.
                aSocket.send(request);

                //initializes a buffer to store incoming data from the server.
                byte[] buffer = new byte[1000];
                //prepares a packet to receive data from the server.
                DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
                //receives response from the server
                aSocket.receive(reply);

                // Use only the actual length of received data
                String replyString = new String(reply.getData(), 0, reply.getLength()).trim();
                System.out.println("Reply from server: " + replyString);

                //ask for another message to send
                System.out.println("Enter the message to send and send halt! if you wish to exit");

                // check if the message is "halt!" and exit
                if ("halt!".equals(replyString)) {
                    System.out.println("UDP Client side quitting");
                    break;
                }
            }
        }catch (SocketException e) {System.out.println("Socket Exception: " + e.getMessage());
        }catch (IOException e){System.out.println("IO Exception: " + e.getMessage());
        }finally {if(aSocket != null) aSocket.close();}
    }
}
