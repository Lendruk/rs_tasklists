package tasklists.views;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
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
import tasklists.models.TaskList;


@WebServlet("/lists/*")
public class TaskListAPI extends HttpServlet {
	TaskListController controller = new TaskListControllerClass();
	
	// Create TaskList
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BufferedReader reader = req.getReader();
		Iterator<String> iterator = reader.lines().iterator();
		// req.getRequestURI() --> get the entire URI
		String name = null;
		int line = 0;
		while(iterator.hasNext()) {
			if(line == 1) {
				name = iterator.next();
			}
			line++;
		}
		if(name != null) {
			controller.createTaskList(name);			
		}
	}
	
	// Delete TaskList
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String listId = req.getParameter("id");
		
		if(listId != null) {
			controller.deleteTaskList(listId);			
		}
	}
	
	
	
	// Put TaskList
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String id = req.getParameter("id");
		String name = req.getParameter("name");
		
		TaskList list = new TaskList();
		controller.putTaskList(list);
	}
	
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        
        List<TaskList> tasks = controller.getTaskLists();
        
        // Tradução para JSON
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        
        for(TaskList list : tasks) {
        	arrayBuilder.add(list.getName());
        	arrayBuilder.add(list.getId());
        	JsonArrayBuilder taskBuilder = Json.createArrayBuilder();
        	for(Task task : list.getTasks() ) {
        		taskBuilder.add(task.getId());
        		taskBuilder.add(task.getDescription());
        		taskBuilder.add(task.getStatus());
        	}
        	arrayBuilder.add(taskBuilder);
        }
   
        // Escrita da representação JSON na resposta
        JsonWriter jsonWriter = Json.createWriter(resp.getWriter());
        jsonWriter.writeArray(arrayBuilder.build());
        jsonWriter.close();
    }
}

