package tasklists.models;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "tasklist")
public class TaskList {
	@Id
	@GeneratedValue(generator="increment")
	@GenericGenerator(name="increment", strategy="increment")
	private int id;
	
	@Column(name = "NAME")
	private String name;
	
	@ElementCollection
	@OneToMany(
		    orphanRemoval = true,
		    cascade = CascadeType.ALL,
		    targetEntity = TaskClass.class,
		    fetch = FetchType.EAGER
			)
	private List<Task> tasks;
	
	public TaskList() {
		super();
	}
	
	public void addTask(Task task) {
		this.tasks.add(task);
	}
	
	public TaskList(String name) {
		this.name = name;
		this.tasks = new ArrayList<Task>();
	}
	
	public TaskList(String name, List<Task> tasks) {
		this.name = name;
		this.tasks = tasks;
	}
	
	public void setTasks(List<Task> tasks) {
		this.tasks = tasks;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public List<Task> getTasks() {
		return this.tasks;
	}
}
