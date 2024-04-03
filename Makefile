# Define a variable for classpath, you can add more directories or jars separated by :
CLASSPATH = ./bin

# Define a variable for Java compiler
JAVAC = javac

# Define a variable for Java runtime
JAVA = java

# Define the source directory
SRC_DIR = src

# Define the binary directory (where .class files will go)
BIN_DIR = bin

# Flag to create the directory if it doesn't exist
MKDIR_P = mkdir -p

# The first rule is the one executed when no parameters are fed into the Makefile
default: cp

# Rule for creating binary directory
$(BIN_DIR):
	$(MKDIR_P) $(BIN_DIR)

# Rule for compiling the code
cp: $(SRC_DIR)/*.java | $(BIN_DIR)
	$(JAVAC) -d $(BIN_DIR) -cp $(CLASSPATH) -Xlint:unchecked $(SRC_DIR)/*.java

# Rule to run the program
run: cp
	$(JAVA) -cp $(CLASSPATH) src.TestTupleGenerator

# Rule for cleaning up the project (deleting all compiled files in bin)
clean:
	rm -rf $(BIN_DIR)/*.class

# Replace 'MainClassName' with the name of your main class (without the .java extension)
# This assumes that you have a main method in one of your classes
