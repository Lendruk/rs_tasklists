package tasklists;

import tasklists.controllers.TaskListControllerClass;
import tasklists.models.TaskList;

public class App {
    public static void main( String[] args ) {
        TaskList taskList = new TaskList("Sample task list");
        TaskListControllerClass controller = new TaskListControllerClass();
        controller.writeTaskList(taskList);
        controller.close();
    }
}
