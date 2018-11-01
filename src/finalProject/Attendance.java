package finalProject;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

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
		response.setContentType("application/json");
		response.setCharacterEncoding("UTF-8");
		if(studentID != null && studentID != ""
			&& lectureID != null && lectureID != "") { //returns string upon success, nothing upon fail
			
			// ************************* CHECK IN *************************************
			
			if (requestType == "checkIn") {
				Date dateRaw = new Date();
			
				//TODO: need to also get time and check in sql if it is in the range of the class meeting
				
				SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
				simpleDateFormat.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
				System.out.println(simpleDateFormat.format(dateRaw));
				String dateFull = simpleDateFormat.format(dateRaw);
				String date = dateFull.substring(0,10);
				String time = dateFull.substring(11);
				
				
				Class.forName("com.mysql.jdbc.Driver");
				conn = DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
				ps = conn.prepareStatement(
						"SELECT * FROM User WHERE studentID=?"
						);
				ps.setString(1, studentID);
					rs = ps.executeQuery();
					if(rs.next()) {
						System.out.println("found student");
						//check if today's date equals a date in the table DaysLectureMeets
						//if datetimecorrect { if location correct { }
						PreparedStatement ps2 = conn.prepareStatement(
						"IF EXISTS (SELECT * FROM DaysLectureMeets WHERE lectureUUID = ? AND lectureDate = ?)"
						+ "INSERT INTO AttendanceRecord (studentID, lectureUUID, lectureDate) VALUES (?, ?, ?)"
					    );
						ps2.setString(1, lectureID);
					    ps2.setString(2, date);
						ps2.setString(3, studentID);
						ps2.setString(4, lectureID);
						ps2.setString(5, date);

						int result = ps2.executeUpdate();
						System.out.println(result);
						if(result > 0) {
							response.getWriter().write("Checked In Successfully.");
						}
						else {
							
							//cases covered: if: class does not meet today 
							//TODO:
							//cases not yet covered: nested if: class is not at the right time
							//not yet covered: un nested: user is not in correct location
						}
						ps2.close();
					}
				ps.close();
			}
			
			
			// ************************* GET HISTORY *************************************
			
			else if (requestType == "getHistory") {
				
				try {
					Class.forName("com.mysql.jdbc.Driver");
					conn = DriverManager.getConnection("jdbc:mysql://localhost/askUSC?user=root&password=root&useSSL=false");
					ps = conn.prepareStatement(
							"SELECT c.department, c.classNumber, c.classDescription, u.fullName "
							+ "FROM lectureRegistration lr"
							+ "INNER JOIN Lecture l "
							+ "ON lr.lectureUUID = l.lectureUUID "
							+ "INNER JOIN Class c "
							+ "ON l.classID = c.classID"
							+ "INNER JOIN User u"
							+ "ON c.instructorID = u.instructorID "
							+ "WHERE studentID = ?"
							);
					ArrayList<Lecture> lectures = new ArrayList<Lecture>();
					ps.setString(1, studentID);
					if(studentID != null && studentID != "") {
						rs = ps.executeQuery();
						while(rs.next()) {
							String dept = rs.getString("c.department");
							String num = rs.getString("c.classNumber");
							String des = rs.getString("c.classDescription");
							String instr = rs.getString("u.fullName");
							Lecture temp = new Lecture(dept, num, des, instr); 
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
