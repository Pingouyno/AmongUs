import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

public class SB extends AmongUs{

                                                                                //DEBUT CODE INUTILE
    public static void updateLine(LobbyPlayer basedPlayer){
        Scoreboard updatedSB=basedPlayer.getLobby().getLobbyScoreBoard();
        int stateType = getStateType(basedPlayer.getLobby());
        switch (stateType) {
            case 0:
                drawEmergencySB(basedPlayer,updatedSB);
                replaceLine(3,basedPlayer);
                return;
            case 1:
                drawSabotageSB(basedPlayer,updatedSB);
                return;
            case 2:
                drawGameSB(basedPlayer,updatedSB);
                return;
            case 3:
                drawLobbySB(basedPlayer,updatedSB);
                replaceLine(3,basedPlayer);
                return;
        }
    }

    public static void replaceLine(int line, LobbyPlayer lobbyPlayer){
        Scoreboard playerSB = lobbyPlayer.getScoreBoard();
        Scoreboard lobbySB = lobbyPlayer.getLobby().getLobbyScoreBoard();
        String s = getLine(line,lobbySB);
        removeLine(line,playerSB);
        Objective obj = playerSB.getObjective(DisplaySlot.SIDEBAR);
        obj.getScore(s).setScore(line);
    }

    public static void removeLine(int row, Scoreboard scoreboard) {
        Objective obj=scoreboard.getObjective(DisplaySlot.SIDEBAR);
        for(String entry : scoreboard.getEntries()) {
            if(obj.getScore(entry).getScore() == row) {
                scoreboard.resetScores(entry);
                break;
            }
        }
    }

    public static String getLine(int row, Scoreboard scoreboard) {
        Objective obj=scoreboard.getObjective(DisplaySlot.SIDEBAR);
        for(String entry : scoreboard.getEntries()) {
            if(obj.getScore(entry).getScore() == row) {
                return entry;
            }
        }
        return null;
    }                                                                       //FIN CODE INUTILE


    public static void redrawAllScoreBoard(Lobby lobby) {
        int stateType = getStateType(lobby);
        switch (stateType) {
            case 0:
                for (LobbyPlayer people:lobby.playerlist){
                    drawEmergencySB(people,people.getOtherScoreBoard());
                }
                return;
            case 1:
                if (lobby.sabotageType==1 || lobby.sabotageType==2){
                    for (LobbyPlayer people:lobby.playerlist){
                        drawSabotageSB(people,people.getOtherScoreBoard());
                    }
                }else{drawSabotageTimerSB(lobby);}
                return;
            case 2:
                for (LobbyPlayer people:lobby.playerlist){
                    drawGameSB(people,people.getOtherScoreBoard());
                }
                return;
            case 3:
                for (LobbyPlayer people:lobby.playerlist){
                    drawLobbySB(people,people.getOtherScoreBoard());
                }
                return;
        }
    }

    public static void redrawScoreBoard(LobbyPlayer people){
        Lobby lobby=people.getLobby();
        int stateType = getStateType(lobby);
        switch (stateType) {
            case 0:
                drawEmergencySB(people,people.getOtherScoreBoard());
                return;
            case 1:
                if (lobby.sabotageType==1 || lobby.sabotageType==2){
                    for (LobbyPlayer players:lobby.playerlist){
                        drawSabotageSB(players,people.getOtherScoreBoard());
                    }
                }else{drawSabotageTimerSB(lobby);}
                return;
            case 2:
                drawGameSB(people,people.getOtherScoreBoard());
                return;
            case 3:
                drawLobbySB(people,people.getOtherScoreBoard());
                return;
        }
    }


    public static void drawEmergencySB(LobbyPlayer people,Scoreboard board) {
        Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
        String colorname;
        int aliveCount = 0;
        Lobby lobby= people.getLobby();

        resetBoard(board);
        for (LobbyPlayer players : lobby.playerlist) {
            if (players.isAlive) {
                if (players.colorID != people.colorID) {                      //ENLEVER LE "!" POUR TESTING
                    aliveCount++;
                    colorname= players.getColorName();
                    obj.getScore(getNeededShift(colorname)+players.getChatColor()+colorname+" §f➜ "+ players.getChatColor() + players.name).setScore(aliveCount);
                }
            }
        }

        obj.getScore("§3Couleur : §l" + people.getChatColor() + people.getColorName()).setScore(aliveCount + 6);
        obj.getScore("§3Votes : " + ChatColor.BLUE + (int) lobby.votecnt + "/" + (aliveCount+1)).setScore(aliveCount + 5);
        obj.getScore(" §4").setScore(aliveCount + 4);
        obj.getScore("§3Temps restant : "+giveTimerBasedColorCode(lobby.timer)+lobby.timer).setScore(aliveCount + 3);
        obj.getScore(" §2").setScore(aliveCount + 2);
        obj.getScore("          §l§aJoueurs :        ").setScore(aliveCount + 1);
        people.getPlayerPlayer().setScoreboard(board);
    }

    public static String giveTimerBasedColorCode(int timer){
        if (timer<10){
            return ("§4");
        }else{
            return ("§a");
        }
    }


    public static void drawSabotageSB(LobbyPlayer people, Scoreboard board){
        Lobby lobby=people.getLobby();
        Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
        resetBoard(board);

        if (lobby.sabotageType==1){
            obj.getScore("     §4Lumières sabotées!").setScore(15);
            obj.getScore("§3Electrical :").setScore(14);
            obj.getScore("§4✖ §3Réparer les lumières").setScore(13);
            drawTasksSB(people,obj);
        }else{
            obj.getScore("   §4Communications sabotées!").setScore(4);
            obj.getScore(" §1").setScore(3);
            obj.getScore("§3Communications :").setScore(2);
            obj.getScore("§4✖ §3Ajuster la fréquence").setScore(1);
        }
        people.getPlayerPlayer().setScoreboard(board);
    }


    public static void drawSabotageTimerSB(Lobby lobby){
        int time=lobby.timer;
        String[] checks=getFinishedCodes(lobby);
        String timeSection=String.valueOf(time);
        if (time<=10){
            timeSection="§4"+timeSection;
        }else{
            timeSection="§a"+timeSection;
        }
        Scoreboard board;
        Objective obj;
        for (LobbyPlayer people : lobby.playerlist) {
            board = people.getOtherScoreBoard();
            resetBoard(board);
            obj = board.getObjective(DisplaySlot.SIDEBAR);
            if(lobby.sabotageType==3){
                obj.getScore("     §cRéacteur saboté : "+timeSection).setScore(15);
                obj.getScore("§6Reactor : ").setScore(14);
                obj.getScore("§4✖ §6Activer les plaques").setScore(13);
            }else{
                obj.getScore("         §cO2 saboté : "+timeSection).setScore(15);
                obj.getScore(checks[0]+"§6Admin : Entrer le code").setScore(14);
                obj.getScore(checks[1]+"§6O2 : Entrer le code").setScore(13);
            }
            drawTasksSB(people,obj);
            people.getPlayerPlayer().setScoreboard(board);
        }
    }


    public static void drawGameSB(LobbyPlayer people,Scoreboard board) {
        Objective obj = board.getObjective(DisplaySlot.SIDEBAR);
        String impState;

        resetBoard(board);
        if (people.isImpostor) {
            impState = "§4IMPOSTEURS";
        } else {
            impState = "§bÉQUIPAGE";
        }

        obj.getScore("§3Couleur : §l" + people.getChatColor() + people.getColorName()).setScore(15);
        obj.getScore("§3Équipe : " + impState).setScore(14);
        obj.getScore(" §5").setScore(13);
        SB.drawTasksSB(people,obj);
        people.getPlayerPlayer().setScoreboard(board);
    }

    public static void drawLobbySB(LobbyPlayer people,Scoreboard board) {
        Objective obj= board.getObjective(DisplaySlot.SIDEBAR);
        Lobby lobby = people.getLobby();

        resetBoard(board);
        obj.getScore("§3Couleur : §l" + people.getChatColor() + people.getColorName()).setScore(1);
        obj.getScore("§1").setScore(2);
        obj.getScore("§3Votes : " + ChatColor.BLUE + (int) lobby.votecnt + "/" + lobby.playercnt).setScore(3);
        obj.getScore("§3Joueurs : " + getColoredPlayerCountLobby(lobby)).setScore(4);
        people.getPlayerPlayer().setScoreboard(board);
    }

    private static String getColoredPlayerCountLobby(Lobby lobby){
        if (lobby.playercnt<4){
            return ChatColor.RED +String.valueOf(lobby.playercnt) + "/10";
        }else{
            return ChatColor.GREEN + String.valueOf(lobby.playercnt) + "/10";
        }
    }


    public static void drawTasksSB(LobbyPlayer player, Objective obj){
        if (player.isImpostor){
            obj.getScore("        §l§aTâches à imiter :  ").setScore(12);
        }else{
            obj.getScore("            §l§aTâches :       ").setScore(12);
        }
        int x=11;
        int cpt=9;
        int step=0;
        for (Task tasks:player.getTasks()){
            obj.getScore("§3"+tasks.getCurrentSubTask().getLocation()+getProgressRatio(tasks)+getEntryVaryingColorCode(step)).setScore(x);
            obj.getScore(getTaskCheckBox(tasks)+" §3"+tasks.getDescription()).setScore(x-1);
            obj.getScore("§"+cpt).setScore(x-2);
            x-=3;
            cpt--;
            step++;
        }
    }

    public static String getEntryVaryingColorCode(int step){
        return ("§"+step+" ");
    }

    public static void resetBoard(Scoreboard board) {
        for (String s : board.getEntries()) {
            board.resetScores(s);
        }
    }

    public static String getNeededShift(String string){
        if (string.length()==4){
            return " ";
        }
        return "";
    }

    public static String getTaskCheckBox(Task task){
        if (task.isFinished()){
            return "§a✔";
        }else{return "§4✖";}
    }

    public static String[] getFinishedCodes(Lobby lobby){
        World world = lobby.getWorld();
        Block block1 = getButtonInList(world, buttonList[5]);
        Block block2 = getButtonInList(world, buttonList[6]);

        Sign sign1 = (Sign) block1.getState();
        Sign sign2 = (Sign) block2.getState();

        String c1;
        String c2;

        if (sign1.getLine(1).equals("Normal")){
            c1="§a✔ ";
        }else{c1="§4✖ ";}

        if (sign2.getLine(1).equals("Normal")){
            c2="§a✔ ";
        }else{c2="§4✖ ";}

        String[] checks={c1,c2};
        return checks;
    }
}

