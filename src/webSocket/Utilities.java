package webSocket;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dataModels.Message;

public class Utilities {
	
	private static Connection conn = null;
	
	private static void tryInitJDBC() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
	}

	public static void saveMessageToDB(Message m) {
		
		String data = m.getData();
		String sender = m.getSender();
		String voters = m.votersToCSV();
		int votes = m.getVotes();
		String TIMESTAMP = m.getTIMESTAMP();
		String classID = m.getClassID();
		String messageID = m.getMessageID();
		String type = m.getType();
		
		try {
			tryInitJDBC();
			PreparedStatement ps = conn.prepareStatement("INSERT INTO Messages "
					+ "(messageID, classID, sender, type, voters, votes, TIMESTAMP, data) "
					+ "VALUES (?, ?, ?, ?, ?, ?, ?");
			ps.setString(1, messageID);
			ps.setString(2, classID);
			ps.setString(3, sender);
			ps.setString(4, type);
			ps.setString(5, voters);
			ps.setInt(6, votes);
			ps.setString(7, TIMESTAMP);
			ps.setString(8, data);
			ps.execute();
			ps.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	// VERY IMPORTANT NOTE:
	// The ONLY way this can work is to have an AUTO_INCREMENT INT ID for each message
	// in the database!!! That way we can look at what's the last message the client 
	// received and select all the messages after it!
	// Note that this is NOT messageID! messageID is a client-side generated UUID while
	// this ID starts from 1 and counts up.
	public static ArrayList<Message> getMessageFromDB(PreparedStatement ps) {
		
		ArrayList<Message> a = new ArrayList<>();
		
		try {
			tryInitJDBC();
			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				String messageID = rs.getString("messageID");
				String classID = rs.getString("classID");
				String sender = rs.getString("sender");
				String type = rs.getString("type");
				String voters = rs.getString("voters");
				String TIMESTAMP = rs.getString("TIMESTAMP");
				String data = rs.getString("data");
				a.add(makeMessageFromParams(messageID, classID, sender, type, voters, TIMESTAMP, data));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return a;
		
	}
	
	private static Message makeMessageFromParams(String messageID, String classID, String sender, String type, String voters, String TIMESTAMP, String data) {
		Message m = new Message(data, classID, messageID, sender);
		m.setVoters(voters);
		m.setTIMESTAMP(TIMESTAMP);
		m.setType(type);
		return m;
	}
	
}
