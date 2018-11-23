package finalProject;

import java.io.IOException;
import java.sql.CallableStatement;
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
 * Servlet implementation class GuestEnterClassroom
 */
@WebServlet("/GuestEnterClassroom")
public class GuestEnterClassroom extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public GuestEnterClassroom() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String lectureID = request.getParameter("lectureID");
		// ************************* GET CLASSES *************************************
				
					try {
						Class.forName("com.mysql.jdbc.Driver");
						conn = DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
						ps = conn.prepareStatement(
								"SELECT c.department, c.classNumber, c.classDescription, u.fullName, l.latitude, l.longitude, l.startTime, l.endTime, l.meetingDaysOfWeek, l.lectureUUID "
								+ "FROM Lecture l "
								+ "INNER JOIN Class c "
								+ "ON l.classID = c.classID "
								+ "INNER JOIN User u "
								+ "ON l.instructorID = u.instructorID "
								+ "WHERE l.lectureUUID = ?"
								);
						ArrayList<Lecture> lectures = new ArrayList<Lecture>();
						ps.setString(1, lectureID);
						if(lectureID != null && lectureID != "") {
							rs = ps.executeQuery();
							while(rs.next()) {
								String dept = rs.getString("department");
								String num = rs.getString("classNumber");
								String des = rs.getString("classDescription");
								String instr = rs.getString("fullName");
								String startTime = rs.getString("startTime");
								String endTime = rs.getString("endTime");
								String latitude = rs.getString("latitude");
								String longitude = rs.getString("longitude");
								String meetingDaysOfWeek = rs.getString("meetingDaysOfWeek");
								String id = rs.getString("lectureUUID");
								Lecture temp = new Lecture(dept, num, des, instr, startTime, endTime, latitude, longitude, meetingDaysOfWeek, id); 
								System.out.println("Found lecture: " + dept + num + ", " + des + ", " + instr);
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
