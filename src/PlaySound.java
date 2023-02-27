import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlaySound {

    public static void playVictorySound(Player player){
        player.playSound(player.getLocation(),Sound.UI_TOAST_CHALLENGE_COMPLETE,6,2);
    }

    public static void playDefeatSound(Player player){
        player.playSound(player.getLocation(),Sound.BLOCK_BEACON_DEACTIVATE,10,0);
    }

    public static void playTaskSound(Player player){
        player.playEffect(player.getLocation(), Effect.STEP_SOUND, Material.EMERALD_BLOCK);
        player.playNote(player.getLocation(), Instrument.PIANO, Note.flat(1, Note.Tone.B));
    }

    public static void playSabotageLoopSound(Player player){
        player.playSound(player.getLocation(),Sound.BLOCK_BELL_USE,10,-2);
    }

    public static void playStartGameSound(Player player){
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BELL_RESONATE,10,-1);
    }

    public static void playStartEmergencySound(Lobby lobby){
        for (LobbyPlayer people:lobby.playerlist){
            playStartEmergencyFirstSound(people.getPlayerPlayer());
        }
        new BukkitRunnable() {
            int time = 0;
            public void run() {
                if (time==1){
                    for (LobbyPlayer people:lobby.playerlist){
                        playStartEmergencySecondSound(people.getPlayerPlayer());
                    }
                    cancel();
                    return;
                }
                time++;
            }
        }.runTaskTimer(Bukkit.getPluginManager().getPlugin("AmongUs"), 0L, 8L);
    }

    public static void playStartEmergencyFirstSound(Player player){
        player.playSound(player.getLocation(),Sound.BLOCK_BELL_USE,10,1);
    }
    public static void playStartEmergencySecondSound(Player player){
        player.playSound(player.getLocation(),Sound.BLOCK_BREWING_STAND_BREW,10,0);
    }

    public static void playVotingSound(Player player){
        player.playSound(player.getLocation(),Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE,10,2);
    }

    public static void playEndEmergencySound(Player player){
        player.playSound(player.getLocation(),Sound.ENTITY_ILLUSIONER_PREPARE_BLINDNESS,10,0);
    }

    public static void playPlayerDeathSound(Player player){
        player.getWorld().playSound(player.getLocation(),Sound.ENTITY_PLAYER_DEATH,1,1);
    }

    public static void playLoopNearEndSound(Player player){
        player.playSound(player.getLocation(),Sound.ITEM_FLINTANDSTEEL_USE,10,2);
    }

    public static void playAsteroidSound(Player player){
        player.getWorld().playSound(player.getLocation(), Sound.ITEM_FIRECHARGE_USE,1,3);
    }

    public static void playShieldSound(Player player){
        player.getWorld().playSound(player.getLocation(), Sound.BLOCK_BEACON_ACTIVATE,1,1);
    }
}
