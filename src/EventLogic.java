import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.meta.ItemMeta;

public class EventLogic extends AmongUs{

    //ICI ON FAIT L'ITEM CLICK

    public static void compareMeta(InventoryClickEvent event){
        ItemMeta meta = event.getCurrentItem().getItemMeta();
        HumanEntity thisplayer = event.getWhoClicked();
        if (meta.equals(ItemManager.Bdoor.getItemMeta())) {
            LobbyPlayer lobbyplayer = getLobbyPlayer(thisplayer);
            event.getWhoClicked().sendMessage("\n§6Vous avez quitté le salon.");
            BroadcastLobbyJoin(lobbyplayer, false);
            DisconnectPlayer(event.getWhoClicked());
        } else if (meta.equals(ItemManager.voter.getItemMeta())) {
            CheckVoteEvent(getLobbyPlayer(thisplayer));
        } else {
            compareName(event);
        }
    }

    public static void compareName(InventoryClickEvent event){
        event.setCancelled(true);
        LobbyPlayer lobbyPlayer = getLobbyPlayer(event.getWhoClicked());
        ItemMeta meta=event.getCurrentItem().getItemMeta();
        String name=meta.getDisplayName();
        if (name.equals(colorBlocks[0].getItemMeta().getDisplayName())) {
            checkColorChange(event.getWhoClicked(), event.getCurrentItem());
        } else if (name.startsWith("§6Voter pour")) {
            checkVoteForPlayer(meta.getDisplayName(), lobbyPlayer);
        } else if (name.equals(ItemManager.skipper.getItemMeta().getDisplayName())) {
            checkVoteForPlayer(meta.getDisplayName(), lobbyPlayer);
        } else if (name.startsWith("§6Saboter")) {
            checkSabotage(event.getCurrentItem(), lobbyPlayer);
        } else if (meta.getLocalizedName().startsWith("task")){
            TaskLogic.checkTaskObject(event,meta,lobbyPlayer);
        }
    }



    //EN DESSOUS ON FAIT L'INTERACT


    public static void checkInteract(PlayerInteractEvent event){

        LobbyPlayer lobbyplayer=getLobbyPlayer(event.getPlayer());
        if (!lobbyplayer.isAlive){
            event.setCancelled(true);
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            checkClickEvent(event);
        } else if (event.getAction().equals(Action.RIGHT_CLICK_AIR)) {
            checkItemMeta(event);
        } else if (event.getAction().equals(Action.PHYSICAL)) {
            checkPhysicEvent(event);
        }
    }

    public static void checkClickEvent(PlayerInteractEvent event){
        event.setCancelled(true);
        Block block = event.getClickedBlock();
        if (block != null) {
            checkBlockName(event,block);
            checkItemMeta(event);
        }
    }


    public static void checkBlockName(PlayerInteractEvent event, Block block){
        Player player = event.getPlayer();
        String name = block.getType().name();
        if (name.equals("IRON_TRAPDOOR")) {
            goTroughVent(event);
        } else if (name.endsWith("PLATE") || (name.endsWith("BUTTON"))) {
            String buttonType = getButtonType(block);
            if (!buttonType.equals("null")) {
                LobbyPlayer lobbyplayer = getLobbyPlayer(player);
                if (buttonType.startsWith("sabo")) {
                    if (lobbyplayer.isAlive) {
                        event.setCancelled(false);
                        checkSabotageMeta(buttonType, lobbyplayer.getLobby());
                    }
                }else{
                    checkButtonMeta(buttonType, lobbyplayer, event);
                }
            } else {
                //player.sendMessage("Objet sans valeur");                     //POUR TESTING UNIQUEMENT
                //player.sendMessage("position: " + String.valueOf(block.getX()) + " " + String.valueOf(block.getY()) + " " + String.valueOf(block.getZ()));
            }
        }
    }

    public static void checkItemMeta(PlayerInteractEvent event) {
        if (!(event.getItem() == null)) {
            if (event.getItem().getItemMeta().equals(ItemManager.voter.getItemMeta())) {
                event.setCancelled(true);
                CheckVoteEvent(getLobbyPlayer(event.getPlayer()));
            }
        }
    }


    public static void checkButtonMeta(String button, LobbyPlayer lobbyplayer, PlayerInteractEvent event) {
        if (!button.equals("null")) {
            if ((lobbyplayer.getLobby().started == true) && (lobbyplayer.getLobby().isLocked == false)) {
                if (button.equals("emergency")){
                    if (lobbyplayer.isAlive){
                        event.setCancelled(false);
                    }
                    checkEmergencyMeeting(lobbyplayer);
                }else if (button.startsWith("task")){
                    TaskManager.checkTaskType(button,lobbyplayer,event);
                }
            }
        }
    }

    public static void checkPhysicEvent(PlayerInteractEvent event){
        Player player = event.getPlayer();
        if (event.getClickedBlock().getType().name().endsWith("PLATE")) {
            LobbyPlayer lobbyplayer = getLobbyPlayer(player);
            String buttonType = getButtonType(event.getClickedBlock());
            if (!buttonType.equals("null")) {
                if (buttonType.startsWith("sabo")) {
                    if (lobbyplayer.isAlive) {
                        checkReactorFixed(lobbyplayer.getLobby());
                    } else {
                        event.setCancelled(true);
                    }
                }else if (buttonType.startsWith("vent")) {
                    event.setCancelled(true);
                    checkVentMeta(buttonType, lobbyplayer);
                }
            }else{
                event.setCancelled(true);
            }
        }
    }
}
