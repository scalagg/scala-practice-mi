package land.esta.plugin.ssb.features;

import land.esta.plugin.ssb.features.imple.*;
import lombok.Getter;
import land.esta.plugin.ssb.features.imple.*;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FeatureManager {
    final List<Feature> features;
    private final StrafeFeature strafeFeature;
    private final HealthPotionThrowing healthPotionThrowing;
    private final BuildingFeature buildingFeature;

    public FeatureManager() {
        features = new ArrayList<>();

        addFeature(new EatingAndDrinking());
        addFeature(healthPotionThrowing = new HealthPotionThrowing());
        addFeature(new ArmorEquipFeature());
        addFeature(new Pearling());
        addFeature(strafeFeature = new StrafeFeature());
        addFeature(new Soups());
        addFeature(new WTapFeature());
        addFeature(new PreGap());
        addFeature(new RodFeature());
        addFeature(new EdgeFeature());
        addFeature(buildingFeature = new BuildingFeature());
    }

    public void addFeature(Feature feature) {
        features.add(feature);
    }
}
