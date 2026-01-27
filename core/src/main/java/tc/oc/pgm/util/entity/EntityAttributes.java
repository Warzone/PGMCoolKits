package tc.oc.pgm.util.entity;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class EntityAttributes {
    public static final EntityAttributes INSTANCE;
    public final Map<String, EntityAttributeDescription> attributeData;

    private EntityAttributes(Map<String, EntityAttributeDescription> attributeData) {
        this.attributeData = Collections.unmodifiableMap(attributeData);
    }

    static {
        final Map<String, EntityAttributeDescription> attributeFunctions = new HashMap<>();
        addAttributeFunction(
            attributeFunctions,
            "charged", Boolean::parseBoolean,
            Creeper.class,
            (creeper, charged) -> ((Creeper) creeper).setPowered((boolean) charged)
        );

        INSTANCE = new EntityAttributes(attributeFunctions);
    }

    private static void addAttributeFunction(
        final Map<String, EntityAttributeDescription> attributeFunctions,
        final String key, final Function<String, Object> parser,
        final Class<? extends Entity> classRequired,
        final BiConsumer<Entity, Object> function
    ) {
        attributeFunctions.put(key, new EntityAttributeDescription(classRequired, parser, function));
    }

    public record EntityAttributeDescription(
        Class<? extends Entity> classRequired,
        Function<String, Object> parser,
        BiConsumer<Entity, Object> function
    ) { }
}
