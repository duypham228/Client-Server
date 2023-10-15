import java.net.*;
import java.io.*;
import java.sql.*;

public class ProductServer {
    public static void main(String[] args) throws Exception {
        Class.forName("org.sqlite.JDBC");  // connect to its local database
        Connection connection = DriverManager.getConnection("jdbc:sqlite:store.db");
        PreparedStatement stmt = connection.prepareStatement("SELECT name, price, quantity FROM products WHERE productID = ?");
        PreparedStatement update_quantity_stmt = connection.prepareStatement("UPDATE products SET quantity = ? WHERE productID = ?");
        PreparedStatement update_price_stmt = connection.prepareStatement("UPDATE products SET price = ? WHERE productID = ?");
        PreparedStatement update_name_stmt = connection.prepareStatement("UPDATE products SET name = ? WHERE productID = ?");

        System.out.println("Waiting for connection at port 7777.....");
        ServerSocket serverSocket = new ServerSocket(7777);
        Socket clientSocket = serverSocket.accept();

        System.out.println("Connection successful");
        System.out.println("Waiting for input.....");

        BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);

        String mode = in.readLine();

        if (mode.equals("GET")) {
            out.println("GET PRODUCT CLIENT SETUP");
            while (true) {
                String inputLine = in.readLine();
                if (inputLine == null || inputLine.toLowerCase().equals("quit")) break;

                System.out.println("Client asks for product ID: " + inputLine);
                int id = Integer.parseInt(inputLine);
                stmt.setInt(1, id);
                ResultSet res = stmt.executeQuery();
                if (!res.next())
                    out.println(-1);         // No product with that id
                else
                    out.println("Name: " + res.getString(1) + " - Price: " + res.getDouble(2) + " - Quantity: " + res.getInt(3));
            }
        } else if (mode.equals("UPDATE")) {
            out.println("UPDATE PRODUCT CLIENT SETUP");
            while (true) {
                String inputLine = in.readLine();
                if (inputLine == null || inputLine.toLowerCase().equals("quit")) break;

                System.out.println("Client want to update for product ID: " + inputLine);
                String[] inputLineArray = inputLine.split(" ");
                int id = Integer.parseInt(inputLineArray[0]);

                String column = inputLineArray[1];
                if (column.equals("name")) {
                    String updatedInfo = inputLineArray[2];
                    update_name_stmt.setString(1, updatedInfo);
                    update_name_stmt.setInt(2, id);
                    update_name_stmt.executeUpdate();
                    out.println("Name updated");
                } else if (column.equals("price")) {
                    double updatedInfo = Double.parseDouble(inputLineArray[2]);
                    update_price_stmt.setDouble(1, updatedInfo);
                    update_price_stmt.setInt(2, id);
                    update_price_stmt.executeUpdate();
                    out.println("Price updated");
                } else if (column.equals("quantity")) {
                    int updatedInfo = Integer.parseInt(inputLineArray[2]);
                    update_quantity_stmt.setInt(1, updatedInfo);
                    update_quantity_stmt.setInt(2, id);
                    update_quantity_stmt.executeUpdate();
                    out.println("Quantity updated");
                } else {
                    out.println("Invalid column");
                    continue;

                }
            }
        }
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();

    }
}
