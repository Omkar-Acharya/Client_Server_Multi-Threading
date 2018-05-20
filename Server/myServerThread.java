import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class myServerThread extends Thread
{

	Socket socket;
	DataInputStream input;
	DataOutputStream output;
	myFtpServerProcess mycommand = new myFtpServerProcess();
	String inputString = null;

	myServerThread(ServerSocket sersocket)
	{
		//Initialize the socket, DataInputStream, DataOutputStream for object
		try
		{
			this.socket = sersocket.accept();
			System.out.println("Client connection arrived");
			this.input = new DataInputStream(socket.getInputStream());
			this.output = new DataOutputStream(socket.getOutputStream());
		}
		catch (IOException ex)
		{
			Logger.getLogger(myServerThread.class.getName()).log(Level.SEVERE, null, ex);
		}
	}

	//Method to split the input command
	public String[] splitCommand(String command)
	{
		return command.split(" ");
	}

	//Method to create command id for the get, put commands
	public CommandLogs createCommandID(String status, String fileName)
	{
		//creating the ArrayList item
		CommandLogs cmd = new CommandLogs(status, fileName);
		return cmd;
	}

	//Method to check concurrency for get method
	public boolean canGetExecute(CommandLogs cmd)
	{
		String cmdFileName="";
		String cFileName="";

		//strip file path from file name of CMD
		if (splitCommand(cmd.fileName)[1].contains("/"))
		{
			String filePath = splitCommand(cmd.fileName)[1];
			String[] pathArray = filePath.split("/");
			cmdFileName = pathArray[pathArray.length - 1];
		}
		else
		{
			cmdFileName = splitCommand(cmd.fileName)[1];
		}

		for(CommandLogs c : CommandLogs.listOfCommands)
		{
			//strip path name
			if (splitCommand(c.fileName)[1].contains("/"))
			{
				String filePath = splitCommand(c.fileName)[1];
				String[] pathArray = filePath.split("/");
				cFileName = pathArray[pathArray.length - 1];
			}
			else
			{
				cFileName = splitCommand(c.fileName)[1];
			}

			if(cFileName.equals(cmdFileName) && c.fileName.split(" ")[0].equals("put"))
			{
				if(c.status.equals("E"))
				{
					return false;
				}
				else
				if(c.status.equals("W"))
				{
					if(c.commandId<cmd.commandId)
					{
						return false;
					}
				}
			}
		}
		return true;
	}

	//Method to check concurrency fot put command
	public boolean canExecute(CommandLogs cmd)
	{
		//check fileName Get all commands for that fileName
		//send false if: status is E
 		//send true otherwise and make its status E
		String cmdFileName="";
		String cFileName="";

		//strip file path from file name
		if (splitCommand(cmd.fileName)[1].contains("/"))
		{
			String filePath = splitCommand(cmd.fileName)[1];
			String[] pathArray = filePath.split("/");
			cmdFileName = pathArray[pathArray.length - 1];
		}
		else
		{
			cmdFileName = splitCommand(cmd.fileName)[1];
		}

		for(CommandLogs c : CommandLogs.listOfCommands)
		{
			//strip path name
			if (splitCommand(c.fileName)[1].contains("/"))
			{
				String filePath = splitCommand(c.fileName)[1];
				String[] pathArray = filePath.split("/");
				cFileName = pathArray[pathArray.length - 1];
			}
			else
			{
				cFileName = splitCommand(c.fileName)[1];
			}

				if(cFileName.equals(cmdFileName))
				{
				if(c.status.equals("E"))
				{
					return false;
				}
				else
				if(c.status.equals("W"))
				{
					if(c.commandId<cmd.commandId)
					{
						return false;
					}
						}
					}
				}
				return true;
			}

	//Running the Thread
	public void run()
	{
		try {
			while (true) {
				while (input.available() == 0)
				{
					try
					{
						Thread.sleep(1);
					}
					catch (InterruptedException e)
					{
						e.printStackTrace();
					}
				}

				// read the command
				inputString = input.readUTF();

				// Call respective methods of ServerProcess of the FTP commands
				if (splitCommand(inputString)[0].equalsIgnoreCase("terminate")) {

					int cmdId = Integer.parseInt(splitCommand(inputString)[1]);
					int index = -1;
					CommandLogs commandToChange=null;
					output.writeUTF("Terminate on Server: "+String.valueOf(cmdId));
					output.flush();

					for(CommandLogs c : CommandLogs.listOfCommands)
					{
						if(c.commandId == cmdId)
						{
							index = CommandLogs.listOfCommands.indexOf(c);
							commandToChange=c;
							break;
						}
					}
					commandToChange.status ="T";
					CommandLogs.listOfCommands.set(index, commandToChange);
				}

				// Call respective methods of ServerProcess of the FTP commands
				if (splitCommand(inputString)[0].equalsIgnoreCase("mkdir")) {
					output.writeUTF(mycommand.mkdir(splitCommand(inputString)[1]));
					output.flush();
				}
				// received from Client
				if (splitCommand(inputString)[0].equalsIgnoreCase("cd"))
				{
					if (!splitCommand(inputString)[1].equalsIgnoreCase("..")) {
						output.writeUTF(mycommand.setCurrent(splitCommand(inputString)[1]));
						output.flush();
					}
					else
					{
						output.writeUTF(mycommand.setPrevious());
						output.flush();
					}
				}

				if (splitCommand(inputString)[0].equalsIgnoreCase("delete")) {
					output.writeUTF(mycommand.delete(splitCommand(inputString)[1]));
					output.flush();
				}

				if (splitCommand(inputString)[0].equalsIgnoreCase("ls")) {

					File[] files;
					String allPath = "";
					if (splitCommand(inputString).length == 1) {
						files = mycommand.ls();

						for (File file : files) {
							allPath = allPath + "  " + file.getName() + '\t';
						}
					} else {
						files = mycommand.ls(new File(splitCommand(inputString)[1]));
						for (File file : files) {
							allPath = allPath + "  " + file.getName() + '\t';
						}
					}
					output.writeUTF(allPath);
					output.flush();
				}

				if (splitCommand(inputString)[0].equalsIgnoreCase("pwd")) {
					output.writeUTF(mycommand.pwd(new File("")));
					output.flush();
				}

				// Get and Put are handled by myftpServer and not
				// myFtpServerProcess
				if (splitCommand(inputString)[0].equalsIgnoreCase("get"))
				{
					CommandLogs cmd	=	createCommandID("W",inputString);
					output.writeUTF(Integer.toString(cmd.commandId));
					output.flush();

					try
					{
					while(!canGetExecute(cmd))
					{
						Thread.sleep((cmd.commandId * 3000));
					}
					}
					catch(Exception ex)
					{
						System.out.println(ex.getMessage());
					}

					//Set status to E
					int index = -1;
					CommandLogs commandToChange=null;
					for(CommandLogs c : CommandLogs.listOfCommands)
					{
						if(c.commandId == cmd.commandId)
						{
							index = CommandLogs.listOfCommands.indexOf(c);
							commandToChange=c;
							break;
						}
					}
					commandToChange.status ="E";
					CommandLogs.listOfCommands.set(index, commandToChange);

					mycommand.get(output, inputString, cmd);

					index = -1;
					commandToChange=null;
					for(CommandLogs c : CommandLogs.listOfCommands)
					{
						if(c.commandId == cmd.commandId)
						{
							index = CommandLogs.listOfCommands.indexOf(c);
							commandToChange=c;
							break;
						}
					}
					commandToChange.status ="D";
					CommandLogs.listOfCommands.set(index, commandToChange);
				}

				//System.out.println("input string is:"+inputString);
				if (splitCommand(inputString)[0].equalsIgnoreCase("put"))
				{
					CommandLogs cmd	=	createCommandID("W",inputString);
					output.writeUTF(Integer.toString(cmd.commandId));
					output.flush();

					try
					{
					while(!canExecute(cmd))
					{
						Thread.sleep((cmd.commandId * 3000));
					}
					}
					catch(Exception ex)
					{
						System.out.println(ex.getMessage());
					}

					//Set status to E
					int index = -1;
					CommandLogs commandToChange=null;
					for(CommandLogs c : CommandLogs.listOfCommands)
					{
						if(c.commandId == cmd.commandId)
						{
							index = CommandLogs.listOfCommands.indexOf(c);
							commandToChange=c;
							break;
						}
					}
					commandToChange.status ="E";
					CommandLogs.listOfCommands.set(index, commandToChange);

					mycommand.put(input, inputString);

				 	index = -1;
					commandToChange=null;
					for(CommandLogs c : CommandLogs.listOfCommands)
					{
						if(c.commandId == cmd.commandId)
						{
							index = CommandLogs.listOfCommands.indexOf(c);
							commandToChange=c;
							break;
						}
					}
					commandToChange.status ="D";
					CommandLogs.listOfCommands.set(index, commandToChange);

				// close input and output streams
				if (inputString.equalsIgnoreCase("quit"))
				{
					input.close();
					output.close();
					socket.close();
					break;
				}
			}

		}
			} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
