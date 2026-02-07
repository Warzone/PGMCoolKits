package tc.oc.pgm.util.entity;

import java.util.function.BiConsumer;
import java.util.function.Function;

import org.bukkit.entity.Entity;


public record EntityAttributeDescription(
    Class<? extends Entity> classRequired,
    Function<String, Object> parser,
    BiConsumer<Entity, Object> function,
    Object defaultValue
) { }
