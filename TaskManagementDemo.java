import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

// 1. Prototype Pattern

 
interface TaskPrototype {
    TaskPrototype clone();
}

class Task implements TaskPrototype {
    private String id;
    private String title;
    private String description;
    private String assignee;
    private TaskState state;
    private String dueDate;
    
    public Task(String id, String title, String description, String dueDate) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
        this.state = new AssignedState();
    }
    
    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }
    
    public String getAssignee() {
        return assignee;
    }
    
    public String getId() {
        return id;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getDescription() {
        return description;
    }
    
    public String getDueDate() {
        return dueDate;
    }
    
    public TaskState getTaskState() {
        return state;
    }
    
    public void setTaskState(TaskState state) {
        this.state = state;
    }
    
    public void moveToNextState() {
        state.nextState(this);
    }
    
    @Override
    public TaskPrototype clone() {
        Task clonedTask = new Task(id, title, description, dueDate);
        clonedTask.setAssignee(assignee);
        clonedTask.setTaskState(state);
        return clonedTask;
    }
    
    @Override
    public String toString() {
        return "Task [id=" + id + ", title=" + title + ", state=" + state.getDescription() + 
               ", assignee=" + (assignee != null ? assignee : "Unassigned") + ", due=" + dueDate + "]";
    }
}

// 2. State Pattern

interface TaskState {
    void nextState(Task task);
    String getDescription();
}

class AssignedState implements TaskState {
    @Override
    public void nextState(Task task) {
        task.setTaskState(new InProgressState());
        System.out.println("Task '" + task.getTitle() + "' moved from Assigned to In Progress state");
    }
    
    @Override
    public String getDescription() {
        return "Assigned";
    }
}


class InProgressState implements TaskState {
    @Override
    public void nextState(Task task) {
        task.setTaskState(new UnderReviewState());
        System.out.println("Task '" + task.getTitle() + "' moved from In Progress to Under Review state");
    }
    
    @Override
    public String getDescription() {
        return "In Progress";
    }
}

class UnderReviewState implements TaskState {
    @Override
    public void nextState(Task task) {
        task.setTaskState(new CompletedState());
        System.out.println("Task '" + task.getTitle() + "' moved from Under Review to Completed state");
    }
    
    @Override
    public String getDescription() {
        return "Under Review";
    }
}


class CompletedState implements TaskState {
    @Override
    public void nextState(Task task) {
        System.out.println("Task '" + task.getTitle() + "' is already completed");
    }
    
    @Override
    public String getDescription() {
        return "Completed";
    }
}

// 3. Composite Pattern

interface TaskComponent {
    void add(TaskComponent component);
    void remove(TaskComponent component);
    void display(int level);
}

class TaskItem implements TaskComponent {
    private Task task;
    
    public TaskItem(Task task) {
        this.task = task;
    }
    
    public Task getTask() {
        return task;
    }
    
    @Override
    public void add(TaskComponent component) {
        System.out.println("Cannot add to a TaskItem");
    }
    
    @Override
    public void remove(TaskComponent component) {
        System.out.println("Cannot remove from a TaskItem");
    }
    
    @Override
    public void display(int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("  ");
        }
        System.out.println(indent + "- " + task);
    }
}


class Project implements TaskComponent {
    private String name;
    private List<TaskComponent> tasks = new ArrayList<>();
    
    public Project(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public void add(TaskComponent component) {
        tasks.add(component);
    }
    
    @Override
    public void remove(TaskComponent component) {
        tasks.remove(component);
    }
    
    @Override
    public void display(int level) {
        StringBuilder indent = new StringBuilder();
        for (int i = 0; i < level; i++) {
            indent.append("  ");
        }
        System.out.println(indent + "Project: " + name);
        for (TaskComponent component : tasks) {
            component.display(level + 1);
        }
    }
    
    public List<TaskComponent> getTasks() {
        return new ArrayList<>(tasks);
    }
}

// 4. Observer Pattern

interface TaskObserver {
    void update(Task task);
}

class TeamMember implements TaskObserver {
    private String name;
    private List<Task> assignedTasks = new ArrayList<>();
    
    public TeamMember(String name) {
        this.name = name;
    }
    
    public String getName() {
        return name;
    }
    
    @Override
    public void update(Task task) {
        System.out.println("Notification to " + name + ": Task '" + task.getTitle() + 
                           "' has been updated, new state: " + task.getTaskState().getDescription());
    }
    
    public void assignTask(Task task) {
        task.setAssignee(name);
        assignedTasks.add(task);
        System.out.println("Task '" + task.getTitle() + "' assigned to " + name);
    }
    
    public Task createTemplateTask(String title, String description, String dueDate) {
        String id = "TASK-" + System.currentTimeMillis() % 10000;
        Task task = new Task(id, title, description, dueDate);
        task.setAssignee(name);
        return task;
    }
    
    public List<Task> getAssignedTasks() {
        return new ArrayList<>(assignedTasks);
    }
}

class TaskManagementSystem {
    private List<TaskObserver> observers = new ArrayList<>();
    private List<Project> projects = new ArrayList<>();
    private List<TeamMember> teamMembers = new ArrayList<>();
    private List<Task> templateTasks = new ArrayList<>();
    
    public void addObserver(TaskObserver observer) {
        observers.add(observer);
    }
    
    public void removeObserver(TaskObserver observer) {
        observers.remove(observer);
    }
    
    public void notifyObservers(Task task) {
        for (TaskObserver observer : observers) {
            observer.update(task);
        }
    }
    
    public Project createProject(String name) {
        Project project = new Project(name);
        projects.add(project);
        return project;
    }
    
    public Task createTask(String title, String description, String dueDate) {
        String id = "TASK-" + System.currentTimeMillis() % 10000;
        return new Task(id, title, description, dueDate);
    }
    
    public void assignTask(Task task, TeamMember member) {
        member.assignTask(task);
        notifyObservers(task);
    }
    
    public void moveTaskToNextState(Task task) {
        task.moveToNextState();
        notifyObservers(task);
    }
    
    public void displayAllProjects() {
        System.out.println("\n===== All Projects =====");
        for (Project project : projects) {
            project.display(0);
        }
    }
    
    public List<Project> getProjects() {
        return new ArrayList<>(projects);
    }
    
    public void addTeamMember(TeamMember member) {
        teamMembers.add(member);
        addObserver(member);
    }
    
    public List<TeamMember> getTeamMembers() {
        return new ArrayList<>(teamMembers);
    }
    
    public void addTemplateTask(Task template) {
        templateTasks.add(template);
    }
    
    public List<Task> getTemplateTasks() {
        return new ArrayList<>(templateTasks);
    }
    
    public Project findProjectByName(String name) {
        for (Project project : projects) {
            if (project.getName().equalsIgnoreCase(name)) {
                return project;
            }
        }
        return null;
    }
    
    public TeamMember findTeamMemberByName(String name) {
        for (TeamMember member : teamMembers) {
            if (member.getName().equalsIgnoreCase(name)) {
                return member;
            }
        }
        return null;
    }
    
    public Task findTaskById(String id) {
        for (Project project : projects) {
            Task task = findTaskInComponent(project, id);
            if (task != null) {
                return task;
            }
        }
        return null;
    }
    
    private Task findTaskInComponent(TaskComponent component, String id) {
        if (component instanceof TaskItem) {
            Task task = ((TaskItem) component).getTask();
            if (task.getId().equals(id)) {
                return task;
            }
        } else if (component instanceof Project) {
            Project project = (Project) component;
            for (TaskComponent child : project.getTasks()) {
                Task task = findTaskInComponent(child, id);
                if (task != null) {
                    return task;
                }
            }
        }
        return null;
    }
}
public class TaskManagementDemo {
    private static TaskManagementSystem tms = new TaskManagementSystem();
    private static Scanner scanner = new Scanner(System.in);
    
    public static void main(String[] args) {
         initializeSystem();
        
        boolean exit = false;
        while (!exit) {
            exit = handleMainMenu();
        }
        
        System.out.println("Exiting Task Management System. Goodbye!");
        scanner.close();
    }
    
    private static void initializeSystem() {
         TeamMember parth = new TeamMember("parth");
        TeamMember aryan = new TeamMember("aryan");
        TeamMember arth = new TeamMember("arth");
        
        tms.addTeamMember(parth);
        tms.addTeamMember(aryan);
        tms.addTeamMember(arth);
        
         Project webDev = tms.createProject("Web Development");
        Project mobileDev = tms.createProject("Mobile App Development");
        
         Task weeklyReportTemplate = arth.createTemplateTask(
            "Weekly Report", 
            "Prepare weekly status report for the team", 
            "Every Friday"
        );
        tms.addTemplateTask(weeklyReportTemplate);
        
        System.out.println("Task Management System initialized with sample data.");
    }
    
     private static boolean handleMainMenu() {
        System.out.println("\n===== TASK MANAGEMENT SYSTEM =====");
        System.out.println("1. Manage Projects");
        System.out.println("2. Manage Tasks");
        System.out.println("3. Manage Team Members");
        System.out.println("4. Display All Projects");
        System.out.println("5. Exit");
        
        int choice = getValidChoice(1, 5);
        
        switch (choice) {
            case 1:  // Manage Projects
                handleProjectsMenu();
                return false;
            case 2:  // Manage Tasks
                handleTasksMenu();
                return false;
            case 3:  // Manage Team Members
                handleTeamMembersMenu();
                return false;
            case 4:  // Display All Projects
                tms.displayAllProjects();
                pressEnterToContinue();
                return false;
            case 5:  // Exit
                return true;
            default:
                return false;
        }
    }
    
    private static void handleProjectsMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== MANAGE PROJECTS =====");
            System.out.println("1. Create New Project");
            System.out.println("2. Create Subproject");
            System.out.println("3. List All Projects");
            System.out.println("4. Back to Main Menu");
            
            int choice = getValidChoice(1, 4);
            
            switch (choice) {
                case 1:  // Create New Project
                    createNewProject();
                    break;
                case 2:  // Create Subproject
                    createSubproject();
                    break;
                case 3:  // List All Projects
                    listAllProjects();
                    pressEnterToContinue();
                    break;
                case 4:  // Back to Main Menu
                    back = true;
                    break;
            }
        }
    }
    
    private static void handleTasksMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== MANAGE TASKS =====");
            System.out.println("1. Create New Task");
            System.out.println("2. Clone from Template Task");
            System.out.println("3. Assign Task to Team Member");
            System.out.println("4. Move Task to Next State");
            System.out.println("5. List All Tasks by Project");
            System.out.println("6. Back to Main Menu");
            
            int choice = getValidChoice(1, 6);
            
            switch (choice) {
                case 1:  // Create New Task
                    createNewTask();
                    break;
                case 2:  // Clone from Template Task
                    cloneTemplateTask();
                    break;
                case 3:  // Assign Task to Team Member
                    assignTaskToMember();
                    break;
                case 4:  // Move Task to Next State
                    moveTaskToNextState();
                    break;
                case 5:  // List All Tasks by Project
                    listTasksByProject();
                    break;
                case 6:  // Back to Main Menu
                    back = true;
                    break;
            }
        }
    }
    
    private static void handleTeamMembersMenu() {
        boolean back = false;
        while (!back) {
            System.out.println("\n===== MANAGE TEAM MEMBERS =====");
            System.out.println("1. Add New Team Member");
            System.out.println("2. List All Team Members");
            System.out.println("3. View Team Member's Tasks");
            System.out.println("4. Create Template Task");
            System.out.println("5. Back to Main Menu");
            
            int choice = getValidChoice(1, 5);
            
            switch (choice) {
                case 1:  // Add New Team Member
                    addNewTeamMember();
                    break;
                case 2:  // List All Team Members
                    listAllTeamMembers();
                    pressEnterToContinue();
                    break;
                case 3:  // View Team Member's Tasks
                    viewTeamMemberTasks();
                    break;
                case 4:  // Create Template Task
                    createTemplateTask();
                    break;
                case 5:  // Back to Main Menu
                    back = true;
                    break;
            }
        }
    }
    
    
    private static int getValidChoice(int min, int max) {
        int choice;
        while (true) {
            System.out.print("Enter your choice (" + min + "-" + max + "): ");
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.println("Invalid choice. Please enter a number between " + min + " and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
    
    private static int getValidListSelection(int max) {
        if (max == 0) {
            return -1; // No items in the list
        }
        
        int choice;
        while (true) {
            System.out.print("Select item (1-" + max + "): ");
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice >= 1 && choice <= max) {
                    return choice - 1; // Convert to zero-based index
                } else {
                    System.out.println("Invalid selection. Please enter a number between 1 and " + max + ".");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
    
     private static String getStringInput(String prompt) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine().trim();
            if (!input.isEmpty()) {
                return input;
            } else {
                System.out.println("Input cannot be empty. Please try again.");
            }
        }
    }
    
    private static void createNewProject() {
        String name = getStringInput("Enter project name: ");
        Project project = tms.createProject(name);
        System.out.println("Project created: " + project.getName());
        pressEnterToContinue();
    }
    
    private static void createSubproject() {
        List<Project> projects = tms.getProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects found. Create a project first.");
            pressEnterToContinue();
            return;
        }
        
        System.out.println("\n=== Projects ===");
        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getName());
        }
        
        int projectIndex = getValidListSelection(projects.size());
        if (projectIndex == -1) return;
        
        Project parentProject = projects.get(projectIndex);
        String name = getStringInput("Enter subproject name: ");
        Project subproject = new Project(name);
        parentProject.add(subproject);
        
        System.out.println("Subproject '" + name + "' added to '" + parentProject.getName() + "'");
        pressEnterToContinue();
    }
    
    private static void listAllProjects() {
        System.out.println("\n=== Projects ===");
        List<Project> projects = tms.getProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
        } else {
            for (int i = 0; i < projects.size(); i++) {
                System.out.println((i + 1) + ". " + projects.get(i).getName());
            }
        }
    }
    
    private static void createNewTask() {
        List<Project> projects = tms.getProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects found. Create a project first.");
            pressEnterToContinue();
            return;
        }
        
        String title = getStringInput("Enter task title: ");
        String description = getStringInput("Enter task description: ");
        String dueDate = getStringInput("Enter due date (e.g., YYYY-MM-DD): ");
        
        Task task = tms.createTask(title, description, dueDate);
        
        System.out.println("\n=== Projects ===");
        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getName());
        }
        
        int projectIndex = getValidListSelection(projects.size());
        if (projectIndex == -1) return;
        
        Project project = projects.get(projectIndex);
        project.add(new TaskItem(task));
        
        System.out.println("Task created and added to project '" + project.getName() + "'");
        pressEnterToContinue();
    }
    
    private static void cloneTemplateTask() {
        List<Task> templates = tms.getTemplateTasks();
        
        if (templates.isEmpty()) {
            System.out.println("No template tasks available. Create a template first.");
            pressEnterToContinue();
            return;
        }
        
        System.out.println("\n=== Template Tasks ===");
        for (int i = 0; i < templates.size(); i++) {
            System.out.println((i + 1) + ". " + templates.get(i).getTitle());
        }
        
        int templateIndex = getValidListSelection(templates.size());
        if (templateIndex == -1) return;
        
        Task templateTask = templates.get(templateIndex);
        Task clonedTask = (Task) templateTask.clone();
        clonedTask.setAssignee(null);  // Clear assignee
        
        String dueDate = getStringInput("Enter due date for this instance (e.g., YYYY-MM-DD): ");
        
        List<Project> projects = tms.getProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects found. Create a project first.");
            pressEnterToContinue();
            return;
        }
        
        System.out.println("\n=== Projects ===");
        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getName());
        }
        
        int projectIndex = getValidListSelection(projects.size());
        if (projectIndex == -1) return;
        
        Project project = projects.get(projectIndex);
        project.add(new TaskItem(clonedTask));
        
        System.out.println("Task cloned from template and added to project '" + project.getName() + "'");
        pressEnterToContinue();
    }
    
    private static void assignTaskToMember() {
        tms.displayAllProjects();
        System.out.println("\nEnter task ID to assign (or press Enter to cancel): ");
        String taskId = scanner.nextLine().trim();
        
        if (taskId.isEmpty()) {
            return;
        }
        
        Task task = tms.findTaskById(taskId);
        
        if (task == null) {
            System.out.println("Task not found. Please check the ID and try again.");
            pressEnterToContinue();
            return;
        }
        
        List<TeamMember> members = tms.getTeamMembers();
        if (members.isEmpty()) {
            System.out.println("No team members available. Add a team member first.");
            pressEnterToContinue();
            return;
        }
        
        System.out.println("\n=== Team Members ===");
        for (int i = 0; i < members.size(); i++) {
            System.out.println((i + 1) + ". " + members.get(i).getName());
        }
        
        int memberIndex = getValidListSelection(members.size());
        if (memberIndex == -1) return;
        
        TeamMember member = members.get(memberIndex);
        tms.assignTask(task, member);
        pressEnterToContinue();
    }
    
    private static void moveTaskToNextState() {
        tms.displayAllProjects();
        System.out.println("\nEnter task ID to update state (or press Enter to cancel): ");
        String taskId = scanner.nextLine().trim();
        
        if (taskId.isEmpty()) {
            return;
        }
        
        Task task = tms.findTaskById(taskId);
        
        if (task == null) {
            System.out.println("Task not found. Please check the ID and try again.");
            pressEnterToContinue();
            return;
        }
        
        tms.moveTaskToNextState(task);
        pressEnterToContinue();
    }
    
    private static void listTasksByProject() {
        List<Project> projects = tms.getProjects();
        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            pressEnterToContinue();
            return;
        }
        
        System.out.println("\n=== Projects ===");
        for (int i = 0; i < projects.size(); i++) {
            System.out.println((i + 1) + ". " + projects.get(i).getName());
        }
        
        int projectIndex = getValidListSelection(projects.size());
        if (projectIndex == -1) return;
        
        Project project = projects.get(projectIndex);
        
        System.out.println("\n=== Tasks in Project: " + project.getName() + " ===");
        project.display(0);
        pressEnterToContinue();
    }
    
    private static void addNewTeamMember() {
        String name = getStringInput("Enter team member name: ");
        TeamMember member = new TeamMember(name);
        tms.addTeamMember(member);
        System.out.println("Team member added: " + name);
        pressEnterToContinue();
    }
    
    private static void listAllTeamMembers() {
        System.out.println("\n=== Team Members ===");
        List<TeamMember> members = tms.getTeamMembers();
        if (members.isEmpty()) {
            System.out.println("No team members found.");
        } else {
            for (int i = 0; i < members.size(); i++) {
                System.out.println((i + 1) + ". " + members.get(i).getName());
            }
        }
    }
    
    private static void viewTeamMemberTasks() {
        List<TeamMember> members = tms.getTeamMembers();
        if (members.isEmpty()) {
            System.out.println("No team members found.");
            pressEnterToContinue();
            return;
        }
        
        System.out.println("\n=== Team Members ===");
        for (int i = 0; i < members.size(); i++) {
            System.out.println((i + 1) + ". " + members.get(i).getName());
        }
        
        int memberIndex = getValidListSelection(members.size());
        if (memberIndex == -1) return;
        
        TeamMember member = members.get(memberIndex);
        
        System.out.println("\n=== Tasks assigned to " + member.getName() + " ===");
        List<Task> tasks = member.getAssignedTasks();
        if (tasks.isEmpty()) {
            System.out.println("No tasks assigned.");
        } else {
            for (Task task : tasks) {
                System.out.println("- " + task);
            }
        }
        pressEnterToContinue();
    }
    
    private static void createTemplateTask() {
        List<TeamMember> members = tms.getTeamMembers();
        if (members.isEmpty()) {
            System.out.println("No team members found. Add a team member first.");
            pressEnterToContinue();
            return;
        }
        
        System.out.println("\n=== Team Members ===");
        for (int i = 0; i < members.size(); i++) {
            System.out.println((i + 1) + ". " + members.get(i).getName());
        }
        
        int memberIndex = getValidListSelection(members.size());
        if (memberIndex == -1) return;
        
        TeamMember member = members.get(memberIndex);
        
        String title = getStringInput("Enter template task title: ");
        String description = getStringInput("Enter template task description: ");
        String dueDate = getStringInput("Enter template schedule (e.g., 'Every Monday'): ");
        
        Task templateTask = member.createTemplateTask(title, description, dueDate);
        tms.addTemplateTask(templateTask);
        System.out.println("Template task created: " + templateTask.getTitle());
        pressEnterToContinue();
    }
    
    private static void pressEnterToContinue() {
        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }
}