# Client Server architecture using Socket with CML Client and Web Client

## How to run

Compile:
javac \*.java

Run:
Part 1:
Server: java -classpath ".:sqlite-jdbc-3.16.1.jar" ProductServer
Client 1 (Get Product Information): java -classpath ".:sqlite-jdbc-3.16.1.jar" ProductClient
Client 2 (Update Product Information): java -classpath ".:sqlite-jdbc-3.16.1.jar" UpdateProductClient
Part 2:
Server: java -classpath ".:sqlite-jdbc-3.16.1.jar" WebServer
Client: Your choice of web browser with this URL format: http://localhost:8080/product/id

## Code Explaination
