package finalProject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class UserLogin
 */
public class UserLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public UserLogin() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String requestType = request.getParameter("requestType");
		String idToken = request.getParameter("idToken");
		//************************** GOOGLE AUTH STUFF ***********************************
		
		
//		GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(transport, jsonFactory)
//				   // Specify the CLIENT_ID of the app that accesses the backend:
//				   .setAudience(Collections.singletonList(CLIENT_ID))
//				   // Or, if multiple clients access the backend:
//				   //.setAudience(Arrays.asList(CLIENT_ID_1, CLIENT_ID_2, CLIENT_ID_3))
//				   .build();
//
//				// (Receive idTokenString by HTTPS POST)
//
//				GoogleIdToken idToken = verifier.verify(idTokenString);
//				if (idToken != null) {
//				 Payload payload = idToken.getPayload();
//
//				 // Print user identifier
//				 String userId = payload.getSubject();
//				 System.out.println("User ID: " + userId);
//
//				 // Get profile information from payload
//				 String email = payload.getEmail();
//				 boolean emailVerified = Boolean.valueOf(payload.getEmailVerified());
//				 String name = (String) payload.get("name");
//				 String pictureUrl = (String) payload.get("picture");
//				 String locale = (String) payload.get("locale");
//				 String familyName = (String) payload.get("family_name");
//				 String givenName = (String) payload.get("given_name");
//
//				 // Use or store profile information
//				 // ...
//
//				} else {
//				 System.out.println("Invalid ID token.");
//				}
		
		//************************** END GOOGLE AUTH STUFF *******************************
    	
    	
		
		Connection conn = null;
		PreparedStatement ps = null;
		int rs;
		
		
		//if(userID != null && userID.trim() != "") {
			
			// ************************* REGISTER USER *************************************
			
			if(requestType.equals("registerUser")) {
		    	String userID = request.getParameter("userID");
				String lastName = request.getParameter("lastName");
				String firstName = request.getParameter("firstName");
				String fullName = firstName + " " + lastName;
				String email = request.getParameter("email");
				String userType = request.getParameter("userType");
				String studentID;
				String instructorID;
				if (userType.equals("student")) {
					studentID = userID;
					instructorID = null;
				}
				else {
					instructorID = userID;
					studentID = null;
				}
				try {
					System.out.println("Trying to add idtoken " + idToken + " to the database.");
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://us-cdbr-iron-east-01.cleardb.net:3306/heroku_6033235a05719ed?user=bcbc373fe829dc&password=345a5a30&useSSL=false");
					ps = conn.prepareStatement(
							"INSERT INTO User (userID, fullName, lastName, firstName, email, userType, studentID) VALUES (?, ?, ?, ?, ?, ?, ?)"
							);
					ps.setString(1, userID);
					ps.setString(2, fullName);
					ps.setString(3, lastName);
					ps.setString(4, firstName);
					ps.setString(5, email);
					ps.setString(6, userType);
					ps.setString(7, studentID);
					if(idToken != null) {
						rs = ps.executeUpdate();
						if (rs > 0) {
							response.getWriter().write("Registered");
							System.out.println(userID + " registered");
						}
						else {
							response.getWriter().write("Not registered");
							System.out.println(userID + " not registered");
						}
					}
					
				} catch(SQLException sqle) {
					System.out.println(sqle.getMessage());
					//response.getWriter().write(sqle.getMessage());
				} catch(ClassNotFoundException cnfe) {
					System.out.println(cnfe.getMessage());
					//response.getWriter().write(cnfe.getMessage());
				} finally {
					try {
						if(ps != null) { ps.close(); }
						if(conn != null) { conn.close(); }
					} catch(SQLException sqle) {
						System.out.println(sqle.getMessage());
					}
				}
			}
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}