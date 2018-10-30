package finalProject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class AddUser
 */
@WebServlet("/AddUser")
public class AddUser extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public AddUser() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userID = request.getParameter("userID");
		String idToken = request.getParameter("idToken");
		String fullName = request.getParameter("fullName");
		String lastName = request.getParameter("lastName");
		String firstName = request.getParameter("lastName");
		String email = request.getParameter("email");
		String userType = request.getParameter("userType");
		Connection conn = null;
		PreparedStatement ps = null;
		int rs;
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
			ps = conn.prepareStatement(
					"INSERT IGNORE INTO User (userID, idToken, fullName, lastName, firstName, email, userType) VALUES (?, ?, ?, ?, ?, ?, ?)"
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

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
