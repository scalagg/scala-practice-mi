package land.esta.plugin.ssb.bots.setting;

import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.utils.MathUtils;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class SettingObject {

    public String name;
    private ItemStack item;
    private Boolean possibleBoolean;
    private Double possibleDouble;
    private Integer possibleInt;
    private double updateBy = 1;
    private double randomizer = 0;
    private boolean hasPerm;
    private double min = 0;
    private double max = 0;
    
    public Object getObject() {
        if (possibleBoolean != null) return possibleBoolean;
        if (possibleDouble != null) return possibleDouble;
        
        return possibleInt;
    }

    public void setObject(Object object) {
        if (object instanceof Boolean) {
            possibleBoolean = (Boolean) object;
        } else if (object instanceof Integer) {
            possibleInt = (Integer) object;
        } else {
            possibleDouble = (Double) object;
        }
    }


    public void setObjectUpdate(Object object) {
        if (object instanceof Boolean) {
            possibleBoolean = (Boolean) object;
        } else if (object instanceof Integer) {
            possibleInt = (Integer) object;
        } else {
            possibleDouble = (Double) object;
        }
    }

    public void setRandomizerUpdate(double randomizer) {
        this.randomizer = randomizer;
    }

    public void setMinUpdate(double value) {
        this.min = value;
    }

    public void setMaxUpdate(double value) {
        this.max = value;
    }

    public List<String> getDescription() {
        List<String> result = new ArrayList<>();
        if (getObject() instanceof Boolean) {
            Boolean bool = (Boolean) getObject();
            result.add("");
            result.add(bool ? "&a&l│ &fOn" : "&c│ &fOn");
            result.add(bool ? "&c│ &fOff" : "&a&l│ &fOff");
            result.add("");
            result.add("&aClick to change!");
        } else {
            result.add("&c&lSettings:");
            if (getObject() instanceof Double) result.add("&c ● &f" + name + ": &c" + MathUtils.stringOfDouble((double) getObject(),3));
            if (getObject() instanceof Integer) result.add("&c ● &f" + name + ": &c" + getObject());
            if (getObject() instanceof Double) result.add("&c ● &fRandomizer" + ": &c" + MathUtils.stringOfDouble(randomizer,3) + " &f(&c"+ (MathUtils.stringOfDouble((double) getObject() - randomizer,3)) +" &f◀▶ &c"+ (MathUtils.stringOfDouble((double) getObject() + randomizer,3)) +"&f)");
            if (getObject() instanceof Integer) result.add("&c ● &fRandomizer" + ": &c" + randomizer + " &f(&c"+ ((int) getObject() - randomizer) +" &f◀▶ &c"+ ((int) getObject() + randomizer) +"&f)");

            result.add("");
            result.add("&c&lMain:");
            result.add(" &a▲ &fLeft Click");
            result.add(" &c▼ &fRight Click");
            result.add("");
            result.add("&c&lRandomization:");
            result.add(" &a▲ &fShift Left Click");
            result.add(" &c▼ &fShift Right Click");
        }

        return result;
    }

    public double getDoubleValue() {
        if (getObject() instanceof Integer) return (int) getObject();
        return (double) getObject() + MathUtils.getRandom(-randomizer,randomizer);
    }

    public double getOriginalDouble() {
        if (getObject() instanceof Integer) return (int) getObject();
        return (double) getObject();
    }

    public double getRandomizerInt() {
        return (int) randomizer;
    }

    public int getIntValue() {
        return (int) getObject() + (int) Math.round(MathUtils.getRandom(-randomizer,randomizer));
    }

    public int getOriginalIntValue() {
        return (int) getObject();
    }

    public boolean isBooleanValue() {
        return (boolean) getObject();
    }

    public SettingObject(String name, ItemStack item,Object object) {
        this.name = name;
        this.item = item;

        if (object instanceof Boolean) {
            possibleBoolean = (Boolean) object;
        } else if (object instanceof Integer) {
            possibleInt = (Integer) object;
        } else {
            possibleDouble = (Double) object;
        }
    }

    public SettingObject(String name, ItemStack item,double doubleValue, double updateBy) {
        this.name = name;
        this.item = item;
        this.possibleDouble = doubleValue;
        this.updateBy = updateBy;
    }

    public SettingObject(String name, ItemStack item,int intValue, double updateBy) {
        this.name = name;
        this.item = item;
        this.possibleInt = intValue;
        this.updateBy = updateBy;
    }

    public SettingObject(String name, ItemStack item,boolean booleanValue) {
        this.name = name;
        this.item = item;
        this.possibleBoolean = booleanValue;
    }

    public SettingObject(String name,Material material,boolean booleanValue) {
        this(name,new ItemStack(material,1),booleanValue);
    }

    public SettingObject(String name,Material material,int intValue) {
        this(name,new ItemStack(material,1),intValue);
    }

    public SettingObject(String name,Material material,double doubleValue) {
        this(name,new ItemStack(material,1),doubleValue);
    }

    public SettingObject(String name,Material material,int intValue,double updateBy) {
        this(name,new ItemStack(material,1),intValue,updateBy);
    }

    public SettingObject(String name,Material material,double doubleValue,double updateBy) {
        this(name,new ItemStack(material,1),doubleValue,updateBy);
    }

    public String toDatabaseString() {
        String[] words = name.replaceAll("\\([^)]*\\)", "").split(" ");
        String databaseStr = words[0].toLowerCase();
        for (int i = 1; i < words.length; i++) {
            databaseStr += words[i].substring(0, 1) + words[i].substring(1).toLowerCase();
        }
        return databaseStr;
    }
}
