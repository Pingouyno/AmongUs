import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class InventoryManager {

    public static void init() {
    }

    public static void giveWireInventory(LobbyPlayer lobbyplayer){
        lobbyplayer.getTask(7).resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 54, "Réparer les câbles");

        for (int i=0;i<46;i+=9){
            inventory.setItem(i,ItemManager.glassItems[2]);
        }
        for (int i=45;i<54;i++){
            inventory.setItem(i,ItemManager.glassItems[2]);
        }
        for (int i=53;i>7;i-=9){
            inventory.setItem(i,ItemManager.glassItems[2]);
        }
        for (int i=8;i>-1;i--){
            inventory.setItem(i,ItemManager.glassItems[2]);
        }

        List<Integer> leftrow = new ArrayList<>(Arrays.asList(10,19,28,37));
        List<Integer> rightrow = new ArrayList<>(Arrays.asList(16,25,34,43));

        int randomNum;
        for (ItemStack wire:ItemManager.colorWires){
            randomNum = ThreadLocalRandom.current().nextInt(0, leftrow.size());
            inventory.setItem(leftrow.get(randomNum), wire);
            leftrow.remove(randomNum);
        }
        for (ItemStack wire:ItemManager.colorWires){
            randomNum = ThreadLocalRandom.current().nextInt(0, rightrow.size());
            inventory.setItem(rightrow.get(randomNum), wire);
            rightrow.remove(randomNum);
        }
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }

    public static void giveScanInventory(LobbyPlayer lobbyplayer){
        lobbyplayer.getTask(14).resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 27, "Scan-Mo-Tron-2000");
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }

    public static void giveScanLoaderInventory(LobbyPlayer lobbyplayer,int time){
        Inventory inventory= lobbyplayer.getTaskInventory();
        List<Integer> slotlist = new ArrayList<>(Arrays.asList(11,12,13,14,15));
        if (time>18){
            if (time==20){
                for (int slot:slotlist) {
                    inventory.setItem(slot,ItemManager.scanloaders[2]);
                }
            }else{
                for (int slot:slotlist) {
                    inventory.setItem(slot,ItemManager.scanloaders[3]);
                }
            }
        }else{
            int rest= slotlist.get(4-(time%5));
            for (int slot:slotlist){
                if (slot==rest){
                    inventory.setItem(slot,ItemManager.scanloaders[1]);
                }else{
                    inventory.setItem(slot,ItemManager.scanloaders[0]);
                }
            }
        }
    }

    public static void giveUploadInventory(LobbyPlayer lobbyplayer){
        lobbyplayer.getTask(15).resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 27, "Transférer les données");
        inventory.setItem(22, ItemManager.uploadstart);
        List<Integer> slotlist = new ArrayList<>(Arrays.asList(10,11,12,13,14,15,16));
        for (int slot:slotlist){
            inventory.setItem(slot,ItemManager.uploadloaders[2]);
        }
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }

    public static void giveUploadLoaderInventory(LobbyPlayer lobbyplayer,int time){

        int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
        if (randomNum==0 || time<4){
            Inventory inventory= lobbyplayer.getTaskInventory();
            ArrayList<Integer> slotlist = new ArrayList<>(Arrays.asList(10,11,12,13,14,15,16));
            double progress=(float)(17-time)/17;
            if (time==1){
                progress=1.0;
            }
            double ratiobar=Math.round(progress*7);

            for (int slot:slotlist){
                if (slot-10<ratiobar){
                    inventory.setItem(slot,ItemManager.uploadloaders[1]);
                }else{
                    inventory.setItem(slot,ItemManager.uploadloaders[0]);
                }
            }
            int percentage;
            if (progress==0){
                percentage=1;
            }else{
                percentage=(int)(progress*100);
            }
            ItemStack item = new ItemStack(Material.PAPER, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD+"Progrès : "+ChatColor.GREEN+percentage+"%");
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            inventory.setItem(22,item);
        }
    }

    public static void giveTrajectoryInventory(LobbyPlayer lobbyplayer){
        lobbyplayer.getTask(2).resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 36, "Tracer la trajectoire");
        int lastRandom=-1;
        int randomNum=-1;
        for (int i=0;i<5;i++){
            while (randomNum==lastRandom){
                randomNum = ThreadLocalRandom.current().nextInt(0, 4);
            }
            lastRandom=randomNum;
            int slot=(i*2)+(randomNum*9);
            if (i==0){
                inventory.setItem(slot,ItemManager.trajectorycursor);
            }else{
                inventory.setItem(slot,ItemManager.trajectoryItems[i]);
            }
        }
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }

    public static void giveFuelInventory(LobbyPlayer lobbyplayer){
        Task task= lobbyplayer.getTask(8);
        if (task.getMetaData()==null){
            task.setMetaData("0");
        }
        int progress=Integer.valueOf(task.getMetaData());
        Inventory inventory = Bukkit.createInventory(null, 9, "Remplir le réservoir");
        for (int i=0;i<9;i++){
            if (i<progress){
                inventory.setItem(i,ItemManager.fuelItems[1]);
            }else if (i==progress){
                inventory.setItem(i,ItemManager.fuelItems[2]);
            }else{
                inventory.setItem(i,ItemManager.fuelItems[0]);
            }
        }
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }

    public static void giveDivertInventory(LobbyPlayer lobbyplayer){
        lobbyplayer.getTask(5).resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 27, "Dérouter le courant");
        for (int i=9;i<18;i++) {
            if (i==9){
                inventory.setItem(i,ItemManager.divertItems[2]);
            }else if (i==13){
                inventory.setItem(i,ItemManager.divertItems[1]);
            }else{
                inventory.setItem(i,ItemManager.divertItems[3]);
            }
        }
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }

    public static void giveAsteroidInventory(LobbyPlayer lobbyplayer){
        lobbyplayer.getTask(4).resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 54, "Détruire les astéroïdes");
        for (int i=0;i<inventory.getSize();i++) {
            inventory.setItem(i,ItemManager.asteroidItems[0]);
        }
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
        TaskLogic.checkAsteroid(lobbyplayer);
    }

    public static void giveAsteroidUpdateInventory(LobbyPlayer lobbyplayer){
        Inventory inventory = lobbyplayer.getTaskInventory();
        for (int i=0; i<9; i++){
            for (int f=0;f<6;f++){
                int slot = i+(f*9);
                ItemMeta meta=inventory.getItem(slot).getItemMeta();
                if (meta!=null){
                    String name=meta.getLocalizedName();
                    if (name!=null &&name.equals("task_04")){
                        if (i!=0){
                            inventory.setItem(slot-1,ItemManager.asteroidItems[1]);
                        }
                        inventory.setItem(slot,ItemManager.asteroidItems[0]);
                    } else if (i==8){
                        int randomNum = ThreadLocalRandom.current().nextInt(0, 14);
                        if (randomNum==1){
                            inventory.setItem(slot,ItemManager.asteroidItems[1]);
                        }
                    }
                }
            }
        }
    }

    public static void giveDistributorInventory(LobbyPlayer lobbyplayer,Boolean firstTime){
        Inventory inventory;
        if (firstTime){
            inventory = Bukkit.createInventory(null, 45, "Calibrer le distributeur");
            Task task= lobbyplayer.getTask(0);
            task.setMetaData("1_00");
        }else{
            inventory = lobbyplayer.getTaskInventory();
        }
        inventory.setItem(7,ItemManager.distributorItems[0]);
        inventory.setItem(8,ItemManager.distributorItems[0]);
        inventory.setItem(25,ItemManager.distributorItems[2]);
        inventory.setItem(26,ItemManager.distributorItems[2]);
        inventory.setItem(43,ItemManager.distributorItems[4]);
        inventory.setItem(44,ItemManager.distributorItems[4]);
        inventory.setItem(23,ItemManager.distributorItems[6]);

        if (firstTime){
            lobbyplayer.setTaskInventory(inventory);
            lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
        }
        TaskLogic.startDistributorLoop(lobbyplayer,1);
    }

    public static void giveDistributorUpdateInventory(LobbyPlayer lobbyplayer, int type, int[] slotlist,int time){
        Inventory inventory= lobbyplayer.getTaskInventory();
        int lastSlot;
        if (time==0){
            lastSlot=slotlist[15];
        }else{
            lastSlot=slotlist[time-1];
        }
        inventory.setItem(slotlist[time],ItemManager.distributorItems[type]);
        inventory.setItem(lastSlot, ItemManager.distributorItems[type-1]);
    }

    public static void giveShieldInventory(LobbyPlayer lobbyplayer){
        lobbyplayer.getTask(10).resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 45, "Activer les boucliers");
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
        int[] drawSquare = {9,1,-9,-1};
        int slot = 2;
        for (int variation:drawSquare){
            for (int i=0;i<4;i++){
                inventory.setItem(slot,ItemManager.shieldItems[2]);
                slot+=variation;
            }
        }
        int redFlagAmount = ThreadLocalRandom.current().nextInt(3, 7);
        List<Integer> redFlagSlots = new ArrayList<>(Arrays.asList(12,13,14,21,22,23,30,31,32));
        for (int i=0;i<redFlagAmount;i++){
            int randomSlot = ThreadLocalRandom.current().nextInt(0, redFlagSlots.size());
            inventory.setItem(redFlagSlots.get(randomSlot),ItemManager.shieldItems[0]);
            redFlagSlots.remove(randomSlot);
        }
        for (int i=0;i<redFlagSlots.size();i++){
            inventory.setItem(redFlagSlots.get(i),ItemManager.shieldItems[1]);
        }
    }

    public static void giveCardInventory(LobbyPlayer lobbyplayer){
        lobbyplayer.getTask(13).resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 45, "Balayer la carte");
        for (int i=0;i<45;i++){
            inventory.setItem(i, ItemManager.cardItems[4]);
        }
        int[] glassSlots={7,8,25,26};
        int[] barSlots={0,6,24,15,18,1,2,3,4,5,19,20,21,22,23};
        int[] emptySlots={10,11,12,13,14};
        inventory.setItem(40, ItemManager.cardItems[0]);
        inventory.setItem(9, ItemManager.cardItems[1]);
        for (int i:barSlots){
            inventory.setItem(i, ItemManager.cardItems[2]);
        }
        for (int i:glassSlots){
            inventory.setItem(i, ItemManager.cardItems[3]);
        }
        inventory.setItem(16, ItemManager.distributorItems[2]);
        inventory.setItem(17,ItemManager.distributorItems[4]);
        for (int i:emptySlots){
            inventory.setItem(i,null);
        }
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }


    public static void giveFilterInventory(LobbyPlayer lobbyplayer){
        lobbyplayer.getTask(3).resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 54, "Nettoyer le filtre");
        for (int i=0;i<inventory.getSize();i++){
            inventory.setItem(i,ItemManager.filterItems[2]);
        }
        inventory.setItem(18,ItemManager.filterItems[1]);
        inventory.setItem(27,ItemManager.filterItems[1]);
        int[] glassSlots={0,9,36,45,46,47,48,49,50,51,52,53,44,35,26,17,8,7,6,5,4,3,2,1};
        for (int i:glassSlots){
            inventory.setItem(i,ItemManager.filterItems[3]);

        }
        generateRandomLeaves(inventory);
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }

    private static void generateRandomLeaves(Inventory inventory){
        int randomNum;
        List<Integer> slotList = new ArrayList<>(Arrays.asList(10,11,12,13,14,15,16,19,20,21,22,23,24,25,28,29,30,31,32,33,34,37,38,39,40,41,42,43));
        for (int i=0;i<6;i++){
            randomNum = ThreadLocalRandom.current().nextInt(0, slotList.size());
            inventory.setItem(slotList.get(randomNum), ItemManager.filterItems[0]);
            slotList.remove(randomNum);
        }
    }


    public static void giveReactorInventory(LobbyPlayer lobbyplayer){
        Task task= lobbyplayer.getTask(12);
        task.resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 45, "Démarrer le réacteur");

        int[] brownSlots={2,3,4,5,6};
        int[] leftSquareSlots={19,20,21,28,29,30,37,38,39};
        int[] rightSquareSlots={23,24,25,32,33,34,41,42,43};

        for (int i:brownSlots){
            inventory.setItem(i,ItemManager.glassItems[5]);
        }
        for (int i:leftSquareSlots){
            inventory.setItem(i,ItemManager.glassItems[3]);
        }
        for (int i:rightSquareSlots){
            inventory.setItem(i,ItemManager.glassItems[3]);
        }
        inventory.setItem(2,ItemManager.glassItems[4]);

        String newMeta="";
        List<Integer> padSlots = new ArrayList<>(Arrays.asList(0,1,2,3,4,5,6,7,8));
        for (int i=0;i<5;i++){
            int randomNum = ThreadLocalRandom.current().nextInt(0, padSlots.size());
            newMeta=newMeta+randomNum;
        }
        newMeta=newMeta+"t"+1+"_0";
        task.setMetaData(newMeta);

        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
        TaskLogic.startReactorAnimation(lobbyplayer,1);
    }


    public static void giveReactorUpdateInventory(Inventory inventory, Task task, int time, int currentIndex){
        int currentSlot=Integer.valueOf(task.getMetaData().substring(currentIndex,currentIndex+1));
        int[] exampleSquareSlots={19,20,21,28,29,30,37,38,39};
        switch (time){
            case 0:
                inventory.setItem(exampleSquareSlots[currentSlot],ItemManager.glassItems[1]);
                return;
            case 3:
                inventory.setItem(exampleSquareSlots[currentSlot],ItemManager.glassItems[3]);
                return;
        }
    }


    public static void giveSteeringInventory(LobbyPlayer lobbyplayer){
        lobbyplayer.getTask(11).resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 54, "Ajuster le guidon");
        int[] possibleCursorSlots={0,1,2,3,4,5,6,7,8,9,10,11,12,14,15,16,17,18,19,20,24,25,26,27,28,34,35,36,37,38,42,43,44,45,46,47,48,50,51,52,53};
        int cursorSlot=possibleCursorSlots[ThreadLocalRandom.current().nextInt(0, possibleCursorSlots.length)];
        giveSteeringUpdateInventory(inventory,cursorSlot);

        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }

    public static void giveSteeringUpdateInventory(Inventory inventory,int cursorSlot){

        for (int i=0;i<54;i++){
            inventory.setItem(i,ItemManager.steeringItems[4]);
        }

        giveSteeringCrosshairs(inventory,cursorSlot,ItemManager.steeringItems[3]);
    }

    public static void giveSteeringCrosshairs(Inventory inventory, int cursorSlot, ItemStack item){
        int minimumXslot=cursorSlot-cursorSlot%9;
        int maximumXslot=minimumXslot+9;
        int minimumYslot=cursorSlot%9;

        for (int i=minimumXslot;i<maximumXslot;i++){
            inventory.setItem(i,item);
        }

        for (int i=minimumYslot;i<54;i+=9){
            inventory.setItem(i,item);
        }
        int[] snowballSlots={22,32,40,30};
        for (int i:snowballSlots){
            inventory.setItem(i,ItemManager.steeringItems[2]);
        }
        inventory.setItem(31,ItemManager.steeringItems[1]);
        inventory.setItem(cursorSlot,ItemManager.steeringItems[0]);
    }


    public static void giveEngineInventory(LobbyPlayer lobbyplayer){
        lobbyplayer.getTask(1).resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 45, "Enligner le moteur");

        int objSlot = ThreadLocalRandom.current().nextInt(0, 5);

        int cursorSlot=0;
        for (int i=0;i<100;i++){
            cursorSlot=ThreadLocalRandom.current().nextInt(0, 5);
            if (objSlot<=cursorSlot-2 || objSlot>=cursorSlot+2){
                break;
            }
        }

        giveEngineCursorAndObj(inventory,cursorSlot,objSlot);

        lobbyplayer.getTask(1).setMetaData(cursorSlot+"-"+objSlot+"f");
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }


    public static void giveEngineCursorAndObj(Inventory inventory, int cursorSlot, int objSlot){
        for (int i=0;i<45;i++){
            inventory.setItem(i,ItemManager.glassItems[7]);
        }
        int slot=36;
        while (slot<44){
            inventory.setItem(slot,ItemManager.glassItems[3]);
            slot++;
        }
        slot--;
        while (slot>15){
            slot-=9;
            inventory.setItem(slot,ItemManager.glassItems[3]);
        }

        int[] possibleObjectiveSlots = {27,0,3,6,33};
        inventory.setItem(possibleObjectiveSlots[objSlot],ItemManager.engineItems[1]);

        int[] possibleCursorSlots={8,17,26,35,44};
        for (int i=0;i<5;i++){
            if (i==cursorSlot){
                inventory.setItem(possibleCursorSlots[i],ItemManager.engineItems[0]);
            }else{
                inventory.setItem(possibleCursorSlots[i],ItemManager.engineItems[3]);
            }
        }

        giveEngineAlignmentLine(inventory, cursorSlot, ItemManager.engineItems[2]);
    }

    public static void giveEngineAlignmentLine(Inventory inventory, int cursorSlot, ItemStack item){
        int[] alignmentSlots=new int[5];
        switch (cursorSlot){
            case 0:
                alignmentSlots = new int[]{30,29,28,27};
                break;
            case 1:
                alignmentSlots = new int[]{30,20,10,0};
                break;
            case 2:
                alignmentSlots = new int[]{30,21,12,3};
                break;
            case 3:
                alignmentSlots = new int[]{30,22,14,6};
                break;
            case 4:
                alignmentSlots = new int[]{30,31,32,33};
                break;
        }
        for (int i:alignmentSlots){
            inventory.setItem(i,item);
        }
    }


    public static void giveChuteInventory(LobbyPlayer lobbyplayer){
        Task task= lobbyplayer.getTask(6);
        task.resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 54, "Vider la chute");

        int[] blackGlassSlots={0,9,18,27,36,45,46,47,48,49,50,51,52,43,34,25,16,7};
        int[] whiteGlassSlots={1,2,3,4,5,6};

        for (int i:blackGlassSlots){
            inventory.setItem(i,ItemManager.glassItems[3]);
        }
        for (int i:whiteGlassSlots){
            inventory.setItem(i,ItemManager.glassItems[8]);
        }
        generateRandomTrash(inventory);

        inventory.setItem(26,ItemManager.chuteItems[0]);
        inventory.setItem(35,ItemManager.chuteItems[1]);

        task.setMetaData("f");
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }

    private static void generateRandomTrash(Inventory inventory){
        int[] trashSlots={19,20,21,22,23,24,28,29,30,31,32,33,37,38,39,40,41,42};
        int randomNum;
        for (int i:trashSlots) {
            randomNum = ThreadLocalRandom.current().nextInt(0, 5);
            inventory.setItem(i,ItemManager.chuteTrashItems[randomNum]);
        }
        List<Integer> lastRow = new ArrayList<>(Arrays.asList(10,11,12,13,14,15));
        int randomSlot;
        for (int i=0;i<3;i++){
            randomSlot = (ThreadLocalRandom.current().nextInt(0, lastRow.size()));
            randomNum = ThreadLocalRandom.current().nextInt(0, 5);
            inventory.setItem(lastRow.get(randomSlot),ItemManager.chuteTrashItems[randomNum]);
            lastRow.remove(randomSlot);
        }
    }

    public static void updateChuteInventory(Inventory inventory){
        int[] trashSlots={42,41,40,39,38,37,33,32,31,30,29,28,24,23,22,21,20,19,15,14,13,12,11,10};
        int nextSlot;
        for (int i:trashSlots){
            nextSlot=i+9;
            if (nextSlot<54){
                inventory.setItem(nextSlot,inventory.getItem(i));
            }
            inventory.setItem(i,null);
        }
    }


    public static void giveSampleInventory(LobbyPlayer lobbyplayer){
        Task task= lobbyplayer.getTask(9);
        Inventory inventory = Bukkit.createInventory(null, 54, "Inspecter l'échantillon");

        task.setMetaData("99_9_0");

        drawSampleBackground(inventory);
        int[] potionSlots={20,21,22,23,24};
        for (int i:potionSlots){
            inventory.setItem(i,ItemManager.samplePotionItems[2]);
        }
        inventory.setItem(4,ItemManager.sampleItems[3]);
        inventory.setItem(10,ItemManager.sampleItems[4]);
        inventory.setItem(49,ItemManager.sampleItems[0]);

        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }


    public static void giveSampleSecondInventory(LobbyPlayer lobbyplayer){
        Task task= lobbyplayer.getTask(9);
        Inventory inventory = Bukkit.createInventory(null, 54, "Inspecter l'échantillon");
        int chosenSlot=Integer.parseInt(task.getMetaData().substring(3,4));
        drawSampleBackground(inventory);
        drawSecondStageItems(inventory,chosenSlot);
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }


    public static void giveSampleCountdownInventory(LobbyPlayer lobbyplayer, String meta){
        Inventory inventory = Bukkit.createInventory(null, 54, "Inspecter l'échantillon");
        inventory.setItem(4,ItemManager.sampleItems[3]);
        int time=Integer.parseInt(meta.substring(0,2));
        drawSampleBackground(inventory);

        inventory.setItem(16,ItemManager.sampleItems[4]);
        inventory.setItem(49,ItemManager.sampleItems[8]);
        inventory.getItem(4).setAmount(time);
        int[] potionSlots={20,21,22,23,24};
        for (int i:potionSlots){
            inventory.setItem(i,ItemManager.samplePotionItems[0]);
        }

        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }

    public static void drawSampleBackground(Inventory inventory){
        int[] blackGlassSlots = {0,1,2,3,5,6,7,8,9,17,18,26,27,35,36,44,45,46,47,48,50,51,52,53};
        int[] whiteGlassSlots = {19,25,28,29,30,31,32,33,34,37,43};
        for (int i:blackGlassSlots){
            inventory.setItem(i,ItemManager.glassItems[3]);
        }
        for (int i:whiteGlassSlots){
            inventory.setItem(i,ItemManager.glassItems[6]);
        }
    }

    public static void drawSecondStageItems(Inventory inventory, int chosenSlot){
        int[] potionSlots={20,21,22,23,24};
        for (int i=0;i<5;i++){
            if (i==chosenSlot){
                inventory.setItem(potionSlots[i],ItemManager.samplePotionItems[1]);
            }else{
                inventory.setItem(potionSlots[i],ItemManager.samplePotionItems[0]);
            }
        }
        int[] buttonSlots={38,39,40,41,42};
        for (int i=0;i<5;i++){
            inventory.setItem(buttonSlots[i],ItemManager.sampleButtonItems[i]);
        }
        inventory.setItem(4,ItemManager.sampleItems[3]);
        inventory.setItem(16,ItemManager.sampleItems[4]);
        inventory.setItem(49,ItemManager.sampleItems[1]);
    }

    public static void giveSampleFlicker(Inventory inventory, int time, boolean success){
        int[] buttonSlots={38,39,40,41,42};
        ItemStack item;
        if (time%2==0){
            if (success){
                item=ItemManager.sampleItems[5];
            }else{
                item=ItemManager.sampleItems[6];
            }
        }else {
            if (success){
                item=ItemManager.sampleItems[7];
            }else{
                item=ItemManager.sampleItems[5];
            }
        }
        for (int i:buttonSlots){
            inventory.setItem(i,item);
        }
    }


    public static void giveManifoldInventory(LobbyPlayer lobbyplayer){
        Task task= lobbyplayer.getTask(16);
        task.resetCurrentSub();
        Inventory inventory = Bukkit.createInventory(null, 18, "Débloquer le collecteur");
        int[] blackSlots = {0,8,9,17};
        int[] glassSlots = {1,7,10,16};
        for (int i:blackSlots){
            inventory.setItem(i,ItemManager.glassItems[3]);
        }
        for (int i:glassSlots){
            inventory.setItem(i,ItemManager.glassItems[8]);
        }
        List<Integer> squareList = new ArrayList<>(Arrays.asList(2,3,4,5,6,11,12,13,14,15));
        int randomNum;
        int slot;
        for (int i=0;i<10;i++){
            randomNum = ThreadLocalRandom.current().nextInt(0, squareList.size());
            slot = squareList.get(randomNum);
            inventory.setItem(slot,ItemManager.manifoldsItems[0]);
            ItemStack item=inventory.getItem(slot);
            item.setAmount(i+1);
            ItemMeta meta= item.getItemMeta();
            meta.setDisplayName(meta.getDisplayName()+(i+1));
            item.setItemMeta(meta);
            squareList.remove(randomNum);

        }
        task.setMetaData("0");
        lobbyplayer.setTaskInventory(inventory);
        lobbyplayer.getPlayerPlayer().openInventory(lobbyplayer.getTaskInventory());
    }


    public static void giveBedWarsInventory(Player player){
        Inventory inventory = Bukkit.createInventory(null, 27, "Arènes BedWars");
        for (int i=0;i<inventory.getSize();i++){
            inventory.setItem(i,ItemManager.bedWarsItems[4]);
        }
        inventory.setItem(10,ItemManager.bedWarsItems[0]);
        inventory.setItem(11,ItemManager.bedWarsItems[1]);
        inventory.setItem(12,ItemManager.bedWarsItems[2]);
        inventory.setItem(13,ItemManager.bedWarsItems[3]);

        player.openInventory(inventory);
    }

    public static void giveGameBarInventory(Player player){
        Inventory inventory = Bukkit.createInventory(null, 9, "Choisir une commande");
        inventory.setItem(0,ItemManager.gameBarItems[5]);

        inventory.setItem(2,ItemManager.gameBarItems[0]);
        inventory.setItem(3,ItemManager.gameBarItems[1]);
        inventory.setItem(4,ItemManager.gameBarItems[2]);
        inventory.setItem(5,ItemManager.gameBarItems[3]);

        inventory.setItem(7,ItemManager.gameBarItems[6]);
        inventory.setItem(8,ItemManager.gameBarItems[4]);

        player.openInventory(inventory);
    }

    public static void giveDuelInventory(Player player){
        Inventory inventory = Bukkit.createInventory(null, 54, "Choisir un joueur à défier");
        int cpt=0;
        for (Player people:Bukkit.getOnlinePlayers()){
            if (cpt==54){
                player.sendMessage(ChatColor.RED+"Le nombre de joueurs connectés dépasse la capacité du menu Duel.\nVeuillez avertir un administrateur.");
                break;
            }
            if (!player.getName().equals(people.getName())){
                String worldName=people.getWorld().getName();
                Boolean busy;
                if (!worldName.startsWith(AmongUs.NOMMONDESURVIE)&&!worldName.equals(AmongUs.NOMMONDESPAWN)){
                    busy=true;
                }else{
                    busy=false;
                }
                inventory.setItem(cpt,ItemManager.createDuelNameItem(people,busy));
                cpt++;
            }
        }
        for (int i=cpt;i<inventory.getSize();i++){
            inventory.setItem(i,ItemManager.duelItem);
        }
        player.openInventory(inventory);
    }
}