import java.io.*;
import java.net.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.*;

class WebServer {
    public static void main(String args[]) throws Exception {
        String requestMessageLine;
        String fileName;

        // check if a port number is given as the first command line argument
        // if not argument is given, use port number 6789
        int myPort = 8080;
        if (args.length > 0) {
            try {
                myPort = Integer.parseInt(args[0]);
            } catch (ArrayIndexOutOfBoundsException e) {
                System.out.println("Need port number as argument");
                System.exit(-1);
            } catch (NumberFormatException e) {
                System.out.println("Please give port number as integer.");
                System.exit(-1);
            }
        }

        // set up connection socket
        ServerSocket listenSocket = new ServerSocket(myPort);

        // listen (i.e. wait) for connection request
        System.out.println("Web server waiting for request on port " + myPort);

        while (true) {

            Socket connectionSocket = listenSocket.accept();

            // set up the read and write end of the communication socket
            BufferedReader inFromClient = new BufferedReader(
                    new InputStreamReader(connectionSocket.getInputStream()));
            DataOutputStream outToClient = new DataOutputStream(
                    connectionSocket.getOutputStream());

            // retrieve first line of request and set up for parsing
            requestMessageLine = inFromClient.readLine();
            System.out.println("Request: " + requestMessageLine);
            StringTokenizer tokenizedLine = new StringTokenizer(requestMessageLine, " /");

            /*
             * check for GET request
             * if (tokenizedLine.nextToken().equals("GET")) {
             * fileName = tokenizedLine.nextToken();
             * 
             * // remove leading slash from line if exists
             * if (fileName.startsWith("/") == true)
             * fileName = fileName.substring(1);
             * }
             */

            String method = tokenizedLine.nextToken();
            System.out.println("HTTP Method: " + method);
            String route = tokenizedLine.nextToken();
            System.out.println("Route: " + route);
            String id = tokenizedLine.nextToken();
            System.out.println("ID: " + id);

            // get data: ...
            Class.forName("org.sqlite.JDBC"); // connect to its local database
            Connection connection = DriverManager.getConnection("jdbc:sqlite:store.db");
            PreparedStatement stmt = connection
                    .prepareStatement("SELECT name, price, quantity FROM products WHERE productID = ?");

            StringBuffer response = new StringBuffer("<html> <body> <h1> Product Information! </h1> ");

            if (route.equals("product")) {
                stmt.setInt(1, Integer.parseInt(id));
                ResultSet res = stmt.executeQuery();
                if (!res.next()) {
                    response.append("<p> No product with that id </p>");
                    response.append("</body> </html>");
                } else {
                    String name = res.getString(1);
                    double price = res.getDouble(2);
                    double quantity = res.getDouble(3);
                    response.append("<p> Product ID: " + id + "</p>");
                    response.append("<p> Product Name: " + name + "</p>");
                    response.append("<p> Price: " + price + "</p>");
                    response.append("<p> Quantity: " + quantity + "</p>");
                    response.append("</body> </html>");
                }
            }

            /*
             * access the requested file
             * File file = new File(fileName);
             * 
             * // convert file to a byte array
             * int numOfBytes = (int) file.length();
             * FileInputStream inFile = new FileInputStream (fileName);
             * byte[] fileInBytes = new byte[numOfBytes];
             * inFile.read(fileInBytes);
             */

            // Send reply

            outToClient.writeBytes("HTTP/1.1 200 OK\r\n");
            /*
             * if (fileName.endsWith(".jpg"))
             * outToClient.writeBytes ("Content-Type: image/jpeg\r\n");
             * if (fileName.endsWith(".gif"))
             * outToClient.writeBytes ("Content-Type: image/gif\r\n");
             */
            outToClient.writeBytes("Content-Type: text/html\r\n");
            outToClient.writeBytes("Content-Length: " + response.length() + "\r\n");
            outToClient.writeBytes("\r\n");
            outToClient.writeBytes(response.toString());

            // read and print out the rest of the request
            requestMessageLine = inFromClient.readLine();
            while (requestMessageLine.length() >= 5) {
                System.out.println("Request: " + requestMessageLine);
                requestMessageLine = inFromClient.readLine();
            }
            System.out.println("Request: " + requestMessageLine);

            connectionSocket.close();
        }
    }
    /*
     * else
     * {
     * System.out.println ("Bad Request Message");
     * }
     */

}
