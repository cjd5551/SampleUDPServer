// Author   : Christian Dunn
// Date     : November 4th, 2016
// Purpose  : To create a class that provides methods to ping from client to server
// Compiler : NetBeans IDE 8.1

import java.io.*;
import java.net.*;

// Class   : PingClient
// Purpose : To define class methods to ping from client to server
public class PingClient 
    {
        // Declare socket in use and maximum length of ping message
        DatagramSocket   socket;
        static final int MAX_PING_LEN = 512;
        
        // Method     : createSocket 
        // Purpose    : Default constructor that creates a socket bound to random port
        // Parameters : DatagramSocket socket
        public void createSocket()
            {
                try
                    { socket = new DatagramSocket();} // end try
                catch (SocketException e)
                    { System.out.println("Error creating socket: " + e);} // end catch
            } // end createSocket
        
        // Method     : createSocket
        // Purpose    : Constructor that creates socket bound to specified port number
        // Parameters : - int port        - DatagramSocket socket
        public void createSocket(int port)
            {
                try
                    { socket = new DatagramSocket(port);} // end try
                catch (SocketException e)
                    { System.out.println("Error creating socket: " + e);} // end catch
            } // end createSocket
        
        // Method     : sendPing
        // Purpose    : Send UDP ping message that is given as an argument
        // Parameters : - Message        ping          - InetAddress host
        //              - int            port          - String      message
        //              - DatagramPacket packet        
        public void sendPing (Message ping)
            {
                InetAddress host    = ping.getIP();
                int         port    = ping.getPort();
                String      message = ping.getContents();
                
                try
                    {
                        // Create datagram packet addressed to the recipient
                        DatagramPacket packet = 
                                new DatagramPacket(message.getBytes(), message.length(), host, port);
                        
                        // Send the packet
                        socket.send(packet);
                        System.out.println("Sent message to " + host + ": " + port);
                    } // end try
                catch (IOException e)
                    { System.out.println("Error sending packet: " + e);} // end catch
            } // end sendPing
        
        // Method     : receivePing
        // Purpose    : Receive UDP ping message and return the received message
        // Parameters : - byte[]  recvBuf    - DatagramPacket recvPacket
        //              - Message reply      
        public Message receivePing() throws SocketTimeoutException
            {
                // Create packet for receiving reply
                byte[] recvBuf = new byte[MAX_PING_LEN];
                DatagramPacket recvPacket = new DatagramPacket(recvBuf, recvBuf.length);
                Message reply = null;
                
                // Read message from socket
                try
                    {
                        socket.receive(recvPacket);
                        
                        System.out.println("Received message from " + 
                                recvPacket.getAddress() + 
                                ": " + recvPacket.getPort());
                        String recvMsg = new String(recvPacket.getData());
                        System.out.println(recvMsg.trim());
                        reply = new Message(recvPacket.getAddress(), recvPacket.getPort(), recvMsg);
                    } // end try
                catch(SocketTimeoutException e)
                    { throw e;} // end SocketTimeout catch
                catch(IOException e)
                    { System.out.println("Error reading from socket: " + e);} // end IOException catch
                
                return reply;
            } // end receivePing
    } // end class PingClient
