# 02239 Data Security - Lab Assignment 2

## Prerequisites

* Java JDK 17.0 or newer
* IntelliJ Idea >= 2020 or Eclipse*

## User credentials for testing

On first run, the server application will create a SQLite databse and populate two users, that can be used for testing.

Username: `user1`, password: `myfirstborn123`

Username: `user2`, password: `nameofmycat`

### Using IntelliJ (recommended)

Choose `File -> Open` and browse to this directory. Click OK.

#### Start Print server

Open file `src/java/com/datasec/server/ServerApplication.java`

Click `Run 'ServerApplication.java'`

#### Client Application

Then start the client GUI by:

Open file `src/java/com/datasec/client/ClientApplication.java`

Click `Run 'ClientApplication'`

### Using Eclipse

Download `lombok` here:

`https://repo1.maven.org/maven2/org/projectlombok/lombok/1.18.30/lombok-1.18.30.jar`

Run the installer:

`java -jar lombok-1.18.30.jar`

Open Eclipse and click `File -> Open Projects from File System`

Browse to this directory and click Finish.

Open file `src/java/com/datasec/server/ServerApplication.java`

Right click on file and choose `Run as Java Application`

Start the client GUI by running `src/java/com/datasec/client/ClientApplication`
