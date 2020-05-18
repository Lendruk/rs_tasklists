package tasklists.models;

import java.util.List;

public interface TaskList {

	String getName();

	List<Task> getTasks();
	
	void addTask(Task task);
	
	int getId();

	void setName(String name);
}
