package finalProject;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class GetClasses
 */
@WebServlet("/GetClasses")
public class GetClasses extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public GetClasses() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		String userID = request.getParameter("userID");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs;
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		try {
			Class.forName("com.mysql.jdbc.Driver");
			conn = DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
			ps = conn.prepareStatement(
					"SELECT department, classNumber, classDescription "
					+ "FROM Class "
					+ "INNER JOIN lectureRegistration "
					+ "ON lectureRegistration.userID=?"
					);
			ArrayList<Lecture> lectures = new ArrayList<Lecture>();
			ps.setString(1, userID);
			if(userID != null && userID != "") {
				rs = ps.executeQuery();
				while(rs.next()) {
					String dept = rs.getString("[department");
					String num = rs.getString("classNumber");
					String des = rs.getString("classDescription");
					Lecture temp = new Lecture(dept, num, des);
					lectures.add(temp);
				}
			}
			String json = new Gson().toJson(lectures);
			response.getWriter().write(json);
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
