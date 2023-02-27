import net.minecraft.server.v1_16_R2.*;
import org.bukkit.*;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.boss.BossBar;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_16_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_16_R2.util.CraftChatMessage;
import org.bukkit.entity.*;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scoreboard.*;
import org.bukkit.scoreboard.Scoreboard;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;



public class AmongUs extends JavaPlugin {

    public static ListStore onceConnectedPlayers;
    public static ListStore onceConnectedNetherEnd;

    public static int INDICE_DE_MONDE = 4;                                              //indice de monde est 4 pour m2c et 0 pour serveur test
    public static int MINLOBBYSIZE = 4;                                                 //Manipuler dans le futur pour choisir la taille de lobby minimale
    public static int lobbycnt = getIndiceAssociatedLobbyCount();
    public static String NOMDEBUTMONDE = "Lobby";
    public static String NOMMONDESPAWN = getIndiceAssociatedSpawnWorldName();
    public static String NOMMONDESURVIE = "world";
    public static String NOMMONDEJEUX = "BedWars";
    public static String NOMMONDECREATIF = "Creatif";

    static boolean FirstTime = true;
    public static Buttons[] buttonList;
    public static Buttons[] buttonTaskList;
    public static int currentlobby = -1;
    public static Lobby[] lobbylist = new Lobby[lobbycnt];
    public final static Colors[] colorlist = new Colors[10];
    public final static ItemStack[] colorBlocks = new ItemStack[10];
    public final static ItemStack[] voteBlocks = new ItemStack[10];
    public static ArrayList<HumanEntity> invisible_list = new ArrayList<>();
    public static Scoreboard globalScoreBoard;
    public static subTaskList[] taskList;


    @Override
    public void onEnable() {
        globalScoreBoard = getServer().getScoreboardManager().getNewScoreboard();
        CreateLobbyList();
        setColorList();
        createButtonList();

        TaskManager.createTaskList();
        getServer().getPluginManager().registerEvents(new Events(), this);
        ItemManager.init();
        InventoryManager.init();
        getCommand("quitter").setExecutor(new Commands());
        getCommand("amongus").setExecutor(new Commands());
        lockNightTimeInLobbyWorlds();
        lockDayTimeInBedwarsWorlds();
        setGameBarWatchLoop();

        String pluginFolder=this.getDataFolder().getAbsolutePath();
        (new File(pluginFolder)).mkdirs();
        this.onceConnectedPlayers = new ListStore(new File(pluginFolder+File.separator+"once-connected.txt"));
        this.onceConnectedPlayers.load();

        (new File(pluginFolder)).mkdirs();
        this.onceConnectedNetherEnd = new ListStore(new File(pluginFolder+File.separator+"once-nether_end.txt"));
        this.onceConnectedNetherEnd.load();

        getServer().getConsoleSender().sendMessage(ChatColor.GREEN + "Noice");
    }


    @Override
    public void onDisable() {
        for (World world:Bukkit.getWorlds()){
            if (world.getName().startsWith(NOMMONDESURVIE)){
                if(world.getName().equals(NOMMONDESURVIE)){
                    List<Player> playerList=world.getPlayers();
                    for (Player players:playerList){
                        if (world.getName().equals(NOMMONDESURVIE)) {
                            onceConnectedPlayers.updatePlayerEntry(players, players.getLocation());
                        }
                    }
                }else{
                    List<Player> playerList=world.getPlayers();
                    for (Player players:playerList){
                            onceConnectedNetherEnd.updatePlayerEntry(players,players.getLocation());
                    }

                }
            }
        }
        this.onceConnectedPlayers.save();
        this.onceConnectedNetherEnd.save();
        for (Lobby lobby : lobbylist) {
            CloseLobby(lobby);
        }
        getServer().getConsoleSender().sendMessage(ChatColor.RED + "Not noice");
    }



        //CHANGER LE FONCTIONNEMENT DE "ISINLOBBYLIST" AVANT LA MISE EN PRODUCTION!!!
        // REMPLACER "world.StartsWith(NOMDEBUTMONDE)"!

    public class Events implements Listener {

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onInventoryItemClick(InventoryClickEvent event) {

            //Bukkit.broadcastMessage(String.valueOf(event.getWhoClicked().getLocation()));        ENLEVER LES SLASH POUR AVOIR LA POSITION SUR INVENTORY CLICK
            //event.getWhoClicked().sendMessage(String.valueOf(event.getSlot()));                 //ENLEVER CA

            if (event.getWhoClicked().getWorld().getName().startsWith(NOMDEBUTMONDE)) {
                event.setCancelled(true);
                if (event.getCurrentItem() != null) {
                    if (event.getCurrentItem().getItemMeta() != null) {
                        EventLogic.compareMeta(event);
                    }
                }else{
                    TaskLogic.checkResetTask(event);
                }
            }else if (checkGameBar(event.getCurrentItem(),event.getWhoClicked())){
                event.setCancelled(true);
            } else if (event.isShiftClick()){
                event.setCancelled(false);
            }
        }


        @EventHandler
        public void onInventoryClose(InventoryCloseEvent event){
            if (event.getPlayer().getWorld().getName().startsWith(NOMDEBUTMONDE)){
                LobbyPlayer lobbyplayer = getLobbyPlayer(event.getPlayer());
                lobbyplayer.getPlayerPlayer().setItemOnCursor(null);
                for (Task tasks:lobbyplayer.getTasks()){
                    if (tasks.getID()!=9){
                        tasks.setMetaData(null);
                    }
                }
            }
        }

        @EventHandler
        public void onBlockPlace(BlockPlaceEvent e){
            String worldName=e.getPlayer().getWorld().getName();
            if (e.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
                if (worldName.startsWith(NOMDEBUTMONDE) || worldName.equals(NOMMONDESPAWN)){
                    e.setCancelled(true);
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerJoin(PlayerJoinEvent event){
            Player player=event.getPlayer();
            player.teleport(getWorldSpawn());
            setGameBarWatch(player);
            player.getInventory().setHeldItemSlot(8);
        }


        @EventHandler
        public void onInteractEntity(PlayerInteractEntityEvent event) {
            String worldName=event.getPlayer().getWorld().getName();
            if (worldName.startsWith(NOMDEBUTMONDE) || (worldName.equals(NOMMONDESPAWN)&&event.getPlayer().getGameMode().equals(GameMode.SURVIVAL))) {
                event.setCancelled(true);
            }
        }


        @EventHandler
        public void onInteractArmorStand(PlayerInteractAtEntityEvent event) {
            Player player = event.getPlayer();
            String worldName = player.getWorld().getName();
            if (worldName.startsWith(NOMDEBUTMONDE)) {
                event.setCancelled(true);
                if (event.getRightClicked().getType().equals(EntityType.ARMOR_STAND)) {
                    LobbyPlayer lobbyPlayer = getLobbyPlayer(player);
                    if (lobbyPlayer.isAlive) {
                        Lobby lobby = lobbyPlayer.getLobby();
                        if (lobby.isInSabotage) {
                            endSabotage(lobby);
                        }
                        if (!lobby.isLocked && !lobby.isInEmergency && !lobby.isSoftLocked()){
                            String msg = "§dCorps de " + event.getRightClicked().getCustomName() + " §dsignalé!";
                            sendMessageToLobby(lobby, "\n" + msg);
                            sendTitleToLobby(lobby, "§cRÉUNION D'URGENCE!", msg);
                            startEmergency(lobbyPlayer);
                        }
                    } else {
                        player.sendMessage("\n§cLes fantômes ne peuvent pas signaler un corps!");
                    }
                }
            }
        }


        @EventHandler
        public void onInteract(PlayerInteractEvent event) {
            String worldName=event.getPlayer().getWorld().getName();
            if (worldName.startsWith(NOMDEBUTMONDE)) {
                EventLogic.checkInteract(event);
            }else{
                if (checkGameBar(event.getItem(),event.getPlayer())){
                    event.setCancelled(true);
                }else if(worldName.equals(NOMMONDESPAWN)&&event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
                    event.setCancelled(true);
                    if (event.getClickedBlock()!=null){
                        String bName=event.getClickedBlock().getType().name();
                        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)){
                            if (bName.endsWith("BUTTON")){
                                event.setCancelled(false);
                            }
                        }else if (event.getAction().equals(Action.PHYSICAL) && bName.endsWith("PLATE")){
                            event.setCancelled(false);
                        }
                    }
                }
            }
        }


        @EventHandler
        public void onLoseHunger(FoodLevelChangeEvent event) {
            String wName = event.getEntity().getWorld().getName();
            if (wName.startsWith(NOMDEBUTMONDE) || wName.equals(NOMMONDESPAWN)) {
                event.setCancelled(true);
            }
        }


        @EventHandler
        public void onPlayerDropItem(PlayerDropItemEvent event) {
            if (event.getPlayer().getWorld().getName().startsWith(NOMDEBUTMONDE)) {
                event.setCancelled(true);
            }else{
                if (checkGameBar(event.getItemDrop().getItemStack(), event.getPlayer())){
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onInventoryItemDrop(InventoryDragEvent event){
            HumanEntity player=event.getWhoClicked();
            if (player.getWorld().getName().startsWith(NOMDEBUTMONDE)) {
                if (isInLobbyList(player.getName()) && event.getOldCursor()!=null){
                    String meta = event.getOldCursor().getItemMeta().getLocalizedName();
                    if (meta!=null){
                        if (!meta.startsWith("task_10")) {
                            int s=Integer.parseInt(meta.substring(5,7));
                            LobbyPlayer lobbyplayer=getLobbyPlayer(player);
                            switch (s){
                                case 7:
                                    TaskLogic.checkWireOnDrop(event, meta, lobbyplayer);
                                    return;
                                case 13:
                                    TaskLogic.checkCardOnDrop(event, meta, lobbyplayer);
                                    return;
                            }
                        }else{event.setCancelled(true);}
                    }else{event.setCancelled(true);}
                }else{event.setCancelled(true);}
            }else{
                if (checkGameBar(event.getOldCursor(),event.getWhoClicked())){
                    event.setCancelled(true);
                }
            }
        }


        @EventHandler
        public void onPlayerQuit(PlayerQuitEvent event) {
            Player player=event.getPlayer();
            String wName = player.getWorld().getName();
            if (isInLobbyList(player.getName())) {
                LobbyPlayer lobbyplayer = getLobbyPlayer(player);
                BroadcastLobbyJoin(lobbyplayer, false);
                DisconnectPlayer(player);
            }else if (wName.startsWith(NOMMONDESURVIE)){
                if (wName.equals(NOMMONDESURVIE)){
                    onceConnectedPlayers.updatePlayerEntry(event.getPlayer(),event.getPlayer().getLocation());
                }else{
                    onceConnectedNetherEnd.updatePlayerEntry(event.getPlayer(),event.getPlayer().getLocation());
                }
            }
        }


        @EventHandler
        public void onPlayerMove(PlayerMoveEvent event) {
            if (event.getPlayer().getWorld().getName().startsWith(NOMDEBUTMONDE)) {
                Lobby lobby = lobbylist[Integer.valueOf(String.valueOf(event.getPlayer().getWorld().getName().charAt(5))) - 1];
                if (lobby.isInEmergency&&!lobby.isLocked) {
                    Location playerLocation = event.getPlayer().getLocation();
                    Location destination = event.getTo();
                    destination.setX(playerLocation.getX());
                    destination.setZ(playerLocation.getZ());
                }
            }
        }


        @EventHandler
        public void onPlayerTeleport(PlayerTeleportEvent event){
            World from=event.getFrom().getWorld();
            World to=event.getTo().getWorld();
            Player player=event.getPlayer();
            String pName=player.getName();
            if (!from.equals(to)){
                String fromName = from.getName();
                String toName = to.getName();

                if (fromName.equals(NOMMONDESPAWN)&&toName.equals(NOMMONDEJEUX)){
                    player.getInventory().clear();


                /*
                }else if (toName.equals(NOMMONDESPAWN)&&fromName.equals(NOMMONDEJEUX)){                                 //Routine pour clearer quitter BedWars
                    new BukkitRunnable() {
                        int time = 0;
                        public void run() {
                            if (time==1){
                                player.getInventory().clear();
                                cancel();
                                return;
                            }
                            time++;
                        }
                    }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 2L);
                    event.getPlayer().getInventory().clear();
                */

                }else if (toName.startsWith(NOMDEBUTMONDE) && !isInThisWorldsLobby(pName,toName) && event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED+"Téléportation vers un monde Among Us annulée.");

                }else if (fromName.startsWith(NOMDEBUTMONDE) && isInThisWorldsLobby(pName,fromName) && event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)) {
                    event.setCancelled(true);
                    player.sendMessage(ChatColor.RED + "Téléportation annulée. Vous êtes en partie!");


                }else if (fromName.startsWith(NOMMONDESURVIE)){
                    if (fromName.equals(NOMMONDESURVIE)){
                        onceConnectedPlayers.updatePlayerEntry(player,player.getLocation());
                        if (toName.startsWith(NOMMONDESURVIE)){
                            onceConnectedNetherEnd.updatePlayerEntry(player,event.getTo());
                        }
                    }else{
                        if (toName.equals(NOMMONDESURVIE)){
                            onceConnectedNetherEnd.remove(onceConnectedNetherEnd.getValueWithNamePrefix(player.getName()));
                        }else{
                            onceConnectedNetherEnd.updatePlayerEntry(player,player.getLocation());
                        }
                    }
                }
            }
        }


        @EventHandler
        public void onPlayerMessage(AsyncPlayerChatEvent event) {
            String name = event.getPlayer().getName();
            String wName=event.getPlayer().getWorld().getName();
            String worldLoopName="";
            if (isInLobbyList(name)) {
                event.setCancelled(true);
                Player player = event.getPlayer();
                LobbyPlayer lobbyPlayer = getLobbyPlayer(player);
                if (lobbyPlayer.getLobby().started) {
                    if (lobbyPlayer.isAlive) {
                        if (lobbyPlayer.getLobby().isInEmergency || lobbyPlayer.getLobby().isLocked) {
                            String message = event.getMessage();
                            sendColorMsgToLobby(lobbyPlayer, message);
                        } else {
                            if (lobbyPlayer.getLobby().sabotageType == 4) {
                                Block block = getButtonInList(lobbyPlayer.getLobby().getWorld(), buttonList[5]);
                                if (block.getType().name().endsWith("SIGN")) {
                                    checkSignCodeLater(event.getMessage(), lobbyPlayer);
                                }

                            } else {
                                event.getPlayer().sendMessage(ChatColor.RED + "Vous ne pouvez pas chatter présentement.");
                            }
                        }
                    } else {
                        String impStatus = "";
                        if (lobbyPlayer.isImpostor) {
                            impStatus += "§4[IMPOSTEUR] §7§o";
                        }
                        for (LobbyPlayer people : lobbyPlayer.getLobby().playerlist) {
                            if (!people.isAlive) {
                                people.getServerPlayer.sendMessage("§7§o<§r" + lobbyPlayer.getChatColor() + lobbyPlayer.name + "§7§o> " + impStatus + event.getMessage());
                            }
                        }
                    }
                } else {
                    sendColorMsgToLobby(lobbyPlayer, event.getMessage());
                }
            }else if (wName.startsWith(NOMMONDESURVIE) || wName.equals(NOMMONDESPAWN) || wName.equals(NOMMONDECREATIF)){              //SI MONDE CRÉATIF, SURVIE OU SPAWN
                event.getRecipients().clear();
                Set<Player> recipients=event.getRecipients();
                for (World world:Bukkit.getWorlds()){
                    worldLoopName=world.getName();
                    if (worldLoopName.startsWith(NOMMONDESURVIE) || worldLoopName.equals(NOMMONDESPAWN) || worldLoopName.equals(NOMMONDECREATIF)){
                        for (Player people:world.getPlayers()){
                            recipients.add(people);
                        }
                    }
                }
            } else if (!wName.equals(NOMMONDEJEUX)) {                                                                                                                //SI PAS LOBBY, CRÉATIF, SURVIE OU SPAWN
                event.getRecipients().clear();
                for (Player people : event.getPlayer().getWorld().getPlayers()) {
                    event.getRecipients().add(people);
                }
            }
        }

        @EventHandler
        public void onBlockBreak (BlockBreakEvent event){
            if (isInLobbyList(event.getPlayer().getName())){
                event.setCancelled(true);
            }else if (event.getPlayer().getWorld().getName().equals(NOMMONDESPAWN)){
                if (event.getPlayer().getGameMode().equals(GameMode.SURVIVAL)){
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler (priority = EventPriority.LOWEST)
        public void onItemFrameBreak(HangingBreakByEntityEvent e) {
            String worldName=e.getEntity().getWorld().getName();
            if (worldName.startsWith(NOMDEBUTMONDE)){
                e.setCancelled(true);
            }else if (worldName.equals(NOMMONDESPAWN)){
                if (e.getRemover() instanceof Player){
                    if (((Player)e.getEntity()).getGameMode().equals(GameMode.SURVIVAL)){
                        e.setCancelled(true);
                    }
                }
            }
        }

        @EventHandler(priority=EventPriority.HIGHEST)
        public void onPlayerDeath(PlayerDeathEvent event) {
            Player player = event.getEntity();
            String wName=player.getWorld().getName();
            if (isInLobbyList(player.getName())) {
                LobbyPlayer lobbyPlayer = getLobbyPlayer(player);
                if (lobbyPlayer.getLobby().started) {
                    if (lobbyPlayer.isAlive) {
                        if (!lobbyPlayer.getLobby().isLocked || lobbyPlayer.getLobby().isInEmergency) {
                            KillPlayer(lobbyPlayer);
                        }
                    }
                }
            }else if (wName.startsWith(NOMMONDESURVIE) || wName.equals(NOMMONDESPAWN)){
                broadcastSurvivalDeathMessage(event.getDeathMessage());
                if (wName.startsWith(NOMMONDESURVIE)){
                    event.setDeathMessage(null);
                    if (!wName.equals(NOMMONDESURVIE)){
                        onceConnectedNetherEnd.remove(onceConnectedNetherEnd.getValueWithNamePrefix(player.getName()));
                    }
                    onceConnectedPlayers.updatePlayerEntry(player,player.getLocation());
                }
            }
            event.setDeathMessage(null);
            event.getDrops().remove(ItemManager.gameBarWatch);
        }

        @EventHandler(priority = EventPriority.HIGHEST)
        public void onPlayerRespawn(PlayerRespawnEvent event) {
            Player player = event.getPlayer();
            String wName=player.getWorld().getName();
            if (isInLobbyList(player.getName())) {
                LobbyPlayer lobbyPlayer = getLobbyPlayer(player);
                Lobby thisLobby = lobbyPlayer.getLobby();
                if (thisLobby.started) {
                    event.setRespawnLocation(thisLobby.locations.shipSpawns[0]);
                }else {
                    event.setRespawnLocation(thisLobby.locations.lobby);
                }
            }else if (wName.startsWith(NOMMONDESURVIE)){
                Location respawnLoc=player.getBedSpawnLocation();
                if (respawnLoc!=null&&respawnLoc.getWorld().getName().startsWith(NOMMONDESURVIE)){
                    event.setRespawnLocation(respawnLoc);
                }else if (wName.equals(NOMMONDESURVIE)){
                    Location basedLoc=ListStore.decodePlayerValue(player.getName(),onceConnectedPlayers.getValueWithNamePrefix(player.getName()));
                    event.setRespawnLocation(getRandomLocationNearby(basedLoc));
                }else{
                    Location basedLoc=ListStore.decodePlayerValue(player.getName(),onceConnectedNetherEnd.getValueWithNamePrefix(player.getName()));
                    event.setRespawnLocation(getRandomLocationNearby(basedLoc));
                }
            }else if (wName.equals(NOMMONDESPAWN)){
                event.setRespawnLocation(getWorldSpawn());
            }else if (wName.equals(NOMMONDECREATIF)){
                event.setRespawnLocation(getCreativeSpawn());
            }
        }


        @EventHandler(priority = EventPriority.HIGHEST)
        public void onEntityAttack(EntityDamageByEntityEvent event) {
            String worldName=event.getEntity().getWorld().getName();
            if (worldName.startsWith((NOMDEBUTMONDE))) {
                event.setCancelled(true);
                event.setDamage(0.0);
                if (event.getEntityType().equals(EntityType.ITEM_FRAME)){
                    event.setCancelled(true);
                }else if ((event.getDamager() instanceof Player)) {
                    LobbyPlayer lobbyplayer = getLobbyPlayer((Player) event.getDamager());
                    HumanEntity attacker = lobbyplayer.getServerPlayer;
                    if (lobbyplayer.getLobby().started) {
                        if (lobbyplayer.isAlive) {
                            if (lobbyplayer.isImpostor && !lobbyplayer.getLobby().isInEmergency) {
                                if (attacker.getItemInHand() != null) {
                                    if (attacker.getItemInHand().getItemMeta()!=null&&attacker.getItemInHand().getItemMeta().equals(ItemManager.murder.getItemMeta())) {
                                        if (attacker.getItemInHand().getAmount() == 1) {
                                            if (event.getEntity() instanceof Player) {
                                                LobbyPlayer victim = getLobbyPlayer((Player) event.getEntity());
                                                if (!victim.isImpostor) {
                                                    event.setCancelled(true);
                                                    victim.getPlayerPlayer().setHealth(0.0);
                                                    resetKillTimer(lobbyplayer.getServerPlayer);
                                                } else {
                                                    attacker.sendMessage("\n§cOn ne trahit pas ses alliés!");
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        } else {event.setCancelled(true);}
                    } else {event.setCancelled(false);}
                }
            }else if (worldName.equals(NOMMONDESPAWN)&&event.getEntityType().equals(EntityType.ITEM_FRAME)) {
                if (event.getDamager() instanceof Player && ((Player) event.getDamager()).getGameMode().equals(GameMode.SURVIVAL)){
                    event.setCancelled(true);
                }
            }
        }

        @EventHandler
        public void onPlugin(PlayerCommandPreprocessEvent event) {
            String msg = event.getMessage().toLowerCase();
            if (msg.startsWith("/")) {
                if (isInLobbyList(event.getPlayer().getName())) {
                    if (!msg.equals("/quitter") && !msg.equals("/quit")) {
                        event.getPlayer().sendMessage("\n" + ChatColor.RED + "Commandes désactivées pendant une partie d'Among Us.");
                        event.setCancelled(true);
                        return;   //METTRE LE SETCANCEL À FALSE POUR TESTING
                    }
                }else if (event.getPlayer().getWorld().getName().equals(NOMMONDEJEUX)){
                    event.setCancelled(true);
                    event.getPlayer().sendMessage(ChatColor.RED+"Commandes désactivées pendant une partie.");
                }
                if ((msg.equals("/?")) || (msg.equals("/pl")) || (msg.equals("/plugins"))) {
                    event.getPlayer().sendMessage("\n§cCette commande est désactivée.");
                    event.setCancelled(true);
                }else if (msg.startsWith("/claim")||msg.startsWith("/landclaiming")){
                    Player p=event.getPlayer();
                    if (!p.getWorld().getName().startsWith(NOMMONDESURVIE) && !p.getWorld().getName().startsWith(NOMMONDECREATIF)){
                        p.sendMessage("§cCette commande est désactivée hors des mondes\nSurvie et Créatif.");
                        event.setCancelled(true);
                    }
                }else if (msg.startsWith("/duel")&&!event.getPlayer().getWorld().getName().equals(NOMMONDESPAWN)){
                    event.getPlayer().teleport(getWorldSpawn());
                }
            }
        }
    }




    public class Commands implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            if (!(sender instanceof Player)) {
                sender.sendMessage("\n" + ChatColor.RED + "Seuls les joueurs peuvent utiliser cette commande!");
                return true;
            }

            Player player = (Player) sender;

            if (cmd.getName().equalsIgnoreCase("quitter")) {
                if (isInLobbyList(player.getName())) {
                    LobbyPlayer lobbyplayer = getLobbyPlayer(player);
                    BroadcastLobbyJoin(lobbyplayer, false);
                    player.sendMessage("\n§6Vous avez quitté le salon.");
                    DisconnectPlayer(player);
                }
                return true;
            }else if (cmd.getName().equalsIgnoreCase("amongus")) {
                if (CheckValidConnect(player)) {
                    ConnectPlayer(player);
                    return true;
                }
            }
            return true;
        }
    }


    public static int getIndiceAssociatedLobbyCount(){
        if (INDICE_DE_MONDE==4){
            return 4;
        }else{
            return 1;
        }
    }

    public static String getIndiceAssociatedSpawnWorldName(){
        if (INDICE_DE_MONDE==4){
            return "Spawn";
        }else{
            return "Lobby1";
        }
    }

    public static Location getWorldSpawn(){
        if (INDICE_DE_MONDE==4){
            return new Location(Bukkit.getWorlds().get(8), 356.737, 62, 357.476, -135.6f, 2.3f);
        }else{
            return new Location(Bukkit.getWorlds().get(0), 339.5, 66, 39.5, 0, 0);
        }
    }

    public static Location getCreativeSpawn(){
        return new Location(Bukkit.getWorld(NOMMONDECREATIF),0.0,4.0,0.0);
    }

    public static void ConnectPlayer(HumanEntity player) {
        if (FirstTime) {
            CreateSpawnLocations();
            FirstTime = false;
        }
        player.sendMessage("\n" + ChatColor.LIGHT_PURPLE + "Bienvenue dans le salon d'Among Us.");

        Lobby lobby = lobbylist[currentlobby];

        player.getInventory().removeItem(ItemManager.gameBarWatch);
        lobby.playerlist.add(new LobbyPlayer(player, currentlobby));
        lobby.playercnt++;
        player.getInventory().clear();
        player.teleport(lobby.locations.lobby);
        player.setGameMode(GameMode.SURVIVAL);
        player.getInventory().clear();
        player.setCanPickupItems(false);
        LobbyPlayer lobbyPlayer = getLobbyPlayer(player);
        player.addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000000, 1, false, false));

        findColor(lobbyPlayer);
        setLeatherArmor(lobbyPlayer);
        giveColorBlocks(lobbyPlayer);

        lobbyPlayer.getPlayerPlayer().setScoreboard(lobby.sboards[lobbyPlayer.colorID][0]);

        player.getInventory().setItem(9, ItemManager.Bdoor);
        player.getInventory().setItem(0, ItemManager.voter);
        BroadcastLobbyJoin(lobbyPlayer, true);
        if (lobbyPlayer.getLobby().started) {
            CheckGameStart(lobbyPlayer.getLobby());
        }
        joinTeam(lobbyPlayer, "lobby");
        joinAlreadyPresentPlayersToTeam(lobbyPlayer);
        SB.redrawAllScoreBoard(lobby);
        lobbyPlayer.getPlayerPlayer().setFoodLevel(20);
        lobbyPlayer.getPlayerPlayer().setHealth(20.0);
        despawnAllArmorStands(lobby);
    }


    public static void DisconnectPlayer(HumanEntity player) {
        if (isInLobbyList(player.getName())) {
            player.setCanPickupItems(true);
            LobbyPlayer lobbyplayer = getLobbyPlayer(player);
            toggleGhost(lobbyplayer, false);
            for (PotionEffect effect : player.getActivePotionEffects()) {
                player.removePotionEffect(effect.getType());
            }

            Player Playerplayer = lobbyplayer.getPlayerPlayer();
            Playerplayer.setAllowFlight(false);
            Playerplayer.resetTitle();
            despawnHologramArmorStands(lobbyplayer);
            leaveTeams(lobbyplayer);

            Lobby thislobby = lobbyplayer.getLobby();

            if (thislobby.isInEmergency) {
                if (lobbyplayer.isAlive) {
                    for (HumanEntity players : getPlayersInLobby(thislobby.playerlist)) {
                        players.getInventory().removeItem(voteBlocks[lobbyplayer.colorID]);
                    }
                }
            }

            thislobby.colorTakenList[lobbyplayer.colorID] = false;
            thislobby.playercnt--;
            lobbyplayer.getLobby().playerlist.remove(lobbyplayer);
            thislobby.getBossBar().removePlayer(Playerplayer);
            player.teleport(getWorldSpawn());
            Playerplayer.setScoreboard(globalScoreBoard);
            thislobby.getBossBar().removePlayer(lobbyplayer.getPlayerPlayer());
            if (lobbyplayer.voted) {
                thislobby.votecnt--;
            }
            setInventory(lobbyplayer);
            removeAllEffects(player);
            if (!thislobby.started) {
                CheckGameStart(thislobby);
            }
            SB.redrawAllScoreBoard(thislobby);
            updateBossBar(thislobby);
            CheckGameEnd(thislobby);
        } else {
            player.sendMessage("\n" + ChatColor.RED + "Vous n'êtes pas dans un salon!");
        }
    }

    public static void removeAllEffects(HumanEntity player) {
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (!(effect.getType().equals(PotionEffectType.NIGHT_VISION))){
                player.removePotionEffect(effect.getType());
            }
        }
    }


    public static boolean checkGameBar(ItemStack item,Player player){
        if (item!=null) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta!=null){
                String name=itemMeta.getLocalizedName();
                if (name!=null && name.startsWith("jeux")){
                    if (!player.getWorld().getName().equals(NOMMONDEJEUX)){
                        switchGameBarType(item,name,player);
                    }else{
                        player.sendMessage(ChatColor.RED+"Barre de jeux indisponible pendant une partie.");
                    }
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean checkGameBar(ItemStack item,HumanEntity player){
        if (item!=null) {
            ItemMeta itemMeta = item.getItemMeta();
            if (itemMeta!=null){
                String name=itemMeta.getLocalizedName();
                if (name!=null && name.startsWith("jeux")){
                    switchGameBarType(item,name,(Player)player);
                    return true;
                }
            }
        }
        return false;
    }

    private static void switchGameBarType(ItemStack item, String name, Player player){
        int type=Integer.parseInt(name.substring(5,6));
        int subType;
        switch (type){
            case 0:
                player.performCommand("amongus");
                break;
            case 1:
                player.performCommand("tntrun join");
                break;
            case 2:
                subType=Integer.parseInt(name.substring(7,8));
                if (subType!=9 && !player.getWorld().getName().equals(NOMMONDESPAWN)){
                    player.teleport(getWorldSpawn());
                }
                switch (subType){
                    case 9:
                        InventoryManager.giveBedWarsInventory(player);
                        break;
                    case 0:
                        player.performCommand("bedwars join Solo");
                        break;
                    case 1:
                        player.performCommand("bedwars join Duo");
                        break;
                    case 2:
                        player.performCommand("bedwars join 3v3");
                        break;
                    case 3:
                        player.performCommand("bedwars join 4v4");
                        break;
                }
                break;

            case 3:
                subType=Integer.parseInt(name.substring(7,8));
                String playerName=item.getItemMeta().getDisplayName().substring(2);
                if (subType!=9 && !player.getWorld().getName().equals(NOMMONDESPAWN)){
                    player.teleport(getWorldSpawn());
                }
                switch (subType){
                    case 9:
                        InventoryManager.giveDuelInventory(player);
                        break;
                    case 0:
                        player.performCommand("duel "+playerName);
                        break;
                    case 1:
                        player.sendMessage(ChatColor.RED+"Ce joueur est déja en partie. Veuillez renvoyer\nun défi plus tard.");
                        break;
                    case 3:
                        return;
                }
                break;

            case 4:
                if (player.getWorld().getName().startsWith(NOMMONDESURVIE)){
                    player.sendMessage(ChatColor.RED+"Vous êtes déjà dans le monde survie!");
                    return;
                }
                String listValue=onceConnectedPlayers.getValueWithNamePrefix(player.getName());
                String otherListValue=onceConnectedNetherEnd.getValueWithNamePrefix(player.getName());
                if (otherListValue!=null){
                    Location loc = ListStore.decodePlayerValue(player.getName(),otherListValue);
                    player.teleport(loc);
                }else if (listValue!=null){
                    Location loc = ListStore.decodePlayerValue(player.getName(),listValue);
                    player.teleport(loc);
                }else{
                    Location loc = getRandomLocation(player,Bukkit.getWorld(NOMMONDESURVIE));
                    if (loc!=null){
                        player.teleport(loc);
                        onceConnectedPlayers.addValueWithNameAndLoc(player.getName(),player.getLocation());
                    }
                }
                break;
            case 5:
                player.performCommand("spawn");
                break;
            case 6:
                player.teleport(getCreativeSpawn());
                break;
            case 9:
                InventoryManager.giveGameBarInventory(player);
                break;
        }
    }


    private static Location getRandomLocationNearby(Location basedLocation){
        World world=basedLocation.getWorld();
        int max=500;
        int basedX=(int)basedLocation.getX();
        int basedZ=(int)basedLocation.getZ();
        int x;
        int y;
        int z;
        for (int i=0;i<220;i++){
            int randomNum=0;
            while (!(randomNum<-150 || randomNum>150)){
                randomNum=ThreadLocalRandom.current().nextInt(-max, +max);
            }
            x=randomNum+basedX;

            randomNum=0;
            while (!(randomNum<-150 || randomNum>150)){
                randomNum=ThreadLocalRandom.current().nextInt(-max, +max);
            }
            z=randomNum+basedZ;

            y=world.getHighestBlockYAt(x,z);
            Material blockUnder=world.getBlockAt(x,y-1,z).getType();
            if ((world.getBlockAt(x,y+1,z).getType().isAir()) || i==219){
                if ((!blockUnder.equals(Material.WATER) &&!blockUnder.equals(Material.LAVA) && !blockUnder.name().endsWith("LEAVES")) || i>199){
                    Location location=new Location(world, x, y, z);
                    return location;
                }
            }
            max++;
        }
        return null;
    }


    private static Location getRandomLocation(Player player, World world){
        for (int i=0;i<200;i++){
            int max=25000000;
            int randomNum;

            randomNum=ThreadLocalRandom.current().nextInt(-max, +max);
            int x=randomNum;
            randomNum=ThreadLocalRandom.current().nextInt(-max, +max);
            int z=randomNum;
            int y=world.getHighestBlockYAt(x,z);
            Material blockUnder=world.getBlockAt(x,y-1,z).getType();
            if ((world.getBlockAt(x,y+1,z).getType().isAir() && !blockUnder.equals(Material.WATER)
                    &&!blockUnder.equals(Material.LAVA) && !blockUnder.name().endsWith("LEAVES")) || i==199){
                Location location=new Location(world, x, y, z);
                return location;
            }
        }
        player.sendMessage(ChatColor.RED+"Une erreur est survenue lors de la téléportation.\nVeuillez contacter un adminnistrateur et réessayer");
        return null;
    }



    public static boolean CheckValidConnect(HumanEntity player) {
        if (currentlobby == -1)
            FindLobby();

        if (currentlobby == -1) {
            player.sendMessage("\n" + ChatColor.RED + "Tous les salons d'Among Us sont présentement occupés.\nVeuillez réessayer plus tard.");
            return false;
        } else {
            if (isInLobbyList(player.getName())) {
                player.sendMessage("\n" + ChatColor.RED + "Déjà dans un salon!");
                return false;
            } else if (player.getWorld().getName().equals(NOMMONDESPAWN)) {
                return true;
            } else {
                player.sendMessage("\n" + ChatColor.RED + "Cette commande est uniquement disponible dans le spawn.");
                return false;
            }
        }
    }

    public static void checkSignCodeLater(String message, LobbyPlayer lobbyPlayer) {
        new BukkitRunnable() {
            int time = 1;

            public void run() {
                if (time == 0) {
                    Lobby lobby = lobbyPlayer.getLobby();
                    if (lobby.isInSabotage) {
                        if (isInLobbyList(lobbyPlayer.name)) {
                            for (int i = 5; i < 7; i++) {
                                Block block = getButtonInList(lobbyPlayer.getLobby().getWorld(), buttonList[i]);
                                Sign sign = getSign(block);
                                if (message.equals(sign.getLine(1)) && (!sign.getLine(1).equals("Normal"))) {
                                    clearSignCodeO2(block);
                                    checkO2Fixed(lobbyPlayer.getLobby());
                                    if (lobbyPlayer.getLobby().isInSabotage) {
                                        String room = " §9";
                                        if (i == 5) {
                                            room += "(Admin)";
                                        } else {
                                            room += "(O2)";
                                        }
                                        sendMessageToLobby(lobby, "\n§aCode entré valide!" + room);
                                    }
                                    break;
                                }
                            }
                        }
                    }
                    cancel();
                    return;
                }
                time--;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 1L);
    }

    public static void FindLobby() {
        int i = 0;
        boolean found = false;

        while (i < lobbycnt) {
            if ((!lobbylist[i].started) && (!lobbylist[i].isLocked)) {
                currentlobby = i;
                found = true;
                break;
            }
            i++;
        }
        if (!(found)) {
            currentlobby = -1;
        }
    }


    private static boolean foundOPPlayer(Lobby lobby){                                                                      //IMPORTANT ENLEVER CE CODE INUTILE
        boolean foundOPPlayer = false;
        String name;
        for (LobbyPlayer people: lobby.playerlist){
            name=people.name.toLowerCase();
            if (name.equals("rozynett") || name.equals("poussinjaune")||name.equals("pingouyno")){
                foundOPPlayer=true;
                break;
            }
        }
        return foundOPPlayer;
    }



    public static void CheckGameStart(Lobby lobby) {

        if (!lobby.isLocked) {
            int lobbysize = lobby.playerlist.size();
            if ((lobbysize == 10) || ((lobbysize >= MINLOBBYSIZE || foundOPPlayer(lobby)) && (lobby.votecnt > (lobbysize / 2.0)))) {    //ENLEVER LE FOUNDOPPLAYER
                lobby.isLocked = true;
                FindLobby();
                TaskLogic.startVisibleShield(lobby.getWorld());
                new BukkitRunnable() {
                    int time1 = 9;

                    public void run() {

                        if (time1==3){
                            for (LobbyPlayer people:lobby.playerlist){
                                PlaySound.playStartGameSound(people.getPlayerPlayer());
                            }
                        }

                        if (2<time1&&time1<8){
                            for (LobbyPlayer people:lobby.playerlist){
                                PlaySound.playLoopNearEndSound(people.getPlayerPlayer());
                            }
                        }

                        if (time1==4){
                            spawnSabotageArmors(lobby.getWorld());
                        }

                        if (time1 == 0) {
                            String impCount = getImpostorAmount(lobby);
                            sendMessageToLobby(lobby, "\n" + impCount);
                            cancel();
                            return;
                        } else if (time1 == 1) {
                            String impAmount = getImpostorAmount(lobby);
                            for (LobbyPlayer people : lobby.playerlist) {
                                if (people.isImpostor) {
                                    people.getServerPlayer.sendMessage("\n§6Vous êtes un " + "§4IMPOSTEUR");
                                    sendTitleToPlayer(people, "§4IMPOSTEUR", impAmount);
                                } else {
                                    people.getServerPlayer.sendMessage("\n§6Vous êtes un " + "§bMEMBRE DE L'ÉQUIPAGE");
                                    sendTitleToPlayer(people, "§bMEMBRE D'ÉQUIPAGE", impAmount);
                                }
                            }
                        } else if (time1 == 2) {
                            if (lobby.playercnt != 0) {
                                if (lobby.isLocked) {
                                    StartGame(lobby);
                                }
                            } else {
                                lobby.reset();
                                cancel();
                                return;
                            }
                        } else if ((time1 < 8) && (time1 > 2)) {
                            String time = ChatColor.GREEN + String.valueOf(time1 - 2);

                            for (Player people : lobby.getWorld().getPlayers()) {
                                people.sendTitle(time, null, 1, 20, 1);
                            }
                        } else if (time1 == 8) {
                            for (LobbyPlayer people : lobby.playerlist) {
                                people.getServerPlayer.sendMessage("\n§aLa partie va commencer. . .");
                            }
                        }
                        time1--;
                    }
                }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 20L);
                return;
            }
        }
    }

    public static void CheckGameEnd(Lobby lobby) {

        if (!lobby.isLocked) {
            if (lobby.started) {
                if (lobby.playercnt == 0) {
                    EndGame(lobby, 0);
                } else {
                    int win = CheckWinByCount(lobby);
                    if (win != 0) {
                        EndGame(lobby, win);
                    }else if (CheckWinByTasks(lobby)){
                        win=2;
                        EndGame(lobby, win);
                    }
                }
            }
        }
    }

    public static void StartGame(Lobby lobby) {

        if (lobby.playercnt == 0) {
            EndGame(lobby, 0);
            return;
        }
        sendMessageToLobby(lobby, "\n" + ChatColor.LIGHT_PURPLE + "La partie est commencée!");
        teleportLobbyToSeats(lobby);
        if (lobby.isLocked) {
            FindLobby();
        }
        lobby.started = true;
        lobby.isLocked = false;
        lobby.votecnt = 0;
        setImpostors(lobby.playerlist);
        giveTasks(lobby);
        spawnTaskArmors(lobby);

        for (LobbyPlayer people : lobby.playerlist) {
            Player player=people.getPlayerPlayer();
            lobby.getBossBar().addPlayer(player);
            player.getInventory().setItem(8, ItemManager.timer);
            player.getInventory().setHeldItemSlot(3);
            people.voted = false;
            player.getInventory().removeItem(ItemManager.voter);
            for (ItemStack item : colorBlocks) {
                player.getInventory().removeItem(item);
            }

            if (!people.isImpostor) {
                joinTeam(people, "crew");                           //Il faut ABSOLUMENT que cette ligne soit après le setImpostors
            }else{
                joinTeam(people, "imps");
            }
        }
        endEmergency(lobby);
        TaskLogic.resetVisibleTasks(lobby);
        World world = lobby.getWorld();
        clearSignCodeO2(getButtonInList(world, buttonList[5]));
        clearSignCodeO2(getButtonInList(world, buttonList[6]));

        clearSignCodeComm(getButtonInList(world, buttonList[9]));
        clearSignCodeComm(getButtonInList(world, buttonList[10]));

        if (INDICE_DE_MONDE == 4) {
            checkOutOfBounds(lobby);
        }

        SB.redrawAllScoreBoard(lobby);
        updateBossBar(lobby);
    }


    public static void giveTasks(Lobby lobby){

        Boolean tricked=false;                                                             //Variable pour choisir des tâches personnalisées
        if (tricked){
            giveTrickedTasks(lobby);
            return;
        }
        giveCommonTask(lobby);
        int id;
        int count;
        Boolean found;
        for (int i=1;i<4;i++){
            for (LobbyPlayer people:lobby.playerlist) {                                   //TRES IMPORTANT REMPLACER!!!
                found=false;
                for (int x=0;x<100;x++){
                    if (!found){
                        found=true;
                        id=getRandomTaskID();
                        if (!people.isImpostor&&(id==13 || id==7)){
                            found=false;continue;
                        }
                        for (Task tasks:people.tasks){
                            if (tasks!=null){
                                if (tasks.getID()==id){
                                    found=false;
                                    break;
                                }
                            }
                        }
                        if (found){
                            count=0;
                            for (LobbyPlayer players:lobby.playerlist){
                                if (players!=people && !players.isImpostor){
                                    for (Task tasks:players.getTasks()){
                                        if (tasks!=null){
                                            if (tasks.getID()==id){
                                                count++;
                                                if (count==tasks.getSubTaskList().getMaxAllowed()){
                                                    found=false;
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        if (found){
                            people.tasks[i]=new Task(id,people);
                        }
                    }else{break;}
                    if (x==99){
                        for (Player players:lobby.getWorld().getPlayers()){
                            players.sendMessage(ChatColor.RED+"Il y a eu une erreur dans l'assignation des tâches. La partie a été arrêtée.");
                            EndGame(lobby,0);
                        }
                    }
                }
            }
        }
    }

    private static void giveCommonTask(Lobby lobby){
        int randomNum = ThreadLocalRandom.current().nextInt(0, 2);
        int taskID;
        if (randomNum==1){
            taskID=13;
        }else{
            taskID=7;
        }
        for (LobbyPlayer people: lobby.playerlist){
            people.tasks[0]=new Task(taskID,people);
        }
    }

    public static void giveTrickedTasks(Lobby lobby){
        for (LobbyPlayer people:lobby.playerlist) {                                   //TRES IMPORTANT RETIRER!!!
            people.tasks[0]=new Task(9,people);
            people.tasks[1]=new Task(7,people);
            people.tasks[2]=new Task(5,people);
            people.tasks[3]=new Task(15,people);
        }
    }

    public static int getRandomTaskID(){
        int randomNum = ThreadLocalRandom.current().nextInt(0, taskList.length);
        return randomNum;
    }

    public static subTask getRandomSubTask(Task task){
        int randomNum;
        for (int i=0;i<100;i++){
            randomNum=ThreadLocalRandom.current().nextInt(0, task.getSubTaskList().getSubTasks().size());
            if (task.getCurrentSubTask()!=null){
                if (randomNum==task.getCurrentSubTask().subID){
                    continue;
                }
                if (randomNum<task.getCurrentSubTask().getID()){
                    if ((task.getCurrentSubTask().getID()+1)<task.getSubTaskList().getSubTasks().size()){
                        continue;
                    }
                }
            }
            subTask subtask = task.getSubTaskList().getSubTasks().get(randomNum);
            return subtask;
        }
        sendMessageToLobby(task.getPlayer().getLobby(),ChatColor.RED+"Erreur dans l'assignation d'une nouvelle sous-tâche.");
        EndGame(task.getPlayer().getLobby(),0);
        return null;
    }


    public static int getStateType(Lobby lobby) {
        if (lobby.isInEmergency) {
            return 0;
        } else if (lobby.isInSabotage) {
            return 1;
        } else if (lobby.started) {
            return 2;
        } else {
            return 3;
        }
    }

    public static void updateBossBar(Lobby lobby){
        BossBar bar=lobby.getBossBar();
        if (lobby.playerlist.size()>0){
            bar.setProgress(getDoneTaskRatio(lobby));
        }
    }

    public static double getDoneTaskRatio(Lobby lobby){
        double doneTasks=0;
        double totalTasks=0;
        for (LobbyPlayer people:lobby.playerlist){
            if (!people.isImpostor){
                for (Task task:people.getTasks()){
                    totalTasks++;
                    if (task.isFinished()){
                        doneTasks++;
                    }
                }
            }
        }
        if (totalTasks!=0){
            return doneTasks/totalTasks;
        }else{return 0.0;}
    }

    public static String getProgressRatio(Task task){
        String ratio=" :";
        if (task.getID()==9){
            String meta=task.getMetaData();
            if (meta!=null && meta.endsWith("0_1")){
                int time=Integer.parseInt(meta.substring(0,2));
                if (time!=0){
                    ratio=" ("+time+" s) :";
                }
            }
        }else if (task.getTotalToDo()!=1){
            int amountFinished=task.getProgress();
            if (!task.isFinished()){
                amountFinished--;
            }
            ratio=" ("+amountFinished+"/"+task.getTotalToDo()+") :";
        }
        return ratio;
    }


    @SuppressWarnings("deprecation")
    public static void joinTeam(LobbyPlayer lobbyplayer, String teamName) {
        leaveTeams(lobbyplayer);
        String playerName=lobbyplayer.getPlayerPlayer().getName();
        Boolean isCamoImp = (teamName.equals("imps")&&lobbyplayer.isImpostor);
        String tName=teamName;
        for (LobbyPlayer people:lobbyplayer.getLobby().playerlist){
            if (isCamoImp){
                if (people.isImpostor){
                    tName="imps";
                }else{
                    tName="camoimps";
                }                                                              //camouflage d'imposteur pour crews dans tab list
            }
            Team team;
            Scoreboard board;

            board=people.getScoreBoard();
            team=board.getTeam(tName);
            team.addEntry(playerName);

            board=people.getOtherScoreBoard();
            team=board.getTeam(tName);
            team.addEntry(playerName);
        }
    }

    @SuppressWarnings("deprecation")
    public static void leaveTeams(LobbyPlayer lobbyplayer) {
        Player player = lobbyplayer.getPlayerPlayer();
        for (LobbyPlayer people:lobbyplayer.getLobby().playerlist){
            Scoreboard board;
            board=people.getScoreBoard();
            for (Team teams : board.getTeams()) {
                teams.removeEntry(player.getName());
            }
            board=people.getOtherScoreBoard();
            for (Team teams : board.getTeams()) {
                teams.removeEntry(player.getName());
            }
        }
    }



    public static void joinAlreadyPresentPlayersToTeam(LobbyPlayer lobbyplayer){
        Scoreboard board;
        Team team;
        String name="";
        board=lobbyplayer.getScoreBoard();
        team = board.getTeam("lobby");
        for (LobbyPlayer people : lobbyplayer.getLobby().playerlist) {
            name=people.getPlayerPlayer().getName();
            team.addEntry(name);
        }
        board=lobbyplayer.getOtherScoreBoard();
        team = board.getTeam("lobby");
        for (LobbyPlayer people : lobbyplayer.getLobby().playerlist) {
            name=people.getPlayerPlayer().getName();
            team.addEntry(name);
        }
    }


    public static void checkOutOfBounds(Lobby lobby) {
        new BukkitRunnable() {
            public void run() {
                if (!lobby.isLocked) {
                    if (lobby.started) {
                        World world=lobby.getWorld();
                        String worldName=world.getName();
                        int x;
                        int y;
                        int z;
                        for (LobbyPlayer people : lobby.playerlist) {
                            Location location = people.getServerPlayer.getLocation();
                            x = location.getBlockX();
                            y = location.getBlockY();
                            z = location.getBlockZ();
                            if (!people.getPlayerPlayer().getWorld().getName().equals(worldName)){
                                people.getPlayerPlayer().sendMessage("\n"+ChatColor.RED+"Vous n'êtes pas dans le bon monde!");
                                people.getPlayerPlayer().teleport(lobby.locations.shipSpawns[people.colorID]);
                            } else if (x > 120 || x < -10) {
                                people.getServerPlayer.teleport(lobby.locations.shipSpawns[people.colorID]);
                            } else if (y < 100) {
                                people.getServerPlayer.teleport(lobby.locations.shipSpawns[people.colorID]);
                            } else if (z > 15 || z < -90) {
                                people.getServerPlayer.teleport(lobby.locations.shipSpawns[people.colorID]);
                            }
                        }
                        if (lobby.playerlist.size()!=lobby.getWorld().getPlayers().size()){
                            for (Player people:world.getPlayers()){
                                if (!isInLobbyList(people.getName())){
                                    people.teleport(getWorldSpawn());
                                    people.sendMessage(ChatColor.RED+"\nErreur détectée. Vous êtes dans un monde Among Us!");
                                }
                            }
                        }
                    } else {
                        cancel();
                        return;
                    }
                } else {
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 60L);
    }


    public static void sendTitleToLobby(Lobby lobby, String title, String subtitle) {
        for (Player people : lobby.getWorld().getPlayers()) {
            people.sendTitle(title, subtitle, 20, 70, 10);
        }
    }

    public static void sendTitleToPlayer(LobbyPlayer player, String title, String subtitle) {
        player.getPlayerPlayer().sendTitle(title, subtitle, 10, 60, 20);
    }

    public static void sendVictoryMessage(Lobby lobby, int win) {
        String title;
        if (win == 1) {
            for (LobbyPlayer people : lobby.playerlist) {
                people.getServerPlayer.sendMessage("\n§6Les " + "§4IMPOSTEURS " + "§6ont gagné!");
                if (people.isImpostor) {
                    title = "§aVICTOIRE";
                    PlaySound.playVictorySound(people.getPlayerPlayer());
                } else {
                    title = "§4DÉFAITE";
                    PlaySound.playDefeatSound(people.getPlayerPlayer());
                }
                sendTitleToPlayer(people, title, "");
            }
        } else if (win==2){
            for (LobbyPlayer people : lobby.playerlist) {
                people.getServerPlayer.sendMessage("\n§bL'ÉQUIPAGE " + "§6a gagné!");
                if (people.isImpostor) {
                    title = "§4DÉFAITE";
                    PlaySound.playDefeatSound(people.getPlayerPlayer());
                } else {
                    title = "§aVICTOIRE";
                    PlaySound.playVictorySound(people.getPlayerPlayer());
                }
                sendTitleToPlayer(people, title, "");
            }
        }
    }


    public static void EndGame(Lobby lobby, int win) {
        lobby.isLocked = true;
        resetAllSubTasks(lobby);

        new BukkitRunnable() {
            int time2 = 5;

            public void run() {
                if ((time2 == 4) && (win != 0)) {
                    sendVictoryMessage(lobby, win);
                } else if (time2 == 0) {
                    CloseLobby(lobby);
                    cancel();
                    return;
                }
                time2--;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 20L);
    }


    public static ArrayList<HumanEntity> getPlayersInLobby(ArrayList<LobbyPlayer> LobbyList) {
        ArrayList<HumanEntity> ServerList = new ArrayList<>();
        int i = 0;
        while (i < LobbyList.size()) {
            ServerList.add(LobbyList.get(i).getServerPlayer);
            i++;
        }
        return ServerList;
    }

    public static void sendMessageToLobby(Lobby lobby, String msg) {
        for (LobbyPlayer people:lobby.playerlist){
            people.getPlayerPlayer().sendMessage(msg);
        }
    }

    public static void sendColorMsgToLobby(LobbyPlayer lobbyPlayer, String message) {

        String nameSection = "§f<" + lobbyPlayer.getChatColor() + lobbyPlayer.name + "§f> ";

        if (lobbyPlayer.isImpostor) {
            for (LobbyPlayer people : lobbyPlayer.getLobby().playerlist) {
                if (people.isImpostor) {
                    people.getServerPlayer.sendMessage(nameSection + "§4[IMPOSTEUR] §f" + message);
                } else {
                    people.getServerPlayer.sendMessage(nameSection + message);
                }
            }
        } else {
            for (LobbyPlayer people : lobbyPlayer.getLobby().playerlist) {
                people.getServerPlayer.sendMessage(nameSection + message);
            }
        }
    }


    public static void findColor(LobbyPlayer player) {
        Boolean[] colortakenlist = new Boolean[10];
        colortakenlist = player.getLobby().colorTakenList;
        for (int i = 0; i < 10; i++) {
            if (!colortakenlist[i]) {
                player.colorID = i;
                player.getLobby().colorTakenList[i] = true;
                return;
            }
        }
    }


    public static void giveColorBlocks(LobbyPlayer player) {
        int i = 20;
        for (ItemStack item : colorBlocks) {
            player.getServerPlayer.getInventory().setItem(i, item);
            i++;
            if (i == 25) {
                i = 29;
            }
        }
    }


    public static void giveVoteBlocks(Lobby lobby) {

        int i = 20;
        for (LobbyPlayer people : lobby.playerlist) {
            people.getServerPlayer.getInventory().setItem(13, ItemManager.skipper);
            if (people.isAlive) {
                for (LobbyPlayer players : lobby.playerlist) {
                    players.getServerPlayer.getInventory().setItem(i, voteBlocks[people.colorID]);
                }
                i++;
                if (i == 25) {
                    i = 29;
                }
            }
        }
    }


    public static void checkSabotage(ItemStack clickedItem, LobbyPlayer player) {
        Lobby lobby = player.getLobby();
        if (!lobby.isInEmergency) {
            if (!lobby.isInSabotage) {
                if (lobby.started) {
                    if (!lobby.isLocked) {
                        if (clickedItem.getAmount() == 1) {
                            if (player.isImpostor) {
                                startSabotage(lobby, clickedItem);
                            }
                        } else {
                            player.getServerPlayer.sendMessage("\n§cSabotage indisponible. Attendez la fin du décompte.");
                        }
                    }
                }
            } else {
                player.getServerPlayer.sendMessage("\n§cDéjà en sabotage!");
            }
        } else {
            player.getServerPlayer.sendMessage("\n§cSabotage indisponible pendant les réunions d'urgence.");
        }
    }


    public static void startSabotage(Lobby lobby, ItemStack clickedItem) {

        if (clickedItem.getItemMeta().equals(ItemManager.sabotageItems[0].getItemMeta())) {
            startLightSabotage(lobby);
        } else if (clickedItem.getItemMeta().equals(ItemManager.sabotageItems[1].getItemMeta())) {
            startCommSabotage(lobby);
        } else if (clickedItem.getItemMeta().equals(ItemManager.sabotageItems[2].getItemMeta())) {
            startReactorSabotage(lobby);
        } else if (clickedItem.getItemMeta().equals(ItemManager.sabotageItems[3].getItemMeta())) {
            startO2Sabotage(lobby);
        }
        SB.redrawAllScoreBoard(lobby);
    }


    public static void startAudioAlarm(Lobby lobby) {
        getButtonInList(lobby.getWorld(), buttonList[14]).setType(Material.REDSTONE_BLOCK);
    }

    public static void startVideoAlarm(Lobby lobby) {
        getButtonInList(lobby.getWorld(), buttonList[15]).setType(Material.REDSTONE_BLOCK);
    }

    public static void endAlarms(Lobby lobby) {
        getButtonInList(lobby.getWorld(), buttonList[14]).setType(Material.AIR);
        getButtonInList(lobby.getWorld(), buttonList[15]).setType(Material.AIR);
    }


    public static void startLightSabotage(Lobby lobby) {
        lobby.isInSabotage = true;
        lobby.sabotageType = 1;
        startVideoAlarm(lobby);
        for (LobbyPlayer people : lobby.playerlist) {
            people.getServerPlayer.sendMessage("\n§cLUMIÈRES SABOTÉES!");
            if ((!people.isImpostor) && (people.isAlive)) {
                people.getServerPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 100000, 2, false, false));
            }
        }
    }

    public static void checkLightsFixed(Lobby lobby) {
        if (lobby.sabotageType == 1) {
            new BukkitRunnable() {
                int time = 1;

                public void run() {
                    if (time == 0) {
                        World world = lobby.getWorld();
                        for (int i = 1; i < 5; i++) {
                            Block block = world.getBlockAt(buttonList[i].pos[0], buttonList[i].pos[1], buttonList[i].pos[2] - 1);
                            if (!block.isBlockPowered()) {
                                cancel();
                                return;
                            }
                        }
                        sendMessageToLobby(lobby, "\n§aLumières rétablies.");
                        endSabotage(lobby);
                        cancel();
                        return;
                    }
                    time--;
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 1L);
        }
    }


    public static void startCommSabotage(Lobby lobby) {
        resetAllSubTasks(lobby);
        lobby.isInSabotage = true;
        lobby.sabotageType = 2;
        startVideoAlarm(lobby);
        World world = lobby.getWorld();
        sendMessageToLobby(lobby, "\n§cCommunications sabotées!");

        Block block1 = getButtonInList(world, buttonList[9]);
        Block block2 = getButtonInList(world, buttonList[10]);
        giveSignRandomFreq(block1, 0);
        giveSignRandomFreq(block2, 1);

    }

    public static void giveSignRandomFreq(Block block, int x) {
        if (block.getType().name().endsWith("SIGN")) {
            Sign sign = (Sign) block.getState();
            String freq = "";
            int rand_int;
            Random rand = new Random();
            for (int i = 0; i < 3; i++) {
                if (i == 2) {
                    freq += ".";
                }
                rand_int = rand.nextInt(10);
                freq += String.valueOf(rand_int);
            }
            sign.setLine(0, "Mettre fréq. à :");
            if (x == 1) {
                sign.setLine(0, "Fréquence :");
            }
            sign.setLine(1, freq);
            sign.setLine(2, "");
            sign.update();
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "(AmongUs) Le cast n'était pas assigné à un panneau!");
        }

    }

    public static void setSignNewFreq(String buttonMeta, World world) {
        int button_num = Integer.valueOf(String.valueOf(buttonMeta.charAt(buttonMeta.length() - 1)));
        button_num--;
        Sign sign = getSign(getButtonInList(world, buttonList[10]));
        String freq = sign.getLine(1);
        if (button_num == 2) {
            button_num = 3;
        }
        int stock = Integer.valueOf(String.valueOf(freq.charAt(button_num)));
        stock++;
        if (stock == 10) {
            stock = 0;
        }
        freq = freq.substring(0, button_num) + String.valueOf(stock) + freq.substring(button_num + 1);
        sign.setLine(1, freq);
        sign.update();
    }

    public static void clearSignCodeComm(Block block) {
        Sign sign = getSign(block);
        sign.setLine(0, "Système de");
        sign.setLine(1, "communications");
        sign.setLine(2, "opérationnel.");
        sign.update();
    }

    public static void checkCommFixed(Lobby lobby) {
        if (lobby.sabotageType==2) {
            World world = lobby.getWorld();
            Block block1 = getButtonInList(world, buttonList[9]);
            Block block2 = getButtonInList(world, buttonList[10]);
            Sign sign1 = getSign(block1);
            Sign sign2 = getSign(block2);
            if (sign1.getLine(1).equals(sign2.getLine(1))) {
                sendMessageToLobby(lobby, "\n§aCommunications rétablies.");
                endSabotage(lobby);
            }
        }

    }


    public static void startReactorSabotage(Lobby lobby) {
        lobby.isInSabotage = true;
        lobby.sabotageType = 3;
        sendMessageToLobby(lobby, "\n§cRÉACTEUR SABOTÉ!");
        startCritSabotage(lobby);
        startVideoAlarm(lobby);
        //startAudioAlarm(lobby);
    }

    public static void checkReactorFixed(Lobby lobby) {
        if (lobby.sabotageType == 3) {
            World world = lobby.getWorld();
            new BukkitRunnable() {
                int time = 1;

                public void run() {
                    if ((!lobby.isLocked) && (lobby.isInSabotage)) {
                        for (int i = 7; i < 9; i++) {
                            int[] pos = buttonList[i].pos;
                            Block adjacentBlock = world.getBlockAt(pos[0], pos[1] - 1, pos[2]);
                            if (!adjacentBlock.isBlockPowered()) {
                                cancel();
                                return;
                            }
                        }
                        if (time == 0) {
                            sendMessageToLobby(lobby, "\n§aRéacteur opérationnel.");
                            endSabotage(lobby);
                            cancel();
                            return;
                        }
                    } else {
                        cancel();
                        return;
                    }
                    time--;
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 20L);
        }
    }


    public static void startO2Sabotage(Lobby lobby) {
        lobby.isInSabotage = true;
        lobby.sabotageType = 4;
        Block block;
        sendMessageToLobby(lobby, "\n§cRÉSERVOIR D'OXYGÈNE SABOTÉ!");
        World world = lobby.getWorld();

        block = getButtonInList(world, buttonList[5]);
        giveSignRandomCode(block);

        block = getButtonInList(world, buttonList[6]);
        giveSignRandomCode(block);

        //startAudioAlarm(lobby);
        startVideoAlarm(lobby);

        startCritSabotage(lobby);
        return;
    }

    public static void giveSignRandomCode(Block block) {
        if (block.getType().name().endsWith("SIGN")) {
            Sign sign = (Sign) block.getState();
            String code = "";
            int rand_int;
            Random rand = new Random();
            for (int i = 0; i < 5; i++) {
                rand_int = rand.nextInt(10);
                code += String.valueOf(rand_int);
            }
            sign.setLine(0, "Code :");
            sign.setLine(1, code);
            sign.update();
        } else {
            Bukkit.getServer().getConsoleSender().sendMessage(ChatColor.LIGHT_PURPLE + "(AmongUs) Le cast n'était pas assigné à un panneau!");
        }
        return;
    }


    public static void clearSignCodeO2(Block block) {
        Sign sign = getSign(block);
        sign.setLine(0, "État réservoir :");
        sign.setLine(1, "Normal");
        sign.update();
    }

    public static Sign getSign(Block block) {
        return ((Sign) block.getState());
    }

    public static void checkO2Fixed(Lobby lobby) {
        if (lobby.sabotageType == 4) {
            World world = lobby.getWorld();
            Block block1 = getButtonInList(world, buttonList[5]);
            Block block2 = getButtonInList(world, buttonList[6]);

            Sign sign1 = (Sign) block1.getState();
            Sign sign2 = (Sign) block2.getState();

            if (sign1.getLine(1).equals("Normal") && (sign2.getLine(1).equals("Normal"))) {
                sendMessageToLobby(lobby, "\n§aRéservoir d'oxygène opérationnel.");
                endSabotage(lobby);
            }
        }
    }


    public static void endSabotage(Lobby lobby) {
        World world = lobby.getWorld();
        lobby.isInSabotage = false;
        lobby.sabotageType = 0;
        for (LobbyPlayer people : lobby.playerlist) {
            people.getServerPlayer.removePotionEffect(PotionEffectType.BLINDNESS);
            if (people.isImpostor) {
                resetSabotageTimer(people);
            }
        }
        clearSignCodeO2(getButtonInList(world, buttonList[5]));
        clearSignCodeO2(getButtonInList(world, buttonList[6]));

        clearSignCodeComm(getButtonInList(world, buttonList[9]));
        clearSignCodeComm(getButtonInList(world, buttonList[10]));

        endAlarms(lobby);
        SB.redrawAllScoreBoard(lobby);
    }

    public static void startCritSabotage(Lobby lobby) {
        new BukkitRunnable() {
            int time = 30;
            public void run() {
                if (!lobby.isLocked && lobby.isInSabotage) {
                    lobby.timer=time;
                    if (time%2==0){
                        for (LobbyPlayer people:lobby.playerlist){
                            PlaySound.playSabotageLoopSound(people.getPlayerPlayer());
                        }
                    }
                    SB.redrawAllScoreBoard(lobby);
                    if (time == 1) {
                        sendMessageToLobby(lobby, "\n§cSABOTAGE CRITIQUE!");
                    }
                    if (time == 0) {
                        EndGame(lobby, 1);
                        cancel();
                        return;
                    }
                } else {
                    cancel();
                    return;
                }
                time--;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 20L);
    }

    public static void resetSabotageTimer(LobbyPlayer lobbyplayer) {
        String playername = lobbyplayer.name;
        Lobby lobby = lobbyplayer.getLobby();
        HumanEntity player = lobbyplayer.getServerPlayer;
        for (ItemStack item : ItemManager.sabotageItems) {
            for (int i = 0; i < 30; i++) {
                player.getInventory().addItem(item);
            }
        }

        new BukkitRunnable() {
            int time = 30;

            public void run() {
                if (!lobby.isLocked && isInLobbyList(playername)) {
                    if (time == 0) {
                        cancel();
                        return;
                    }
                    for (ItemStack item : ItemManager.sabotageItems) {
                        player.getInventory().removeItem(item);
                    }
                } else {
                    cancel();
                    return;
                }
                time--;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 20L);
    }


    public static void setImpostors(ArrayList<LobbyPlayer> PlayerList) {

        int imp1;
        int imp2 = 0;

        if (PlayerList.size() < 7) {
            imp1 = (0 + (int) (Math.random() * ((PlayerList.size() - 1) + 1)));
            LobbyPlayer imp = PlayerList.get(imp1);
            imp.isImpostor = true;
            setImpostorInventory(imp);
        } else {
            imp1 = (0 + (int) (Math.random() * ((PlayerList.size() - 1) + 1)));
            PlayerList.get(imp1).isImpostor = true;

            int i = 0;
            while (i < 100) {
                imp2 = (0 + (int) (Math.random() * ((PlayerList.size() - 1) + 1)));
                if (imp2 != imp1) {
                    break;
                }
                i++;
            }
            LobbyPlayer[] imps = {PlayerList.get(imp1), PlayerList.get(imp2)};
            for (LobbyPlayer imp : imps) {
                imp.isImpostor = true;
                setImpostorInventory(imp);
            }
        }
    }

    public static void setImpostorInventory(LobbyPlayer player) {
        player.getPlayerPlayer().getInventory().setItem(0,ItemManager.murder);
        int inventoryNumber = 16;
        for (int i = 0; i < 4; i++) {
            player.getServerPlayer.getInventory().setItem(inventoryNumber, ItemManager.sabotageItems[i]);
            inventoryNumber++;
            if (inventoryNumber == 18) {
                inventoryNumber = 25;
            }
        }
    }

    public static void CheckVoteEvent(LobbyPlayer thisplayer) {
        if (!thisplayer.getLobby().isLocked) {
            if (thisplayer.voted) {
                thisplayer.getServerPlayer.sendMessage("\n" + ChatColor.RED + "Vous avez déjà voté!");
            } else if (thisplayer.getLobby().started) {
                thisplayer.getServerPlayer.sendMessage("\n" + ChatColor.RED + "Vous ne pouvez pas voter présentement.");
            } else {
                thisplayer.voted = true;
                thisplayer.getLobby().votecnt++;
                String voteRatio = "§a (" + String.valueOf(Math.round(thisplayer.getLobby().votecnt)) + "/" + String.valueOf(thisplayer.getLobby().playercnt) + ")";
                thisplayer.getServerPlayer.sendMessage("\n§6Vous avez voté pour commencer la partie." + voteRatio);
                thisplayer.getServerPlayer.getInventory().removeItem((new ItemStack(ItemManager.voter)));
                for (LobbyPlayer people: thisplayer.getLobby().playerlist){
                    PlaySound.playVotingSound(people.getPlayerPlayer());
                }
                for (LobbyPlayer people : thisplayer.getLobby().playerlist) {
                    if (!people.name.equals(thisplayer.name)) {
                        people.getServerPlayer.sendMessage("\n" + thisplayer.getChatColor() + thisplayer.name + "§6 a voté pour commencer la partie." + voteRatio);
                    }
                }
                SB.redrawAllScoreBoard(thisplayer.getLobby());
                CheckGameStart(thisplayer.getLobby());
            }
        } else {
            thisplayer.getServerPlayer.sendMessage("\n" + ChatColor.RED + "Vous ne pouvez pas voter présentement.");
        }
    }


    public static void toggleGhost(LobbyPlayer lobbyplayer, Boolean setToGhost) {
        Player player = lobbyplayer.getPlayerPlayer();
        if (!setToGhost) {
            if (invisible_list.contains(player)) {
                for (Player people : Bukkit.getServer().getOnlinePlayers()) {
                    people.showPlayer(player);
                }
                invisible_list.remove(player);
                player.setFlying(false);
                player.setAllowFlight(false);
                removeAllEffects(player);
            }
        } else if (!invisible_list.contains(player)) {
            for (Player people : player.getWorld().getPlayers()) {
                if (!invisible_list.contains(people)) {
                    people.hidePlayer(player);
                } else {
                    player.showPlayer(people);
                }
            }

            invisible_list.add(player);
            player.setAllowFlight(true);
            player.setFlying(true);
            joinTeam(lobbyplayer, "ghosts");
            sendGhostMessage(player);
            String name = player.getName();

            new BukkitRunnable() {
                int time = 1;

                public void run() {
                    if (time == 0) {
                        if (isInLobbyList(name)) {
                            for (Player people : Bukkit.getServer().getOnlinePlayers()) {
                                if (people.getName().equals(name)) {
                                    lobbyplayer.getPlayerPlayer().addPotionEffect(new PotionEffect(PotionEffectType.NIGHT_VISION, 10000000, 1, false, false));
                                    player.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 1, true, false));
                                }
                            }
                        }
                        cancel();
                        return;
                    }
                    time--;
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 2L);
        }
    }


    public void toggleGhostOld(LobbyPlayer lobbyplayer, boolean setToGhost) {
        Player player = lobbyplayer.getPlayerPlayer();
        if (setToGhost) {
            player.setGameMode(GameMode.SPECTATOR);
            sendGhostMessage(lobbyplayer.getServerPlayer);
        } else {
            player.setGameMode(GameMode.SURVIVAL);
        }
    }


    public void hideNameTag(LobbyPlayer lobbyPlayer) {
        HumanEntity player = lobbyPlayer.getServerPlayer;
    }

    public void showNameTag(LobbyPlayer lobbyPlayer) {
        HumanEntity player = lobbyPlayer.getServerPlayer;
    }


    public static void KillPlayer(LobbyPlayer lobbyplayer) {
        lobbyplayer.getLobby().setSoftLock(true);
        PlaySound.playPlayerDeathSound(lobbyplayer.getPlayerPlayer());
        toggleGhost(lobbyplayer, true);
        for (Task tasks: lobbyplayer.getTasks()){
            if (tasks.getID()!=9){
                tasks.resetCurrentSub();
            }
        }
        lobbyplayer.isAlive = false;
        if (!lobbyplayer.getLobby().isInEmergency) {
            spawnBodyArmorStand(lobbyplayer);
            CheckGameEnd(lobbyplayer.getLobby());
        }
        lobbyplayer.getLobby().setSoftLock(false);
    }

    public static void spawnBodyArmorStand(LobbyPlayer lobbyplayer) {

        HumanEntity player = lobbyplayer.getServerPlayer;
        ArmorStand armorstand = (ArmorStand) player.getWorld().spawnEntity(player.getLocation(), EntityType.ARMOR_STAND);
        armorstand.getEquipment().setLeggings(lobbyplayer.getServerPlayer.getInventory().getLeggings());
        armorstand.getEquipment().setBoots(lobbyplayer.getServerPlayer.getInventory().getBoots());
        armorstand.setCustomName(lobbyplayer.getChatColor() + lobbyplayer.name);
        armorstand.setBasePlate(false);
    }


    public static Block getButtonInList(World world, Buttons button) {
        Block block = world.getBlockAt(button.pos[0], button.pos[1], button.pos[2]);
        return block;
    }

    public static void resetKillTimer(HumanEntity player) {

        for (int i = 0; i < 20; i++) {
            player.getInventory().addItem(ItemManager.murder);
        }

        new BukkitRunnable() {
            int minHeight = getButtonInList(player.getWorld(), buttonList[5]).getY() - 1;
            int time0 = 20;

            public void run() {
                if (time0 == 0) {
                    cancel();
                    return;
                }
                if (isInLobbyList(player.getName())) {
                    LobbyPlayer thisPlayer = getLobbyPlayer(player);
                    if (!thisPlayer.getLobby().isLocked) {
                        if (thisPlayer.isImpostor) {
                            if (player.getLocation().getY() >= minHeight) {
                                player.getInventory().removeItem((new ItemStack(ItemManager.murder)));
                                time0--;
                            }
                        } else {
                            this.time0 = 0;
                            cancel();
                            return;
                        }
                    } else {
                        cancel();
                        return;
                    }
                } else {
                    this.time0 = 0;
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 20);
    }

    public static void resetEmergencyTimer(Lobby lobby) {

        for (LobbyPlayer people : lobby.playerlist) {
            for (int i = 0; i < 20; i++) {
                people.getServerPlayer.getInventory().addItem(ItemManager.timer);
            }
        }

        new BukkitRunnable() {
            int time = 20;
            public void run() {
                if (!lobby.isLocked) {
                    if (time == 0) {
                        cancel();
                        return;
                    }
                    for (LobbyPlayer people : lobby.playerlist) {
                        people.getServerPlayer.getInventory().removeItem(ItemManager.timer);
                    }
                    time--;
                } else {
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 20L);
    }



    public static void broadcastSurvivalDeathMessage(String msg){
        for (World world:Bukkit.getWorlds()){
            if (world.getName().startsWith(NOMMONDESURVIE)||world.getName().equals(NOMMONDESPAWN)){
                List<Player> list =world.getPlayers();
                for (Player player : list){
                    player.sendMessage(msg);
                }
            }
        }
    }



    public static void BroadcastLobbyJoin(LobbyPlayer player, boolean join) {
        Lobby lobby = player.getLobby();
        String var;
        if (join) {
            var = "§arejoint";
        } else {
            var = "§cquitté";
        }
        for (LobbyPlayer people : lobby.playerlist) {
            String name = player.name;
            if (!people.name.equals(name)) {
                people.getServerPlayer.sendMessage("\n" + player.getChatColor() + name + " §6a " + var + "§6 le salon.");
            }
        }
        BroadcastLobbyCount(player, join);
    }

    public static void BroadcastLobbyCount(LobbyPlayer player, boolean join) {
        Lobby lobby = player.getLobby();
        if (join) {
            for (LobbyPlayer people : lobby.playerlist) {
                String name = player.name;
                if (!people.name.equals(name)) {
                    people.getServerPlayer.sendMessage("§6Nombre de joueurs dans le salon : " + ChatColor.BLUE + (lobby.playercnt));
                } else {
                    people.getServerPlayer.sendMessage("\n§6Nombre de joueurs dans le salon : " + ChatColor.BLUE + (lobby.playercnt));
                }
            }
        } else {
            for (LobbyPlayer people : lobby.playerlist) {
                String name = player.name;
                if (!people.name.equals(name)) {
                    people.getServerPlayer.sendMessage("§6Nombre de joueurs dans le salon : " + ChatColor.BLUE + (lobby.playercnt - 1));
                }
            }
        }
    }


    public static void CloseLobby(Lobby lobby) {
        for (HumanEntity people : getPlayersInLobby(lobby.playerlist)) {
            DisconnectPlayer(people);
        }
        endSabotage(lobby);
        lobby.reset();
        if (currentlobby == -1 || currentlobby==lobby.id) {
            FindLobby();
        }
    }


    public static ItemStack[] getLeatherArmor() {
        ItemStack[] LeatherArmor = new ItemStack[4];
        LeatherArmor[0] = new ItemStack(Material.LEATHER_HELMET, 1);
        LeatherArmor[1] = new ItemStack(Material.LEATHER_CHESTPLATE, 1);
        LeatherArmor[2] = new ItemStack(Material.LEATHER_LEGGINGS, 1);
        LeatherArmor[3] = new ItemStack(Material.LEATHER_BOOTS, 1);
        return LeatherArmor;
    }

    public static void teleportLobbyToSeats(Lobby lobby) {
        for (LobbyPlayer people : lobby.playerlist) {
            people.getServerPlayer.teleport(lobby.locations.shipSpawns[people.colorID]);
        }
    }

    public static void teleportBackToSeats(Lobby lobby, int EmergencyCaller) {
        Location location;
        Location Plocation;
        World world=lobby.getWorld();
        for (LobbyPlayer people : lobby.playerlist) {

            if (people.colorID == EmergencyCaller) {
                location = lobby.locations.shipSpawns[10];
            } else {
                location = lobby.locations.shipSpawns[people.colorID];
            }
            Player player = people.getPlayerPlayer();
            Plocation = player.getLocation();
            if (!world.getBlockAt(location).equals(world.getBlockAt(Plocation))){
                Location destination = new Location(location.getWorld(), location.getX(), location.getY(), location.getZ(), Plocation.getYaw(), Plocation.getPitch());
                player.teleport(destination);
            }
        }
    }


    public static void goTroughVent(PlayerInteractEvent event){
        Player player=event.getPlayer();
        LobbyPlayer lobbyplayer = getLobbyPlayer(player);
        if ((lobbyplayer.isImpostor || !lobbyplayer.isAlive) && (!lobbyplayer.getLobby().isInEmergency)) {
            Location trap = event.getClickedBlock().getLocation();
            trap.setX(trap.getX() + 0.5);
            trap.setZ(trap.getZ() + 0.5);
            if (player.getLocation().getY() >= trap.getY()) {
                if (trap.getX()==91.5&&trap.getZ()==-36.5){
                    trap.setYaw(180.0f);
                }
                trap.setY(trap.getY() - 1);
                player.teleport(trap);
            } else {
                trap.setY(trap.getY() + 1);
                player.teleport(trap);
            }
        }
    }


    public static void setLeatherArmor(LobbyPlayer lobbyplayer) {
        ItemStack[] armor = getLeatherArmor();
        HumanEntity player = lobbyplayer.getServerPlayer;
        for (ItemStack i : armor) {
            LeatherArmorMeta meta = (LeatherArmorMeta) i.getItemMeta();
            meta.setColor(lobbyplayer.getColor());
            meta.setDisplayName("§6Uniforme");
            meta.setUnbreakable(true);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_UNBREAKABLE);
            i.setItemMeta(meta);

        }
        player.getInventory().setHelmet(armor[0]);
        player.getInventory().setChestplate(armor[1]);
        player.getInventory().setLeggings(armor[2]);
        player.getInventory().setBoots(armor[3]);
    }

    public static void setColorList() {
        Color[] ingamelist = {Color.RED, Color.GREEN, Color.LIME, Color.WHITE, Color.BLACK, Color.fromBGR(255, 120, 255), Color.AQUA, Color.PURPLE, Color.YELLOW, Color.BLUE};
        ChatColor[] chatlist = {ChatColor.RED, ChatColor.DARK_GREEN, ChatColor.GREEN, ChatColor.GRAY, ChatColor.DARK_GRAY, ChatColor.LIGHT_PURPLE, ChatColor.AQUA, ChatColor.DARK_PURPLE, ChatColor.YELLOW, ChatColor.DARK_BLUE};
        Material[] blocklist = {Material.RED_WOOL, Material.GREEN_WOOL, Material.LIME_WOOL, Material.WHITE_WOOL, Material.BLACK_WOOL, Material.PINK_WOOL, Material.LIGHT_BLUE_WOOL, Material.PURPLE_WOOL, Material.YELLOW_WOOL, Material.BLUE_WOOL,};
        String[] colorname = {"rouge", "vert", "lime", "blanc", "noir", "rose", "aqua", "mauve", "jaune", "bleu"};
        for (int i = 0; i < 10; i++) {
            colorlist[i] = new Colors();
            colorlist[i].chat = chatlist[i];
            colorlist[i].color = ingamelist[i];
            colorlist[i].name = colorname[i];
            colorlist[i].material = blocklist[i];
            colorlist[i].ID = i;
        }
    }


    public static void lockNightTimeInLobbyWorlds(){
        new BukkitRunnable() {
            int time=0;
            public void run() {
                if (time!=0){
                    for (Lobby lobby: lobbylist){
                        lobby.getWorld().setTime(18000);
                        lobby.getWorld().setStorm(false);
                    }
                }
                time++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 100L);
    }


    public void lockDayTimeInBedwarsWorlds(){
        if (INDICE_DE_MONDE == 4){
            new BukkitRunnable() {
                public void run() {
                    for (World world :getServer().getWorlds()){
                        if (world.getName().equals(NOMMONDEJEUX)){
                            world.setTime(1000);
                            world.setStorm(false);
                        }
                    }
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 100L);
        }
    }


    public void setGameBarWatchLoop(){
        if (INDICE_DE_MONDE==4){
            new BukkitRunnable() {
                public void run() {
                    for (World world:Bukkit.getServer().getWorlds()){
                        String wName=world.getName();
                        if (wName.startsWith(NOMMONDESURVIE)||wName.equals(NOMMONDESPAWN)||wName.equals(NOMMONDECREATIF)){
                            for (Player player : world.getPlayers()){
                                setGameBarWatch(player);
                            }
                        }
                    }
                }
            }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 60L);
        }
    }


    public static void CreateSpawnLocations() {

        List<World> worlds = Bukkit.getServer().getWorlds();
        for (int i = INDICE_DE_MONDE; i < lobbycnt + INDICE_DE_MONDE; i++) {
            if (worlds.get(i).getName().startsWith(NOMDEBUTMONDE)){
                World world = worlds.get(i);
                Locations locations = new Locations();
                locations.lobby = new Location(world, 11.5, 120.0, -21.5, 0, 0);
                locations.shipSpawns = createShipSpawns(world);
                locations.ventSpawns = createVentSpawns(world);
                lobbylist[i - INDICE_DE_MONDE].locations = locations;
                createButtonMetaNames(world);
            }
        }
    }

    public static Location[] createShipSpawns(World world) {
        Location[] spawnlist = new Location[11];
        spawnlist[0] = new Location(world, 62.5, 120.5, -64.5, -134.64f, 0.706f);
        spawnlist[1] = new Location(world, 62.5, 120.5, -68.5, -45.11f, 1.57f);
        spawnlist[2] = new Location(world, 66.5, 120.5, -68.5, -315f, 1f);
        spawnlist[3] = new Location(world, 66.5, 120.5, -64.5, -226.816f, 1.858f);
        spawnlist[4] = new Location(world, 63.5, 120.5, -64.5, -181.16f, 1f);
        spawnlist[5] = new Location(world, 63.5, 120.5, -68.5, 1.33f, 3f);
        spawnlist[6] = new Location(world, 66.5, 120.5, -65.5, -270.366f, 0.5f);
        spawnlist[7] = new Location(world, 65.5, 120.5, -68.5, -1.159f, 1.85f);
        spawnlist[8] = new Location(world, 62.5, 120.5, -65.5, -88.9f, 2.722f);
        spawnlist[9] = new Location(world, 66.5, 120.5, -67.5, -272f, 1.858f);
        spawnlist[10] = new Location(world, 64.5, 121, -66.5, -359.892f, 2.722f);
        return spawnlist;
    }


    public static void createButtonList() {

        if (INDICE_DE_MONDE == 4) {
            buttonList = new Buttons[39];

            buttonList[0] = new Buttons("emergency", new int[]{64, 121, -67});

            buttonList[1] = new Buttons("sabo_lights1", new int[]{45, 122, -41});
            buttonList[2] = new Buttons("sabo_lights2", new int[]{46, 122, -41});
            buttonList[3] = new Buttons("sabo_lights3", new int[]{45, 121, -41});
            buttonList[4] = new Buttons("sabo_lights4", new int[]{46, 121, -41});
            buttonList[5] = new Buttons("sabo_o2_1", new int[]{73, 121, -53});
            buttonList[6] = new Buttons("sabo_o2_2", new int[]{78, 121, -58});
            buttonList[7] = new Buttons("sabo_reactor_1", new int[]{25, 120, -46});
            buttonList[8] = new Buttons("sabo_reactor_2", new int[]{25, 120, -62});
            buttonList[9] = new Buttons("sabo_comm-sign_1", new int[]{78, 121, -32});
            buttonList[10] = new Buttons("sabo_comm-sign_2", new int[]{77, 121, -32});
            buttonList[11] = new Buttons("sabo_comm_1", new int[]{78, 121, -33});
            buttonList[12] = new Buttons("sabo_comm_2", new int[]{77, 121, -33});
            buttonList[13] = new Buttons("sabo_comm_3", new int[]{76, 121, -33});
            buttonList[14] = new Buttons("sabo_audio_alarm", new int[]{63, 116, -68});
            buttonList[15] = new Buttons("sabo_video_alarm", new int[]{65, 116, -68});

            //Notation qui suit:  "vent" + "emplacement du boutton" + "destination à téléporter dans l'array de Location du lobby."
            buttonList[16] = new Buttons("vent_cafeteria_4", new int[]{72, 116, -66});
            buttonList[17] = new Buttons("vent_cafeteria_13", new int[]{74, 116, -66});
            buttonList[18] = new Buttons("vent_weapons_2", new int[]{83, 116, -69});
            buttonList[19] = new Buttons("vent_navUP_1", new int[]{106, 116, -55});
            buttonList[20] = new Buttons("vent_navDOWN_5", new int[]{106, 116, -49});
            buttonList[21] = new Buttons("vent_couloir_0", new int[]{90, 116, -46});
            buttonList[22] = new Buttons("vent_couloir_13", new int[]{88, 116, -46});
            buttonList[23] = new Buttons("vent_shields_3", new int[]{91, 116, -39});
            buttonList[24] = new Buttons("vent_admin_4", new int[]{71, 116, -48});
            buttonList[25] = new Buttons("vent_admin_0", new int[]{69, 116, -48});
            buttonList[26] = new Buttons("vent_electrical_8", new int[]{46, 116, -44});
            buttonList[27] = new Buttons("vent_electrical_12", new int[]{44, 116, -44});
            buttonList[28] = new Buttons("vent_lowengine_9", new int[]{39, 116, -37});
            buttonList[29] = new Buttons("vent_reactorDOWN_7", new int[]{27, 116, -49});
            buttonList[30] = new Buttons("vent_reactorUP_11", new int[]{23, 116, -58});
            buttonList[31] = new Buttons("vent_security_6", new int[]{44, 116, -51});
            buttonList[32] = new Buttons("vent_security_12", new int[]{46, 116, -51});
            buttonList[33] = new Buttons("vent_upengine_10", new int[]{38, 116, -67});
            buttonList[34] = new Buttons("vent_medbay_8", new int[]{50, 116, -51});
            buttonList[35] = new Buttons("vent_medbay_6", new int[]{48, 116, -51});

            //On a ajouté ces panneaux pour faire des portes-armures

            buttonList[36] = new Buttons("sabo_reactor-sign_1", new int[]{25, 121, -62});
            buttonList[37] = new Buttons("sabo_reactor-sign_2", new int[]{25, 121, -46});
            buttonList[38] = new Buttons("sabo_lights-sign_1", new int[]{47, 121, -41});



        } else {
            buttonList = new Buttons[19];

            buttonList[0] = new Buttons("emergency", new int[]{60, 66, -70});

            buttonList[1] = new Buttons("sabo_lights1", new int[]{62, 67, -69});
            buttonList[2] = new Buttons("sabo_lights2", new int[]{61, 67, -69});
            buttonList[3] = new Buttons("sabo_lights3", new int[]{62, 66, -69});
            buttonList[4] = new Buttons("sabo_lights4", new int[]{61, 66, -69});
            buttonList[5] = new Buttons("sabo_o2_1", new int[]{59, 67, -69});
            buttonList[6] = new Buttons("sabo_o2_2", new int[]{59, 67, -70});
            buttonList[7] = new Buttons("sabo_reactor_1", new int[]{60, 66, -73});
            buttonList[8] = new Buttons("sabo_reactor_2", new int[]{61, 66, -73});
            buttonList[9] = new Buttons("sabo_comm-sign_1", new int[]{58, 67, -75});
            buttonList[10] = new Buttons("sabo_comm-sign_2", new int[]{59, 67, -75});
            buttonList[11] = new Buttons("sabo_comm_1", new int[]{58, 66, -75});
            buttonList[12] = new Buttons("sabo_comm_2", new int[]{59, 66, -75});
            buttonList[13] = new Buttons("sabo_comm_3", new int[]{60, 66, -75});
            buttonList[14] = new Buttons("sabo_audio_alarm", new int[]{62, 66, -75});
            buttonList[15] = new Buttons("sabo_video_alarm", new int[]{63, 66, -75});
            buttonList[16] = new Buttons("sabo_reactor-sign_1", new int[]{25, 121, -62});
            buttonList[17] = new Buttons("sabo_reactor-sign_2", new int[]{25, 121, -46});
            buttonList[18] = new Buttons("sabo_lights-sign_1", new int[]{47, 121, -41});
        }
        TaskManager.createButtonTaskList();
    }


    public static Location[] createVentSpawns(World world) {
        Location[] spawnlist = new Location[14];
        spawnlist[0] = new Location(world, 73.5, 116, -63.5, -180f, 10f);         // 0cafeteria
        spawnlist[1] = new Location(world, 83.5, 116, -71.5, 0f, 10f);            // 1weapons
        spawnlist[2] = new Location(world, 106.5, 116, -56.5, 0f, 10f);           // 2navUP
        spawnlist[3] = new Location(world, 106.5, 116, -50.5, 0f, 10f);           // 3navDOWN
        spawnlist[4] = new Location(world, 89.5, 116, -47.5, 0f, 10f);            // 4couloir
        spawnlist[5] = new Location(world, 91.5, 116, -36.5, -180f, 10f);         // 5shields
        spawnlist[6] = new Location(world, 45.5, 116, -45.5, 0f, 10f);            // 6electrical
        spawnlist[7] = new Location(world, 39.5, 116, -39.5, 0f, 10f);            // 7lowengine
        spawnlist[8] = new Location(world, 45.5, 116, -48.5, -180f, 10f);         // 8security
        spawnlist[9] = new Location(world, 27.5, 116, -50.5, 0f, 10f);            // 9reactorDOWN
        spawnlist[10] = new Location(world, 23.5, 116, -59.5, 0f, 10f);           // 10reactorUP
        spawnlist[11] = new Location(world, 38.5, 116, -68.5, 0f, 10f);           // 11upengine
        spawnlist[12] = new Location(world, 49.5, 116, -52.5, 0f, 10f);           // 12medbay
        spawnlist[13] = new Location(world, 70.5, 116, -49.5, 0f, 10f);           // 13admin
        return spawnlist;
    }


    public static void createButtonMetaNames(World world) {
        for (Buttons button : buttonList) {
            world.getBlockAt(button.pos[0], button.pos[1], button.pos[2]).setMetadata("button", new FixedMetadataValue(Bukkit.getPluginManager().getPlugin("AmongUs"), button.name));
        }
    }

    public static void spawnTaskArmors(Lobby lobby){
        World world = lobby.getWorld();
        WorldServer s = ((CraftWorld)world).getHandle();
        for (Buttons button : buttonTaskList) {
            world.getBlockAt(button.pos[0], button.pos[1], button.pos[2]).setMetadata("button", new FixedMetadataValue(Bukkit.getPluginManager().getPlugin("AmongUs"), button.name));
            spawnTaskArmorStand(button,lobby,s);
        }
    }

    public static void spawnSabotageArmors(World world){
        spawnCustomArmorStand(world,buttonList[0],"§cRéunion d'urgence");
        spawnCustomArmorStand(world,buttonList[5],"§bRéservoir O2");
        spawnCustomArmorStand(world,buttonList[6],"§bRéservoir O2");
        spawnCustomArmorStand(world,buttonList[10],"§bTransmetteur FM");
        int i;
        if (INDICE_DE_MONDE==0){
            i=16;
        }else{
            i=36;
        }
        spawnCustomArmorStand(world,buttonList[i],"§bRefroidissement #1");
        spawnCustomArmorStand(world,buttonList[i+1],"§bRefroidisement #2");
        spawnCustomArmorStand(world,buttonList[i+2],"§bPanneau électrique");
    }

    public static void spawnCustomArmorStand(World world, Buttons button, String name) {
        Location location = new Location(world, button.pos[0]+0.5, button.pos[1]+0.5, button.pos[2]+0.5, 0, 0);
        ArmorStand armorstand = (ArmorStand) world.spawnEntity(location, EntityType.ARMOR_STAND);
        armorstand.setCustomName(name);
        armorstand.setVisible(false);
        armorstand.setMarker(true);
        armorstand.setCustomNameVisible(true);
    }

    public static void spawnTaskArmorStand(Buttons button, Lobby lobby, WorldServer s) {

        String buttonName=button.name;
        int taskID=Integer.parseInt(buttonName.substring(5,7));
        String assembledName;
        assembledName=taskList[taskID].descript;
        //assembledName=taskList[taskID].descript+getEntryVaryingColorCode(buttonName);                //si on veut ajouter à une team comme entry

        EntityArmorStand stand = new EntityArmorStand(s,button.pos[0]+0.5,button.pos[1]-0.5,button.pos[2]+0.5);
        stand.setCustomName(CraftChatMessage.fromStringOrNull(assembledName));
        stand.setCustomNameVisible(true);
        stand.setNoGravity(true);
        stand.setSmall(true);
        stand.setInvisible(true);
        checkSetArmorstandVisibility(lobby, taskID, stand);
        stand.killEntity();
    }


    private static void checkSetArmorstandVisibility(Lobby lobby, int taskID, EntityArmorStand stand){
        for (LobbyPlayer people:lobby.playerlist){
            for (Task task:people.getTasks()){
                if (task.getID()==taskID){
                    sendMetaPackets(people,stand);
                    people.getHologramList().add(stand.getId());
                    break;
                }
            }
        }
    }



    private static void sendMetaPackets(LobbyPlayer people, EntityArmorStand stand){
        PlayerConnection connection=((CraftPlayer)people.getPlayerPlayer()).getHandle().playerConnection;
        EntityLiving entity = stand;

        PacketPlayOutSpawnEntityLiving spawnPacket = new PacketPlayOutSpawnEntityLiving(entity);
        PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata(entity.getId(),entity.getDataWatcher(),true);

        connection.sendPacket(spawnPacket);
        connection.sendPacket(metaPacket);
    }


    private static String getEntryVaryingColorCode(String buttonName){
        int subID = Integer.parseInt(buttonName.substring(8,10));
        return ("§"+subID+" ");
    }


    public static void finishSubTask(Task task){
        PlaySound.playTaskSound(task.getPlayer().getPlayerPlayer());
        if (task.getProgress()<task.getTotalToDo()){
            task.progress++;
            task.setNewSubTask();
        } else if (task.getProgress()==task.getTotalToDo()){
            task.setFinished(true);
            CheckGameEnd(task.getPlayer().getLobby());
        }
        SB.redrawScoreBoard(task.getPlayer());
        updateBossBar(task.getPlayer().getLobby());
    }


    public static String getButtonType(Block b) {
        List<MetadataValue> metaDataValues = b.getMetadata("button");
        for (MetadataValue value : metaDataValues) {
            return value.asString();
        }
        return "null";
    }


    public static void CreateLobbyList() {
        int i = 0;
        while (i < lobbycnt) {
            lobbylist[i] = new Lobby();
            lobbylist[i].id = i;
            lobbylist[i].reset();
            i++;
        }
        assignWorldToLobbies();
    }

    private static void assignWorldToLobbies(){
        new BukkitRunnable() {
            int time = 0;
            public void run() {
                if (time==1){
                    for (Lobby lobby:lobbylist){
                        lobby.world=Bukkit.getServer().getWorlds().get(lobby.id+ AmongUs.INDICE_DE_MONDE);
                    }
                    cancel();
                    return;
                }
                time++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 20L);
    }


    public static void setInventory(LobbyPlayer player) {
        player.getServerPlayer.getInventory().clear();
        for (ItemStack item : player.inventory) {
            if (item != null) {
                if (isArmor(item)) {
                    setArmor(item, player.getServerPlayer);
                } else {
                    player.getServerPlayer.getInventory().addItem(item);
                }
            }
        }
    }



    public static void checkSabotageMeta(String blockname, Lobby lobby) {
        if (blockname.startsWith("sabo_lights")) {
            checkLightsFixed(lobby);
        } else if (blockname.startsWith("sabo_comm_")) {
            if (lobby.sabotageType==2){
                setSignNewFreq(blockname, lobby.getWorld());
                checkCommFixed(lobby);
            }
        }
    }

    public static void checkVentMeta(String blockname, LobbyPlayer player) {
        if (player.isImpostor || !player.isAlive) {
            if (blockname.startsWith("vent")) {
                String locationIndex = String.valueOf(blockname.charAt(blockname.length() - 1));
                if (Character.isDigit(blockname.charAt(blockname.length() - 2))) {
                    locationIndex = String.valueOf(blockname.charAt(blockname.length() - 2)) + locationIndex;
                }
                player.getServerPlayer.teleport(player.getLobby().locations.ventSpawns[Integer.valueOf(locationIndex)]);
            }
        }
    }


    public static void checkEmergencyMeeting(LobbyPlayer player) {
        Lobby lobby = player.getLobby();
        if (player.isAlive) {
            if (!lobby.isInEmergency) {
                if (!player.getLobby().isInSabotage) {
                    if (!lobby.isLocked && !lobby.isSoftLocked()) {
                        if (player.getServerPlayer.getInventory().getItem(8).getAmount() == 1) {
                            String msg = "§cRÉUNION D'URGENCE!";
                            sendMessageToLobby(lobby, "\n" + msg);
                            sendTitleToLobby(lobby, msg, "");
                            startEmergency(player);
                        } else {
                            player.getServerPlayer.sendMessage("\n§cRéunion d'urgence indisponible. Attendez la fin du décompte.");
                        }
                    }
                } else {
                    player.getServerPlayer.sendMessage("\n§cRéunion d'urgence indisponible pendant les sabotages.");
                }
            } else {
                player.getServerPlayer.sendMessage("\n§cDéjà en réunion d'urgence!");
            }
        } else {
            player.getServerPlayer.sendMessage("\n§cLes fantômes ne peuvent pas appeler une réunion d'urgence!");
        }
    }

    public static void startEmergency(LobbyPlayer player) {
        Lobby lobby = player.getLobby();
        for (LobbyPlayer people:lobby.playerlist){
            people.getPlayerPlayer().getInventory().setHeldItemSlot(3);
        }
        resetAllSubTasks(lobby);
        teleportLobbyToSeats(lobby);
        PlaySound.playStartEmergencySound(player.getLobby());
        player.getServerPlayer.teleport(lobby.locations.shipSpawns[10]);
        lobby.isInEmergency = true;
        lobby.isInVotingSession = true;
        giveVoteBlocks(lobby);
        int oneWhoStarted = player.colorID;

        despawnBodies(lobby);
        SB.redrawAllScoreBoard(lobby);

        new BukkitRunnable() {
            int stock = 3;
            int time = 60;

            public void run() {
                if ((!lobby.isLocked) && (lobby.isInVotingSession)) {
                    lobby.timer=time;
                    SB.redrawAllScoreBoard(lobby);

                    if (time<10){
                        for (LobbyPlayer people:lobby.playerlist){
                            PlaySound.playLoopNearEndSound(people.getPlayerPlayer());
                        }
                    }

                    if (time % stock == 0) {
                        teleportBackToSeats(lobby, oneWhoStarted);
                    }

                    if (time == 0) {
                        checkVoteOutcome(lobby);
                    }
                    time--;
                } else {
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 20L);
    }

    public static String getImpostorAmount(Lobby lobby) {
        int impCount = 0;
        for (LobbyPlayer people : lobby.playerlist) {
            if ((people.isAlive) && (people.isImpostor))
                impCount++;
        }
        String impCount_S;
        if (impCount == 1) {
            impCount_S = " Imposteur";
        } else {
            impCount_S = " Imposteurs";
        }
        String msg = ("§dIl y a " + "§4" + impCount + impCount_S + "§d parmi nous.");
        return msg;
    }


    public static void checkVoteOutcome(Lobby lobby) {
        lobby.isInVotingSession = false;
        new BukkitRunnable() {
            int time = 8;

            public void run() {

                if (!lobby.isLocked) {
                    if (time == 7) {
                        sendMessageToLobby(lobby, "\n§dFin des votes!");
                    } else if (time == 6) {
                        checkSkipOrKill(lobby);
                    } else if (time == 2) {
                        String impMessage = getImpostorAmount(lobby);
                        sendMessageToLobby(lobby, "\n" + impMessage);
                        sendTitleToLobby(lobby, "", impMessage);
                        endEmergency(lobby);

                    } else if (time == 0) {
                        CheckGameEnd(lobby);
                        cancel();
                        return;
                    }
                    time--;
                } else {
                    lobby.isInEmergency = false;
                    cancel();
                    return;
                }
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 20L);
    }


    public static void checkSkipOrKill(Lobby lobby) {
        if (lobby.playercnt > 0) {
            int mostVotedIndex = 0;
            int mostvotes = 0;
            int secondmostvotes = 0;

            for (LobbyPlayer people : lobby.playerlist) {
                if (people.votecount > mostvotes) {
                    mostVotedIndex = people.colorID;
                    mostvotes = people.votecount;
                }
            }

            for (LobbyPlayer people : lobby.playerlist) {
                if ((people.votecount > secondmostvotes) && (people.colorID != mostVotedIndex)) {
                    secondmostvotes = people.votecount;
                }
            }

            if (lobby.skipcount > mostvotes) {
                sendMessageToLobby(lobby, "\n§6Personne n'est exclu cette ronde-ci. (Passée)");
                sendTitleToLobby(lobby, "§dRonde passée", "");
            } else if (lobby.skipcount == mostvotes || mostvotes == secondmostvotes) {
                sendMessageToLobby(lobby, "\n§6Personne n'est exclu cette ronde-ci. (Égalité)");
                sendTitleToLobby(lobby, "§dRonde passée", "");
            } else {
                int cnt = 0;
                for (LobbyPlayer people:lobby.playerlist){
                    if (people.colorID==mostVotedIndex){
                        break;
                    }
                    cnt++;
                }
                LobbyPlayer killedplayer=lobby.playerlist.get(cnt);
                if (killedplayer.isAlive) {
                    startKillProcedure(killedplayer);
                }
            }
        }
    }

    public static void startKillProcedure(LobbyPlayer killedplayer) {
        Lobby lobby = killedplayer.getLobby();
        HumanEntity p = killedplayer.getServerPlayer;
        killedplayer.getPlayerPlayer().setAllowFlight(true);
        p.addPotionEffect(new PotionEffect(PotionEffectType.LEVITATION, 100000, 1, false, false));
        new BukkitRunnable() {
            int time = 5;

            public void run() {
                if (p.getWorld().getName().startsWith(NOMDEBUTMONDE)) {
                    if (!lobby.isLocked) {
                        if (time == 3) {
                            String isImpostorMessage;
                            if (killedplayer.isImpostor) {
                                isImpostorMessage = "§6 était un Imposteur.";
                            } else {
                                isImpostorMessage = "§6 n'était pas un Imposteur.";
                            }
                            String msg = killedplayer.getChatColor() + killedplayer.name + isImpostorMessage;
                            sendMessageToLobby(lobby, "\n" + msg);
                            sendTitleToLobby(lobby, "", msg);
                        } else if (time == 0) {

                            p.removePotionEffect(PotionEffectType.LEVITATION);
                            p.setHealth(0.0);
                            cancel();
                            return;
                        }
                    } else {
                        cancel();
                        return;
                    }
                } else {
                    cancel();
                    return;
                }
                time--;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 10L);
    }


    public static void endEmergency(Lobby lobby) {
        if (lobby.isInEmergency){
            for (LobbyPlayer people:lobby.playerlist){
                PlaySound.playEndEmergencySound(people.getPlayerPlayer());
            }
        }
        lobby.isInEmergency = false;

        for (LobbyPlayer people : lobby.playerlist) {

            for (int i = 0; i-1 < lobby.skipcount; i++) {
                people.getServerPlayer.getInventory().removeItem(ItemManager.skipper);
            }

            for (LobbyPlayer players : lobby.playerlist) {
                for (int i = 0; i-1 < players.votecount; i++) {
                    people.getServerPlayer.getInventory().removeItem(voteBlocks[players.colorID]);
                }
            }
            people.votecount = 0;
            people.voted = false;

            if (people.isImpostor) {
                resetKillTimer(people.getServerPlayer);
            }
        }
        lobby.skipcount = 0;
        despawnBodies(lobby);
        resetEmergencyTimer(lobby);
        SB.redrawAllScoreBoard(lobby);
        lobby.votecnt = 0;
    }


    public static void checkVoteForPlayer(String itemName, LobbyPlayer thisplayer) {

        if (thisplayer.isAlive) {
            if (!thisplayer.voted) {
                if (thisplayer.getLobby().isInVotingSession) {
                    Lobby lobby = thisplayer.getLobby();
                    thisplayer.voted = true;
                    if (itemName.equals(ItemManager.skipper.getItemMeta().getDisplayName())) {
                        sendMessageToLobby(lobby, "\n" + thisplayer.getChatColor() + thisplayer.name + "§6 a voté pour passer cette ronde.");
                        lobby.skipcount++;
                        lobby.votecnt++;
                        for (LobbyPlayer people:lobby.playerlist){
                            PlaySound.playVotingSound(people.getPlayerPlayer());
                        }
                        if (lobby.skipcount > 1) {
                            for (HumanEntity players : getPlayersInLobby(lobby.playerlist)) {
                                players.getInventory().addItem(ItemManager.skipper);
                            }
                        }
                        SB.redrawAllScoreBoard(lobby);
                        checkEndEmergency(lobby);
                        return;
                    } else {
                        for (int i = 0; i < 10; i++) {
                            if (voteBlocks[i].getItemMeta().getDisplayName().equals(itemName)) {
                                for (LobbyPlayer people : lobby.playerlist) {
                                    if (people.colorID == i) {
                                        people.votecount++;
                                        lobby.votecnt++;
                                        sendMessageToLobby(lobby, "\n" + thisplayer.getChatColor() + thisplayer.name + "§6 a voté pour " + people.getChatColor() + people.name + ".");
                                        if (people.votecount > 1) {
                                            for (LobbyPlayer players : lobby.playerlist) {
                                                players.getPlayerPlayer().getInventory().addItem(voteBlocks[people.colorID]);
                                            }
                                        }
                                        for (LobbyPlayer lplayers:lobby.playerlist){
                                            PlaySound.playVotingSound(lplayers.getPlayerPlayer());
                                        }
                                        SB.redrawAllScoreBoard(lobby);
                                        checkEndEmergency(lobby);
                                        return;
                                    }
                                }
                                return;
                            }
                        }
                    }
                } else {
                    thisplayer.getServerPlayer.sendMessage("\n§cVous ne pouvez pas voter présentement");
                }
            } else {
                thisplayer.getServerPlayer.sendMessage("\n§cVous avez déjà voté!");
            }
        } else {
            thisplayer.getServerPlayer.sendMessage("\n§cLes fantômes ne peuvent pas voter!");
        }
    }


    public static void checkEndEmergency(Lobby lobby) {
        int aliveCount = 0;
        for (LobbyPlayer people : lobby.playerlist) {
            if (people.isAlive) {
                aliveCount++;
            }
        }
        if (lobby.votecnt == aliveCount) {
            if (lobby.isInVotingSession && lobby.isInEmergency) {
                checkVoteOutcome(lobby);
            }
        }
    }


    public static void checkColorChange(HumanEntity player, ItemStack thisblock) {
        int i = 0;
        for (ItemStack blocks : colorBlocks) {
            if (thisblock.getItemMeta().equals(blocks.getItemMeta())) {
                LobbyPlayer lobbyplayer = getLobbyPlayer(player);
                Lobby lobby = lobbyplayer.getLobby();
                if (i == lobbyplayer.colorID) {
                    player.sendMessage("\n§cVous êtes déjà " + colorlist[i].chat + colorlist[i].name + "§c !");
                    return;
                }
                if ((lobby.colorTakenList[i] == false) && (!lobby.started)) {
                    lobby.colorTakenList[lobbyplayer.colorID] = false;
                    lobbyplayer.colorID = i;
                    lobby.colorTakenList[lobbyplayer.colorID] = true;
                    setLeatherArmor(lobbyplayer);
                    player.sendMessage("\n§6Vous êtes maintenant " + colorlist[i].chat + colorlist[i].name + "§6.");
                    lobbyplayer.getPlayerPlayer().setScoreboard(lobby.sboards[lobbyplayer.colorID][0]);
                    SB.redrawScoreBoard(lobbyplayer);
                } else {
                    player.sendMessage("\n§cCouleur déjà prise!");
                }
                return;
            }
            i++;
        }
    }


    public static void sendGhostMessage(HumanEntity player) {
        LobbyPlayer thisplayer = getLobbyPlayer(player);
        Lobby thisLobby = thisplayer.getLobby();
        sendTitleToPlayer(thisplayer, "§cVous êtes mort(e)!", "");

        if (CheckWinByCount(thisLobby) == 0) {
            if (thisplayer.isImpostor) {
                String msg = "\n§dVous êtes un fantôme. Aidez l'autre Imposteur en sabotant le vaisseau et finissez le travail!";

                player.sendMessage(msg);
            } else {
                String msg = "\n§dVous êtes un fantôme. Finissez vos tâches et aidez les autres membres d'équipage à sortir d'ici!";
                player.sendMessage(msg);
            }
        }
    }


    public static LobbyPlayer getLobbyPlayer(Player player) {
        String wName=player.getWorld().getName();
        String pName=player.getName();
        if (wName.startsWith(NOMDEBUTMONDE) && wName.length()==6) {
            int worldValue = Integer.valueOf(String.valueOf(wName.charAt(wName.length() - 1)));
            Lobby thislobby = lobbylist[worldValue - 1];
            for (LobbyPlayer people : thislobby.playerlist) {
                if (people!=null && people.name.equals(pName)) {
                    return people;
                }
            }
        }
        player.sendMessage("\n"+ChatColor.RED+"Une exception a été détectée. Vous n'êtes pas dans le bon monde.");
        for (Lobby lobby: lobbylist){
            for (LobbyPlayer people:lobby.playerlist){
                if (people.name.equals(pName)){
                    player.teleport(lobby.locations.shipSpawns[0]);
                    return people;
                }
            }
        }
        return null;
    }

    public static LobbyPlayer getLobbyPlayer(HumanEntity player) {
        String wName=player.getWorld().getName();
        String pName=player.getName();
        if (wName.startsWith(NOMDEBUTMONDE) && wName.length()==6) {
            int worldValue = Integer.valueOf(String.valueOf(wName.charAt(wName.length() - 1)));
            Lobby thislobby = lobbylist[worldValue - 1];
            for (LobbyPlayer people : thislobby.playerlist) {
                if (people!=null && people.name.equals(pName)) {
                    return people;
                }
            }
        }
        player.sendMessage("\n"+ChatColor.RED+"Une exception a été détectée. Vous n'êtes pas dans le bon monde.");
        for (Lobby lobby: lobbylist){
            for (LobbyPlayer people:lobby.playerlist){
                if (people.name.equals(pName)){
                    player.teleport(lobby.locations.shipSpawns[0]);
                    return people;
                }
            }
        }
        return null;
    }


    public static boolean isInLobbyList(String name) {
        int i;
        int f = 0;
        while (f < lobbycnt) {
            i = 0;
            while (i < lobbylist[f].playercnt) {
                if (name.equals(lobbylist[f].playerlist.get(i).name)) {
                    return true;
                }
                i++;
            }
            f++;
        }
        return false;
    }

    public static boolean isInThisWorldsLobby(String playerName, String worldName) {
        if (worldName.startsWith(NOMDEBUTMONDE)){
            for (LobbyPlayer people:lobbylist[Integer.parseInt(worldName.substring(5,6))-1].playerlist){
                if (people.name.equals(playerName)){
                    return true;
                }
            }
        }
        return false;
    }

    public static int CheckWinByCount(Lobby lobby) {
        double[] ImpAndCrew = getImpAndCrewCount(lobby);
        double impCount = ImpAndCrew[0];
        double crewCount = ImpAndCrew[1];
        if (impCount >= crewCount) {
            return 1;
        } else {
            if (impCount == 0) {
                return 2;
            } else {
                return 0;
            }
        }
    }

    public static Boolean CheckWinByTasks(Lobby lobby){
        return getDoneTaskRatio(lobby)==1 ? true : false;
    }

    public static double[] getImpAndCrewCount(Lobby lobby) {

        double[] ImpAndCrew = new double[2];
        double impCount = 0;
        double crewCount = 0;

        for (int i = 0; i < lobby.playercnt; i++) {
            if ((lobby.playerlist.get(i).isImpostor) && (lobby.playerlist.get(i).isAlive)) {
                impCount++;
            } else if ((!(lobby.playerlist.get(i).isImpostor)) && (lobby.playerlist.get(i).isAlive)) {
                crewCount++;
            }
        }
        ImpAndCrew[0] = impCount;
        ImpAndCrew[1] = crewCount;
        return ImpAndCrew;
    }


    public static void resetAllSubTasks(Lobby lobby){
        for (LobbyPlayer people:lobby.playerlist){
            if (people.isAlive) {
                people.getPlayerPlayer().removePotionEffect(PotionEffectType.GLOWING);
            }
            people.getPlayerPlayer().removePotionEffect(PotionEffectType.JUMP);
            for (Task tasks:people.getTasks()) {
                if (tasks.getID()!=9){
                    tasks.resetCurrentSub();
                }
            }
        }
    }


    public static boolean isArmor(ItemStack itemStack) {
        if (itemStack == null)
            return false;
        final String typeNameString = itemStack.getType().name();
        if (typeNameString.endsWith("_HELMET")
                || typeNameString.endsWith("_CHESTPLATE")
                || typeNameString.endsWith("_LEGGINGS")
                || typeNameString.endsWith("_BOOTS")) {
            return true;
        }
        return false;
    }

    public static void setArmor(ItemStack item, HumanEntity player) {
        String typeNameString = item.getType().name();

        if ((typeNameString.endsWith("_HELMET") && (player.getInventory().getHelmet() == null))) {
            player.getInventory().setHelmet(item);

        } else if ((typeNameString.endsWith("_CHESTPLATE") && (player.getInventory().getChestplate() == null))) {
            player.getInventory().setChestplate(item);

        } else if ((typeNameString.endsWith("_LEGGINGS") && (player.getInventory().getLeggings() == null))) {
            player.getInventory().setLeggings(item);

        } else if ((typeNameString.endsWith("_BOOTS") && (player.getInventory().getBoots() == null))) {
            player.getInventory().setBoots(item);

        } else {
            player.getInventory().addItem(item);
        }
    }


    public static void despawnBodies(Lobby lobby) {
        for (Entity entity : lobby.getWorld().getEntities()) {
            if (entity instanceof ArmorStand) {
                if (((ArmorStand) entity).isVisible()){
                    entity.remove();
                }
            }
        }
    }

    public static void despawnAllArmorStands(Lobby lobby){
        for (Entity entity : lobby.getWorld().getEntities()) {
            if (entity instanceof ArmorStand) {
                entity.remove();
            }
        }
    }


    private static void despawnHologramArmorStands(LobbyPlayer player){
        PlayerConnection connection=((CraftPlayer)player.getPlayerPlayer()).getHandle().playerConnection;
        for (Integer hologramID:player.getHologramList()){
            PacketPlayOutEntityDestroy destroy = new PacketPlayOutEntityDestroy(hologramID);
            connection.sendPacket(destroy);
        }
    }

    public static class Locations {
        public Location lobby;
        public Location[] shipSpawns;
        public Location[] ventSpawns;

        public Locations() {
            this.lobby = lobby;
            this.shipSpawns = shipSpawns;
            this.ventSpawns = ventSpawns;
        }
    }

    public static class Buttons {
        public int[] pos;
        public String name;

        public Buttons(String name, int[] pos) {
            this.pos = pos;
            this.name = name;
        }
    }


    public static class Colors {
        public ChatColor chat;
        public Color color;
        public String name;
        public Material material;
        public int ID;

        public Colors() {
            this.chat = chat;
            this.color = color;
            this.name = name;
            this.ID = ID;
            this.material = material;
        }
    }


    public static Boolean[] newFalseList() {
        Boolean[] list = {false, false, false, false, false, false, false, false, false, false};
        return list;
    }

    public static Scoreboard[][] newScoreBoardList() {

        Scoreboard[] sboardlist;
        Scoreboard[][] sboards = new Scoreboard[11][2];
        for (int i = 0; i < 10; i++) {
            sboardlist=new Scoreboard[2];
            sboardlist[0]=newPlayerScoreBoard();
            sboardlist[1]=newPlayerScoreBoard();
            sboards[i] = sboardlist;
        }
        sboardlist=new Scoreboard[2];
        sboardlist[0]=newLobbyScoreBoard();
        sboardlist[1]=newLobbyScoreBoard();
        sboards[10]=sboardlist;
        return sboards;
    }

    public static Scoreboard newPlayerScoreBoard() {
        Scoreboard board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("test", "dummy", "hey");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("§a» §dAmong Us §a«");

        board.registerNewTeam("imps");
        board.registerNewTeam("camoimps");
        board.registerNewTeam("crew");
        board.registerNewTeam("ghosts");
        board.registerNewTeam("lobby");

        Team team;

        team = board.getTeam("ghosts");
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        team.setColor(ChatColor.GRAY);
        team.setCanSeeFriendlyInvisibles(true);

        team = board.getTeam("imps");
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        team.setColor(ChatColor.RED);
        team.setPrefix(ChatColor.WHITE+"");

        team = board.getTeam("camoimps");
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OTHER_TEAMS);
        team.setColor(ChatColor.AQUA);

        team = board.getTeam("crew");
        team.setOption(Team.Option.NAME_TAG_VISIBILITY, Team.OptionStatus.FOR_OWN_TEAM);
        team.setColor(ChatColor.AQUA);
        team.setCanSeeFriendlyInvisibles(true);

        return board;
    }

    public static Scoreboard newLobbyScoreBoard() {
        Scoreboard board = Bukkit.getServer().getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("test", "dummy", "hey");
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);
        objective.setDisplayName("Scoreboardtest");
        objective.getName();

        Score score = objective.getScore(ChatColor.RED + "ERREUR");
        score.setScore(1);
        return board;
    }


    public static void setGameBarWatch (Player player){
        player.getInventory().setItem(8,ItemManager.gameBarWatch);
    }
}
