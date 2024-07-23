package land.esta.plugin.ssb.utils.config;

public class ConfigManager {

    //This class should probably not be in the utility package
    //,but since I am not going to create a package just for him, he will be here
    public static CustomConfig data;
    public static CustomConfig settings;

    public static void init() {
        data = new CustomConfig("data");
        settings = new CustomConfig("settings");
    }
}
