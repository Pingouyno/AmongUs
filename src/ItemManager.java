import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;


import java.util.ArrayList;
import java.util.List;

public class ItemManager extends AmongUs {

    public static ItemStack Bdoor;
    public static ItemStack murder;
    public static ItemStack voter;
    public static ItemStack timer;
    public static ItemStack skipper;
    public static ItemStack uploadstart;
    public static ItemStack trajectorycursor;
    public static ItemStack[] sabotageItems = new ItemStack[4];
    public static ItemStack[] colorWires = new ItemStack[4];
    public static ItemStack[] scanloaders = new ItemStack[4];
    public static ItemStack[] uploadloaders = new ItemStack[3];
    public static ItemStack[] fuelItems = new ItemStack[3];
    public static ItemStack[] trajectoryItems = new ItemStack[5];
    public static ItemStack[] divertItems = new ItemStack[4];
    public static ItemStack[] asteroidItems = new ItemStack[2];
    public static ItemStack[] distributorItems = new ItemStack[7];
    public static ItemStack[] shieldItems = new ItemStack[3];
    public static ItemStack[] cardItems = new ItemStack[5];
    public static ItemStack[] filterItems = new ItemStack[4];
    public static ItemStack[] glassItems = new ItemStack[10];
    public static ItemStack[] reactorItems = new ItemStack[2];
    public static ItemStack[] steeringItems = new ItemStack[5];
    public static ItemStack[] engineItems = new ItemStack[4];
    public static ItemStack[] chuteItems= new ItemStack[2];
    public static ItemStack[] chuteTrashItems= new ItemStack[5];
    public static ItemStack[] sampleItems = new ItemStack[9];
    public static ItemStack[] samplePotionItems = new ItemStack[3];
    public static ItemStack[] sampleButtonItems = new ItemStack[5];
    public static ItemStack[] manifoldsItems = new ItemStack[2];

    public static ItemStack gameBarWatch;
    public static ItemStack[] gameBarItems = new ItemStack[7];
    public static ItemStack[] bedWarsItems = new ItemStack[5];
    public static ItemStack duelItem;

    public static void init() {
        createBdoor();
        createmurder();
        createvoter();
        createtimer();
        createColorBlocks();
        createVoteBlocks();
        createSkipBlock();
        createSabotageItems();
        createColorWires();
        createUploadStart();
        createScanLoaders();
        createUploadLoaders();
        createTrajectoryItems();
        createFuelItems();
        createDivertItems();
        createAsteroidItems();
        createDistributorItems();
        createShieldItems();
        createCardItems();
        createFilterItem();
        createGlassItems();
        createReactorItems();
        createSteeringItems();
        createEngineItems();
        createChuteItems();
        createChuteTrashItems();
        createSampleItems();
        createSamplePotionItems();
        createSampleButtonItems();
        createManifoldsItems();
        createGameBarWatch();
        createGameBarItems();
        createBedWarsItems();
        createDuelItem();
    }


    private static void createDuelItem(){
        Material mats = Material.LIGHT_BLUE_STAINED_GLASS_PANE;
        String description = ChatColor.GOLD+"";
        ItemStack item = new ItemStack(mats, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(description);
        meta.setLocalizedName("jeux_3_2");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        duelItem=item;
    }


    private static void createBedWarsItems(){
        Material[] mats = {Material.RED_BED,Material.LIGHT_BLUE_STAINED_GLASS_PANE};
        String[] descriptions = {ChatColor.GREEN+"BedWars 1v1", ChatColor.GREEN+"BedWars 2v2",ChatColor.GREEN+"BedWars 3v3",ChatColor.GREEN+"BedWars 4v4",ChatColor.AQUA+""};
        for (int i=0;i<5;i++){
            ItemStack item;
            if (i == 4) {
                item=new ItemStack(mats[1], 1);
            }else{
                item=new ItemStack(mats[0], 1);
            }
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(descriptions[i]);
            meta.setLocalizedName("jeux_2_"+i);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            bedWarsItems[i]=item;
        }
    }


    private static void createGameBarWatch(){
        ItemStack item = new ItemStack(Material.CLOCK, 1);
        ItemMeta meta = item.getItemMeta();
        List<String> lore = new ArrayList<>();
        lore.add("§7Cliquer pour accéder");
        lore.add("§7aux jeux disponibles");
        meta.setLore(lore);
        meta.setDisplayName(ChatColor.GOLD+"Barre de jeux");
        meta.setLocalizedName("jeux_9");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        gameBarWatch=item;
    }

    private static void createGameBarItems(){
        Material[] mats = {Material.BONE,Material.TNT,Material.RED_BED,Material.IRON_SWORD,Material.GOLDEN_APPLE,Material.NETHER_STAR,Material.GRASS_BLOCK};
        String[] descriptions = {ChatColor.LIGHT_PURPLE+"Among Us", ChatColor.RED+"TNTRun",ChatColor.AQUA+"BedWars",ChatColor.GOLD+"Duel",ChatColor.WHITE+"Survie",ChatColor.GREEN+"Revenir au spawn",ChatColor.WHITE+"Créatif"};

        for (int i=0;i<7;i++){
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            if (i==0){
                List<String> lore = new ArrayList<>();
                lore.add("§7Exclusivité M2C!");
                meta.setLore(lore);
            }
            meta.setDisplayName(descriptions[i]);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setLocalizedName("jeux_"+i);
            if (i==2){
                meta.setLocalizedName(meta.getLocalizedName()+"_9");
            }else if (i==3){
                meta.setLocalizedName(meta.getLocalizedName()+"_9<");
            }
            item.setItemMeta(meta);
            gameBarItems[i]=item;
        }
    }


    private static void createGlassItems(){
        Material[] materials={Material.RED_STAINED_GLASS_PANE,Material.BLUE_STAINED_GLASS_PANE,Material.GRAY_STAINED_GLASS_PANE,
                Material.BLACK_STAINED_GLASS_PANE,Material.LIME_STAINED_GLASS_PANE,Material.BROWN_STAINED_GLASS_PANE,
                Material.WHITE_STAINED_GLASS_PANE,Material.GREEN_STAINED_GLASS_PANE,Material.GLASS_PANE,Material.LIGHT_GRAY_STAINED_GLASS_PANE};
        int i=0;
        for (Material material:materials) {
            ItemStack item = new ItemStack(material, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN+"");
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            glassItems[i]=item;
            i++;
        }
    }


    private static void createColorWires(){

        ChatColor[] colors={ChatColor.RED,ChatColor.BLUE,ChatColor.YELLOW,ChatColor.LIGHT_PURPLE};
        Material[] materials={Material.RED_CARPET,Material.BLUE_CARPET,Material.YELLOW_CARPET,Material.MAGENTA_CARPET};

        for (int i = 0; i < 4; i++) {
            ItemStack item = new ItemStack(materials[i], 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(colors[i]+"Câble");
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            meta.setLocalizedName("task_07_"+String.valueOf(i));                      //task_ + numéro tâche + indice dans le tableau colorwires
            item.setItemMeta(meta);
            colorWires[i]=item;
        }
    }

    private static void createBdoor() {
        ItemStack item = new ItemStack(Material.OAK_DOOR, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Quitter");
        List<String> lore = new ArrayList<>();
        lore.add("§7Quitter le salon");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        Bdoor = item;
    }

    private static void createmurder() {
        ItemStack item = new ItemStack(Material.BONE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Assassiner");
        meta.addEnchant(Enchantment.LUCK, 1, false);
        List<String> lore = new ArrayList<>();
        lore.add("§7Il ne le verront jamais");
        lore.add("§7venir, ha ha ha...");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_ENCHANTS);
        item.setItemMeta(meta);
        murder = item;
    }

    private static void createvoter() {
        ItemStack item = new ItemStack(Material.PAPER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Voter");
        List<String> lore = new ArrayList<>();
        lore.add("§7Voter pour commencer");
        lore.add("§7la partie.");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        voter = item;
    }

    private static void createtimer() {
        ItemStack item = new ItemStack(Material.CLOCK, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Décompte d'urgence");
        List<String> lore = new ArrayList<>();
        lore.add("§7Temps restant avant de");
        lore.add("§7pouvoir déclencher une");
        lore.add("§7réunion d'urgence");
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        timer = item;
    }

    private static void createColorBlocks() {
        for (int i = 0; i < 10; i++) {
            ItemStack item = new ItemStack(colorlist[i].material, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6Changer de couleur");
            List<String> lore = new ArrayList<>();
            lore.add(colorlist[i].chat + "devenir " + colorlist[i].name);
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            colorBlocks[i] = item;
        }
    }

    private static void createVoteBlocks() {
        for (int i = 0; i < 10; i++) {
            ItemStack item = new ItemStack(colorlist[i].material, 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6Voter pour " + colorlist[i].chat + colorlist[i].name);
            List<String> lore = new ArrayList<>();
            meta.setLore(lore);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            AmongUs.voteBlocks[i] = item;
        }
    }

    private static void createSkipBlock() {
        ItemStack item = new ItemStack(Material.BARRIER, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Voter pour passer cette ronde");
        List<String> lore = new ArrayList<>();
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        skipper = item;
    }


    private static void createSabotageItems() {

        ItemStack item;
        ItemMeta meta;
        List<String> lore;

        item = new ItemStack(Material.REDSTONE_TORCH, 1);
        meta = item.getItemMeta();
        meta.setDisplayName("§6Saboter les lumières");
        lore = new ArrayList<>();
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        sabotageItems[0] = item;

        item = new ItemStack(Material.HOPPER, 1);
        meta = item.getItemMeta();
        meta.setDisplayName("§6Saboter les communications");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        sabotageItems[1] = item;

        item = new ItemStack(Material.NETHER_STAR, 1);
        meta = item.getItemMeta();
        meta.setDisplayName("§6Saboter le réacteur");
        lore = new ArrayList<>();
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        sabotageItems[2] = item;

        item = new ItemStack(Material.SOUL_CAMPFIRE, 1);
        meta = item.getItemMeta();
        meta.setDisplayName("§6Saboter le réservoir d'oxygène");
        lore = new ArrayList<>();
        meta.setLore(lore);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        sabotageItems[3] = item;
    }

    private static void createUploadStart(){
        ItemStack item = new ItemStack(Material.SCUTE, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§6Démarrer le transfer");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLocalizedName("task_15");
        item.setItemMeta(meta);
        uploadstart = item;
    }

    private static void createScanLoaders() {
        Material[] mats = {Material.BLACK_WOOL,Material.WHITE_WOOL,Material.GREEN_WOOL};
        for (int i = 0; i < 4; i++) {
            ItemStack item;
            if (i==3){
                item = new ItemStack(Material.BARRIER, 1);
            }else{item = new ItemStack(mats[i], 1);}
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD+"Scan en cours...");
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            scanloaders[i]=item;
        }
    }

    private static void createUploadLoaders() {
        Material[] mats = {Material.RED_BANNER,Material.LIME_BANNER,Material.RED_BANNER};
        for (int i = 0; i < 3; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            if (i==2){
                meta.setDisplayName(ChatColor.GOLD+"En attente de transfert.");
            }else{meta.setDisplayName(ChatColor.GOLD+"Transfert en cours...");}
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            uploadloaders[i]=item;
        }
    }

    private static void createTrajectoryItems() {
        int count;
        for (int i = 0; i < 5; i++) {
            count=i+1;
            ItemStack item = new ItemStack(Material.BEACON, count);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD+"Destination "+(count));
            meta.setLocalizedName("task_02_"+i);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            trajectoryItems[i]=item;
        }
        ItemStack item = new ItemStack(Material.BIRCH_BOAT, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+"Vaisseau");
        meta.setLocalizedName("task_02_5");
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        trajectorycursor=item;
    }

    private static void createFuelItems(){
        Material[] mats = {Material.RED_STAINED_GLASS_PANE,Material.YELLOW_STAINED_GLASS_PANE,Material.ORANGE_STAINED_GLASS_PANE};
        String[] descriptions = {ChatColor.RED+"À remplir",ChatColor.GREEN+"Rempli",ChatColor.GOLD+"Cliquer pour remplir"};
        for (int i = 0; i < 3; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(descriptions[i]);
            if (i==2){
                meta.setLocalizedName("task_08");
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            fuelItems[i]=item;
        }
    }

    private static void createDivertItems(){
        Material[] mats = {Material.REPEATER,Material.COMPARATOR,Material.END_CRYSTAL,Material.NETHER_SPROUTS};
        String[] descriptions = {ChatColor.GREEN+"Circuit fermé",ChatColor.RED+"Circuit ouvert",ChatColor.GOLD+"Source",ChatColor.GOLD+"Câble"};
        for (int i = 0; i < 4; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(descriptions[i]);
            if (i==1){
                meta.setLocalizedName("task_05");
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            divertItems[i]=item;
        }
    }

    private static void createAsteroidItems(){
        Material[] mats = {Material.LIME_STAINED_GLASS_PANE, Material.CHARCOAL};
        String[] descriptions = {ChatColor.GREEN+" ",ChatColor.LIGHT_PURPLE+"Astéroïde"};
        for (int i = 0; i < 2; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(descriptions[i]);
            if (i==1){
                meta.setLocalizedName("task_04");
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            asteroidItems[i]=item;
        }
    }

    private static void createDistributorItems(){
        Material[] mats = {Material.ORANGE_STAINED_GLASS_PANE, Material.YELLOW_STAINED_GLASS_PANE,Material.GREEN_STAINED_GLASS_PANE,
                Material.LIME_STAINED_GLASS_PANE,Material.RED_STAINED_GLASS_PANE,Material.PINK_STAINED_GLASS_PANE,Material.SCUTE};
        for (int i = 0; i < 7; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GREEN+"");
            switch (i){
                case 6:
                    meta.setLocalizedName("task_00");
                    meta.setDisplayName(ChatColor.GOLD+"Calibrer");
                    break;
                case 1:
                    meta.setLocalizedName("1");
                    break;
                case 3:
                    meta.setLocalizedName("2");
                    break;
                case 5:
                    meta.setLocalizedName("3");
                    break;
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            distributorItems[i]=item;
        }
    }

    private static void createShieldItems(){
        Material[] mats = {Material.RED_BANNER, Material.WHITE_BANNER, Material.BLUE_STAINED_GLASS_PANE};
        String[] descriptions = {ChatColor.RED+"Désactivé",ChatColor.GREEN+"ACTIVÉ",ChatColor.GREEN+""};
        for (int i = 0; i < 3; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(descriptions[i]);
            if (i==0 || i==1){
                meta.setLocalizedName("task_10_"+i);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            shieldItems[i]=item;
        }
    }

    private static void createCardItems(){
        Material[] mats = {Material.WHITE_BANNER,Material.END_CRYSTAL,Material.IRON_BARS,Material.GLASS,Material.BLACK_STAINED_GLASS_PANE};
        String[] descriptions = {ChatColor.GOLD+"Carte",ChatColor.LIGHT_PURPLE+"Lecteur de carte",ChatColor.GREEN+""};
        for (int i = 0; i < 5; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            if (i==0){
                meta.setDisplayName(descriptions[i]);
                meta.setLocalizedName("task_13");
            }else if(i==1){
                meta.setDisplayName(descriptions[i]);
            }else{
                meta.setDisplayName(descriptions[2]);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            cardItems[i]=item;
        }
    }

    private static void createFilterItem(){
        Material[] mats = {Material.KELP, Material.CAULDRON, Material.LIGHT_BLUE_STAINED_GLASS_PANE,Material.GLASS};
        String[] descriptions = {ChatColor.GOLD+"Feuille",ChatColor.LIGHT_PURPLE+"Poubelle",ChatColor.GREEN+""};
        for (int i = 0; i < 4; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            if (i<2){
                meta.setDisplayName(descriptions[i]);
                meta.setLocalizedName("task_03_"+i);
            }else{
                if (i==2){
                    meta.setLocalizedName("task_03_"+i);
                }
                meta.setDisplayName(descriptions[2]);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            filterItems[i]=item;
        }
    }

    private static void createReactorItems(){
        Material[] mats = {Material.WHITE_STAINED_GLASS_PANE,Material.LIME_STAINED_GLASS_PANE};
        for (int i = 0; i < 2; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(ChatColor.GOLD+"");
            meta.setLocalizedName("task_12_"+i);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            reactorItems[i]=item;
        }
    }

    private static void createSteeringItems(){
        Material[] mats = {Material.NETHER_STAR,Material.HEART_OF_THE_SEA,Material.SNOWBALL,Material.WHITE_STAINED_GLASS_PANE,Material.LIGHT_BLUE_STAINED_GLASS_PANE};
        String[] descriptions = {ChatColor.GOLD+"Direction",ChatColor.LIGHT_PURPLE+"Destination"};
        for (int i = 0; i < 5; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            if (i<2){
                meta.setDisplayName(descriptions[i]);
            }else{
                meta.setDisplayName(ChatColor.GREEN+"");
            }
            meta.setLocalizedName("task_11_"+i);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            steeringItems[i]=item;
        }
    }

    private static void createEngineItems(){
        Material[] mats = {Material.NETHER_STAR,Material.REDSTONE_BLOCK,Material.RED_STAINED_GLASS_PANE,Material.LIGHT_GRAY_STAINED_GLASS_PANE};
        String[] descriptions = {ChatColor.GOLD+"Curseur",ChatColor.LIGHT_PURPLE+"Objectif",ChatColor.LIGHT_PURPLE+"Enlignement du moteur"};
        for (int i = 0; i < 4; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            if (i==3){
                meta.setDisplayName(ChatColor.GREEN+"");
                meta.setLocalizedName("task_01_"+i);
            }else{
                meta.setDisplayName(descriptions[i]);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            engineItems[i]=item;
        }
    }

    public static void createChuteItems(){
        Material[] mats = {Material.SCUTE,Material.BEETROOT_SOUP};
        String[] descriptions = {ChatColor.GOLD+"Ouvrir la chute",ChatColor.GOLD+"Fermer la chute"};
        for (int i = 0; i < 2; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(descriptions[i]);
            meta.setLocalizedName("task_06_"+i);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            chuteItems[i]=item;
        }
    }

    public static void createChuteTrashItems(){
        Material[] mats = {Material.DEAD_BUSH,Material.MAP,Material.KELP,Material.ROTTEN_FLESH,Material.DRIED_KELP};
        String[] descriptions = {ChatColor.LIGHT_PURPLE+"Déchets"};
        for (int i = 0; i < 5; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(descriptions[0]);
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            chuteTrashItems[i]=item;
        }
    }

    public static void createSampleItems(){
        Material[] mats = {Material.SCUTE,Material.BOOK,Material.BOOK,Material.CLOCK,Material.HOPPER,Material.SUNFLOWER,Material.BEETROOT_SOUP,Material.SCUTE,Material.BOOK};
        String[] descriptions = {ChatColor.GOLD+"Démarrer la séquence",ChatColor.GOLD+"Sélectionner l'intrus",ChatColor.GOLD+"Remplissage...",ChatColor.GOLD+"Revenez plus tard."};
        for (int i = 0; i < 9; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            if (i<3){
                meta.setDisplayName(descriptions[i]);
            }else{
                if (i==8){
                    meta.setDisplayName(descriptions[3]);
                }else{
                    meta.setDisplayName(ChatColor.GREEN+"");
                }
            }
            if (i==0) {
                meta.setLocalizedName("task_09_" + i);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            sampleItems[i]=item;
        }
    }

    public static void createSamplePotionItems(){

        Material[] mats = {Material.POTION,Material.POTION,Material.GLASS_BOTTLE,};
        Color[] colors = {Color.BLUE,Color.FUCHSIA};
        for (int i = 0; i < 3; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            if (i<2){
                PotionMeta potionMeta = (PotionMeta) meta;
                ((PotionMeta) meta).setColor(colors[i]);
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
                meta.setDisplayName(ChatColor.GREEN+"");
                item.setItemMeta(potionMeta);
            }else{
                meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
                meta.setDisplayName(ChatColor.GREEN+"");
                item.setItemMeta(meta);
            }
            samplePotionItems[i]=item;
        }
    }

    public static void createSampleButtonItems(){
        Material mats=Material.SCUTE;
        String descriptions=ChatColor.LIGHT_PURPLE+"Échantillion ";
        for (int i = 0; i < 5; i++) {
            ItemStack item = new ItemStack(mats, i+1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(descriptions+(i+1));
            meta.setLocalizedName("task_09_1");
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            sampleButtonItems[i]=item;
        }
    }

    public static void createManifoldsItems(){
        Material[] mats={Material.BLUE_STAINED_GLASS_PANE,Material.LIME_STAINED_GLASS_PANE};
        String description=ChatColor.GOLD+"";
        for (int i = 0; i < 2; i++) {
            ItemStack item = new ItemStack(mats[i], 1);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(description);
            if (i==0){
                meta.setLocalizedName("task_16_"+i);
            }
            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            item.setItemMeta(meta);
            manifoldsItems[i]=item;
        }
    }

    public static ItemStack createDuelNameItem(Player player,boolean isBusy){
        ItemStack item = new ItemStack(Material.PLAYER_HEAD, 1);
        SkullMeta meta = (SkullMeta) item.getItemMeta();
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(player.getUniqueId()));
        meta.setDisplayName(ChatColor.GOLD+player.getName());
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        if (isBusy){
            meta.setLocalizedName("jeux_3_1");
            List<String> lore = new ArrayList<>();
            lore.add("§cJoueur présentement");
            lore.add("§coccupé");
            meta.setLore(lore);
        }else{
            meta.setLocalizedName("jeux_3_0");
        }
        item.setItemMeta(meta);
        return item;
    }
}