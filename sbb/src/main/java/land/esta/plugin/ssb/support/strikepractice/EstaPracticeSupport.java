package land.esta.plugin.ssb.support.strikepractice;

import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.support.PluginSupport;

@Getter
@Setter
public class EstaPracticeSupport extends PluginSupport {

    public EstaPracticeSupport(String name) {
        super(name);
    }

    private SPEvents events;
    private SPManager manager;

    @Override
    public PluginSupport enable() {
        this.manager = new SPManager(this);
        this.events = new SPEvents(this);
        return super.enable();
    }
}
