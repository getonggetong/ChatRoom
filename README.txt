tg2473 | Tong GE | Project 1 | Computer Networks

----------------------------------------
a. A brief description of my code
========================================

My chat room program works as requried.

I have develped a client program and a server program. 

The client program has two main jobs:
	1. To listen to the server message and display it to user.
	2. To monitor the user keyboard input message and send it back to the server.
I use the main thread to do the job#1 and a new thread to do the job#2.

The server program does more work:
	1. To listen to new client.
	2. To authenticate client.
	3. To keep database updated.
	4. To listen to clients' commands and do corresponding response.
The main thread keeps listening to the new incoming clients. Once a new client is connected, a new thread is started to dedicatedly serve it. I use a hash map as the database of all clients' status. 

----------------------------------------
b. Details on development environment
========================================

My program is developed under Eclipse, java 1.7. But I have tested it under java 1.6, and removed inappropriate stuff, so that it works well under java 1.6 using terminal.

----------------------------------------
c. Instructions on how to run my code
========================================

Open a terminal --> using 'cd' to the project folder which includes all .java source files and user_pass.txt --> run 'make' --> run 'java Server <PORT>' to start the server
Open a terminal --> using 'cd' to the project folder which includes all .java source files and user_pass.txt --> run 'java Client <SERVER_IP> <SERVER_PORT>' to connect to server

----------------------------------------
d. Sample commands to invoke my code
========================================

The program supports all required commands. Examples are:

whoelse
wholasthr
broadcast hello world
message foobar are you there?
block Columbia
unblock Columbia
logout

----------------------------------------
e. Description of an additional functionalities and how they should be executed/tested
========================================

I have two addtional funtion. 

1. The client is automatically logout and database is updated when the client program shut down without giving logout command. So that the client can login normally when he connects to the server next time. It can be tested by just interrupt the client program by ctrl+c or similar commands, and then login again with the same user name. There would not be 'duplicate login' warning.

2. The database is with a read-write lock, so that threads would not inference each other, and the reading from and writing to database would leave the database with consistency. It can be tested by running several clients program simultaneously and send commands which have operation on the database to check if the result is correct. For example, logout one client and give 'whoelse'/'broadcast'/'message' command from another client simultaneously.
