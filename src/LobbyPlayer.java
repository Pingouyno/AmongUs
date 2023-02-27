import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;

public class LobbyPlayer{
    HumanEntity getServerPlayer;
    Player player;
    int lobbyID;
    int votecount;
    String name;
    Boolean isAlive;
    Boolean isImpostor;
    Boolean voted;
    ItemStack[] inventory;
    Inventory taskInv;
    int colorID;
    Task[] tasks;
    ArrayList<Integer> holograms;

    public LobbyPlayer(HumanEntity serverPlayer, int lobbyID) {
        this.player = ((Player) serverPlayer);
        this.name = serverPlayer.getName();
        this.isAlive = true;
        this.isImpostor = false;
        this.voted = false;
        this.votecount = votecount;
        this.lobbyID = lobbyID;
        this.getServerPlayer = serverPlayer;
        this.inventory = serverPlayer.getInventory().getContents();
        this.colorID = colorID;
        this.tasks=new Task[4];
        this.taskInv=Bukkit.createInventory(null, 54, "Tâche");
        this.holograms=new ArrayList<>();
    }

    public Lobby getLobby() {
        return AmongUs.lobbylist[lobbyID];
    }
    public Scoreboard getScoreBoard() {
        return this.getPlayerPlayer().getScoreboard();
    }
    public Scoreboard getOtherScoreBoard(){
        for (Scoreboard board:getLobby().sboards[colorID]){
            if (!board.equals(getScoreBoard())){
                return board;
            }
        }
        return null;
    }
    public Player getPlayerPlayer() {
        return player;
    }
    public Color getColor() {
        return AmongUs.colorlist[colorID].color;
    }
    public ChatColor getChatColor() {
        return AmongUs.colorlist[colorID].chat;
    }
    public String getColorName() {
        String name = AmongUs.colorlist[colorID].name;
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public Task[] getTasks() {
        return tasks;
    }

    public Task getTask(int ID_in_tasklist){
        for (Task task:tasks){
            if (task.getID()==ID_in_tasklist){
                return task;
            }
        }
        return null;
    }

    public Inventory getTaskInventory(){
        return taskInv;
    }

    public void setTaskInventory(Inventory inventory){
        taskInv=inventory;
    }

    public ArrayList<Integer> getHologramList(){return holograms;}
}
