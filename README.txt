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

I have four additional features. 

1. The client is automatically logout and database is updated when the client program shut down without giving logout command. So that the client can login normally when he connects to the server next time. It can be tested by just interrupt the client program by ctrl+c or similar commands, and then login again with the same user name. There would not be 'duplicate login' warning.

2. The database is with a read-write lock, so that threads would not inference each other, and the reading from and writing to database would leave the database with consistency. It can be tested by running several clients program simultaneously and send commands which have operation on the database to check if the result is correct. For example, logout one client and give 'whoelse'/'broadcast'/'message' command from another client simultaneously.

3. I added timed private message function so that users would know when messages are sent to them. For offline messages, the time is when the message being sent to them, so that clients would know better about the received message. To test this function, give “tmessage <target username> <message>” command. The received message would be displayed with time.

4. The last login IP address would be displayed if it is not the first login. If that IP address appears unfamiliar or weird, the user would know that maybe his/her chat room account is hacked. To test it, login to the chat room and then logout. Login again, the last login IP address would be displayed.

----------------------------------------
f. Implementation details worth mentioning
========================================

About the login lock function, I implemented it like this:
When a user input a wrong combination of username and password, the counter for this user and this IP address is increased by 1. The server would keep this counter specifically for this user using this IP address even if the user's client program gets disconnect. I did it this way because it is reasonable for a server to block fake users from guessing users' password without restriction. And once the user using some certain IP address is locked, this user would be disconnected once the same username is input through same IP address. It is more user-friendly to stop the user earlier instead of when all information needed is input. 
