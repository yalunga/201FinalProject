package finalProject;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class Classes
 */
@WebServlet("/Classes")
public class ClassesStudent extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public ClassesStudent() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		String requestType = request.getParameter("requestType");
		String studentID = request.getParameter("studentID");
		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs;
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		System.out.println("request type: " + requestType);
		System.out.println("studentId: " + studentID);
		// ************************* REGISTER CLASSES ********************************
		
		if (requestType.equals("registerClass")) {
			String lectureID = request.getParameter("lectureID");
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn =  DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
				ps =  conn.prepareStatement(
						"SELECT * FROM Lecture WHERE lectureUUID=?"
						);
				ps.setString(1, lectureID);
				if(lectureID != null && lectureID != "") {
					rs = ps.executeQuery();
					if(rs.next()) {
						System.out.println("found a lecture");
						PreparedStatement ps2 =  conn.prepareStatement(
								"INSERT IF NOT EXISTS INTO LectureRegistration (userID, lectureUUID) VALUES (?, ?)"
								);
						ps2.setString(1, studentID);
						ps2.setString(2, lectureID);
						int result = ps2.executeUpdate();
						if(result > 0) {
							CallableStatement stmt = conn.prepareCall("{CALL setAttendance(?, ?)}");
							stmt.setString(1, studentID);
							stmt.setString(2, lectureID);
							ResultSet res = stmt.executeQuery();
							response.getWriter().write("Added");
						}
						ps2.close();
					}
				} else {
					
					response.getWriter().write("Lecture Not Found");
				}
				ps.close();
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
		
		// ************************* UNREGISTER CLASSES ******************************
		
		else if (requestType.equals("unregisterClass")) {
			String lectureID = request.getParameter("lecutreID");
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
				ps = conn.prepareStatement(" DELETE FROM LectureRegistration WHERE studentID = ? AND LectureUUID = ?");
				ps.setString(1, studentID);
				ps.setString(2, lectureID);
				if(studentID != null && studentID != ""
					&& lectureID != null && lectureID != "") {
					int deleted = ps.executeUpdate();
					if (deleted > 0) {
						System.out.println("Deleted successfully.");
						response.getWriter().write("Deleted");

					}
					else {
						System.out.println("NOTHING WAS DELETED!");
						response.getWriter().write("Lecture not found");

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
		
		
	
		
		
		
		// ************************* GET CLASSES *************************************
		
		else if (requestType.equals("getClasses")) {
			//I want to return class info and instructor name
			//I am given a studentID only.
			System.out.println("Reached getClasses");
			try {
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
				ps = conn.prepareStatement(
						"SELECT c.department, c.classNumber, c.classDescription, u.fullName, l.latitude, l.longitude, l.startTime, l.endTime, l.meetingDaysOfWeek, l.lectureUUID FROM lectureRegistration lr INNER JOIN Lecture l ON lr.lectureUUID = l.lectureUUID INNER JOIN Class c ON l.classID = c.classID INNER JOIN User u ON l.instructorID = u.instructorID WHERE lr.userID = ?"
						);
				ArrayList<Lecture> lectures = new ArrayList<Lecture>();
				ps.setString(1, studentID);
				if(studentID != null && studentID != "") {
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
		}//end if statement
		
	
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
