package tasklists.models;

public interface Task {

	String getDescription();

	String getStatus();
	
	int getId();
	
	void setDescription(String description);
	
	void setStatus(String status);

}
