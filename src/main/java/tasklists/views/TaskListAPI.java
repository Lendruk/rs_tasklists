package tasklists.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonStructure;
import javax.json.JsonValue;
import javax.json.JsonWriter;
import javax.json.stream.JsonParser;
import javax.json.stream.JsonParserFactory;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tasklists.controllers.TaskListController;
import tasklists.controllers.TaskListControllerClass;
import tasklists.models.Task;
import tasklists.models.TaskClass;
import tasklists.models.TaskList;


@WebServlet("/lists/*")
public class TaskListAPI extends HttpServlet {
	TaskListController controller = new TaskListControllerClass();
	
	private ArrayList<String> parseUrl(String url) {
		System.out.println(url);
		
		String[] splitUrl = url.split("/");
		System.out.println("TEST LOGS ");
		System.out.println(splitUrl.length);
		String type = "";
		String listId = "";
		String taskId = "";
		if(splitUrl.length > 3) {
			
			if(splitUrl.length > 4) {
				
				if(splitUrl.length == 6) {
					type = "task";
					listId = splitUrl[3];
					taskId = splitUrl[5];
				} else {
					type = "task";
					listId= splitUrl[3];
				}
				
			} else {
				type = "list";
				listId = splitUrl[3];
			}
		
		} else if (splitUrl.length == 3) {
			type = "list";
			listId = "";
		} else {
			type = "list";
			listId = "";
		}
		
		ArrayList<String> res = new ArrayList<String>();
		res.add(type);
		res.add(listId);
		res.add(taskId);
		return res;
	}
	
	// Create TaskList
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		
		ArrayList<String> action = parseUrl(uri);
		
		String type = action.get(0);
		String listId = action.get(1);
		String taskId = action.get(2);
		
		JsonReader reader = Json.createReader(req.getReader());
		
		if(type == "list") {
			JsonObject obj = reader.readObject();
	
			controller.createTaskList(obj.getString("name"));			
		} else {
			if(taskId != "") {
				String json = req.getReader().lines().collect(Collectors.joining(System.lineSeparator()));
				String description = json.split(":")[1].replace("\n", "").replace("/", "").trim();
				controller.createTask(listId, description);
			}
		}
	}
	
	// Delete TaskList
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		
		ArrayList<String> action = parseUrl(uri);
		
		String type = action.get(0);
		String listId = action.get(1);
		String taskId = action.get(2);	
		
		if(listId != "") {
			
			if(taskId != "") {
				controller.deleteTask(listId, taskId);
			} else {
				controller.deleteTaskList(listId);	
			}		
		}
	}
	
	
	
	// Put TaskList
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		
		ArrayList<String> action = parseUrl(uri);
		JsonReader reader = Json.createReader(req.getReader());
		JsonObject item = reader.readObject();
		String listId = action.get(1);
		String taskId = action.get(2);
		if(listId != "") {
			if(taskId != "") {	
				controller.updateTask(listId, taskId, item.getString("description"), item.getString("status"));
			} else {
				JsonObject taskObj = item.getJsonObject("task");
				List<Task> tasks = new ArrayList<>();
				
				//for(int i = 0 ; i < taskArray.size(); i++) {
					//JsonObject val = taskArray.getJsonObject(i);
					if (taskObj != null) {
						Task task = new TaskClass(taskObj.getString("description"));
						if (taskObj.containsKey("id")) {
							task.setId(taskObj.getInt("id"));
						}
						task.setStatus(taskObj.getString("status"));
						tasks.add(task);
					}

				//}
				
				TaskList tList = new TaskList(item.getString("name"));
				tList.setId(Integer.parseInt(listId));
				if(item.containsKey("id")) {
					tList.setTasks(tasks);
				} else {
					controller.createTask(listId, taskObj.getString("description"));
					
				}
				
				controller.putTaskList(tList);
			}
		}
	}
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        
		String uri = req.getRequestURI();
		
		ArrayList<String> action = parseUrl(uri);
		
		String type = action.get(0);
		String listId = action.get(1);
		String taskId = action.get(2);
		
		System.out.println(type);
		System.out.println(listId);
		System.out.println(taskId);
		
		if(type == "list") {
			if(listId != "") {
				TaskList list = controller.getTaskList(listId);
				
				JsonObjectBuilder listBuilder = Json.createObjectBuilder();
				
				JsonArrayBuilder taskBuilder = Json.createArrayBuilder();
				for(Task task : list.getTasks()) {
					JsonObjectBuilder objectBuilder = Json.createObjectBuilder();
					objectBuilder.add("id",task.getId());
					objectBuilder.add("description",task.getDescription());
					objectBuilder.add("status",task.getStatus());
					
					taskBuilder.add(objectBuilder);
				}
				
				listBuilder.add("id", list.getId());
				listBuilder.add("tasks", taskBuilder);
				listBuilder.add("name", list.getName());
				
		        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
		        jsonWriter.writeObject(listBuilder.build());
		        jsonWriter.close();
				
			} else {
				List<TaskList> tasks = controller.getTaskLists();
		        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
		        
		        for(TaskList list : tasks) {
		        	JsonObjectBuilder listBuilder = Json.createObjectBuilder();
		        	listBuilder.add("name", list.getName());
		        	listBuilder.add("id", list.getId());
		        	JsonArrayBuilder arrTaskBuilder = Json.createArrayBuilder();
		        	for(Task task : list.getTasks() ) {
		        		JsonObjectBuilder taskBuilder = Json.createObjectBuilder();
		        		
		        		taskBuilder.add("id", task.getId());
		        		taskBuilder.add("description", task.getDescription());
		        		taskBuilder.add("status", task.getStatus());
		        		
		        		arrTaskBuilder.add(taskBuilder);
		        	}
		        	listBuilder.add("tasks", arrTaskBuilder);
		        	
		        	arrayBuilder.add(listBuilder);
		        }
		        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
		        jsonWriter.writeArray(arrayBuilder.build());
		        jsonWriter.close();
			}
		} else {
			if(taskId != "") {
				Task task = controller.getTask(listId, taskId);
				
				JsonObjectBuilder taskBuilder = Json.createObjectBuilder();
				
				taskBuilder.add("id", task.getId());
				taskBuilder.add("description", task.getDescription());
				taskBuilder.add("status", task.getStatus());
				
				JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
				jsonWriter.writeObject(taskBuilder.build());
				jsonWriter.close();
			} else {
				TaskList list = controller.getTaskList(listId);
				
				JsonArrayBuilder arrTaskBuilder = Json.createArrayBuilder();
	        	for(Task task : list.getTasks() ) {
	        		JsonObjectBuilder taskBuilder = Json.createObjectBuilder();
	        		
	        		taskBuilder.add("id", task.getId());
	        		taskBuilder.add("description", task.getDescription());
	        		taskBuilder.add("status", task.getStatus());
	        		
	        		arrTaskBuilder.add(taskBuilder);
	        	}
	        	
		        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
		        jsonWriter.writeArray(arrTaskBuilder.build());
		        jsonWriter.close();
			}
		}
    }
}

