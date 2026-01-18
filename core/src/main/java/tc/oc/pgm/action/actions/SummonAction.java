package tc.oc.pgm.action.actions;

import org.bukkit.entity.EntityType;
import tc.oc.pgm.api.location.MatchLocation;

public class SummonAction extends AbstractAction<MatchLocation> {
    private final EntityType entityType;

    public SummonAction(EntityType entityType) {
        super(MatchLocation.class);
        this.entityType = entityType;
    }

    @Override
    public void trigger(MatchLocation matchLocation) {
        matchLocation.getLocation().getWorld().spawnEntity(matchLocation.getLocation(), entityType);
    }
}