# SoNe
CLI Social Network  
Work in progress

This is a simple CLI Social Network I made for fun in Java, to learn about networking and the Server-Client model.

Compile both Client and Server with `mvn assembly:assembly`and launch the server or client with dependencies with `java -jar -Dfile.encoding=UTF-8 (jar-file)`

Create a postgresql database and link to it in the settings file of the server (`Server/src/main/resources/settings.txt`). Also assign what port the server should communicate with the client on in this file. Use the script in `Server/src/database_resources/TableInit.sql` to create the necessary tables in the database. 

In the Client settings (`Client/src/main/resources/settings.txt`) you can assign the IP of the server (needs to be port-forwarded if on another network) and port that the server communicates on. 

After this the server and client should be configured and can communicate with each other.
