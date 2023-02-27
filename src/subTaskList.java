import java.util.List;

public class subTaskList {
    int ID;
    int totalTasks;
    String descript;
    List<subTask> subTasks;
    int unique;

    public subTaskList(int thisID,int tasksToDo, String description,int maxAllowed,List<subTask> subTaskList){
        this.ID=thisID;
        this.totalTasks=tasksToDo;
        this.descript=description;
        this.subTasks=subTaskList;
        this.unique=maxAllowed;
    }

    public int getID(){ return ID; }
    public List<subTask> getSubTasks(){ return subTasks; }
    public int getTasksToDo(){
        return totalTasks;
    }
    public String getDescription(){return descript;}
    public int getMaxAllowed(){return unique;}
}
