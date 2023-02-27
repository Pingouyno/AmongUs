import org.bukkit.ChatColor;
import org.bukkit.event.player.PlayerInteractEvent;
import java.util.ArrayList;
import java.util.List;

public class TaskManager extends AmongUs{

    public static List<subTask> newSubTask(String[] locationList){
        List<subTask> subtaskAssemblor = new ArrayList<>();
        int i=0;
        for (String locations:locationList){
            subtaskAssemblor.add(new subTask(i,locations));
            i++;
        }
        return subtaskAssemblor;
    }

    public static void createButtonTaskList(){
        if (INDICE_DE_MONDE==0){
            buttonTaskList = new Buttons[40];
            buttonTaskList[0] = new Buttons("task_00_00", new int[]{57, 66, -60});

            buttonTaskList[1] = new Buttons("task_01_00", new int[]{58, 66, -60});
            buttonTaskList[2] = new Buttons("task_01_01", new int[]{58, 66, -59});

            buttonTaskList[3] = new Buttons("task_02_00", new int[]{59, 66, -60});

            buttonTaskList[4] = new Buttons("task_03_00", new int[]{60, 66, -60});

            buttonTaskList[5] = new Buttons("task_04_00", new int[]{61, 66, -60});

            buttonTaskList[6] = new Buttons("task_05_00", new int[]{62, 66, -60});
            buttonTaskList[7] = new Buttons("task_05_01", new int[]{62, 66, -59});
            buttonTaskList[8] = new Buttons("task_05_02", new int[]{62, 66, -58});
            buttonTaskList[9] = new Buttons("task_05_03", new int[]{62, 66, -57});
            buttonTaskList[10] = new Buttons("task_05_04", new int[]{62, 66, -56});
            buttonTaskList[11] = new Buttons("task_05_05", new int[]{62, 66, -55});
            buttonTaskList[12] = new Buttons("task_05_06", new int[]{62, 66, -54});
            buttonTaskList[13] = new Buttons("task_05_07", new int[]{62, 66, -53});
            buttonTaskList[14] = new Buttons("task_05_08", new int[]{62, 66, -52});

            buttonTaskList[15] = new Buttons("task_06_00", new int[]{63, 66, -60});
            buttonTaskList[16] = new Buttons("task_06_01", new int[]{63, 66, -59});
            buttonTaskList[17] = new Buttons("task_06_02", new int[]{63, 66, -58});

            buttonTaskList[18] = new Buttons("task_16_00", new int[]{73, 66, -60});                                      //Ce bouton n'est pas classé en ordre alphanumérique

            buttonTaskList[19] = new Buttons("task_07_00", new int[]{64, 66, -60});
            buttonTaskList[20] = new Buttons("task_07_01", new int[]{64, 66, -59});
            buttonTaskList[21] = new Buttons("task_07_02", new int[]{64, 66, -58});
            buttonTaskList[22] = new Buttons("task_07_03", new int[]{64, 66, -57});
            buttonTaskList[23] = new Buttons("task_07_04", new int[]{64, 66, -56});
            buttonTaskList[24] = new Buttons("task_07_05", new int[]{64, 66, -55});

            buttonTaskList[25] = new Buttons("task_08_00", new int[]{65, 66, -60});
            buttonTaskList[26] = new Buttons("task_08_01", new int[]{65, 66, -59});
            buttonTaskList[27] = new Buttons("task_08_02", new int[]{65, 66, -58});

            buttonTaskList[28] = new Buttons("task_09_00", new int[]{66, 66, -60});

            buttonTaskList[29] = new Buttons("task_10_00", new int[]{67, 66, -60});

            buttonTaskList[30] = new Buttons("task_11_00", new int[]{68, 66, -60});

            buttonTaskList[31] = new Buttons("task_12_00", new int[]{69, 66, -60});

            buttonTaskList[32] = new Buttons("task_13_00", new int[]{70, 66, -60});

            buttonTaskList[33] = new Buttons("task_14_00", new int[]{71, 66, -60});

            buttonTaskList[34] = new Buttons("task_15_00", new int[]{72, 66, -60});
            buttonTaskList[35] = new Buttons("task_15_01", new int[]{72, 66, -59});
            buttonTaskList[36] = new Buttons("task_15_02", new int[]{72, 66, -58});
            buttonTaskList[37] = new Buttons("task_15_03", new int[]{72, 66, -57});
            buttonTaskList[38] = new Buttons("task_15_04", new int[]{72, 66, -56});
            buttonTaskList[39] = new Buttons("task_15_05", new int[]{72, 66, -55});

        }else{
            buttonTaskList = new Buttons[40];

            buttonTaskList[0] = new Buttons("task_00_00", new int[]{51, 121, -46});

            buttonTaskList[1] = new Buttons("task_01_00", new int[]{32, 121, -62});
            buttonTaskList[2] = new Buttons("task_01_01", new int[]{33, 121, -41});

            buttonTaskList[3] = new Buttons("task_02_00", new int[]{112, 121, -57});

            buttonTaskList[4] = new Buttons("task_03_00", new int[]{76, 121, -57});

            buttonTaskList[5] = new Buttons("task_04_00", new int[]{85, 121, -68});

            buttonTaskList[6] = new Buttons("task_05_00", new int[]{45, 121, -47});
            buttonTaskList[7] = new Buttons("task_05_01", new int[]{33, 121, -47});
            buttonTaskList[8] = new Buttons("task_05_02", new int[]{106, 121, -58});
            buttonTaskList[9] = new Buttons("task_05_03", new int[]{82, 121, -37});
            buttonTaskList[10] = new Buttons("task_05_04", new int[]{82, 121, -58});
            buttonTaskList[11] = new Buttons("task_05_05", new int[]{46, 121, -57});
            buttonTaskList[12] = new Buttons("task_05_06", new int[]{93, 121, -44});
            buttonTaskList[13] = new Buttons("task_05_07", new int[]{34, 121, -69});
            buttonTaskList[14] = new Buttons("task_05_08", new int[]{87, 121, -67});

            buttonTaskList[15] = new Buttons("task_06_00", new int[]{76, 121, -55});
            buttonTaskList[16] = new Buttons("task_06_01", new int[]{66, 121, -73});
            buttonTaskList[17] = new Buttons("task_06_02", new int[]{68, 121, -31});


            buttonTaskList[18] = new Buttons("task_16_00", new int[]{23, 121, -61});                        //Ce bouton n'est pas classé en ordre alphanumérique


            buttonTaskList[19] = new Buttons("task_07_00", new int[]{48, 121, -46});
            buttonTaskList[20] = new Buttons("task_07_01", new int[]{64, 121, -49});
            buttonTaskList[21] = new Buttons("task_07_02", new int[]{67, 121, -54});
            buttonTaskList[22] = new Buttons("task_07_03", new int[]{103, 121, -55});
            buttonTaskList[23] = new Buttons("task_07_04", new int[]{57, 121, -72});
            buttonTaskList[24] = new Buttons("task_07_05", new int[]{38, 121, -55});

            buttonTaskList[25] = new Buttons("task_08_00", new int[]{62, 121, -35});
            buttonTaskList[26] = new Buttons("task_08_01", new int[]{36, 121, -43});
            buttonTaskList[27] = new Buttons("task_08_02", new int[]{35, 121, -64});

            buttonTaskList[28] = new Buttons("task_09_00", new int[]{51, 121, -51});

            buttonTaskList[29] = new Buttons("task_10_00", new int[]{86, 121, -36});

            buttonTaskList[30] = new Buttons("task_11_00", new int[]{114, 121, -54});

            buttonTaskList[31] = new Buttons("task_12_00", new int[]{24, 121, -54});

            buttonTaskList[32] = new Buttons("task_13_00", new int[]{72, 121, -51});

            buttonTaskList[33] = new Buttons("task_14_00", new int[]{55, 121, -52});

            buttonTaskList[34] = new Buttons("task_15_00", new int[]{70, 121, -72});
            buttonTaskList[35] = new Buttons("task_15_01", new int[]{77, 121, -37});
            buttonTaskList[36] = new Buttons("task_15_02", new int[]{46, 121, -47});
            buttonTaskList[37] = new Buttons("task_15_03", new int[]{84, 121, -72});
            buttonTaskList[38] = new Buttons("task_15_04", new int[]{69, 121, -53});
            buttonTaskList[39] = new Buttons("task_15_05", new int[]{109, 121, -58});
        }
    }

    public static void createTaskList(){
        taskList=new subTaskList[17];
        String[] locationList;

        locationList=new String[]{"Electrical"};
        taskList[0]=new subTaskList(0,1, "Calibrer le distributeur",1,newSubTask(locationList));

        locationList=new String[]{"Upper Engine","Lower Engine"};
        taskList[1]=new subTaskList(1,2, "Enligner les moteurs",1,newSubTask(locationList));

        locationList=new String[]{"Navigation"};
        taskList[2]=new subTaskList(2,1, "Tracer la trajectoire",1,newSubTask(locationList));

        locationList=new String[]{"O2"};
        taskList[3]=new subTaskList(3,1, "Nettoyer le filtre",1,newSubTask(locationList));

        locationList=new String[]{"Weapons"};
        taskList[4]=new subTaskList(4,20, "Détruire les astéroïdes",2,newSubTask(locationList));

        locationList=new String[]{"Electrical","Lower Engine","Navigation","Communications","O2","Security","Shields","Upper Engine","Weapons"};
        taskList[5]=new subTaskList(5,2, "Dérouter le courant",10,newSubTask(locationList));

        locationList=new String[]{"O2","Cafeteria","Storage"};
        taskList[6]=new subTaskList(6,2, "Vider la chute",2,newSubTask(locationList));

        locationList=new String[]{"Electrical","Storage","Admin","Navigation","Cafeteria","Security"};
        taskList[7]=new subTaskList(7,3, "Réparer les câbles",10,newSubTask(locationList));

        locationList=new String[]{"Storage","Upper Engine","Lower Engine"};
        taskList[8]=new subTaskList(8,2, "Remplir le réservoir",2,newSubTask(locationList));

        locationList=new String[]{"Medbay"};
        taskList[9]=new subTaskList(9,1, "Analyser l'échantillon",1,newSubTask(locationList));

        locationList=new String[]{"Shields"};
        taskList[10]=new subTaskList(10,1, "Activer les boucliers",1,newSubTask(locationList));

        locationList=new String[]{"Navigation"};
        taskList[11]=new subTaskList(11,1, "Ajuster le guidon",1,newSubTask(locationList));

        locationList=new String[]{"Reactor"};
        taskList[12]=new subTaskList(12,1, "Démarrer le réacteur",1,newSubTask(locationList));

        locationList=new String[]{"Admin"};
        taskList[13]=new subTaskList(13,1, "Balayer la carte",10,newSubTask(locationList));

        locationList=new String[]{"MedBay"};
        taskList[14]=new subTaskList(14,1, "Faire un scan",2,newSubTask(locationList));

        locationList=new String[]{"Cafeteria","Communications","Electrical","Weapons","Admin","Navigation"};
        taskList[15]=new subTaskList(15,2, "Transférer les données",10,newSubTask(locationList));

        locationList=new String[]{"Reactor"};
        taskList[16]=new subTaskList(16,1, "Débloquer le collecteur",1,newSubTask(locationList));
    }


    public static void checkTaskType(String button, LobbyPlayer lobbyplayer, PlayerInteractEvent e){
        int but_num=Integer.parseInt(button.substring(5,7));
        for (Task tasks:lobbyplayer.getTasks()){
            if (but_num==tasks.getID()){
                if (Integer.parseInt(button.substring(8,10))==tasks.getCurrentSubTask().getID()){
                    if (lobbyplayer.isAlive){
                        e.setCancelled(false);
                    }
                    if (!tasks.isFinished()&&!lobbyplayer.getLobby().isLocked){
                        if (lobbyplayer.getLobby().sabotageType!=2){
                            switch (but_num){                                                               // Une tâche est "task_01_02" par exemple
                                case 0:
                                    InventoryManager.giveDistributorInventory(lobbyplayer,true);
                                    return;
                                case 1:
                                    InventoryManager.giveEngineInventory(lobbyplayer);
                                    return;
                                case 2:
                                    InventoryManager.giveTrajectoryInventory(lobbyplayer);
                                    return;
                                case 3:
                                    InventoryManager.giveFilterInventory(lobbyplayer);
                                    return;
                                case 4:
                                    InventoryManager.giveAsteroidInventory(lobbyplayer);
                                    return;
                                case 5:
                                    InventoryManager.giveDivertInventory(lobbyplayer);
                                    return;
                                case 6:
                                    InventoryManager.giveChuteInventory(lobbyplayer);
                                    return;
                                case 7:
                                    InventoryManager.giveWireInventory(lobbyplayer);
                                    return;
                                case 8:
                                    InventoryManager.giveFuelInventory(lobbyplayer);
                                    return;
                                case 9:
                                    String meta=tasks.getMetaData();
                                    if (meta!=null && meta.substring(5,6).equals("1")){
                                        if (meta.startsWith("00")){
                                            InventoryManager.giveSampleSecondInventory(lobbyplayer);
                                        }else{
                                            InventoryManager.giveSampleCountdownInventory(lobbyplayer,meta);
                                        }
                                    }else{
                                        InventoryManager.giveSampleInventory(lobbyplayer);
                                    }
                                    return;
                                case 10:
                                    InventoryManager.giveShieldInventory(lobbyplayer);
                                    return;
                                case 11:
                                    InventoryManager.giveSteeringInventory(lobbyplayer);
                                    return;
                                case 12:
                                    InventoryManager.giveReactorInventory(lobbyplayer);
                                    return;
                                case 13:
                                    InventoryManager.giveCardInventory(lobbyplayer);
                                    return;
                                case 14:
                                    InventoryManager.giveScanInventory(lobbyplayer);
                                    TaskLogic.checkScan(lobbyplayer);
                                    return;
                                case 15:
                                    InventoryManager.giveUploadInventory(lobbyplayer);
                                    return;
                                case 16:
                                    InventoryManager.giveManifoldInventory(lobbyplayer);
                                    return;
                            }
                        }else{
                            lobbyplayer.getPlayerPlayer().sendMessage(ChatColor.RED+"\nTâches indisponibles tant que les communications sont sabotées!");
                        }
                    }
                }else{
                    lobbyplayer.getPlayerPlayer().sendMessage(ChatColor.RED+"\nVous n'êtes pas au bon endroit!");
                }
            }
        }
    }
}
