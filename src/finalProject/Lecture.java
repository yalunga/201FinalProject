package finalProject;

public class Lecture {
	public String department;
	public String classNumber;
	public String classDescription;
	public String instructor;
	public String startTime;
	public String endTime;
	public String latitude;
	public String longitude;
	public String meetingDaysOfWeek;
	public Lecture(String department, String classNumber, String classDescription, String instructor, String startTime, String endTime, String latitude, String longitude, String meetingDaysOfWeek) {
		this.department = department;
		this.classNumber = classNumber;
		this.classDescription = classDescription;
		this.instructor = instructor;
		this.startTime = startTime;
		this.endTime = endTime;
		this.latitude = latitude;
		this.longitude = longitude;
		this.meetingDaysOfWeek = meetingDaysOfWeek;
		
	}
}
