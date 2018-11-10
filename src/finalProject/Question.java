package finalProject;



public class Question {
	private String question;
	private int upvotes;
	
	public Question(String question, int upvotes) {
		this.question = question;
		this.upvotes = upvotes;
	}
	
	public void incrementUpvote() {
		upvotes++;
	}
}
