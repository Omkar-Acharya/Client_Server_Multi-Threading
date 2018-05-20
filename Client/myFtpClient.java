
import java.util.*;
import java.net.*;
import java.io.*;

class myFtpClient {

    // Method to take input from Console
    public static String takeInput() throws Exception {
        System.out.print("mytftp> ");
        InputStreamReader reader = new InputStreamReader(System.in);
        BufferedReader buffer = new BufferedReader(reader);
        return buffer.readLine();
    }

    @SuppressWarnings("SleepWhileInLoop")
    public static void main(String[] args) throws Exception {

        String machineName = args[0];
    		String nportNo = args[1];
    		String tportNo = args[2];
    		int nportNumber = Integer.parseInt(nportNo);
    		int tportNumber = Integer.parseInt(tportNo);

        //configure normal port
        Socket nclientSocket = new Socket(machineName, nportNumber);
        myClientThread myClientThread = new myClientThread(nclientSocket,"nport");
        myClientThread.start();

        //configure terminate port
        Socket tclientSocket = new Socket(machineName, tportNumber);
        myClientThread myClientThreadTerminate = new myClientThread(tclientSocket,"tport");

        //create thread object for handling & commands
        boolean isAmpersantStarted=false;
        myClientThread myClientThreadamparsent=null;

        while (true) {
            Thread.sleep(510);
            String command = takeInput();

            //handle ampersand commands
            if(command.contains("&"))
            {

              myClientThreadamparsent = new myClientThread(nclientSocket,"nport");
              myClientThreadamparsent.start();
              isAmpersantStarted=true;
              myClientThreadamparsent.sendDataToServer(command);
            }else if(!command.contains("terminate"))
            {
            myClientThread.sendDataToServer(command);
            }
            else
            {
              //handle terminate commands
              myClientThreadamparsent.terminated = true;
              myClientThreadTerminate.sendDataToServer(command);
            }

            if (command.equalsIgnoreCase("quit")) {
              //interrupt threads and close sockets
            	if(isAmpersantStarted)
                {
              		System.out.println("interrupting thread");
              		myClientThread.interrupt();
              		myClientThreadamparsent.interrupt();
              		myClientThread.close();
              		myClientThreadamparsent.close();
              		break;
               }
            	else
            	{
            		myClientThread.interrupt();
            		myClientThread.close();
            		break;
            	}
            }

        }
    }
}
