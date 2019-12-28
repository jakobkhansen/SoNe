[1mdiff --git a/.gitignore b/.gitignore[m
[1mindex 7cdd353..4d23d89 100644[m
[1m--- a/.gitignore[m
[1m+++ b/.gitignore[m
[36m@@ -21,6 +21,11 @@[m [mServer/target/*[m
 target/*[m
 Server/.classpath[m
 [m
[32m+[m[32mClient/.project[m
[32m+[m[32mClient/.settings/*[m
[32m+[m[32mClient/target/*[m
[32m+[m[32mClient/.classpath[m
[32m+[m
 # Sensitive or high-churn files[m
 .idea/**/dataSources/[m
 .idea/**/dataSources.ids[m
[1mdiff --git a/Client/pom.xml b/Client/pom.xml[m
[1mnew file mode 100644[m
[1mindex 0000000..3900c06[m
[1m--- /dev/null[m
[1m+++ b/Client/pom.xml[m
[36m@@ -0,0 +1,55 @@[m
[32m+[m[32m<?xml version="1.0" encoding="UTF-8"?>[m
[32m+[m[32m<project xmlns="http://maven.apache.org/POM/4.0.0"[m
[32m+[m[32m         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"[m
[32m+[m[32m         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">[m
[32m+[m[32m    <modelVersion>4.0.0</modelVersion>[m
[32m+[m
[32m+[m[32m    <groupId>org.example</groupId>[m
[32m+[m[32m    <artifactId>SoNe</artifactId>[m
[32m+[m[32m    <version>1.0-SNAPSHOT</version>[m
[32m+[m
[32m+[m[32m    <dependencies>[m
[32m+[m
[32m+[m
[32m+[m[32m        <dependency>[m
[32m+[m[32m            <groupId>com.googlecode.json-simple</groupId>[m
[32m+[m[32m            <artifactId>json-simple</artifactId>[m
[32m+[m[32m            <version>1.1.1</version>[m
[32m+[m[32m        </dependency>[m
[32m+[m[32m        <dependency>[m
[32m+[m[32m            <groupId>com.lambdaworks</groupId>[m
[32m+[m[32m            <artifactId>scrypt</artifactId>[m
[32m+[m[32m            <version>1.4.0</version>[m
[32m+[m[32m        </dependency>[m
[32m+[m[32m    </dependencies>[m
[32m+[m
[32m+[m
[32m+[m
[32m+[m[32m    <build>[m
[32m+[m[32m        <plugins>[m
[32m+[m[32m            <plugin>[m
[32m+[m[32m              <artifactId>maven-assembly-plugin</artifactId>[m
[32m+[m[32m              <configuration>[m
[32m+[m[32m                <archive>[m
[32m+[m[32m                  <manifest>[m
[32m+[m[32m                    <mainClass>com.SoNe.Client.ClientCLI</mainClass>[m
[32m+[m[32m                  </manifest>[m
[32m+[m[32m                </archive>[m
[32m+[m[32m                <descriptorRefs>[m
[32m+[m[32m                  <descriptorRef>jar-with-dependencies</descriptorRef>[m
[32m+[m[32m                </descriptorRefs>[m
[32m+[m[32m              </configuration>[m
[32m+[m[32m            </plugin>[m
[32m+[m[32m            <plugin>[m
[32m+[m[32m                <groupId>org.apache.maven.plugins</groupId>[m
[32m+[m[32m                <artifactId>maven-compiler-plugin</artifactId>[m
[32m+[m[32m                <version>3.8.1</version>[m
[32m+[m[32m                <configuration>[m
[32m+[m[32m                    <source>1.8</source>[m
[32m+[m[32m                    <target>1.8</target>[m
[32m+[m[32m                    <encoding>UTF-8</encoding>[m
[32m+[m[32m                </configuration>[m
[32m+[m[32m            </plugin>[m
[32m+[m[32m        </plugins>[m
[32m+[m[32m    </build>[m
[32m+[m[32m</project>[m
[1mdiff --git a/Client/src/main/java/com/SoNe/Client/ClientCLI.java b/Client/src/main/java/com/SoNe/Client/ClientCLI.java[m
[1mnew file mode 100644[m
[1mindex 0000000..229119d[m
[1m--- /dev/null[m
[1m+++ b/Client/src/main/java/com/SoNe/Client/ClientCLI.java[m
[36m@@ -0,0 +1,7 @@[m
[32m+[m[32mpackage com.SoNe.Client;[m
[32m+[m
[32m+[m[32mpublic class ClientCLI {[m
[32m+[m[32m    public static void main( String[] args ) {[m
[32m+[m[32m        System.out.println( "Hello World!" );[m
[32m+[m[32m    }[m
[32m+[m[32m}[m
[1mdiff --git a/Client/src/test/java/com/SoNe/Client/AppTest.java b/Client/src/test/java/com/SoNe/Client/AppTest.java[m
[1mnew file mode 100644[m
[1mindex 0000000..b5acc9d[m
[1m--- /dev/null[m
[1m+++ b/Client/src/test/java/com/SoNe/Client/AppTest.java[m
[36m@@ -0,0 +1,20 @@[m
[32m+[m[32mpackage com.SoNe.Client;[m
[32m+[m
[32m+[m[32mimport static org.junit.Assert.assertTrue;[m
[32m+[m
[32m+[m[32mimport org.junit.Test;[m
[32m+[m
[32m+[m[32m/**[m
[32m+[m[32m * Unit test for simple App.[m
[32m+[m[32m */[m
[32m+[m[32mpublic class AppTest[m[41m [m
[32m+[m[32m{[m
[32m+[m[32m    /**[m
[32m+[m[32m     * Rigorous Test :-)[m
[32m+[m[32m     */[m
[32m+[m[32m    @Test[m
[32m+[m[32m    public void shouldAnswerWithTrue()[m
[32m+[m[32m    {[m
[32m+[m[32m        assertTrue( true );[m
[32m+[m[32m    }[m
[32m+[m[32m}[m
[1mdiff --git a/Server/src/main/java/com/SoNe/Server/ClientHandler.java b/Server/src/main/java/com/SoNe/Server/ClientHandler.java[m
[1mindex b710afc..086514e 100644[m
[1m--- a/Server/src/main/java/com/SoNe/Server/ClientHandler.java[m
[1m+++ b/Server/src/main/java/com/SoNe/Server/ClientHandler.java[m
[36m@@ -2,6 +2,7 @@[m [mpackage com.SoNe.Server;[m
 [m
 import java.io.DataInputStream;[m
 import java.io.DataOutputStream;[m
[32m+[m[32mimport java.io.IOException;[m
 import java.net.Socket;[m
 [m
 public class ClientHandler extends Thread {[m
[36m@@ -19,5 +20,17 @@[m [mpublic class ClientHandler extends Thread {[m
     @Override[m
     public void run() {[m
         // Handle clients[m
[32m+[m
[32m+[m[32m        String received;[m
[32m+[m[32m        while (true) {[m
[32m+[m[32m            try {[m
[32m+[m[41m                [m
[32m+[m[32m                received = dataIn.readUTF();[m[41m [m
[32m+[m[32m                System.out.println(received);[m
[32m+[m[32m            } catch (IOException e) {[m
[32m+[m[32m                e.printStackTrace();[m
[32m+[m[32m                System.exit(1);[m
[32m+[m[32m            }[m
[32m+[m[32m        }[m
     }[m
 }[m
