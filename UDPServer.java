// Author   : Chrisitan Dunn
// Date     : November 4th, 2016
// Purpose  : To create UDP server class that listens for pinging from client end
// Compiler : NetBeans IDE 8.1

import java.io.*;
import java.net.*;

// Class   : UDPServer
// Purpose : To create UDPServer class to create listener for incoming pings from client
public class UDPServer 
    {
        // Method     : main
        // Purpose    : To perform operations used in listening for ping through
        //              the use of an infinte while loop
        // Parameters : - String         args[]           - int    port
        //              - DatagramSocket serverSocket     - byte[] receiveData
        //              - byte[]         sendData         - String sentence
        //              - InetAddress    IPAddress        - int    senderPort
        //              - DatagramPacket sendPacket
        public static void main(String args[]) throws Exception
            {
                int port = 0;
                
                // Parse port number from command line 
                try
                    {
                        port = Integer.parseInt(args[0]);
                    } // end try
                catch (ArrayIndexOutOfBoundsException e)
                    {
                        System.out.println("Need one argument: port number.");
                        System.exit(-1);
                    } // end ArrayIndex catch
                catch (NumberFormatException e)
                    {
                        System.out.println("Please give port number as integer.");
                        System.exit(-1);
                    } // end NumberFormat catch

                // Create a new Datagram socket at the port 
                DatagramSocket serverSocket = new DatagramSocket(port);
                byte[]         receiveData  = new byte[512];
                byte[]         sendData     = new byte[512];

                // Indicate that the server is running 
                System.out.println("The UDP server is listening on port " + port);

                // Infinite while loop to listen for incoming pings on port 
                while(true)
                    {
                        // Create new datagram packet and let socket receive it 
                        DatagramPacket receivePacket = 
                                new DatagramPacket(receiveData, receiveData.length);
                        serverSocket.receive(receivePacket);

                        // Print the message received 
                        String sentence = new String(receivePacket.getData());
                        if(!sentence.equals(""))
                            System.out.println(sentence.trim());

                        // Get the IP address of the sender 
                        InetAddress IPAddress = receivePacket.getAddress();

                        // Get the port number of the sender
                        int senderPort = receivePacket.getPort();

                        // Prepare the data to send back
                        sendData = sentence.getBytes();
                        DatagramPacket sendPacket = 
                                new DatagramPacket(sendData, sendData.length, IPAddress, senderPort);

                        // Send the packet
                        serverSocket.send(sendPacket);
                    } // end while
            }// end main
    } // end UDPServer
