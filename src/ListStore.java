import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.ArrayList;

public class ListStore {

    private File storageFile;
    private ArrayList<String> values;

    public ListStore (File file){
        this.storageFile=file;
        this.values=new ArrayList<String>();

        if (this.storageFile.exists() == false){
            try{
                this.storageFile.createNewFile();
            } catch (IOException e){
                e.printStackTrace();
            }
        }
    }

    public void load(){
        try{
            DataInputStream input = new DataInputStream(new FileInputStream(this.storageFile));
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));

            String line, value;

            while ((line=reader.readLine()) != null){
                if (this.contains(line) == false){
                    this.values.add(line);
                }
            }
            reader.close();
            input.close();
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public void save(){
        try{
            FileWriter stream = new FileWriter(this.storageFile);
            BufferedWriter out = new BufferedWriter(stream);
            for (String value:this.values){
                out.write(value);
                out.newLine();
            }
            out.close();
            stream.close();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public boolean contains(String value){
        return this.values.contains(value);
    }

    public void add(String value){
        if (this.contains(value)==false){
            this.values.add(value);
        }
    }

    public void remove(String value){
        this.values.remove(value);
    }

    public ArrayList<String> getValues(){
        return this.values;
    }

    public String getValueWithNamePrefix(String playerName){
        for (String value:this.values){
            if (value.startsWith(playerName)){
                return value;
            }
        }
        return null;
    }

    public void addValueWithNameAndLoc(String playerName, Location location){
        String assembledValue = playerName+"_"+Math.round(location.getX())+"_"+Math.round(location.getY())+"_"
                +Math.round(location.getZ())+"_"+getIntergerWorldType(location)+"_";
        this.add(assembledValue);
    }

    public int getIntergerWorldType(Location location){
        String wName=location.getWorld().getName();
        if (wName.endsWith("end")){
            return 2;
        }else if (wName.endsWith("nether")){
            return 1;
        }else{
            return 0;
        }
    }

    public static Location decodePlayerValue(String pName, String listValue){
        int cpt=0;
        int x=0,y=0,z=0;
        int startIndex=pName.length()+1;
        int worldInteger=0;
        for (int i=startIndex;i<listValue.length();i++){
            if (listValue.charAt(i)=='_'){
                int current=Integer.parseInt(listValue.substring(startIndex,i));
                if (cpt==0){
                    x=current;
                }else if (cpt==1){
                    y=current;
                }else if (cpt==2){
                    z=current;
                }else if (cpt==3){
                    worldInteger=current;
                }
                cpt++;
                startIndex=i+1;
            }
        }
        World world=getWorldFromEntryInteger(worldInteger);
        Location location = new Location(world, x,y,z);
        return location;
    }

    public static World getWorldFromEntryInteger(int worldInteger){
        if (worldInteger==2){
            return Bukkit.getWorld(AmongUs.NOMMONDESURVIE+"_the_end");
        }else if (worldInteger==1){
            return Bukkit.getWorld(AmongUs.NOMMONDESURVIE+"_nether");
        }else{
            return Bukkit.getWorld(AmongUs.NOMMONDESURVIE);
        }
    }


    public void updatePlayerEntry(Player player,Location newLocation){
        String value=this.getValueWithNamePrefix(player.getName());
        if (value!=null){
            this.remove(value);
        }
        this.addValueWithNameAndLoc(player.getName(),newLocation);
    }
}
