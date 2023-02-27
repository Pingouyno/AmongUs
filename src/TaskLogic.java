import com.sun.org.apache.xpath.internal.operations.Bool;
import org.bukkit.*;
import org.bukkit.block.Beacon;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;


public class TaskLogic extends AmongUs {

    public static void checkResetTask(InventoryClickEvent event){
        LobbyPlayer lobbyplayer=getLobbyPlayer(event.getWhoClicked());
        if (!lobbyplayer.getLobby().started){
            return;
        }
        for (Task task:lobbyplayer.getTasks()){
            switch (task.getID()){
                case 2:
                    if (task.getMetaData()!=null){
                        task.setMetaData(Integer.parseInt(task.getMetaData().substring(0,1))+"_1");
                    }
                    continue;
            }
        }
    }

    //Script si l'objet cliqué dans inventaire a localizedname à "task"

    public static void checkTaskObject(InventoryClickEvent event, ItemMeta meta, LobbyPlayer lobbyplayer){
        int s=Integer.parseInt(meta.getLocalizedName().substring(5,7));
        switch (s){
            case 0:
                checkDistributor(event,lobbyplayer);
                return;
            case 1:
                checkEngine(event,lobbyplayer);
                return;
            case 2:
                checkTrajectory(event,meta,lobbyplayer);
                return;
            case 3:
                checkFilter(event,meta,lobbyplayer);
                return;
            case 4:
                checkUpdateAsteroid(lobbyplayer,event);
                return;
            case 5:
                checkDivert(event, meta, lobbyplayer);
                return;
            case 6:
                checkChute(meta, lobbyplayer);
                return;
            case 7:
                checkWireOnDrag(event, lobbyplayer);
                return;
            case 8:
                checkFuel(event, meta, lobbyplayer);
                return;
            case 9:
                checkSample(event,meta,lobbyplayer);
                return;
            case 10:
                checkShield(event,meta,lobbyplayer);
                return;
            case 11:
                checkSteering(event,meta,lobbyplayer);
                return;
            case 12:
                checkReactor(event,lobbyplayer);
                return;
            case 13:
                checkCardOnClick(event);
                return;
            case 14:
                checkScan(lobbyplayer);
                return;
            case 15:
                checkUpload(lobbyplayer);
                return;
            case 16:
                checkManifolds(event, lobbyplayer);
                return;
        }
    }

    public static void checkWireOnDrag(InventoryClickEvent event, LobbyPlayer lobbyplayer){
        event.setCancelled(true);
        if (event.getClickedInventory().getSize()==54){
            lobbyplayer.getPlayerPlayer().setItemOnCursor(event.getCurrentItem());
            lobbyplayer.getPlayerPlayer().getItemOnCursor().setAmount(9);
            lobbyplayer.getPlayerPlayer().updateInventory();
        }
    }

    public static void checkWireOnDrop(InventoryDragEvent event, String meta, LobbyPlayer lobbyplayer){
        event.setCancelled(true);
        Inventory inventory=lobbyplayer.getTaskInventory();
        if (event.getInventory().getSize()==54&&inventory.equals(event.getInventory())){
            int match=0;
            Task task=lobbyplayer.getTask(7);
            String blockNum=String.valueOf(Integer.parseInt(meta.substring(8,9)));
            Set<Integer> eventSlots=event.getInventorySlots();

            List<Integer> leftrow = new ArrayList<>(Arrays.asList(11,20,29,38));
            for (Integer slot:leftrow){
                if (eventSlots.contains(slot)){
                    ItemStack nearbySlot=inventory.getItem(slot-1);
                    if (nearbySlot!=null){
                        if (!nearbySlot.getItemMeta().getLocalizedName().endsWith(blockNum)){
                            resetWire(lobbyplayer);
                            return;
                        }else{
                            match++;
                        }
                    }
                }
            }

            List<Integer> rightrow = new ArrayList<>(Arrays.asList(15,24,33,42));
            for (Integer slot:rightrow){
                if (eventSlots.contains(slot)){
                    ItemStack nearbySlot=inventory.getItem(slot+1);
                    if (nearbySlot!=null){
                        if (!nearbySlot.getItemMeta().getLocalizedName().endsWith(blockNum)){
                            resetWire(lobbyplayer);
                            return;
                        }else{
                            match++;
                        }
                    }
                }
            }
            if (match==2){
                for (int i=0;i<2;i++){
                    int blockID=Integer.valueOf(blockNum);
                    inventory.remove(ItemManager.colorWires[blockID]);
                }
                resetWire(lobbyplayer);
                checkWireFixed(lobbyplayer,task);
            }
        }
    }

    public static void resetWire(LobbyPlayer lobbyplayer){
        new BukkitRunnable() {
            Player player=lobbyplayer.getPlayerPlayer();
            public void run() {
                if (player!=null){
                    player.setItemOnCursor(null);
                    player.updateInventory();
                }
                cancel();
                return;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 0L);
    }


    public static void checkWireFixed(LobbyPlayer lobbyplayer,Task task){
        for (ItemStack item:lobbyplayer.getTaskInventory().getContents()){
            if (item!=null&&item.getItemMeta().getLocalizedName().startsWith("t")){
                return;
            }
        }
        finishWireTask(lobbyplayer, task);
    }

    private static void finishWireTask(LobbyPlayer lobbyplayer, Task task){
        task.finishCurrentSub();
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Câbles réparés.");
    }


    public static void checkScan(LobbyPlayer lobbyplayer){
        Task task= lobbyplayer.getTask(14);
        if (task.getMetaData()==null){
            if (!lobbyplayer.isImpostor){
                if (lobbyplayer.isAlive){
                    lobbyplayer.getPlayerPlayer().addPotionEffect(new PotionEffect(PotionEffectType.GLOWING, 1000000, 1, false, true));
                }
            }
            int count=20;
            task.setMetaData(String.valueOf(count));
            new BukkitRunnable() {
                int time = count;
                public void run() {
                    if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked ){
                        if (task.getMetaData()!=null){
                            if (Integer.parseInt(task.getMetaData())==time){
                                if (time == 0) {
                                    finishScanTask(lobbyplayer,task);
                                    cancel();
                                    return;
                                }
                                InventoryManager.giveScanLoaderInventory(lobbyplayer,time);
                            }else{endScan(lobbyplayer,task);cancel();return;}
                        }else{endScan(lobbyplayer,task);cancel();return;}
                    } else {cancel();return;}
                    task.setMetaData(String.valueOf(Integer.parseInt(task.getMetaData())-1));
                    time--;
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 10L);
        }
    }

    private static void endScan(LobbyPlayer lobbyplayer,Task task){
        if (lobbyplayer.isAlive){
            lobbyplayer.getPlayerPlayer().removePotionEffect(PotionEffectType.GLOWING);
        }
        task.setMetaData(null);
    }

    private static void finishScanTask(LobbyPlayer lobbyplayer,Task task){
        endScan(lobbyplayer,task);
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Scan terminé.");
        task.finishCurrentSub();
    }

    public static void checkUpload(LobbyPlayer lobbyplayer){
        Task task=lobbyplayer.getTask(15);
        if (task.getMetaData()==null){
            int count=17;
            task.setMetaData(String.valueOf(count));
            new BukkitRunnable() {
                int time = count;
                public void run() {
                    if (isInLobbyList(lobbyplayer.name)&&!lobbyplayer.getLobby().isLocked ){
                        if (task.getMetaData()!=null){
                            if (Integer.parseInt(task.getMetaData())==time){
                                if (time == 0) {
                                    finishUploadTask(lobbyplayer,task);
                                    cancel();
                                    return;
                                }
                                InventoryManager.giveUploadLoaderInventory(lobbyplayer,time);
                            }else{task.setMetaData(null);cancel();return;}
                        }else{task.setMetaData(null);cancel();return;}
                    } else {cancel();return;}
                    task.setMetaData(String.valueOf(Integer.parseInt(task.getMetaData())-1));
                    time--;
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 10L);
        }
    }

    private static void finishUploadTask(LobbyPlayer lobbyplayer,Task task){
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Transfert terminé.");
        task.finishCurrentSub();
    }


    private static void checkTrajectory(InventoryClickEvent event, ItemMeta meta, LobbyPlayer lobbyplayer){
        int blockNum=Integer.parseInt(meta.getLocalizedName().substring(8,9));
        Task task=lobbyplayer.getTask(2);
        String info=task.getMetaData();
        Inventory inventory= lobbyplayer.getTaskInventory();
        if(info==null){
            if (blockNum==5){
                task.setMetaData("0_0");
            }
        }else{
            int lastClicked=Integer.parseInt(info.substring(0,1));
            int taskLocked=Integer.parseInt(info.substring(2,3));
            if (blockNum==5){
                task.setMetaData(lastClicked+"_0");
            }else{
                if (taskLocked==0){
                    if (lastClicked+1==blockNum){
                        inventory.setItem(inventory.first(ItemManager.trajectorycursor), ItemManager.trajectoryItems[blockNum-1]);
                        inventory.setItem(event.getSlot(), ItemManager.trajectorycursor);
                        task.setMetaData(blockNum+"_0");
                        if (blockNum==4){
                            finishTrajectoryTask(lobbyplayer,task);
                        }
                    }else{
                        task.setMetaData(lastClicked+"_1");
                    }
                }
            }
        }
    }
    private static void finishTrajectoryTask(LobbyPlayer lobbyplayer,Task task){
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Trajectoire tracée.");
        task.finishCurrentSub();
    }

    private static void checkFuel(InventoryClickEvent event, ItemMeta meta, LobbyPlayer lobbyplayer){
        Task task=lobbyplayer.getTask(8);
        String info=task.getMetaData();
        int progress=Integer.valueOf(info);
        Inventory inventory= lobbyplayer.getTaskInventory();
        int randomNum = ThreadLocalRandom.current().nextInt(0, 7);
        if (randomNum<4){
            task.setMetaData(String.valueOf(progress+1));
        }else{
            return;
        }
        for (int i=0;i<9;i++){
            if (i<progress){
                inventory.setItem(i,ItemManager.fuelItems[1]);
            }else if (i==progress){
                inventory.setItem(i,ItemManager.fuelItems[2]);
            }else{
                inventory.setItem(i,ItemManager.fuelItems[0]);
            }
        }
        if (task.getMetaData().equals("10")){
            finishFuelTask(lobbyplayer,task);
        }
    }

    private static void finishFuelTask(LobbyPlayer lobbyplayer,Task task){
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Réservoir rempli.");
        task.finishCurrentSub();
    }

    private static void checkDivert(InventoryClickEvent event, ItemMeta meta, LobbyPlayer lobbyplayer){
        Task task=lobbyplayer.getTask(5);
        Inventory inventory= lobbyplayer.getTaskInventory();
        inventory.setItem(13,ItemManager.divertItems[0]);
        task.setMetaData("1");
        new BukkitRunnable() {
            int time = 1;
            public void run() {
                if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked ){
                    if (task.getMetaData()!=null){
                        if (time==0){
                            finishDivertTask(lobbyplayer,task);
                            cancel();
                            return;
                        }
                    }else{cancel();return;}
                } else {cancel();return;}
                time--;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 20L);
    }

    private static void finishDivertTask(LobbyPlayer lobbyplayer, Task task){
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Courant dérouté.");
        task.finishCurrentSub();
    }


    public static void checkUpdateAsteroid(LobbyPlayer lobbyplayer, InventoryClickEvent event){
        lobbyplayer.getTaskInventory().setItem(event.getSlot(),ItemManager.asteroidItems[0]);
        Task task=lobbyplayer.getTask(4);
        task.progress++;
        if (task.getProgress()-1==task.getTotalToDo()) {
            finishAsteroidTask(lobbyplayer,task);
        }else{
            if (!lobbyplayer.isImpostor){
                fireAsteroid(lobbyplayer);
            }
            SB.redrawScoreBoard(lobbyplayer);
            updateBossBar(lobbyplayer.getLobby());
        }
    }


    public static void fireAsteroid(LobbyPlayer lobbyplayer){
        if (lobbyplayer.isAlive){
            PlaySound.playAsteroidSound(lobbyplayer.getPlayerPlayer());
        }
        World world=lobbyplayer.getLobby().getWorld();
        int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
        if (INDICE_DE_MONDE==4){
            if (randomNum == 0) {
                world.getBlockAt(85,121,-67).setType(Material.SOUL_TORCH);
            } else {
                world.getBlockAt(85,121,-69).setType(Material.SOUL_TORCH);
            }
        }else{
            if (randomNum == 0) {
                world.getBlockAt(61,66,-59).setType(Material.SOUL_TORCH);
            } else {
                world.getBlockAt(61,66,-61).setType(Material.SOUL_TORCH);
            }
        }
        new BukkitRunnable() {
            int index = randomNum;
            int time=1;
            public void run() {
                if (time==0){
                    resetVisibleAsteroid(world,index);
                    cancel();return;
                }
                time--;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 9L);
    }


    public static void checkAsteroid(LobbyPlayer lobbyplayer){
        Task task= lobbyplayer.getTask(4);
        if (task.getMetaData()==null){
            int count=0;
            task.setMetaData(String.valueOf(count));
            new BukkitRunnable() {
                int time = count;
                public void run() {
                    if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked){
                        if (task.getMetaData()!=null){
                            if (Integer.parseInt(task.getMetaData())==time){
                                InventoryManager.giveAsteroidUpdateInventory(lobbyplayer);
                            }else{endAsteroid(lobbyplayer,task);cancel();return;}
                        }else{endAsteroid(lobbyplayer,task);cancel();return;}
                    } else {cancel();return;}
                    task.setMetaData(String.valueOf(Integer.parseInt(task.getMetaData())+1));
                    time++;
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 4L);
        }
    }
    public static void endAsteroid(LobbyPlayer lobbyplayer,Task task){
        task.setMetaData(null);
        lobbyplayer.getPlayerPlayer().closeInventory();
    }


    private static void finishAsteroidTask(LobbyPlayer lobbyplayer, Task task){
        task.progress--;
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Astéroïdes détruits.");
        endAsteroid(lobbyplayer,task);
        task.finishCurrentSub();
    }


    public static void startDistributorLoop(LobbyPlayer lobbyplayer, int stage){
        Task task= lobbyplayer.getTask(0);
        Inventory inventory=lobbyplayer.getTaskInventory();
        int speed=6;
        int type=1;
        switch (stage){
            case 2:
                speed=4;
                type=3;
                break;
            case 3:
                speed=3;
                type=5;
                break;
        }
        ItemStack circleMaterial=ItemManager.distributorItems[type-1];
        int[] slotlist={18,27,36,37,38,39,40,31,22,13,4,3,2,1,0,9};
        for (int i:slotlist){
            inventory.setItem(i,circleMaterial);
        }
        inventory.setItem(21,ItemManager.distributorItems[type]);
        inventory.setItem(18, ItemManager.distributorItems[type]);
        if (task.getMetaData()!=null){
            String initialMeta = task.getMetaData();
            int count=0;
            int itemtype=type;
            int level=stage;
            new BukkitRunnable() {
                int time = count;
                public void run() {
                    if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked){
                        if (task.getMetaData()!=null){
                            if (task.getMetaData().equals(initialMeta)){
                                InventoryManager.giveDistributorUpdateInventory(lobbyplayer,itemtype,slotlist,time);
                            }else{cancel();return;}
                        }else{task.resetCurrentSub();cancel();return;}
                    } else {cancel();return;}
                    time++;
                    if (time==16){
                        time=0;
                    }
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, speed);
        }
    }

    public static void checkDistributor(InventoryClickEvent event, LobbyPlayer lobbyplayer){
        Task task=lobbyplayer.getTask(0);
        int slot=event.getSlot();
        Inventory inventory=event.getClickedInventory();
        ItemStack referenceBlock=inventory.getItem(slot-2);
        if (inventory.getItem(slot-1).equals(referenceBlock) || inventory.getItem(slot-10).equals(referenceBlock)){
            int newStage = 1+Integer.valueOf(task.getMetaData().substring(0,1));
            task.setMetaData(newStage+"_00");
            switch (newStage){
                case 2:
                    inventory.setItem(7,ItemManager.distributorItems[1]);
                    inventory.setItem(8,ItemManager.distributorItems[1]);
                    break;
                case 3:
                    inventory.setItem(25,ItemManager.distributorItems[3]);
                    inventory.setItem(26,ItemManager.distributorItems[3]);
                    break;
                case 4:
                    inventory.setItem(43,ItemManager.distributorItems[5]);
                    inventory.setItem(44,ItemManager.distributorItems[5]);
                    finishDistributorTask(lobbyplayer,task);
                    return;
            }
            startDistributorLoop(lobbyplayer,newStage);
        }else{
            int newMissedValue=Integer.parseInt(task.getMetaData().substring(2,4))+1;
            if (newMissedValue>99){
                newMissedValue=0;
            }
            String newMissedString=String.valueOf(newMissedValue);
            if (newMissedString.length()==1){
                newMissedString="0"+newMissedString;
            }
            task.setMetaData("1_"+newMissedString);
            InventoryManager.giveDistributorInventory(lobbyplayer,false);
        }
    }

    private static void finishDistributorTask(LobbyPlayer lobbyplayer,Task task){
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Distributeur calibré.");
        task.finishCurrentSub();
    }


    public static void checkShield(InventoryClickEvent event, ItemMeta meta,LobbyPlayer lobbyplayer){
        Inventory inventory = lobbyplayer.getTaskInventory();
        int type=Integer.parseInt(meta.getLocalizedName().substring(8,9));
        int clickedSlot=event.getSlot();
        if (type==0){
            inventory.setItem(clickedSlot,ItemManager.shieldItems[1]);
            checkShieldFixed(lobbyplayer,inventory);
        }else{
            inventory.setItem(clickedSlot,ItemManager.shieldItems[0]);
        }
    }

    private static void checkShieldFixed(LobbyPlayer lobbyplayer, Inventory inventory){
        int[] slotList = {12,13,14,21,22,23,30,31,32};
        for (int i:slotList){
            if (inventory.getItem(i).getItemMeta().getLocalizedName().endsWith("0")){
                return;
            }
        }
        finishShieldTask(lobbyplayer);
    }

    private static void finishShieldTask(LobbyPlayer lobbyplayer){
        Task task= lobbyplayer.getTask(10);
        if (!lobbyplayer.isImpostor) {
            startVisibleShield(lobbyplayer.getLobby().getWorld());
            PlaySound.playShieldSound(lobbyplayer.getPlayerPlayer());
        }
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Boucliers activés");
        task.finishCurrentSub();
    }


    public static void startVisibleShield(World world){
        if (INDICE_DE_MONDE==4){
            world.getBlockAt(94,109,-38).setType(Material.LIGHT_GRAY_STAINED_GLASS);
            world.getBlockAt(93,109,-37).setType(Material.LIGHT_GRAY_STAINED_GLASS);
            world.getBlockAt(92,109,-36).setType(Material.LIGHT_GRAY_STAINED_GLASS);
        }
    }


    public static void checkCardOnClick(InventoryClickEvent event){
        event.getCurrentItem().setAmount(5);
        event.setCancelled(false);
    }

    public static void checkCardOnDrop(InventoryDragEvent event, String meta, LobbyPlayer lobbyplayer) {
        Inventory inventory= lobbyplayer.getTaskInventory();
        Set<Integer> eventSlots=event.getInventorySlots();
        if (event.getInventory().getSize()==45 && eventSlots.size()==5){
            Integer[] cardSlots = {10,11,12,13,14};
            int count=0;
            for (Integer i:eventSlots){
                if (i.intValue()!=cardSlots[count]){
                    resetCardCursor(lobbyplayer,inventory,event);
                    return;
                }
                count++;
            }
        endCard(lobbyplayer);
        }else{resetCardCursor(lobbyplayer,inventory,event);}
    }

    private static void resetCardCursor(LobbyPlayer lobbyplayer, Inventory inventory, InventoryDragEvent event){
        event.setCancelled(true);
        new BukkitRunnable() {
            String name=lobbyplayer.getServerPlayer.getName();
            Inventory inventory=lobbyplayer.getTaskInventory();
            int time=3;
            public void run() {
                if (isInLobbyList(name)&&inventory.equals(lobbyplayer.getTaskInventory())){
                    if (time%2==0){
                        inventory.setItem(17, ItemManager.distributorItems[4]);
                        if (time==0){
                            inventory.setItem(40,ItemManager.cardItems[0]);
                            cancel();return;
                        }
                    }else{
                        if (time==3){
                            lobbyplayer.getPlayerPlayer().setItemOnCursor(null);
                            lobbyplayer.getPlayerPlayer().updateInventory();
                        }
                        inventory.setItem(17,ItemManager.distributorItems[5]);
                    }
                }else{cancel();return; }
                time--;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 5L);
    }

    private static void endCard(LobbyPlayer lobbyplayer){
        Task task=lobbyplayer.getTask(13);
        task.setMetaData("lock");
        int[] emptySlots={10,11,12,13,14};

        new BukkitRunnable() {
            int time = 3;
            Inventory inventory=lobbyplayer.getTaskInventory();
            public void run() {
                if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked){
                    if (task.getMetaData()!=null){
                        switch (time){
                            case 0:
                                cardToggleLight(false,inventory);
                                finishCardTask(lobbyplayer, task);
                                cancel();
                                return;
                            case 1:
                                cardToggleLight(true,inventory);
                                break;
                            case 2:
                                cardToggleLight(false,inventory);
                                break;
                            case 3:
                                for (int i:emptySlots){
                                    inventory.setItem(i,null);
                                }
                                cardToggleLight(true,inventory);
                                break;
                        }
                    }else{cancel();return;}
                } else {cancel();return;}
                time--;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 5L);
    }

    private static void cardToggleLight(boolean on, Inventory inventory){
        if (on){
            inventory.setItem(16, ItemManager.distributorItems[3]);
        }else{
            inventory.setItem(16, ItemManager.distributorItems[2]);
        }
    }

    private static void finishCardTask(LobbyPlayer lobbyplayer, Task task){
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Carte acceptée.");
        task.finishCurrentSub();
    }


    private static void checkFilter(InventoryClickEvent event, ItemMeta meta,LobbyPlayer lobbyplayer){
        Inventory inventory = lobbyplayer.getTaskInventory();
        int blockID=Integer.parseInt(meta.getLocalizedName().substring(8,9));
        if (blockID==1){
            checkFilterOnDrop(inventory,lobbyplayer);
        }else{
            checkFilterOnDrag(event,inventory,lobbyplayer, blockID);
        }
    }

    private static void checkFilterOnDrag(InventoryClickEvent event, Inventory inventory,LobbyPlayer lobbyplayer, int id){
        Player player=lobbyplayer.getPlayerPlayer();
        if (id==0&&event.getCursor().getType().equals(Material.AIR)){
            inventory.setItem(event.getSlot(),ItemManager.filterItems[2]);
            lobbyplayer.getPlayerPlayer().setItemOnCursor(ItemManager.filterItems[0]);
        }else if (id==2 && event.getCursor().equals(ItemManager.filterItems[0])){
            lobbyplayer.getPlayerPlayer().setItemOnCursor(null);
            inventory.setItem(event.getSlot(),ItemManager.filterItems[0]);
        }
    }

    private static void checkFilterOnDrop(Inventory inventory,LobbyPlayer lobbyplayer){
        lobbyplayer.getPlayerPlayer().setItemOnCursor(null);
        checkFilterEnd(inventory,lobbyplayer);
    }

    private static void checkFilterEnd(Inventory inventory, LobbyPlayer lobbyplayer){
        if (!inventory.contains(ItemManager.filterItems[0])){
            finishFilterTask(lobbyplayer);
        }
    }

    private static void finishFilterTask(LobbyPlayer lobbyplayer){
        Task task=lobbyplayer.getTask(3);
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Filtre nettoyé");
        task.finishCurrentSub();
    }

    private static void checkReactor(InventoryClickEvent event,LobbyPlayer lobbyplayer){
        Task task = lobbyplayer.getTask(12);
        String meta=task.getMetaData();
        int[] rightSquareSlots={23,24,25,32,33,34,41,42,43};
        if (meta!=null && meta.substring(5,6).equals("f")){
            int subLevelIndex=Integer.parseInt(meta.substring(8,9));
            int requiredSlot=rightSquareSlots[Integer.valueOf(meta.substring(subLevelIndex,subLevelIndex+1))];
            if (event.getSlot()==requiredSlot){
                event.getInventory().setItem(requiredSlot,ItemManager.reactorItems[1]);

                new BukkitRunnable() {
                    int timer=0;
                    int slot=requiredSlot;
                    public void run() {
                        if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked){
                            if (task.getMetaData()!=null){
                                if (timer==1){
                                    if (task.getMetaData().substring(5,6).equals("f")){
                                        lobbyplayer.getTaskInventory().setItem(slot,ItemManager.reactorItems[0]);
                                    }else{
                                        lobbyplayer.getTaskInventory().setItem(slot,ItemManager.glassItems[3]);
                                    }
                                    cancel();return;
                                }
                            }else{task.resetCurrentSub();cancel();return;}
                        } else {cancel();return;}
                        timer++;
                    }
                }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 2l);


                subLevelIndex++;
                task.setMetaData(meta.substring(0,8)+subLevelIndex);
                int stage=Integer.valueOf(meta.substring(6,7));
                if (stage==subLevelIndex){
                    stage++;
                    task.setMetaData(meta.substring(0,6)+stage+"_"+subLevelIndex);
                    if (subLevelIndex==5){
                        finishReactorTask(lobbyplayer, task);
                    }else{
                        startReactorAnimation(lobbyplayer,stage);
                    }
                }
            }else{
                startReactorAnimation(lobbyplayer,1);
            }
        }
    }


    public static void startReactorAnimation(LobbyPlayer lobbyplayer, int stage){
        Task task= lobbyplayer.getTask(12);
        Inventory inventory=lobbyplayer.getTaskInventory();
        int[] brownSlots={2,3,4,5,6};
        for (int i=0;i<5;i++){
            if (i<stage){
                inventory.setItem(brownSlots[i],ItemManager.glassItems[4]);
            }else{
                inventory.setItem(brownSlots[i],ItemManager.glassItems[5]);
            }
        }
        int[] rightSquareSlots={23,24,25,32,33,34,41,42,43};
        for (int i:rightSquareSlots){
            inventory.setItem(i,ItemManager.glassItems[3]);
        }
        task.setMetaData(task.getMetaData().substring(0,5)+"t"+stage+"_0");

        new BukkitRunnable() {
            int timer=0;
            String initialMeta = task.getMetaData();
            public void run() {
                if (isInLobbyList(lobbyplayer.name) &&!lobbyplayer.getLobby().isLocked){
                    if (task.getMetaData()!=null && task.getMetaData().equals(initialMeta)){
                        if (timer==1){
                            startReactorLoop(task,lobbyplayer,0,stage);
                            cancel();return;
                        }
                    }else{task.resetCurrentSub();cancel();return;}
                } else {cancel();return;}
                timer++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 22l);
    }

    public static void startReactorLoop(Task task, LobbyPlayer lobbyplayer,int count,int stage){
        Inventory inventory= lobbyplayer.getTaskInventory();
        if (task.getMetaData()!=null){
            String initialMeta = task.getMetaData();
            new BukkitRunnable() {
                int counter = count;
                int time=0;
                public void run() {
                    if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked){
                        if (task.getMetaData()!=null && task.getMetaData().equals(initialMeta)){
                            if (time==5){
                                counter++;
                                if (counter==stage){
                                    startReactorCopySession(task,lobbyplayer);
                                }else{
                                    startReactorLoop(task,lobbyplayer,counter,stage);
                                }
                                cancel();
                                return;
                            }else{
                                InventoryManager.giveReactorUpdateInventory(inventory,task,time,counter);
                            }
                        }else{task.resetCurrentSub();cancel();return;}
                    } else {cancel();return;}
                    time++;
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 2l);
        }
    }

    public static void startReactorCopySession(Task task, LobbyPlayer lobbyplayer){

        new BukkitRunnable() {
            int timer=0;
            String initialMeta = task.getMetaData();
            public void run() {
                if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked ){
                    if (task.getMetaData()!=null && task.getMetaData().equals(initialMeta)){
                        if (timer==1){
                            Inventory inventory=lobbyplayer.getTaskInventory();
                            String meta=task.getMetaData();
                            task.setMetaData(meta.substring(0,5)+"f"+meta.substring(6,9));

                            int[] rightSquareSlots={23,24,25,32,33,34,41,42,43};
                            for (int i:rightSquareSlots){
                                inventory.setItem(i,ItemManager.reactorItems[0]);
                            }
                            cancel();return;
                        }
                    }else{task.resetCurrentSub();cancel();return;}
                } else {cancel();return;}
                timer++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 5l);
    }

    private static void finishReactorTask(LobbyPlayer lobbyplayer, Task task){
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Réacteur démarré.");
        task.finishCurrentSub();
    }



    private static void checkSteering(InventoryClickEvent event, ItemMeta meta, LobbyPlayer lobbyplayer){
        Task task= lobbyplayer.getTask(11);
        if (task.getMetaData()==null){
            String blockType=meta.getLocalizedName().substring(8,9);
            if (blockType.equals("0")){
                Player player=lobbyplayer.getPlayerPlayer();
                if (player.getItemOnCursor().getType().equals(Material.AIR)){
                    player.setItemOnCursor(new ItemStack(Material.NETHER_STAR,1));
                }else{
                    lobbyplayer.getPlayerPlayer().setItemOnCursor(null);
                }
            }else{
                lobbyplayer.getPlayerPlayer().setItemOnCursor(null);
            }
            InventoryManager.giveSteeringUpdateInventory(lobbyplayer.getTaskInventory(),event.getSlot());
            if (event.getSlot()==31){

                task.setMetaData("t");

                new BukkitRunnable() {
                    int time = 0;
                    Inventory inventory=lobbyplayer.getTaskInventory();
                    public void run() {
                        if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked){
                            if (task.getMetaData()!=null&&task.getMetaData().equals("t")){
                                switch (time){
                                    case 0:
                                        InventoryManager.giveSteeringCrosshairs(inventory,31,ItemManager.glassItems[4]);
                                        break;
                                    case 1:
                                        InventoryManager.giveSteeringCrosshairs(inventory,31,ItemManager.glassItems[6]);
                                        break;
                                    case 2:
                                        InventoryManager.giveSteeringCrosshairs(inventory,31,ItemManager.glassItems[4]);
                                        break;
                                    case 3:
                                        InventoryManager.giveSteeringCrosshairs(inventory,31,ItemManager.glassItems[6]);
                                        finishSteeringTask(lobbyplayer,task);
                                        cancel();return;
                                }
                            }else{task.resetCurrentSub();cancel();return;}
                        } else {cancel();return;}
                        time++;
                    }
                }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 4L);
            }
        }
    }

    private static void finishSteeringTask(LobbyPlayer lobbyplayer, Task task){
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Guidon aligné.");
        task.finishCurrentSub();
    }


    private static void checkEngine(InventoryClickEvent event, LobbyPlayer lobbyplayer){
        Task task= lobbyplayer.getTask(1);
        String meta=task.getMetaData();
        Boolean taskIsLocked=(meta.substring(3,4).equals("t"));
        if (meta!=null&&!taskIsLocked){
            int cursorSlotID=Integer.parseInt(meta.substring(0,1));
            int objSlotID=Integer.parseInt(meta.substring(2,3));
            int[] possibleCursorSlots={8,17,26,35,44};

            int actualCursorSlot=possibleCursorSlots[cursorSlotID];
            if (event.getSlot()<actualCursorSlot){
                cursorSlotID--;
            }else if(event.getSlot()>actualCursorSlot){
                cursorSlotID++;
            }else{
                return;
            }
            if (cursorSlotID==objSlotID){
                task.setMetaData(cursorSlotID+"_"+objSlotID+"t");
                InventoryManager.giveEngineCursorAndObj(lobbyplayer.getTaskInventory(),cursorSlotID, objSlotID);
                new BukkitRunnable() {
                    int time = 0;
                    Inventory inventory=event.getClickedInventory();
                    int cursorSlot=Integer.valueOf(task.getMetaData().substring(0,1));
                    public void run() {
                        if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked){
                            if (task.getMetaData()!=null&&task.getMetaData().substring(3,4).equals("t") && inventory.equals(lobbyplayer.getTaskInventory())){
                                switch (time){
                                    case 0:
                                        InventoryManager.giveEngineAlignmentLine(inventory,cursorSlot,ItemManager.glassItems[4]);
                                        break;
                                    case 1:
                                        InventoryManager.giveEngineAlignmentLine(inventory,cursorSlot,ItemManager.glassItems[6]);
                                        break;
                                    case 2:
                                        InventoryManager.giveEngineAlignmentLine(inventory,cursorSlot,ItemManager.glassItems[4]);
                                        break;
                                    case 3:
                                        InventoryManager.giveEngineAlignmentLine(inventory,cursorSlot,ItemManager.glassItems[6]);
                                        break;
                                    case 4:
                                        finishEngineTask(lobbyplayer,task);
                                        break;
                                }
                            }else{task.resetCurrentSub();cancel();return;}
                        } else {cancel();return;}
                        time++;
                    }
                }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 4L);
            }else{
                task.setMetaData(cursorSlotID+"_"+objSlotID+"f");
                InventoryManager.giveEngineCursorAndObj(lobbyplayer.getTaskInventory(),cursorSlotID, objSlotID);
            }
        }else{task.resetCurrentSub();}
    }

    private static void finishEngineTask(LobbyPlayer lobbyplayer, Task task){
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Moteur enligné");
        task.finishCurrentSub();
    }


    private static void checkChute(ItemMeta blockMeta, LobbyPlayer lobbyplayer) {
        Inventory inventory=lobbyplayer.getTaskInventory();
        Task task=lobbyplayer.getTask(6);
        String meta=task.getMetaData();
        String blockType=blockMeta.getLocalizedName().substring(8,9);
        if (blockType.equals("0") && meta.equals("f")){
            task.setMetaData("t");
            new BukkitRunnable() {
                int time = 0;
                Boolean found=true;
                int foundTime=-1;
                int[] lastTwoRows={37,38,39,40,41,42,46,47,48,49,50,51};
                public void run() {
                    if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked ){
                        if (task.getMetaData()!=null && task.getMetaData().equals("t")){
                            if (time<2){
                                if (time==1) {
                                    int[] lastRowSlots = {46, 47, 48, 49, 50, 51};
                                    for (int i : lastRowSlots) {
                                        inventory.setItem(i, null);
                                    }
                                }
                            }else{
                                InventoryManager.updateChuteInventory(inventory);
                                if (found==false){
                                    if (time==foundTime){
                                        finishChuteTask(lobbyplayer, task);
                                    }
                                }else if (found){
                                    found=false;
                                    for (int i:lastTwoRows){
                                        if (inventory.getItem(i)!=(null)){
                                            found=true;
                                        }
                                    }
                                    if (found==false){
                                        foundTime=time+2;
                                    }
                                }
                            }
                        }else{cancel();return;}
                    } else {cancel();return;}
                    time++;
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 7L);


        }else if (blockType.equals("1")&&meta.equals("t")){
            task.setMetaData("f");
            int[] lastRowSlots = {46,47,48,49,50,51};
            for (int i:lastRowSlots){
                inventory.setItem(i,ItemManager.glassItems[3]);
            }
            int[] secondLastRowSlots = {37,38,39,40,41,42};
            for (int i:secondLastRowSlots){
                if (inventory.getItem(i)!=null){
                    return;
                }
            }
            finishChuteTask(lobbyplayer,task);
        }
    }

    private static void finishChuteTask(LobbyPlayer lobbyplayer, Task task){
        if (!lobbyplayer.isImpostor){
            startVisibleChute(lobbyplayer.getLobby().getWorld(),task);
        }
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Chute à déchets vide.");
        task.finishCurrentSub();
    }

    private static void startVisibleChute(World world, Task task){
        Location chuteLocation;
        int type=task.getCurrentSubTask().subID;
        if (INDICE_DE_MONDE==4){
            switch (type){
                case 0:
                    chuteLocation = new Location(world,76.5,126,-54.5);
                    break;
                case 1:
                    chuteLocation = new Location(world,67.5,126,-72.5);
                    break;
                default:
                    chuteLocation = new Location(world,67.5,126,-27.5);
                    break;
            }
        }else{
            chuteLocation = new Location(world,63.5,71,-60.5);
        }

        new BukkitRunnable() {
            int time=0;
            Location location2 = new Location(world,68.5,126,-27.5);
            public void run() {
                if (time<5){
                    world.dropItem(chuteLocation,ItemManager.chuteTrashItems[time]);
                    if (type==2){
                        world.dropItem(location2,ItemManager.chuteTrashItems[4-time]);
                    }
                }else if (time==23){
                    for(Entity current : world.getEntities()){
                        if (current instanceof Item){
                            current.remove();
                        }
                    }
                    cancel();return;
                }
                time++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 2L);
    }


    public static void checkSample(InventoryClickEvent event, ItemMeta blockMeta, LobbyPlayer lobbyplayer){
        Inventory inventory=event.getClickedInventory();
        Task task= lobbyplayer.getTask(9);
        String meta=task.getMetaData();
        if (meta!=null){
            String blockType=blockMeta.getLocalizedName().substring(8,9);
            String stage=meta.substring(5,6);
            if (blockType.equals("0") &&meta.equals("99_9_0")){
                task.setMetaData("88_8_0");
                new BukkitRunnable() {

                    String initialMeta=task.getMetaData();
                    int time=0;
                    int[] hopperSlots={10,11,12,13,14,15,16};
                    int[] potionSlots={20,21,22,23,24};
                    int slotIndex;
                    public void run() {
                        if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked ){
                            if (task.getMetaData()!=null && task.getMetaData().equals(initialMeta)){
                                if (time==0){
                                }else if (time%2==0){
                                    slotIndex=(time/2)-1;
                                    inventory.setItem(potionSlots[slotIndex],ItemManager.samplePotionItems[0]);
                                }else{
                                    slotIndex=(time+1)/2;
                                    inventory.setItem(hopperSlots[slotIndex-1],null);
                                    inventory.setItem(hopperSlots[slotIndex],ItemManager.sampleItems[4]);
                                }
                                if (time==1){
                                    inventory.setItem(49,ItemManager.sampleItems[2]);
                                }
                                if (time==11){
                                    startSampleCountDown(lobbyplayer,inventory);
                                    cancel();return;
                                }

                            }else{task.resetCurrentSub();cancel();return;}
                        } else {cancel();return;}
                        time++;
                    }
                }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 5l);
            }else if(stage.equals("1")&&stage.equals(blockType)&&meta.startsWith("00")){
                task.setMetaData("66_6_6");
                boolean success;
                if (inventory.getItem(event.getSlot()-18).equals(ItemManager.samplePotionItems[1])){
                    success=true;
                }else {
                    success=false;
                }
                new BukkitRunnable() {
                    int time=0;
                    public void run() {
                        if (isInLobbyList(lobbyplayer.name) && !lobbyplayer.getLobby().isLocked ){
                            if (task.getMetaData()!=null&&task.getMetaData()=="66_6_6"){
                                switch (time){
                                    case 0:
                                        InventoryManager.giveSampleFlicker(inventory,time,success);
                                        break;
                                    case 1:
                                        InventoryManager.giveSampleFlicker(inventory,time,success);
                                        break;
                                    case 2:
                                        InventoryManager.giveSampleFlicker(inventory,time,success);
                                        break;
                                    case 3:
                                        InventoryManager.giveSampleFlicker(inventory,time,success);
                                        break;
                                    case 4:
                                        checkSampleEnd(lobbyplayer,success);
                                        cancel();return;
                                }
                            }else{checkSampleEnd(lobbyplayer,success);cancel();return;}
                        } else {cancel();return;}
                        time++;
                    }
                }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 5l);
            }
        }

    }


    private static void startSampleCountDown(LobbyPlayer lobbyplayer, Inventory inventory){

        Task task=lobbyplayer.getTask(9);
        String playerName=lobbyplayer.getServerPlayer.getName();
        int lobbyID=lobbyplayer.getLobby().id;

        inventory.setItem(49,ItemManager.sampleItems[8]);
        inventory.getItem(4).setAmount(61);
        task.setMetaData("61_0_1");

        new BukkitRunnable() {
            String lastMeta=task.getMetaData();
            int time=60;
            public void run() {
                Inventory currentInventory=lobbyplayer.getTaskInventory();
                if (isInLobbyList(playerName) && !lobbylist[lobbyID].isLocked){
                    if (task.getMetaData()!=null && task.getMetaData().equals(lastMeta)){
                        String newMeta=time+task.getMetaData().substring(2,6);
                        if (newMeta.length()==5){
                            newMeta="0"+newMeta;
                        }
                        task.setMetaData(newMeta);
                        lastMeta=newMeta;
                        SB.redrawScoreBoard(lobbyplayer);
                        if (inventory!=null && inventory.getSize()==54 && inventory.getItem(20)!=null&&inventory.getItem(20).equals(ItemManager.samplePotionItems[0])){
                            currentInventory.removeItem(ItemManager.sampleItems[3]);
                            lobbyplayer.getPlayerPlayer().updateInventory();
                        }
                        if (time==0){
                            startSampleSecondStage(lobbyplayer);
                            lobbyplayer.getPlayerPlayer().updateInventory();
                            cancel();return;
                        }

                    }else{task.setMetaData(null);cancel();return;}
                } else {cancel();return;}
                time--;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 20l);
    }


    private static void startSampleSecondStage(LobbyPlayer lobbyplayer){
        Inventory inventory=lobbyplayer.getTaskInventory();
        Task task=lobbyplayer.getTask(9);
        int randomNum = ThreadLocalRandom.current().nextInt(0, 5);
        task.setMetaData("00_"+randomNum+"_1");
        if (inventory!=null && inventory.getSize()==54 && inventory.getItem(20)!=null&&inventory.getItem(20).equals(ItemManager.samplePotionItems[0])){
            InventoryManager.drawSecondStageItems(inventory,randomNum);
        }
    }

    public static void checkSampleEnd(LobbyPlayer lobbyplayer,boolean success){
        Task task=lobbyplayer.getTask(9);
        if (success){
            finishSampleTask(lobbyplayer,task);
        }else{
            task.setMetaData(null);
            lobbyplayer.getPlayerPlayer().closeInventory();
            lobbyplayer.getPlayerPlayer().sendMessage("\n" + ChatColor.RED + "Incorrect, Veuillez réessayer.");
        }
    }


    private static void finishSampleTask(LobbyPlayer lobbyplayer, Task task){
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Analyse terminée");
        task.finishCurrentSub();
        lobbyplayer.getPlayerPlayer().closeInventory();
    }



    public static void checkManifolds(InventoryClickEvent event, LobbyPlayer lobbyplayer){
        Task task=lobbyplayer.getTask(16);
        Inventory inventory=lobbyplayer.getTaskInventory();
        if (task.getMetaData()!=null){
            int lastClicked=Integer.parseInt(task.getMetaData());
            int currentClicked=event.getCurrentItem().getAmount();
            if (currentClicked==lastClicked+1){
                task.setMetaData(String.valueOf(currentClicked));
                inventory.setItem(event.getSlot(),ItemManager.manifoldsItems[1]);
                ItemStack item=inventory.getItem(event.getSlot());
                item.setAmount(currentClicked);
                ItemMeta meta= item.getItemMeta();
                meta.setDisplayName(ChatColor.GOLD+String.valueOf(currentClicked));
                item.setItemMeta(meta);
                if (currentClicked==10){
                    finishManifoldTask(lobbyplayer,task);
                }
            }else{
                resetManifoldsTask(inventory,task);
            }
        }else{task.resetCurrentSub();}
    }

    public static void resetManifoldsTask(Inventory inventory,Task task){
        task.setMetaData("0");
        int[] slotList = {2,3,4,5,6,11,12,13,14,15};
        for (int slot:slotList){
            int amount=inventory.getItem(slot).getAmount();
            inventory.setItem(slot,ItemManager.manifoldsItems[0]);
            ItemStack item=inventory.getItem(slot);
            item.setAmount(amount);
            ItemMeta meta=item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD+String.valueOf(amount));
            item.setItemMeta(meta);
        }
    }

    public static void finishManifoldTask(LobbyPlayer lobbyplayer, Task task){
        lobbyplayer.getPlayerPlayer().sendMessage("\n"+ChatColor.GREEN+"Collecteur opérationnel.");
        task.finishCurrentSub();
        lobbyplayer.getPlayerPlayer().closeInventory();
    }



    public static void resetVisibleTasks(Lobby lobby){
        World world=lobby.getWorld();
        resetVisibleAsteroid(world,0);
        resetVisibleAsteroid(world,1);
        resetVisibleShield(world);
    }

    public static void resetVisibleAsteroid(World world,int index){
        if (INDICE_DE_MONDE==0){
            if (index==0){
                world.getBlockAt(61,66,-59).setType(Material.REDSTONE_TORCH);
            }else{
                world.getBlockAt(61,66,-61).setType(Material.REDSTONE_TORCH);
            }
        }else if (INDICE_DE_MONDE==4){
            if (index==0){
                world.getBlockAt(85,121,-67).setType(Material.REDSTONE_TORCH);
            }else{
                world.getBlockAt(85,121,-69).setType(Material.REDSTONE_TORCH);
            }
        }
    }

    public static void resetVisibleShield(World world){
        if (INDICE_DE_MONDE==4){
            world.getBlockAt(94,109,-38).setType(Material.BLACK_CONCRETE);
            world.getBlockAt(93,109,-37).setType(Material.BLACK_CONCRETE);
            world.getBlockAt(92,109,-36).setType(Material.BLACK_CONCRETE);
        }
    }
}
