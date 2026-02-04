package tc.oc.pgm.projectile.definition;

import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.features.SelfIdentifyingFeatureDefinition;

public class IdentifiedFeatureContainer extends SelfIdentifyingFeatureDefinition {
    public final Object feature;

    public IdentifiedFeatureContainer(@Nullable String id, Object feature) {
        super(id);
        this.feature = feature;
    }
}