import java.net.*;
import java.io.*;
import java.nio.file.FileSystems;
import java.nio.file.Path;

class myFtpServer {


    public static void main(String[] args) {
        //Taking command-line parameters
        String snport = args[0];
        int nport = Integer.parseInt(snport);
        String stport = args[1];
        int tport = Integer.parseInt(stport);

        // create object of ServerProcess that executes the command
        System.out.println("Server Started");

        try {
            // Create nport and tport sockets
            ServerSocket nserSocket = new ServerSocket(nport);
            ServerSocket tserSocket = new ServerSocket(tport);
            while (true) {
                //Creating thread object for nport
                myServerThread myNServerThread = new myServerThread(nserSocket);
                //startung the thread
                myNServerThread.start();
                //Creating thread object for tport
                myServerThread myTServerThread = new myServerThread(tserSocket);
                //startung the thread
                myTServerThread.start();
            }
        } catch (Exception ex) {
            System.out.println("exceptionnn" + ex + " exception " + ex.getMessage());
        }
    }
}
