package finalProject;

import java.io.IOException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.*;

import com.google.gson.Gson;
import com.mysql.*;

/**
 * Servlet implementation class Attendance
 */
@WebServlet("/Attendance")
public class Attendance extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public Attendance() {
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		// response.getWriter().append("Served at: ").append(request.getContextPath());
		String requestType = request.getParameter("requestType");
		String studentID = request.getParameter("studentID");
		String lectureID = request.getParameter("lectureID");

		Connection conn = null;
		PreparedStatement ps = null;
		ResultSet rs;
		ResultSet rs2;
		ResultSet rs3;
		
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		if(studentID != null && studentID != ""
			&& lectureID != null && lectureID.trim() != "") { //returns string upon success, nothing upon fail
			
			// ************************* CHECK IN *************************************
			
			if (requestType.equals("checkIn")) {
				
				//Log datetime that student sent this request
				Date dateRaw = new Date();
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
				System.out.println(simpleDateFormat.format(dateRaw));
				String dateFull = simpleDateFormat.format(dateRaw);
				//Final result: 
				String date = dateFull.substring(0,10);
				String time = dateFull.substring(11);
				
				try {	

					Class.forName("com.mysql.jdbc.Driver");
					conn =  DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
					
					
					ps = conn.prepareStatement(
							"SELECT * FROM User WHERE studentID=?"
							);
					ps.setString(1, studentID);
					rs = ps.executeQuery();
					
					if(rs.next()) { //Student exists
						System.out.println("Success: student exists.");					
						//check if today's date equals a date in the table DaysLectureMeets
						//if datetimecorrect { if location correct { }
						PreparedStatement ps2 = conn.prepareStatement("INSERT INTO AttendanceRecord (studentID, lectureUUID, lectureDate) VALUES (?, ?, ?)");
						ps2.setString(1, studentID);
						ps2.setString(2, lectureID);
						ps2.setString(3, date);
						int result = ps2.executeUpdate();
						if(result == 0) {
							System.out.println("Attendance was not recorded");
						}
					}
				}catch(SQLException sqle) {
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
			
			
			// ************************* GET HISTORY *************************************
			
else if (requestType.equals("getHistory")) {
				
				try {
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
					
					
					ArrayList<String> daysAbsent = new ArrayList<String>();
					
					// get all dates lecture met
					PreparedStatement ps1 = conn.prepareStatement(
							"SELECT d.lectureDate "
							+ "FROM DaysLectureMeets d "
							+ "WHERE lectureUUID = ?"
							+ "AND NOT EXISTS"
							+ "(SELECT a.lectureDate"
							+ "FROM AttendanceRecord a "
							+ "WHERE a.studentID = ? AND a.lectureUUID = ?)"
							);
					ps1.setString(1, lectureID);
					ps1.setString(2, studentID);
					ps1.setString(3, lectureID);
					rs = ps1.executeQuery();
					while(rs.next()) {
						String date = rs.getString("d.lectureDate");
						daysAbsent.add(date);
					}

				
					String json = new Gson().toJson(daysAbsent);
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
