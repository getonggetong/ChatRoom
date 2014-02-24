.SUFFIXES: .java .class 
# That tells make to add .java and .class to the list of filename suffixes that it knows about.  But you still need to tell it what to do with files with those suffixes.  So put these lines in your Makefile too:
.java.class:
	javac $<
# the second line -- the command -- needs to start with a tab character, not spaces.
# That tells make that any file with a .class suffix depends on a file with the same name but a .java suffix, and the way to get the first from the second is to run javac with the depended-on file (the .java file) as argument.
CLASSES = ChatThread.class Client.class ClientSenderThread.class LastLoginTimer.class LockTimer.class Server.class ShutDownHook.class TimeOutTimer.class
all: $(CLASSES)
clean:
	/bin/rm $(CLASSES)