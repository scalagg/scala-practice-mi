package land.esta.plugin.ssb.support.estapractice;

import gg.tropic.practice.games.GameImpl;
import gg.tropic.practice.games.robot.RobotGameLifecycle;
import gg.tropic.practice.games.robot.RobotInstance;
import gg.tropic.practice.map.metadata.anonymous.BukkitExtKt;
import kotlin.jvm.functions.Function1;
import land.esta.plugin.ssb.bot.impl.EstaPracticeRobotInstanceImpl;
import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.support.PluginSupport;

import java.util.concurrent.CompletableFuture;

@Getter
@Setter
public class EstaPracticeSupport extends PluginSupport {

    public EstaPracticeSupport(String name) {
        super(name);
    }

    private EstaPracticeEvents events;
    private EstaPracticeManager manager;

    @Override
    public PluginSupport enable() {
        this.manager = new EstaPracticeManager(this);
        this.events = new EstaPracticeEvents(this);

        RobotGameLifecycle.INSTANCE.setCreateNewRobotInstance(game -> {
            // get from redis bot type
            return CompletableFuture.completedFuture(
                    new EstaPracticeRobotInstanceImpl(game, manager)
            );
        });
        return super.enable();
    }
}
