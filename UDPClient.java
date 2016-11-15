// Author   : Christian Dunn
// Date     : November 4th, 2016
// Purpose  : To create UDPClient class for client end of UDP connection
// Compiler : NetBeans IDE 8.1

import java.net.*;
import java.util.*;

// Class   : UDPClient
// Purpose : To extend client ping to send message to server end and gain feedback
//           regarding round trip time.
public class UDPClient extends PingClient implements Runnable 
    {
        String           remoteHost;                                // Host to ping
        int              remotePort;                                // Port number of host
        static final int NUM_PINGS     = 10;                        // Number of pings to send
        int              numReplies    = 0;                         // Number of replies received
        static boolean[] replies       = new boolean[NUM_PINGS];    // Array to hold replies
        static long[]    rtt           = new long[NUM_PINGS];       // Array to hold RTTs
        static final int TIMEOUT       = 1000;                      // One second timeout wait time
        static final int REPLY_TIMEOUT = 5000;                      // Five second timeout collection time
        
        // Method     : UDPClient
        // Purpose    : Constructor that sets host and port for client end
        // Parameters : - String host       - int port
        public UDPClient(String host, int port)
            {
                remoteHost = host;
                remotePort = port;
            } // end UDPClient constructor
        
        // Method     : main
        // Purpose    : To create client and run client for pinging to server
        // Parameters : - String args[]         - String    host 
        //              - int    port           - UDPClient Client
        public static void main(String args[])
            {
                String host = null;
                int    port = 0;
                
                // Parse data from command line into host and port number variables
                try 
                    {
                        host = args[0];
                        port = Integer.parseInt(args[1]);
                    } // end try
                catch (ArrayIndexOutOfBoundsException e)
                    {
                        System.out.println("Need two arguments: remoteHost, remotePort");
                        System.exit(-1);
                    } // end ArrayIndex catch
                catch (NumberFormatException e)
                    {
                        System.out.println("Please give port number as integer.");
                        System.exit(-1);
                    } // end NumberFormat catch
                
                System.out.println("Contacting host " + host + " at port " + port);
                
                UDPClient Client = new UDPClient(host, port);
                Client.run();
            } // end main
        
        // Method     : run
        // Purpose    : Implement run method used in creating socket
        // Parameters : - Date    now         - String message
        //              - Message ping      
        public void run()
            {
                // Create socket with random local port
                createSocket();
                
                // Set timeout value
                try
                    { socket.setSoTimeout(TIMEOUT);} // end try
                catch(SocketException e)
                    { System.out.println("Error setting timeout TIMEOUT: " + e);} // end catch
                
                // For loop to send defined amount of pings to receiver
                for(int i = 0; i < NUM_PINGS; i++)
                    {
                        // Send current time as message to server
                        Date    now     = new Date();
                        String  message = "PING " + i + " " + now.getTime() + " ";
                        Message ping   = null;
                        replies[i]     = false;
                        rtt[i]         = 1000000;
                        
                        // Send ping to recipient
                        try
                            {
                                ping = new Message(InetAddress.getByName(remoteHost), 
                                                remotePort, message);
                            } // end try
                        catch(UnknownHostException e)
                            { System.out.println("Cannot find host: " + e);} // end catch
                        
                        sendPing(ping);
                        
                        // Read the reply by getting the received ping message
                        try
                            {
                                Message reply = receivePing();
                                handleReply(reply.getContents());
                            } // end try
                        catch(SocketTimeoutException e)
                            { System.out.println("Socket timeout occured.");} // end catch
                    } // end for
                
                // Check for missing replies. If nothing comes, assume lost.
                try
                    { socket.setSoTimeout(REPLY_TIMEOUT);} // end try
                catch(SocketException e)
                    { System.out.println("Error setting timeout REPLY_TIMEOUT: " + e);} // end catch
                
                // Enter while loop until a reply is received for each ping, or loss occurs
                while (numReplies < NUM_PINGS)
                    {
                        try
                            {
                                Message reply = receivePing();
                                handleReply(reply.getContents());
                            } // end try
                        catch(SocketTimeoutException e)
                            { numReplies = NUM_PINGS;} // end catch
                    } // end while
                
                // Print info regarding each ping and reply
                for(int i = 0; i < NUM_PINGS; i++)
                    {
                        System.out.println("PING " + i + ": " + replies[i] + 
                                " RTT: " + rtt[i]);
                    } // end for
            } // end run
        
        // Method     : handleReply
        // Purpose    : Used to handle reply information and calculate RTT for given reply
        // Parameters : - String reply          - String[] tmp
        //              - int    pingNumber     - long     oldTime
        //              - Date   now
        private void handleReply(String reply)
            {
                String[] tmp        = reply.split(" ");
                int      pingNumber = Integer.parseInt(tmp[1]);
                long     oldTime    = Long.parseLong(tmp[2]);
                Date     now        = new Date();
                replies[pingNumber] = true;
                
                // Calculate RTT and store in rtt array given old and current time
                rtt[numReplies] = now.getTime() - oldTime;
                numReplies++;
            } // end handleReply
    } // end class UDPClient
