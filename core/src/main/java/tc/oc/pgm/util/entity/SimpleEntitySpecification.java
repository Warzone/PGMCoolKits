package tc.oc.pgm.util.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.function.BiConsumer;

public record SimpleEntitySpecification(
    Class<? extends Entity> entityType, List<AttributeApplication> applications,
    boolean showNametag
) implements EntitySpecification {
    public record AttributeApplication(Object data, BiConsumer<Entity, Object> function) {}

    public Entity spawn(final World world, final Location location) {
        final Entity entity = world.spawn(location, entityType);
        for (final AttributeApplication application : applications) {
            application.function.accept(entity, application.data);
        }
        return entity;
    }

    @Override
    public boolean showNametag() {
        return showNametag;
    }
}