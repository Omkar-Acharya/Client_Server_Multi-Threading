import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class myClientThread extends Thread {

    Socket socket,tsocket;
    boolean terminated = false;
    DataInputStream input,tinput;
    DataOutputStream output,toutput;
    String command = "";
    boolean shouldrun = true;
    String port;

    myClientThread(Socket socket, String port) {
        try {
        	this.port=port;

        	if(this.port=="nport")
        	{
	            this.socket = socket;
	            input = new DataInputStream(socket.getInputStream());
	            output = new DataOutputStream(socket.getOutputStream());
        	}
        	else
        	{
        		this.tsocket = socket;
 	            tinput = new DataInputStream(tsocket.getInputStream());
 	            toutput = new DataOutputStream(tsocket.getOutputStream());
              // start();
        	}
        	}
        catch (IOException ex)
        {
            Logger.getLogger(myClientThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //sendDataToServer sends command received to the Server
    public void sendDataToServer(String mycommand) {
        try {
            //use appropriate ports
            if(port == "nport")
            {
              this.command = mycommand;
              // System.out.println("Inside sendDataToServer Method; command is: "+this.command);

            if (command.contains("put"))
            {
                output.writeUTF(command);
                output.flush();
            }
            else
            {
                output.writeUTF(command);
            }
            output.flush();
          }
          else
          {
            // System.out.println("Inside sendDataToServer Method of TPORT");
            command = mycommand;
            toutput.writeUTF(command);
            toutput.flush();
            System.out.println(tinput.readUTF());
          }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
    //Run method for multithreading
    public void run() {
        try {
            while (shouldrun) {

            	 if(input!=null)
                 {
                     if(shouldrun)
                    while ((shouldrun) && (input.available() == 0))
                    {
                    try
                    {
                        Thread.sleep(1);
                    }
                    catch (InterruptedException e)
                    {
                        //e.printStackTrace();
                    	Thread.currentThread().interrupt();
                    }
                    if(!shouldrun)
                    {
                      break;
                    }
                }
                }
                if (command.contains("get"))
                {
                  System.out.println("command ID from server is: "+input.readUTF());
                	executeGet(this.input, this.output);
                  this.command="";
                }
                else
                if(command.contains("put"))
                {
                  // System.out.println("inside command.contains put");
                  System.out.println("command ID from server is: "+input.readUTF());
                  executePut();
                  this.command="";
                }
                else
                {
                    if(command!="")
                    {
                    // for any other commands than get put quit; simply send the
                    // command to the Server

                    String inputString = "";
                    if (shouldrun) {
                        inputString = input.readUTF();
                    }
                    System.out.println(inputString);
                    this.command="";
                  }
                }
                // this.command="";

            }
        } catch (Exception e) {
          System.out.println(e.getMessage());
          //  e.printStackTrace();
        }
    }

  synchronized public void executeGet(DataInputStream input1, DataOutputStream output1)
  	{
    	try {
    		String filePath = "";
    		String fileName = "";

    		// Check if input has path name. Extract file name from it.
    		if (command.contains("/")) {
    			filePath = command.split(" ")[1];
    			String[] pathArray = filePath.split("/");
    			fileName = pathArray[pathArray.length - 1];
    		} else {
    			fileName = command.split(" ")[1];
    		}
    		// create blank file

        FileOutputStream fileoutput = new FileOutputStream(fileName);

    		int characters;
        boolean endOfFile = false;
    		// Write characters coming in from inputStream
    		do {
    			characters = Integer.parseInt(input1.readUTF());
    			if (characters != -1 && characters!=-2)
          {
    				fileoutput.write(characters);
    			}
          else
          if(characters == -2)  //-2 is received if server terminates
          {
          fileoutput.close();
          File deleteFile = new File(fileName);
          deleteFile.delete();
          break;
          }

    		} while (characters != -1);

    		fileoutput.close();
        //System.out.println("ID IS:"+input.readUTF());
    		System.out.println("Operation is completed");

    	}catch(Exception ex)
    	{
      //  ex.printStackTrace();
    		System.out.println(ex.getMessage());
    	}
    }

synchronized public void executePut() {
        try {
            // System.out.println("inside execyte put");
            String fileName = command.split(" ")[1];

            File file = new File(fileName);
            FileInputStream myFile = new FileInputStream(file.getAbsolutePath());
            //System.out.println(file.getAbsolutePath());
            int characters;
            // Send characters to getOutputStream
            do {
                characters = myFile.read();
                output.writeUTF(String.valueOf(characters));
                if(this.terminated)
                {
                  output.flush();
  								output.writeUTF(String.valueOf(-2));  //send minus 2 if terminate
  								// System.out.println("breaking");
  								break;
                }
            } while (characters != -1);
            output.flush();
            myFile.close();
            System.out.println("Operation is done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void close() {
        try {
        	// System.out.println("inside void close");
            shouldrun = false;
            input.close();
            output.close();
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
