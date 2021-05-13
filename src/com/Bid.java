package com;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author IT19374666
 *
 */
public class Bid {
	
	//Create connection
	public Connection connect() {
		Connection con = null;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			con = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/buyerandpaymentdb","root", "");
			 	
		//For testing
			 System.out.print("Successfully connected");
		
		} catch(Exception e){
				e.printStackTrace();
		}

			
		return con;
	}
		
	
	//Insert bids
	public String insertBid(String itemCode, String customerId, String amount, String sConditions,String dueDate) {
	
		String output = "";
		String accepted = "No";
		try {
			Connection con = connect();
			if (con == null){
				return "Error while connecting to the database";
			}

			// create a prepared statement
			String query = " insert into bids(`itemCode`,`customerId`,`Amount`,`sConditions`,`dueDate`,`accepted`)"
				+ " values (?, ?, ?, ?, ?,?)";
			
			PreparedStatement preparedStmt = con.prepareStatement(query);
			// binding values
			
			
			preparedStmt.setString(1, itemCode);
			preparedStmt.setString(2, customerId);
			preparedStmt.setDouble(3, Double.parseDouble(amount));
			preparedStmt.setString(4, sConditions); 
			preparedStmt.setString(5, dueDate);
			preparedStmt.setString(6,accepted);

			//execute the statement
			preparedStmt.execute();
			con.close();
			
			String newBids = readBids();
			output = "{\"status\":\"success\", \"data\": \"" +
			newBids + "\"}";
			
		}catch (Exception e) {
			output = "{\"status\":\"error\", \"data\": \"" +
					  "Error while inserting the bid.\"}";
					 System.err.println(e.getMessage());
		}
		 	
		return output;
	}

	//Read all the bids
	public String readBids() {
		String output = "";
		try {
			Connection con = connect();
			if (con == null) {
				return "Error while connecting to the database for reading.";
			}
		// Prepare the html table to be displayed
			output = "<table border='1'><tr>"
					+ "<th>Bid Id</th>"
					+"<th>Item Code</th>"
					+ "<th>Customer Id</th>"
					+ "<th>Amount</th>"
					+ "<th>Special Conditions</th>"
					+ "<th>Due date</th>"
					+ "<th>Accepted</th>"
					+"<th>Update</th>"
					+"<th>Delete</th>"
					+ "</tr>";
			String query = "select * from bids";
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery(query);
			
			// iterate through the rows in the result set
			while (rs.next()) {
				String bidId = Integer.toString(rs.getInt("bidid"));
				String itemCode = rs.getString("itemCode");
				String customerId = rs.getString("customerId");
				String amount = Double.toString(rs.getDouble("amount"));
				String specConditions = rs.getString("sConditions");
				String dueDate = rs.getString("dueDate");
				String accepted = rs.getString("accepted");
				// Add a row into the html table
				output += "<tr><td><input id='hidBidIDUpdate'"
						+"name='hidItemIDUpdate' type='hidden' value='" +bidId+"'</td></tr>";
				output += "<tr><td>" + bidId + "</td>";
				output += "<td>" + itemCode + "</td>";
				output += "<td>" + customerId + "</td>";
				output += "<td>" + amount + "</td>"; 
				output += "<td>" + specConditions +"</td>";
				output += "<td>" +  dueDate  +"</td>";
				output += "<td>" + accepted   +"</td>";
				
				// buttons
				output += "<td><input name='btnUpdate' type='button' value='Update' "
				+ "class='btnUpdate btn btn-secondary' data-bidid='" + bidId + "'></td>"
				+ "<td><input id='btnRemove' type='button' value='Remove' "
				+ "class='btnRemove btn btn-danger' data-bidid='" + bidId + "'></td></tr>";
				 
				
			}
			
			
			
			con.close();
			// Complete the html table
			
			output += "</table>";
		} catch (Exception e) {
			output = "Error while reading the bids";
			System.err.println(e.getMessage());
		}
		return output;
	}
	
		

	//select one bid
	public String getBid(String itemId, String customerId) {
		String output = "";
		
		try {
			Connection con = this.connect();
			if (con == null) {
				return "Error while connecting to retrieve a specific bid";
			}		
			
			String query = "select * from bids where itemCode = ? and customerId = ?";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, itemId);
			preparedStatement.setString(2, customerId);
			ResultSet rs = preparedStatement.executeQuery();
			
			if (rs.next()) {	
				String bidId = rs.getString("bidid");
				String itemCode = rs.getString("itemCode");
				String customerID = rs.getString("customerId");
				String amount = Double.toString(rs.getDouble("Amount"));
				String specConditions = rs.getString("sConditions");
				String dueDate = rs.getString("dueDate");
				String accepted = rs.getString("accepted");
				
				output = "<form method='post' action='bids.jsp'>";	
				output += "<h2>Bid ID: " + bidId + "</h2>";
				output += "Item code: <input name=\"itemCode\" type=\"text\" value=\""+ itemCode + "\" class=\"form-control\"><br>";
				output += "Customer Id: <input name=\"customerId\" type=\"text\" value=\""+ customerID + "\" class=\"form-control\"><br>";
				output += "Bid Amount: <input name=\"amount\" type=\"text\" value=\""+ amount + "\" class=\"form-control\"><br>";
				output += "Special Conditions: <input name=\"conditions\" type=\"text\" value=\""+ specConditions + "\" class=\"form-control\"><br>";
				output += "Due date: <input name=\"duedate\" type=\"text\" value=\""+ dueDate + "\" class=\"form-control\"><br>";
				output += "Accepted: <input name=\"accepted\" type=\"text\" value=\""+ accepted + "\" class=\"form-control\"><br>";
				output += "<input name='bidID' type='hidden' value='" + bidId+ "'>";
			
			

			}
			
			con.close();			
			
		} catch (Exception e) {
			output = "Error while getting bid ";
			System.err.println(e.getMessage());
		}
		
		return output;
	}
		
		
	//select bids for an item	
	public String readBidsForItem(String itemId) {
		String output = "";
		try {
			Connection con = connect();
			if (con == null) {
				return "Error while connecting to the database for reading.";
			}
			// Prepare the html table to be displayed
			output = "<table border='1'><tr>"
					+ "<th>Bid Id</th>"
					+"<th>Item Code</th>"
					+ "<th>Customer Id</th>"
					+ "<th>Amount</th>"
					+ "<th>Special Conditions</th>"
					+ "<th>Due date</th>"
					+ "<th>Accepted</th></tr>";
			String query = "select * from bids where itemCode = ?  order by amount desc";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, itemId);
			
			ResultSet rs = preparedStatement.executeQuery();
			// iterate through the rows in the result set
			while (rs.next()) {
				String bidId = Integer.toString(rs.getInt("bidid"));
				String itemCode = rs.getString("itemCode");
				String customerId = rs.getString("customerId");
				String amount = Double.toString(rs.getDouble("amount"));
				String specConditions = rs.getString("sConditions");
				String dueDate = rs.getString("dueDate");
				String accepted = rs.getString("accepted");
				// Add a row into the html table
				output += "<tr><td>" + bidId + "</td>";
				output += "<td>" + itemCode + "</td>";
				output += "<td>" + customerId + "</td>";
				output += "<td>" + amount + "</td>"; 
				output += "<td>" + specConditions +"</td>";
				output += "<td>" +  dueDate  +"</td>";
				output += "<td>" + accepted   +"</td></tr>";
		 
					
			}
			con.close();
			// Complete the html table
				
			output += "</table>";
		} catch (Exception e) {
			output = "Error while reading the bids";
			System.err.println(e.getMessage());
		}
		return output;
	}
	
	//select the lowest bid
	public String getLowestBid(String itemId) {
		String output = "";
		
		try {
			Connection con = this.connect();
			if (con == null) {
				return "Error while connecting to retrieve a specific bid";
			}		
			
			String query = "select bidid from bids where itemCode = ? and amount In (select Min(amount) from bids where itemCode = ? group by itemCode) ";
			PreparedStatement preparedStatement = con.prepareStatement(query);
			preparedStatement.setString(1, itemId);
			preparedStatement.setString(2, itemId);
			ResultSet rs = preparedStatement.executeQuery();
			
			if (rs.next()) {	
				String bidId = rs.getString("bidid");
				
				output = bidId;
				
			
			

			}
			
			con.close();			
			
		} catch (Exception e) {
			output = "Error while getting bid ";
			System.err.println(e.getMessage());
		}
			
		return output;
	}
				
				
		
				
		
	//Update bid details
	public String updateBids(String bidId, String amount, String sConditions, String dueDate) {
		String output = "";
		
		
		
		try {
			Connection con = connect();
			if (con == null){
				return "Error while connecting to the database";
			}
			// create a prepared statement
			String query = "update bids set `Amount` = ? ,`sConditions` = ?,`dueDate` = ? where `bidid` = ?";		
			PreparedStatement preparedStmt = con.prepareStatement(query);
			
			// binding values
			
			preparedStmt.setDouble(1, Double.parseDouble(amount));
			preparedStmt.setString(2, sConditions);
			preparedStmt.setString(3, dueDate); 
			preparedStmt.setInt(4, Integer.parseInt(bidId));

			//execute the statement
			preparedStmt.executeUpdate();
			con.close();
			
			String newBids = readBids();
			output = "{\"status\":\"success\", \"data\": \"" +
					newBids + "\"}";
		}catch (Exception e) {
			output = "{\"status\":\"error\", \"data\": \"" +
					  "Error while updating the bid.\"}";
					 System.err.println(e.getMessage());
	 	}
	 	
		return output;
		
		
	}
		
	//Accepted bid
	public String updateBidtoAccepted(String bidId) {
		String output = "";
		String accepted = "Yes";
		
		
		
		try {
			Connection con = connect();
			if (con == null){
				return "Error while connecting to the database";
			}
			// create a prepared statement
			String query = "update bids set `accepted` = ?  where `bidid` = ?";		
			PreparedStatement preparedStmt = con.prepareStatement(query);
			
			// binding values
			
			
			preparedStmt.setString(1, accepted);
			preparedStmt.setInt(2, Integer.parseInt(bidId));

			//execute the statement
			preparedStmt.executeUpdate();
			con.close();
			output = "Updated successfully";
		}catch (Exception e) {
			output = "Error while updating accepted bids";
			System.err.println(e.getMessage());
	 	}
	 	
		return output;
		
		
	}
		
		
	//Delete a bid
	public String deleteBid (String bidID) {
			
			
		String output = "";
		
		try {
			
			Connection con = connect();
			
			if (con == null)
			{
				return "Error while connecting to the database";
			}
	
			// create a prepared statement		
			String query = "delete from bids where bidid = ?" ;							
			PreparedStatement preparedStmt = con.prepareStatement(query);
			
			// binding the value of itemID
			preparedStmt.setString(1, bidID);
	
			//execute the statement
			preparedStmt.execute();
			con.close();
			
			String newBids = readBids();
			output = "{\"status\":\"success\", \"data\": \"" +
					newBids + "\"}";
			
		} catch (Exception e)
		{
			output = "{\"status\":\"error\", \"data\": \"" +
					  "Error while deleting the bid.\"}";
					 System.err.println(e.getMessage());
		}
		return output;

	}		
	
	//Return bid state
	public String returnBidState(String bidId) {
		String output = "";
		try {
			Connection con = connect();
			if (con == null){
				return "Error while connecting to the database";
			}

			//Return accepted state of the bid
			String accQuery = "select accepted from bids where bidid = ?";
			PreparedStatement preparedStmt = con.prepareStatement(accQuery);
			
			preparedStmt.setInt(1, Integer.parseInt(bidId));
			
			System.out.println("bidId :" +bidId);
			ResultSet rsSet = preparedStmt.executeQuery();
			if (rsSet.next()) {	
				String accepted = rsSet.getString("accepted");
				
				output = accepted;

				con.close();		
				
			
			

			}
			
		}catch(Exception e){
			output = "Error while reading";
			System.err.println(e.getMessage());
		}
			
		
			
		
		return output;
	}
		
		
		
		


}
