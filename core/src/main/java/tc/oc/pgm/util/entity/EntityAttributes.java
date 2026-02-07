package tc.oc.pgm.util.entity;

import org.bukkit.DyeColor;
import org.bukkit.entity.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static tc.oc.pgm.util.nms.NMSHacks.NMS_HACKS;

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
            (creeper, charged) -> ((Creeper) creeper).setPowered((boolean) charged),
            false
        );

        addAttributeFunction(
            attributeFunctions,
            "noGravity", Boolean::parseBoolean,
            Entity.class,
            (entity, noGravity) -> NMS_HACKS.setNoGravity(entity, (boolean) noGravity),
            false
        );
        addAttributeFunction(
            attributeFunctions,
            "autoExpire", Boolean::parseBoolean,
            FallingBlock.class,
            (entity, autoExpire) -> NMS_HACKS.setFallingBlockAutoExpire(entity, (boolean) autoExpire),
            true
        );

        // Common Entity attributes

        // Ageable
        addAttributeFunction(
            attributeFunctions,
            "baby", Boolean::parseBoolean,
            Ageable.class,
            (entity, baby) -> { if ((boolean) baby) ((Ageable) entity).setBaby(); else ((Ageable) entity).setAdult(); },
            false
        );

        // Slime / MagmaCube
        addAttributeFunction(
            attributeFunctions,
            "slimeSize", Integer::parseInt,
            Slime.class,
            (entity, size) -> ((Slime) entity).setSize((int) size),
            1
        );

        // Sheep
        addAttributeFunction(
            attributeFunctions,
            "sheepColor", (s) -> DyeColor.valueOf(s.toUpperCase()),
            Sheep.class,
            (entity, color) -> ((Sheep) entity).setColor((DyeColor) color),
            DyeColor.WHITE
        );
        addAttributeFunction(
            attributeFunctions,
            "sheared", Boolean::parseBoolean,
            Sheep.class,
            (entity, sheared) -> ((Sheep) entity).setSheared((boolean) sheared),
            false
        );

        // Wolf
        addAttributeFunction(
            attributeFunctions,
            "angry", Boolean::parseBoolean,
            Wolf.class,
            (entity, angry) -> ((Wolf) entity).setAngry((boolean) angry),
            false
        );
        addAttributeFunction(
            attributeFunctions,
            "collarColor", (s) -> DyeColor.valueOf(s.toUpperCase()),
            Wolf.class,
            (entity, color) -> ((Wolf) entity).setCollarColor((DyeColor) color),
            DyeColor.RED
        );

        // Rabbit
        addAttributeFunction(
            attributeFunctions,
            "rabbitType", (s) -> Rabbit.Type.valueOf(s.toUpperCase()),
            Rabbit.class,
            (entity, type) -> ((Rabbit) entity).setRabbitType((Rabbit.Type) type),
            Rabbit.Type.BROWN
        );

        // Horse
        addAttributeFunction(
            attributeFunctions,
            "horseColor", (s) -> Horse.Color.valueOf(s.toUpperCase()),
            Horse.class,
            (entity, color) -> ((Horse) entity).setColor((Horse.Color) color),
            Horse.Color.BROWN
        );
        addAttributeFunction(
            attributeFunctions,
            "horseStyle", (s) -> Horse.Style.valueOf(s.toUpperCase()),
            Horse.class,
            (entity, style) -> ((Horse) entity).setStyle((Horse.Style) style),
            Horse.Style.NONE
        );

        attributeFunctions.putAll(NMS_HACKS.getPlatformEntityAttributes().getAttributeFunctions());

        INSTANCE = new EntityAttributes(attributeFunctions);
    }

    private static void addAttributeFunction(
        final Map<String, EntityAttributeDescription> attributeFunctions,
        final String key, final Function<String, Object> parser,
        final Class<? extends Entity> classRequired,
        final BiConsumer<Entity, Object> function,
        final Object defaultValue
    ) {
        if (attributeFunctions.containsKey(key)) {
            throw new RuntimeException("Duplicate entity attribute '" + key + "', this must be unique");
        }
        attributeFunctions.put(key, new EntityAttributeDescription(classRequired, parser, function, defaultValue));
    }
}
