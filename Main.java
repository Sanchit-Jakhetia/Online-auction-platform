import java.sql.*;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        

        try {
            Class.forName("oracle.jdbc.driver.OracleDriver");
			connection = DriverManager.getConnection(
                "jdbc:oracle:thin:@localhost:1521:xe", "auction_user", "auction123");
			System.err.println("\nDatabase connected");
			System.out.println("\n|---------------------|");
			System.out.println("|     AUCTION APP     |");
			System.out.println("|---------------------|");
			System.out.println("|--------Login--------|");
            login();
        } catch (Exception e) {
            System.out.println("An unexpected error occurred.");
            e.printStackTrace();
        } finally {
            try {
				if (connection != null && !connection.isClosed()) {
					connection.close();
					System.err.println("Disconnected from database.");
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
        }
    }

    private static Connection connection;

    private static void login() throws SQLException {
        Scanner sc = new Scanner(System.in);
        System.out.print("|Username: ");
        String username = sc.nextLine();
        System.out.print("|Password: ");
        String password = sc.nextLine();
		System.out.println("|---------------------|");
		
        String sql = "SELECT user_id, role FROM users WHERE username = ? AND password = ?";
        PreparedStatement stmt = connection.prepareStatement(sql);
        stmt.setString(1, username);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();

        if (rs.next()) {
            int userId = rs.getInt("user_id");
            String role = rs.getString("role");
            System.out.println("\nLogin successful as " + role.toUpperCase());

            switch (role.toLowerCase()) {
                case "admin":
                    showAdminDashboard(userId);
                    break;
                case "buyer":
                    showBuyerDashboard(userId);
                    break;
                case "seller":
                    showSellerDashboard(userId);
                    break;
                default:
                    System.out.println("Unknown role assigned.");
            }
        } else {
            System.out.println("Invalid username or password.");
        }

        rs.close();
        stmt.close();
    }

    private static void showAdminDashboard(int userId) throws SQLException {
        Scanner sc = new Scanner(System.in);
        while (true) {
            System.out.println("\n|-------------------|");
            System.out.println("|    ADMIN PANEL    |");
            System.out.println("|-------------------|");
            System.out.println("|1. View All Users  |");
            System.out.println("|2. Manage Products |");
            System.out.println("|3. Logout          |");
			System.out.println("|-------------------|");
            System.out.print("Enter your choice: ");

            String input = sc.nextLine();
			System.out.println();
            switch (input) {
                case "1":
                    viewAllUsers();
                    break;
                case "2":
                    manageProducts();
                    break;
                case "3":
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid choice. Please try again.");
            }
        }
    }

    private static void viewAllUsers() throws SQLException {
        String query = "SELECT user_id, username, email, role, first_name, last_name FROM users ORDER BY user_id";
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

        System.out.println("|-----------------------| USERS |-----------------------|");
        System.out.printf("| %-3s | %-13s | %-10s | %-10s \t|\n", "ID", "Username", "Role", "Name");
        System.out.println("|-------------------------------------------------------|");

        while (rs.next()) {
            int id = rs.getInt("user_id");
            String username = rs.getString("username");
            String role = rs.getString("role");
            String name = rs.getString("first_name") + " " + rs.getString("last_name") + "    \t";
            System.out.printf("| %-3d | %-13s | %-10s | %-10s|\n", id, username, role, name);
        }

        System.out.println("|-------------------------------------------------------|");
        rs.close();
        stmt.close();
    }

	private static void manageProducts() throws SQLException {
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("\n|---------------------------|");
			System.out.println("|   MANAGE PRODUCTS PANEL   |");
			System.out.println("|---------------------------|");
			System.out.println("|1. View All Products       |");
			System.out.println("|2. Add New Product         |");
			System.out.println("|3. Update Product          |");
			System.out.println("|4. Delete Product          |");
			System.out.println("|5. Back to Admin Panel     |");
			System.out.println("|---------------------------|");
			System.out.print("Enter your choice: ");

			String input = sc.nextLine();

			switch (input) {
				case "1":
					viewAllProducts();
					break;
				case "2":
					addProduct();
					break;
				case "3":
					updateProduct();
					break;
				case "4":
					deleteProduct();
					break;
				case "5":
					return;
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		}
	}
	
    private static void viewAllProducts() throws SQLException {
		String query = "SELECT product_id, seller_id, product_name, category, description, start_time, end_time, highest_bid, highest_bidder, status, created_at, updated_at FROM products ORDER BY product_id";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		System.out.println();
		System.out.println("|---------------------------------------------------------------------| PRODUCTS |-----------------------------------------------------------------------|");
		System.out.printf("| %-3s | %-5s | %-20s | %-15s | %-23s | %-23s | %-8s | %-10s | %-10s | %-40s |\n", 
						  "ID", "S-ID", "Product Name", "Category", "Start Time", "End Time", "Bid", "Bidder", "Status", "Description");
		System.out.println("|-----------------------------------------------------------------------------------------------------------------------------------------------------------|");

		while (rs.next()) {
			int productId = rs.getInt("product_id");
			int sellerId = rs.getInt("seller_id");
			String productName = rs.getString("product_name");
			String category = rs.getString("category");
			String description = rs.getString("description");
			Timestamp startTime = rs.getTimestamp("start_time");
			Timestamp endTime = rs.getTimestamp("end_time");
			double highestBid = rs.getDouble("highest_bid");
			String highestBidder = rs.getString("highest_bidder");
			String status = rs.getString("status");
			Timestamp createdAt = rs.getTimestamp("created_at");
			Timestamp updatedAt = rs.getTimestamp("updated_at");

			String startTimeStr = (startTime != null) ? startTime.toString() : "N/A";
			String endTimeStr   = (endTime != null) ? endTime.toString() : "N/A";
	
			System.out.printf("| %-3d | %-5d | %-20s | %-15s | %-23s | %-23s | %-8.2f | %-10s | %-10s | %-40s |\n", 
                      productId, sellerId, productName, category, startTimeStr, endTimeStr, highestBid, highestBidder, status, description);
		}

		System.out.println("|-----------------------------------------------------------------------------------------------------------------------------------------------------------|");		rs.close();
		stmt.close();
	}
	
	private static void addProduct() throws SQLException {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter product name: ");
		String productName = sc.nextLine();
		System.out.print("Enter product category: ");
		String category = sc.nextLine();
		System.out.print("Enter product description: ");
		String description = sc.nextLine();
		System.out.print("Enter start time (YYYY-MM-DD HH:MM:SS): ");
		String startTimeString = sc.nextLine();
		System.out.print("Enter end time (YYYY-MM-DD HH:MM:SS): ");
		String endTimeString = sc.nextLine();
		System.out.print("Enter initial highest bid: ");
		double highestBid = sc.nextDouble();
		sc.nextLine();  
		System.out.print("Enter product status (active, closed, pending): ");
		String status = sc.nextLine();

		Timestamp startTime = Timestamp.valueOf(startTimeString);
		Timestamp endTime = Timestamp.valueOf(endTimeString);

		String sql = "INSERT INTO products (seller_id, product_name, category, description, start_time, end_time, highest_bid, highest_bidder, status, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, 1);
		stmt.setString(2, productName);
		stmt.setString(3, category);
		stmt.setString(4, description);
		stmt.setTimestamp(5, startTime);
		stmt.setTimestamp(6, endTime);
		stmt.setDouble(7, highestBid);
		stmt.setString(8, "");  
		stmt.setString(9, status);
		stmt.setTimestamp(10, new Timestamp(System.currentTimeMillis()));
		stmt.setTimestamp(11, new Timestamp(System.currentTimeMillis()));

		int rowsAffected = stmt.executeUpdate();
		if (rowsAffected > 0) {
			System.out.println("Product added successfully!");
		} else {
			System.out.println("Failed to add product.");
		}

		stmt.close();
	}

	private static void updateProduct() throws SQLException {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter product ID to update: ");
		int productId = sc.nextInt();
		sc.nextLine();  

		String selectSql = "SELECT * FROM products WHERE product_id = ?";
		PreparedStatement selectStmt = connection.prepareStatement(selectSql);
		selectStmt.setInt(1, productId);
		ResultSet rs = selectStmt.executeQuery();

		if (rs.next()) {
			System.out.println("Current product details:");
			System.out.println("Name: " + rs.getString("product_name"));
			System.out.println("Category: " + rs.getString("category"));
			System.out.println("Description: " + rs.getString("description"));
			System.out.println("Start Time: " + rs.getTimestamp("start_time"));
			System.out.println("End Time: " + rs.getTimestamp("end_time"));
			System.out.println("Highest Bid: " + rs.getDouble("highest_bid"));
			System.out.println("Highest Bidder: " + rs.getString("highest_bidder"));
			System.out.println("Status: " + rs.getString("status"));

			System.out.print("Enter new name (or press Enter to keep current): ");
			String productName = sc.nextLine();
			if (productName.isEmpty()) productName = rs.getString("product_name");

			System.out.print("Enter new category (or press Enter to keep current): ");
			String category = sc.nextLine();
			if (category.isEmpty()) category = rs.getString("category");

			System.out.print("Enter new description (or press Enter to keep current): ");
			String description = sc.nextLine();
			if (description.isEmpty()) description = rs.getString("description");

			System.out.print("Enter new start time (or press Enter to keep current): ");
			String startTimeString = sc.nextLine();
			Timestamp startTime = startTimeString.isEmpty() ? rs.getTimestamp("start_time") : Timestamp.valueOf(startTimeString);

			System.out.print("Enter new end time (or press Enter to keep current): ");
			String endTimeString = sc.nextLine();
			Timestamp endTime = endTimeString.isEmpty() ? rs.getTimestamp("end_time") : Timestamp.valueOf(endTimeString);

			System.out.print("Enter new highest bid (or press Enter to keep current): ");
			String highestBidInput = sc.nextLine();
			double highestBid = highestBidInput.isEmpty() ? rs.getDouble("highest_bid") : Double.parseDouble(highestBidInput);

			System.out.print("Enter new status (or press Enter to keep current): ");
			String status = sc.nextLine();
			if (status.isEmpty()) status = rs.getString("status");

			String updateSql = "UPDATE products SET product_name = ?, category = ?, description = ?, start_time = ?, end_time = ?, highest_bid = ?, highest_bidder = ?, status = ?, updated_at = ? WHERE product_id = ?";
			PreparedStatement updateStmt = connection.prepareStatement(updateSql);
			updateStmt.setString(1, productName);
			updateStmt.setString(2, category);
			updateStmt.setString(3, description);
			updateStmt.setTimestamp(4, startTime);
			updateStmt.setTimestamp(5, endTime);
			updateStmt.setDouble(6, highestBid);
			updateStmt.setString(7, rs.getString("highest_bidder"));  
			updateStmt.setString(8, status);
			updateStmt.setTimestamp(9, new Timestamp(System.currentTimeMillis()));
			updateStmt.setInt(10, productId);

			int rowsAffected = updateStmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("Product updated successfully!");
			} else {
				System.out.println("Failed to update product.");
			}

			updateStmt.close();
		} else {
			System.out.println("Product not found.");
		}

		rs.close();
		selectStmt.close();
	}

	private static void deleteProduct() throws SQLException {
		Scanner sc = new Scanner(System.in);
		System.out.print("Enter product ID to delete: ");
		int productId = sc.nextInt();

		String sql = "DELETE FROM products WHERE product_id = ?";
		PreparedStatement stmt = connection.prepareStatement(sql);
		stmt.setInt(1, productId);

		int rowsAffected = stmt.executeUpdate();
		if (rowsAffected > 0) {
			System.out.println("Product deleted successfully!");
		} else {
			System.out.println("Product not found.");
		}

		stmt.close();
	}

	private static void showBuyerDashboard(int userId) throws SQLException {
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("|----------------------|");
			System.out.println("|   BUYER DASHBOARD    |");
			System.out.println("|----------------------|");
			System.out.println("|1. View Products      |");
			System.out.println("|2. Place a Bid        |");
			System.out.println("|3. View My Bids       |");
			System.out.println("|4. Logout             |");
			System.out.println("|----------------------|");
			System.out.print("Enter your choice: ");

			String input = sc.nextLine();

			switch (input) {
				case "1":
					viewAvailableProducts();
					break;
				case "2":
					placeBid(userId);
					break;
				case "3":
					viewMyBids(userId);
					break;
				case "4":
					System.out.println("Logging out...");
					return;
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private static void viewAvailableProducts() throws SQLException {
		String query = "SELECT product_id, product_name, category, highest_bid, end_time " +
					   "FROM products WHERE status = 'active' ORDER BY end_time ASC";

		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		System.out.println();
		System.out.println("|---------------------------------| AVAILABLE PRODUCTS |---------------------------------|");
		System.out.printf("| %-5s | %-20s | %-15s | %-10s | %-23s |\n", "ID", "Product Name", "Category", "Highest Bid", "Ends At");
		System.out.println("|----------------------------------------------------------------------------------------|");

		while (rs.next()) {
			int productId = rs.getInt("product_id");
			String productName = rs.getString("product_name");
			String category = rs.getString("category");
			double highestBid = rs.getDouble("highest_bid");
			Timestamp endTime = rs.getTimestamp("end_time");
			String endTimeStr = (endTime != null) ? endTime.toString() : "N/A";

			System.out.printf("| %-5d | %-20s | %-15s | %-11.2f | %-23s |\n",
							  productId, productName, category, highestBid, endTimeStr);
		}

		System.out.println("|----------------------------------------------------------------------------------------|\n");

		rs.close();
		stmt.close();
	}

	private static void placeBid(int userId) throws SQLException {
		Scanner sc = new Scanner(System.in);

		String query = "SELECT product_id, product_name, highest_bid FROM products WHERE status = 'active' ORDER BY end_time";
		Statement stmt = connection.createStatement();
		ResultSet rs = stmt.executeQuery(query);

		System.out.println();
		System.out.println("|----------------| ACTIVE PRODUCTS |----------------|");
		System.out.printf("| %-5s | %-25s | %-12s |\n", "ID", "Product Name", "Highest Bid");
		System.out.println("|---------------------------------------------------|");

		while (rs.next()) {
			int id = rs.getInt("product_id");
			String name = rs.getString("product_name");
			double highestBid = rs.getDouble("highest_bid");
			System.out.printf("| %-5d | %-25s | %-12.2f |\n", id, name, highestBid);
		}

		System.out.println("|---------------------------------------------------|\n");
		rs.close();
		stmt.close();

		System.out.print("Enter the Product ID you want to bid on: ");
		int productId = Integer.parseInt(sc.nextLine());

		String checkQuery = "SELECT highest_bid, end_time FROM products WHERE product_id = ? AND status = 'active'";
		PreparedStatement checkStmt = connection.prepareStatement(checkQuery);
		checkStmt.setInt(1, productId);
		ResultSet checkRs = checkStmt.executeQuery();

		if (!checkRs.next()) {
			System.out.println("Invalid product ID or auction is closed.");
			return;
		}

		double currentBid = checkRs.getDouble("highest_bid");
		Timestamp endTime = checkRs.getTimestamp("end_time");

		if (endTime != null && endTime.before(new Timestamp(System.currentTimeMillis()))) {
			System.out.println("Bidding time for this product has expired.");
			return;
		}

		System.out.print("Enter your bid (must be higher than " + currentBid + "): ");
		double newBid = Double.parseDouble(sc.nextLine());

		if (newBid <= currentBid) {
			System.out.println("Your bid must be higher than the current highest bid.");
			return;
		}

		try {
			connection.setAutoCommit(false);

			String insertBid = "INSERT INTO bids (product_id, buyer_id, bid_amount, bid_time, status) " +
							   "VALUES (?, ?, ?, CURRENT_TIMESTAMP, 'active')";
			PreparedStatement insertStmt = connection.prepareStatement(insertBid);
			insertStmt.setInt(1, productId);
			insertStmt.setInt(2, userId);
			insertStmt.setDouble(3, newBid);
			insertStmt.executeUpdate();
			insertStmt.close();

			String updateProduct = "UPDATE products SET highest_bid = ?, highest_bidder = ? WHERE product_id = ?";
			PreparedStatement updateStmt = connection.prepareStatement(updateProduct);
			updateStmt.setDouble(1, newBid);
			updateStmt.setInt(2, userId);
			updateStmt.setInt(3, productId);
			updateStmt.executeUpdate();
			updateStmt.close();

			connection.commit();
			System.out.println("Bid placed successfully!");

		} catch (SQLException ex) {
			connection.rollback();
			System.out.println("Failed to place bid. Transaction rolled back.");
			ex.printStackTrace();
		} finally {
			connection.setAutoCommit(true);
			checkRs.close();
			checkStmt.close();
		}
	}

	private static void viewMyBids(int userId) throws SQLException {
		String query = "SELECT b.bid_id, b.product_id, p.product_name, p.category, b.bid_amount, b.bid_time, b.status " +
					   "FROM bids b JOIN products p ON b.product_id = p.product_id " +
					   "WHERE b.buyer_id = ? ORDER BY b.bid_time DESC";

		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, userId);
		ResultSet rs = stmt.executeQuery();

		System.out.println();
		System.out.println("|------------------------------------------| MY BIDS HISTORY |------------------------------------------|");
		System.out.printf("| %-5s | %-4s | %-20s | %-12s | %-10s | %-23s | %-8s |\n",
						  "BID", "PID", "Product Name", "Category", "Amount", "Time", "Status");
		System.out.println("|------------------------------------------------------------------------------------------------------|");

		boolean found = false;
		while (rs.next()) {
			found = true;
			int bidId = rs.getInt("bid_id");
			int productId = rs.getInt("product_id");
			String productName = rs.getString("product_name");
			String category = rs.getString("category");
			double bidAmount = rs.getDouble("bid_amount");
			Timestamp bidTime = rs.getTimestamp("bid_time");
			String status = rs.getString("status");

			System.out.printf("| %-5d | %-4d | %-20s | %-12s | %-10.2f | %-23s | %-8s |\n",
							  bidId, productId, productName, category, bidAmount, bidTime.toString(), status);
		}

		if (!found) {
			System.out.println("|                     You have not placed any bids yet.                  |");
		}

		System.out.println("|------------------------------------------------------------------------------------------------------|\n");

		rs.close();
		stmt.close();
	}

	private static void showSellerDashboard(int userId) throws SQLException {
		Scanner sc = new Scanner(System.in);
		while (true) {
			System.out.println("\n|----------------------|");
			System.out.println("|   SELLER DASHBOARD   |");
			System.out.println("|----------------------|");
			System.out.println("|1. View My Products   |");
			System.out.println("|2. Add New Product    |");
			System.out.println("|3. View Bids on Item  |");
			System.out.println("|4. Logout             |");
			System.out.println("|----------------------|");
			System.out.print("Enter your choice: ");

			String input = sc.nextLine();

			switch (input) {
				case "1":
					viewMyProducts(userId);
					break;
				case "2":
					addProduct(userId);
					break;
				case "3":
					viewBidsOnProduct(userId);
					break;
				case "4":
					System.out.println("Logging out...");
					return;
				default:
					System.out.println("Invalid choice. Please try again.");
			}
		}
	}

	private static void viewMyProducts(int userId) throws SQLException {
		String query = "SELECT product_id, product_name, category, highest_bid, status " +
					   "FROM products WHERE seller_id = ? ORDER BY created_at DESC";

		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, userId);
		ResultSet rs = stmt.executeQuery();

		System.out.println();
		System.out.println("|--------------------------| MY PRODUCTS |--------------------------|");
		System.out.printf("| %-5s | %-15s | %-14s | %-10s | %-8s |\n", 
						  "ID", "Product Name", "Category", "Highest Bid", "Status");
		System.out.println("|-------------------------------------------------------------------|");

		boolean found = false;
		while (rs.next()) {
			found = true;
			int id = rs.getInt("product_id");
			String name = rs.getString("product_name");
			String category = rs.getString("category");
			double highestBid = rs.getDouble("highest_bid");
			String status = rs.getString("status");

			System.out.printf("| %-5d | %-15s | %-14s | %-11.2f | %-8s |\n",
							  id, name, category, highestBid, status);
		}

		if (!found) {
			System.out.println("|            You have not listed any products yet.                  |");
		}

		System.out.println("|-------------------------------------------------------------------|\n");

		rs.close();
		stmt.close();
	}

	private static void addProduct(int userId) throws SQLException {
		Scanner sc = new Scanner(System.in);

		System.out.print("Enter product name: ");
		String productName = sc.nextLine();

		System.out.print("Enter product category: ");
		String category = sc.nextLine();

		System.out.print("Enter product description: ");
		String description = sc.nextLine();

		System.out.print("Enter start time (YYYY-MM-DD HH:MM:SS): ");
		String startTimeStr = sc.nextLine();

		System.out.print("Enter end time (YYYY-MM-DD HH:MM:SS): ");
		String endTimeStr = sc.nextLine();
		
		System.out.print("Enter initial highest bid: ");
		double highestBid = sc.nextDouble();

		Timestamp startTime = Timestamp.valueOf(startTimeStr);
		Timestamp endTime = Timestamp.valueOf(endTimeStr);

		if (startTime.after(endTime)) {
			System.out.println("Start time must be before end time.");
			return;
		}

		String query = "INSERT INTO products (seller_id, product_name, category, description, start_time, end_time, highest_bid, status, created_at, updated_at) " +
					   "VALUES (?, ?, ?, ?, ?, ?, 0.0, 'active', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)";

		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, userId);
		stmt.setString(2, productName);
		stmt.setString(3, category);
		stmt.setString(4, description);
		stmt.setTimestamp(5, startTime);
		stmt.setTimestamp(6, endTime);

		int rowsInserted = stmt.executeUpdate();
		if (rowsInserted > 0) {
			System.out.println("Product added successfully! Your auction is now live.");
		} else {
			System.out.println("Failed to add product.");
		}

		stmt.close();
	}

	private static void viewBidsOnProduct(int userId) throws SQLException {
		Scanner sc = new Scanner(System.in);

		String query = "SELECT product_id, product_name FROM products WHERE seller_id = ? ORDER BY created_at DESC";
		PreparedStatement stmt = connection.prepareStatement(query);
		stmt.setInt(1, userId);
		ResultSet rs = stmt.executeQuery();

		System.out.println();
		System.out.println("|---------| YOUR PRODUCTS |---------|");
		System.out.printf("| %-5s | %-25s |\n", "ID", "Product Name");
		System.out.println("|-----------------------------------|");

		boolean found = false;
		while (rs.next()) {
			found = true;
			int productId = rs.getInt("product_id");
			String productName = rs.getString("product_name");

			System.out.printf("| %-5d | %-25s |\n", productId, productName);
		}

		if (!found) {
			System.out.println("|            You have not listed any products yet.                  |");
			return;
		}

		System.out.println("|-----------------------------------|\n");

		System.out.print("Enter the Product ID to view bids: ");
		int productId = Integer.parseInt(sc.nextLine());

		String bidQuery = "SELECT b.bid_id, b.buyer_id, b.bid_amount, b.bid_time, b.status " +
						  "FROM bids b WHERE b.product_id = ? ORDER BY b.bid_time DESC";
		PreparedStatement bidStmt = connection.prepareStatement(bidQuery);
		bidStmt.setInt(1, productId);
		ResultSet bidRs = bidStmt.executeQuery();

		System.out.println();
		System.out.println("|-----------------------| PRODUCT BIDS |-----------------------|");
		System.out.printf("| %-3s | %-5s | %-7s | %-23s | %-10s |\n", "BID", "BUYER", "Amount", "Time", "Status");
		System.out.println("|--------------------------------------------------------------|");

		boolean bidFound = false;
		while (bidRs.next()) {
			bidFound = true;
			int bidId = bidRs.getInt("bid_id");
			int buyerId = bidRs.getInt("buyer_id");
			double bidAmount = bidRs.getDouble("bid_amount");
			Timestamp bidTime = bidRs.getTimestamp("bid_time");
			String status = bidRs.getString("status");

			System.out.printf("| %-3d | %-5d | %-7.2f | %-23s | %-9s |\n",
							  bidId, buyerId, bidAmount, bidTime.toString(), status);
		}

		if (!bidFound) {
			System.out.println("| No bids placed yet for this product.                    |");
		}

		System.out.println("|--------------------------------------------------------------|\n");

		bidRs.close();
		bidStmt.close();
		rs.close();
		stmt.close();
	}

}