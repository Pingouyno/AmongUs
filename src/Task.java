public class Task {
    subTask currentSubTask;
    Boolean state;
    int progress;
    subTaskList thisSubTaskList;
    LobbyPlayer lobbyplayer;
    String meta;
    int id;

    public Task(int ID_in_taskList, LobbyPlayer lobbyPlayer){
        this.id=ID_in_taskList;
        this.thisSubTaskList=AmongUs.taskList[ID_in_taskList];
        this.state=false;
        this.currentSubTask=AmongUs.getRandomSubTask(this);
        this.progress=1;
        this.lobbyplayer=lobbyPlayer;
        this.meta=meta;
    }

    public int getID(){
        return id;
    }
    public LobbyPlayer getPlayer() {return lobbyplayer;}
    public int getSize(){
        return thisSubTaskList.getTasksToDo();
    }
    public subTask getCurrentSubTask(){
        return currentSubTask;
    }
    public String getDescription(){
        return thisSubTaskList.getDescription();
    }
    public Boolean isFinished(){
        return state;
    }
    public void setFinished(Boolean isFinished){state=isFinished;}
    public void resetCurrentSub(){meta=null; lobbyplayer.getPlayerPlayer().closeInventory();}
    public void finishCurrentSub(){AmongUs.finishSubTask(this);resetCurrentSub();}
    public subTaskList getSubTaskList(){return thisSubTaskList;}
    public int getProgress(){return progress;}
    public int getTotalToDo(){return thisSubTaskList.getTasksToDo();}
    public void setNewSubTask(){currentSubTask=AmongUs.getRandomSubTask(this);}
    public String getMetaData(){return meta;}
    public void setMetaData(String data){meta=data;}
}
