import java.util.*;
public class CommandLogs
{
  //Command Id for get, put commands
  public  int commandId;
  //E for executing, D for Done, T for Terminated
  public  String status;
  //File name to be stored in the list
  public  String fileName;
  //Creating the ArrayList
  public static List<CommandLogs> listOfCommands = new ArrayList<CommandLogs>();
  //Counter for commandId
  public static int cmdIDCounter = 0;

  //Initialize and add the list with commandId, status, Filename
  CommandLogs(String stat, String file)
  {
    this.commandId = cmdIDCounter++;
    this.status = stat;
    this.fileName = file;
    listOfCommands.add(this);
  }
}
