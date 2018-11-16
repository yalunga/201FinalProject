package finalProject;

public class AttendanceObject {
	private String date;
	private int attended;
	private String userID;
	private String name;
	
	public AttendanceObject(String date, int attended) {
		this.date = date;
		this.attended = attended;
	}
	
	public AttendanceObject(String date, int attended, String userID, String name) {
		this.date = date;
		this.attended = attended;
		this.userID = userID;
		this.name = name;
	}
}
