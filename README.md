# Client Server architecture using Socket with CML Client and Web Client

## 1. How to run

### Compile:

`javac \*.java`

### Run:

#### Part 1:

- <b>Server:</b> `java -classpath ".:sqlite-jdbc-3.16.1.jar" ProductServer`
- <b>Client 1 (Get Product Information):</b> `java -classpath ".:sqlite-jdbc-3.16.1.jar" ProductClient`
  Enter product id to show information about that product (eg. 1 to display product with id 1)
- <b>Client 2 (Update Product Information):</b> `java -classpath ".:sqlite-jdbc-3.16.1.jar" UpdateProductClient`
  Enter the following format to update (id column info) (eg. 1 name Orange to change product with id 1 to name Orange)

#### Part 2:

- <b>Server:</b> `java -classpath ".:sqlite-jdbc-3.16.1.jar" WebServer`
- <b>Client:</b> Your choice of web browser with this URL format: http://localhost:8080/product/id

## 2. Code Explaination

### Part 1:

#### Server:

- <b>ProductServer.java:</b> This class is the server side of the application. Since we have 2 type of client programs. When the server start, it will wait for the client to send the client type to the server. Then the server will start a while loop to handle that type of client. For example, if we start the ProductClient, it will send a message "GET", when we start a UpdateProductClient, it will send a message "UPDATE". The server will create a connection to the local store.db file to get the data. Then it will send the data to the client. If the client is a UpdateProductClient, it will wait for the client to send the new data to update the product information.

```java
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
```

- <b>WebServer.java:</b> This class is the server side of the application, but will return a HTML page to the client. When the server start, it will wait for the client to send the product id to the server. Then the server will create a connection to the local store.db file to get the data. Then it will send the data to the client in HTML format.

```java
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
```

#### Client:

- <b>ProductClient.java:</b> This class is the first client side of the application. It will send a message "GET" to the server to tell the server that it is a ProductClient. Then it will wait for the user to enter the product id to show the information of that product.
- <b>UpdateProductClient.java:</b> This class is the second client side of the application. It will send a message "UPDATE" to the server to tell the server that it is a UpdateProductClient. Then it will wait for the user to enter the product id, attribute to update, and new data to update the information of that product.

## 3. UAT Video Recording

- <b>Question 1, 2:</b> https://youtu.be/5FsxD4zaDGQ
- <b>Question 3:</b> https://youtu.be/dJqqX5vFL14
