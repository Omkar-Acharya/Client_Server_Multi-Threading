# Client_Server_Multi-Threading
Client and Server Architecture with Multi-Threading functionality using Java

Contributors:
1. Akshay Mendki
2. Omkar Acharya

This command simulates basic commands of FTP using Java Sockets. Following commands have been simulated.

1. pwd:           Gives current directory path of Server
2. mkdir dirName: Creates new directory with name directoryName
3. get fileName:  Downloads file present in the Server to the Client. fileName can include the entire Absolute path too. Server gives commandID to the client
4. put fileName:  Uploads file present in the Client to the Server. fileName can include the entire absolute path too. Server gives commandID to the client.
5. cd dirName:    Sets the current directory to the directoryName mentioned in the command. dirName can include the entire absolute path.
6. cd ..  :       Sets the current directory to the parent directory.
7. ls:            Lists the entire files and folders present in current directory.
8. delete file :  Deletes the file present in the current directory. FileName can has path name too, in that case file present in the path will be deleted.
9. quit :         Closes the connection.
10. put fileName &:  Uses a new thread to execute put filename
11. get fileName &:   Uses a new thread to execute get filename
12 terminate commandID:  This command terminates the get or put operation, identified by the commandID

How to compile and run?

1. myFtpServer.java, myFtpServerProcess.java, myServerThread.java and CommandLogs.java, are needed to run Server. 
2. myFtpClient.java and myClientThread.java are needed to run the Client.
3. Compile .java files with the command javac fileName.java
4. Run java files after compilation with the command java fileName
5. After compilation execute the Server using java myFtpServer portNumber. Use any port number that is available to use. Server will listen to the port number mentioned in the command line arguments.
6. Once Server has started, run the Client using java myFtpClient machine_name portnumber (where machine_name and portnumber are command-line parameters). Machine name is the name of machine where server is executed and port number is the port that Server is listening to.
7. Once Client and Server are connected you can try any commands mentioned above. Connection will close if quit is sent by the client.

Folder Structure:

--Client
    --myFtpClient.java
	--myClientThread.java
--Server
    --myFtpServer.java
    --myFtpServerProcess.java
	--CommandLogs.java
	--myServerThread.java
--readMe.md

NOTES:

1. The only command that client can enter is terminate when get-put are followed by &
2. Threads will sleep in case of conflicting get put commands on same file name. Conflicting cases would be put-put, get-put, put-get on same file name.
	Threads will wakeup when locking operation is done executing.
3. If threads are sleeping, they will wakeup in first come first serve basis.
4. Terminate delay...
5. When terminate is entered, the corresponding file transfer is stopped and partially created files are deleted.
6. If there are files created already in client or server, get or put with same file name will overwrite the existing files.
7. It may happen that response from server will appear in front of myftp> prompt. Even this happens, you can enter the command on next line and will receive the output of that command successfully.

This project was done in its entirety by Akshay Mendki and Omkar Acharya. We hereby state that we have not received unauthorized help of any form.
