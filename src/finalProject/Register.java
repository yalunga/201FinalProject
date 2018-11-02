package finalProject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Servlet implementation class Register
 */
@WebServlet("/Register")
public class Register extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public Register() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String lectureID = request.getParameter("lectureID");
		String studentID = request.getParameter("studentID");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs;
		response.setContentType("text/plain");
		response.setCharacterEncoding("UTF-8");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false&allowPublicKeyRetrieval=true");
			ps = conn.prepareStatement(
					"SELECT * FROM Lecture WHERE lectureUUID=?"
					);
			ps.setString(1, lectureID);
			if(lectureID != null && lectureID != "") {
				rs = ps.executeQuery();
				if(rs.next()) {
					System.out.println("found a lecture");
					PreparedStatement ps2 = conn.prepareStatement(
							"INSERT INTO LectureRegistration (userID, lectureUUID) VALUES (?, ?)"
							);
					ps2.setString(1, studentID);
					ps2.setString(2, lectureID);
					int result = ps2.executeUpdate();
					System.out.println(result);
					if(result > 0) {
						response.getWriter().write("Added");
					}
					ps2.close();
				}
			} else {
				
				response.getWriter().write("Lecture Not Found");
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
