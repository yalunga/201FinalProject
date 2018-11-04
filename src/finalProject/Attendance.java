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
				String longitude = request.getParameter("longitude");
				String latitude = request.getParameter("latitude");
				
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
						PreparedStatement ps2 = conn.prepareStatement(
								"IF EXISTS (SELECT * FROM DaysLectureMeets WHERE lectureUUID = ? AND lectureDate = ?)"
								+ "SELECT * FROM Lecture WHERE lectureUUID = ? AND ? BETWEEN startTime AND endTime"
								);
						//AddTime(startTime, '00:10:00')
						ps2.setString(1, lectureID);
					    ps2.setString(2, date);
					    ps2.setString(3, lectureID);
					    ps2.setString(4, time);
						rs2 = ps2.executeQuery();
						
						if(rs2.next()) {	//class is in session
							PreparedStatement ps3 = conn.prepareStatement(
									"SELECT * FROM Lecture WHERE lectureUUID = ? AND longitude = ? AND latitude = ?)"
							);
							ps3.setString(1, lectureID);
							ps3.setString(2, longitude);
						    ps3.setString(3, latitude);
						    rs3 = ps3.executeQuery();
						    if(rs3.next()) {    //user is in the correct location
						    	
						    	PreparedStatement ps4 = conn.prepareStatement(
						    			"IF EXISTS (SELECT * FROM Lecture WHERE lectureUUID = ? AND ? BETWEEN startTime AND AddTime(startTime, '00:10:00'))"
						    			+ "INSERT INTO AttendanceRecord (studentID, lectureUUID, lectureDate) VALUES (?, ?, ?)"
						    	); //if inserted, it meant that the user checked in during the 10 minute time slot
						    	ps4.setString(1, lectureID);
							    ps4.setString(2, time);
						    	ps4.setString(3, studentID);
						    	ps4.setString(4, lectureID);
						    	ps4.setString(5, date);
						    	
						    	int resultNum = ps4.executeUpdate();
						    	if (resultNum > 0) {
						    		System.out.println("4: User is in the correct location within checkin time.");
						    		response.getWriter().write("4");
						    	}
						    	else {
						    		System.out.println("3: User is in the correct location within the classtime.");
						    		response.getWriter().write("3");
						    	}
						    	
						    	
						    }
						    else {
						    	System.out.println("2: You must be in the classroom to type questions.");
						    	response.getWriter().write("2");
						    }
							ps3.close();
							
						} //endif (datetime was correct)
						
						else { //datetime incorrect
							//TODO:
							//output some error
							System.out.println("1: Class is not in session.");
							response.getWriter().write("1");
						}
						ps2.close();
						
					} //endif (student existed)
					ps.close();
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
