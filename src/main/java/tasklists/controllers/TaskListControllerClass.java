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
	
	public void updateTask(String listId, String taskId, String description, String status) {
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		TaskList list = session.get(TaskList.class, listId);

		
		for(Task task : list.getTasks()) {
			if(task.getId() == Integer.parseInt(taskId)) {
				task.setDescription(description);
				task.setStatus(status);
			}
		}
		
		session.update("TaskList", list);
		
		session.getTransaction().commit();
		session.close();
		
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
		
		List<TaskList> tasks = session.createQuery("from TaskList", TaskList.class).list();
		
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

	public boolean hasTaskList(String id) {
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		
		Iterator<TaskList> iterator = session.createQuery("from TaskList list where list.id like '"+id, TaskList.class).getResultStream().iterator();
		
		TaskList lst = iterator.next();
		return lst != null;
	}

	public TaskList createTaskList(String name) {
		TaskList taskList = new TaskList(name);
		this.writeTaskList(taskList);
		return taskList;
	}

	public TaskList getTaskList(String id) {
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		Iterator<TaskList> iterator = session.createQuery("from TaskList list where list.id like '"+id+"'", TaskList.class).getResultStream().iterator();
		TaskList list = null;
		if(iterator.hasNext()) {
			list = iterator.next();
		}
		
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
		TaskList list = this.getTaskList(taskListId);
		
		List<Task> tasks = list.getTasks();
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		for(Task task : tasks) {
			if(task.getId() == Integer.parseInt(taskId)) {
				session.delete("TaskClass", task);
				
				session.getTransaction().commit();
				session.close();
				break;
			}
		}
		

	}
	
	public boolean hasTask(String taskListId, String taskId) {
		TaskList tasks = this.getTaskList(taskListId);
		
		for(Task task : tasks.getTasks()) {
			if(task.getId() == Integer.parseInt(taskId)) {
				return true;
			}
		}
		return false;
	}

	public boolean hasTaskInList(String taskListId, String taskId) {
		TaskList list = this.getTaskList(taskListId);
		
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
		TaskList taskList = getTaskList(taskListId);
		Task task = new TaskClass(description);
		
		Session session = sessionFactory.openSession();
		
		taskList.addTask(task);
		session.beginTransaction();
		
		session.save("TaskList", taskList);
		
		session.getTransaction().commit();
		session.close();
		
		return taskList;
	}
	
	public void putTaskList(TaskList list) {
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		
		session.update("TaskList", list);		
		session.getTransaction().commit();
		session.close();
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
		TaskList list = this.getTaskList(id);
		Session session = sessionFactory.openSession();
		
		session.beginTransaction();
		session.delete("TaskList", list);
		session.getTransaction().commit();
		session.close();

	}

}
