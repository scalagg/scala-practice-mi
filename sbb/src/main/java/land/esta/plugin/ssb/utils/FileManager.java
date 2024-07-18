package land.esta.plugin.ssb.utils;

import land.esta.plugin.ssb.utils.config.CustomConfig;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class FileManager {

    public static void arraysSetVal(JavaPlugin main, ArrayList list, String path1, String path2, Object value, Boolean defaults) {
        list.forEach(item -> {
            if (defaults) {
                setDefault(main,path1 + "." + item + "." + path2,value);
            } else {
                main.getConfig().set(path1 + "." + item + "." + path2,value);
            }
        });
    }


    /* Great example of what it does */
    /*
    public static void example() {
        ArrayList<String> list = new ArrayList();
        list.add("cat1");
        list.add("cat2");
        arraysSetVal(SmartBotsBackend.getInstance(),list,"cats","settings.health","10",true);
    }
     */

    public static void setDefault(JavaPlugin main,String path,Object value) {
        if (!main.getConfig().isSet(path)) {
            main.getConfig().set(path,value);
            main.saveConfig();
        }
    }

    public static double getOrDefault(JavaPlugin main,String path,Double def) {
        if (!main.getConfig().isSet(path)) {
            main.getConfig().set(path,def);
            main.saveConfig();
            return def;
        } else {
            return main.getConfig().getDouble(path);
        }
    }

    public static <V> V getOrDefault(CustomConfig config, String path, V def) {
        if (!config.getConfig().isSet(path)) {
            config.getConfig().set(path, def);
            config.saveConfig();
            return def;
        } else {
            return (V) config.getConfig().get(path);
        }
    }

    public static void update(CustomConfig config, String path, Object value) {
        config.getConfig().set(path, value);
        config.saveConfig();
    }

    public static boolean getOrDefault(JavaPlugin main,String path,boolean def) {
        if (!main.getConfig().isSet(path)) {
            main.getConfig().set(path,def);
            main.saveConfig();
            return def;
        } else {
            return main.getConfig().getBoolean(path);
        }
    }

    public static Object getOrDefault(JavaPlugin main,String path,Object def) {
        if (!main.getConfig().isSet(path)) {
            main.getConfig().set(path,def);
            main.saveConfig();
            return def;
        } else {
            return main.getConfig().get(path);
        }
    }

    public static <V> V getOrDefaultSimple(JavaPlugin main, String path, V def) {
        if (!main.getConfig().isSet(path)) {
            main.getConfig().set(path,def);
            main.saveConfig();
            return def;
        } else {
            return (V) main.getConfig().get(path);
        }
    }

    public static List<Map<String,Object>> getOrDefaultSection(JavaPlugin main, String path, Object def) {
        return null;
    }

    public static List<?> getOrDefault(JavaPlugin main,String path,ArrayList<?> def) {
        if (!main.getConfig().isSet(path)) {
            main.getConfig().set(path,def);
            main.saveConfig();
            return def;
        } else {
            return main.getConfig().getList(path);
        }
    }
}
