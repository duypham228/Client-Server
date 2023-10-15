import java.io.*;
import java.net.*;

public class UpdateProductClient {
    public static void main(String[] args) throws Exception {

        String serverHostname = new String("127.0.0.1");
        int portNumber = 7777;

        System.out.println("Attemping to connect to host " + serverHostname + " on port " + portNumber);

        Socket socket = new Socket(serverHostname, portNumber);
        PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        BufferedReader stdIn = new BufferedReader(new InputStreamReader(System.in));

        out.println("UPDATE");
        System.out.println(in.readLine());

        while (true) {
            System.out.print("Enter (Product ID  Column  updatedInfo): ");
            String userInput = stdIn.readLine();
            out.println(userInput);
            if (userInput.toLowerCase().equals("quit")) break;
            String serverAnswer = in.readLine();
            System.out.println("Product Info: " + serverAnswer);
        }

        out.close();
        in.close();
        stdIn.close();
        socket.close();
    }
}

