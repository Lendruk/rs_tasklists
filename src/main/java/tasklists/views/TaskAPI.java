package tasklists.views;

import java.io.IOException;

import javax.json.Json;
import javax.json.JsonObjectBuilder;
import javax.json.JsonWriter;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tasklists.controllers.TaskListController;
import tasklists.controllers.TaskListControllerClass;
import tasklists.models.Task;

@WebServlet("/lists/tasks")
public class TaskAPI extends HttpServlet{
	TaskListController controller = new TaskListControllerClass();
	
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String listId = req.getParameter("listId");
		String taskName = req.getParameter("taskName");
		
		controller.createTask(listId, taskName);
	}
	
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String status = req.getParameter("status");
		String description = req.getParameter("description");
		String listId = req.getParameter("listId");
		String taskId = req.getParameter("taskId");
		
		controller.updateTask(listId, taskId, description, status);
	}
	
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String listId = req.getParameter("listId");
		String taskId = req.getParameter("taskId");
		
		controller.deleteTask(listId, taskId);
	}
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String listId = req.getParameter("listId");
		String taskId = req.getParameter("taskId");
		
		Task task = controller.getTask(listId, taskId);
		
		JsonObjectBuilder objBuilder = Json.createObjectBuilder();
		
		objBuilder.add("description", task.getDescription());
		objBuilder.add("status", task.getStatus());
		objBuilder.add("id", task.getId());
		
		JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
		jsonWriter.write(objBuilder.build());
		jsonWriter.close();
		
	}
}
