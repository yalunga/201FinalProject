package finalProject;

import java.io.IOException;
import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

/**
 * Servlet implementation class UserLogin
 */
@WebServlet("/UserLogin")
public class UserLogin extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public UserLogin() {
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
		
		
		if(userID != null && userID.trim() != "") {
			
			// ************************* REGISTER USER *************************************
			
			if(requestType.equals("registerUser")) {
		    	String userID = request.getParameter("userID");
				String lastName = request.getParameter("lastName");
				String firstName = request.getParameter("lastName");
				String fullName = firstName + " " + lastName;
				String email = request.getParameter("email");
				String userType = request.getParameter("userType");
				if (userType.equals("student")) {
					String studentID = userID;
					String instructorID = null;
				}
				else {
					String instructorID = userID;
					String studentID = null;
				}
				
				try {
					
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
					ps = conn.prepareStatement(
							"INSERT IGNORE INTO User (userID, idToken, fullName, lastName, firstName, email, userType, studentID) VALUES (?, ?, ?, ?, ?, ?, ?)"
							);
					ps.setString(1, userID);
					ps.setString(2, idToken);
					ps.setString(3, fullName);
					ps.setString(4, lastName);
					ps.setString(5, firstName);
					ps.setString(6, email);
					ps.setString(7, userType);
					if(idToken != null) {
						rs = ps.executeUpdate();
						if (rs > 0) {
							response.getWriter().write("Registered");
						}
						else {
							response.getWriter().write("Not registered");
						}
					}
					
				} catch(SQLException sqle) {
					System.out.println(sqle.getMessage());
				} catch(ClassNotFoundException cnfe) {
					System.out.println(cnfe.getMessage());
				} finally {
					try {
						if(ps != null) { ps.close(); }
						if(conn != null) { conn.close(); }
					} catch(SQLException sqle) {
						System.out.println(sqle.getMessage());
					}
				}
			}
			
			// ************************* GET USER PROFILE *************************************

			else if (requestType.equals("getUserProfile")) {
				
				try {
					
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
					ps = conn.prepareStatement(
							"SELECT * FROM User WHERE UserID = ?"
							);
					ps.setString(1, userID);
					
					
					
				} catch(SQLException sqle) {
					System.out.println(sqle.getMessage());
				} catch(ClassNotFoundException cnfe) {
					System.out.println(cnfe.getMessage());
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
		
		
	}


	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
