package tasklists.controllers;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.MetadataSources;
import org.hibernate.boot.registry.StandardServiceRegistry;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;

import tasklists.models.Task;
import tasklists.models.TaskClass;
import tasklists.models.TaskList;
import tasklists.models.TaskListClass;

public class TaskListControllerClass implements TaskListController {
	private SessionFactory sessionFactory;
	
	public TaskListControllerClass() {
		final StandardServiceRegistry registry = new StandardServiceRegistryBuilder()
				.configure("resource/hibernate.cfg.xml")
				.build();
        try {
        	this.sessionFactory  = new MetadataSources(registry).buildMetadata().buildSessionFactory();
        } catch(Exception e) {
        	e.printStackTrace();
        	System.exit(1);
        }
	}
	
	private TaskList readTaskList(String taskListId) {
        Session session = sessionFactory.openSession();
		// Open Hibernate session
        session.beginTransaction();
        // Save TaskList object
        TaskList list = session.get(TaskList.class, taskListId);
        session.getTransaction().commit();
        // Close session
        session.close();
		return list;
	}
	
	public void writeTaskList(TaskList taskList) {
		// TODO: change object id
        Session session = sessionFactory.openSession();
		// Open Hibernate session
        session.beginTransaction();
        // Save TaskList object
        session.save(taskList);
        session.getTransaction().commit();
        // Close session
        session.close();
	}
	
	public void close() {
		sessionFactory.close();
	}
	
	public List<TaskList> getTaskLists() {
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		
		Iterator<TaskList> iterator = session.createQuery("SELECT * FROM TaskList", TaskList.class).getResultStream().iterator();
		
		List<TaskList> tasks = new ArrayList<TaskList>();
		while(iterator.hasNext()) {
			tasks.add(iterator.next());
		}
		
		return tasks;
	}

	public boolean hasTasksLists() {
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		boolean hasList = session.contains(TaskList.class);
		session.getTransaction().commit();
		session.close();
		return hasList;
	}

	public boolean hasTaskList(String name) {
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		
		Iterator<TaskList> iterator = session.createQuery("SELECT * FROM TaskList WHERE name ="+name, TaskList.class).getResultStream().iterator();
		
		TaskList lst = iterator.next();
		return lst != null;
	}

	public TaskList createTaskList(String name) {
		TaskList taskList = new TaskListClass(name);
		this.writeTaskList(taskList);
		return taskList;
	}

	public TaskList getTaskList(String name) {
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		
		Iterator<TaskList> iterator = session.createQuery("SELECT * FROM TaskList WHERE name ="+name, TaskList.class).getResultStream().iterator();
		
		if(iterator.hasNext()) {
			return iterator.next();
		}
		
		return null;
	}
	
	public TaskList getTaskListById(String id) {
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		TaskList list = session.get(TaskList.class, id);
		session.getTransaction().commit();
		session.close();
		
		return list;
	}
	
	public void changeTaskListName(String name, String newName) {
		TaskList list = this.getTaskList(name);
		
		list.setName(newName);
		
		this.writeTaskList(list);
		
	}
	
	public void deleteTask(String taskListId, String taskId) {
		TaskList list = this.getTaskListById(taskListId);
		
		List<Task> tasks = list.getTasks();
		for(Task task : tasks) {
			if(task.getId() == Integer.parseInt(taskId)) {
				tasks.remove(task);
				break;
			}
		}
		

	}
	
	public boolean hasTask(String taskListId, String taskId) {
		TaskList tasks = this.getTaskListById(taskListId);
		
		for(Task task : tasks.getTasks()) {
			if(task.getId() == Integer.parseInt(taskId)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasTaskInList(String taskListId, String taskId) {
		TaskList list = this.getTaskListById(taskListId);
		
		for(Task task : list.getTasks()) {
			if(task.getId() == Integer.parseInt(taskId)) {
				return true;
			}
		}
		return false;
	}

	public void changeTaskDescription(String taskId, String taskDescription) {
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		Task task = session.get(Task.class, taskId);
		
		task.setDescription(taskDescription);
		
		session.update("Task", task);
		
		session.getTransaction().commit();
		session.close();

	}

	public void changeTaskStatus(String taskListId, String taskId, String status) {
		Task task = this.getTask(taskListId, taskId);
		
		task.setStatus(status);
		
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		
		session.update("Task", task);		
		session.getTransaction().commit();
		session.close();
		
	}

	public TaskList createTask(String taskListId, String description) {
		TaskList taskList = getTaskListById(taskListId);
		Task task = new TaskClass(description);
		
		taskList.addTask(task);
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		
		session.save("TaskList", taskList);
		
		session.getTransaction().commit();
		session.close();
		
		return taskList;
	}

	public Task getTask(String taskListId, String taskId) {
	       Session session = sessionFactory.openSession();
			// Open Hibernate session
	        session.beginTransaction();
	        // Save TaskList object
	        TaskList tasks = session.get(TaskList.class, taskListId);
	        Task task = null;
	        for(Task tsk : tasks.getTasks()) {
	        	if(tsk.getId() == Integer.parseInt(taskId)) {
	        		task = tsk;
	        		break;
	        	}
	        }
	        session.getTransaction().commit();
	        // Close session
	        session.close();
		return task;
	}

	public void deleteTaskList(String id) {
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		session.delete("TaskList", this.getTaskListById(id));
		session.getTransaction().commit();
		session.close();

	}

}
