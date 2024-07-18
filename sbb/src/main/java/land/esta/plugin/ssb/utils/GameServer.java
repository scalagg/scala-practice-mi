package land.esta.plugin.ssb.utils;

import gg.scala.commons.agnostic.sync.ServerSync;
import lombok.experimental.UtilityClass;

/**
 * @author GrowlyX
 * @since 7/18/2024
 */
@UtilityClass
public class GameServer {

    public static boolean is() {
        return ServerSync.INSTANCE.getLocal().getGroups().contains("mipgame");
    }
}
