package land.esta.plugin.ssb.support.estapractice;

import lombok.Getter;
import lombok.Setter;
import land.esta.plugin.ssb.support.PluginSupport;

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


        return super.enable();
    }
}
