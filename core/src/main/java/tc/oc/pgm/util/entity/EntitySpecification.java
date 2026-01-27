package tc.oc.pgm.util.entity;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

import java.util.List;
import java.util.function.BiConsumer;

public record EntitySpecification(
    Class<? extends LivingEntity> entityType, List<AttributeApplication> applications
) {
    public record AttributeApplication(Object data, BiConsumer<Entity, Object> function) {}

    public LivingEntity spawn(final World world, final Location location) {
        final LivingEntity entity = world.spawn(location, entityType);
        for (final AttributeApplication application : applications) {
            application.function.accept(entity, application.data);
        }
        return entity;
    }
}
