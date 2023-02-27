import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.scoreboard.Scoreboard;

import java.util.ArrayList;

public class Lobby{


    public int id;
    public int sabotageType;
    public int skipcount;
    public AmongUs.Locations locations;
    public ArrayList<LobbyPlayer> playerlist = new ArrayList<>();
    public int playercnt = 0;
    public double votecnt = 0.0;
    public boolean started;
    public boolean softLock;
    public boolean isLocked;
    public boolean isInEmergency = false;
    public boolean isInVotingSession = false;
    public boolean isInSabotage = false;
    public Boolean[] colorTakenList = AmongUs.newFalseList();
    public Scoreboard[][] sboards = AmongUs.newScoreBoardList();
    public BossBar bar;
    public int timer;
    public World world;

    public Lobby() {
        this.softLock = false;
        this.skipcount = skipcount;
        this.sabotageType = sabotageType;
        this.id = id;
        this.locations = locations;
        this.votecnt = votecnt;
        this.playerlist = playerlist;
        this.playercnt = playercnt;
        this.started = started;
        this.isInEmergency = isInEmergency;
        this.isInSabotage = isInSabotage;
        this.isLocked = isLocked;
        this.colorTakenList = colorTakenList;
        this.isInVotingSession = isInVotingSession;
        this.sboards = sboards;
        this.timer=0;
        this.bar= Bukkit.createBossBar("Tâches", BarColor.GREEN, BarStyle.SEGMENTED_6);
    }

    public void reset() {
        softLock=false;
        playerlist = new ArrayList<>();
        playercnt = 0;
        votecnt = 0.0;
        started = false;
        isLocked = false;
        isInEmergency = false;
        isInSabotage = false;
        isInVotingSession = false;
        skipcount = 0;
        colorTakenList = AmongUs.newFalseList();
        sabotageType = 0;
        bar.removeAll();
        timer=0;
    }

    public World getWorld() {return world;}

    public Scoreboard getLobbyScoreBoard() {
        return sboards[10][0];
    }

    public BossBar getBossBar(){return bar;}

    public boolean isSoftLocked(){return softLock;}

    public void setSoftLock(boolean lock){this.softLock=lock;}
}
