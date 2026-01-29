package tc.oc.pgm.util.entity;

import org.bukkit.Material;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import tc.oc.pgm.util.material.MaterialData;

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
            (creeper, charged) -> ((Creeper) creeper).setPowered((boolean) charged)
        );
        addAttributeFunction(
            attributeFunctions,
            "blockType", (s) -> Material.valueOf(s.replace(" ", "_").toUpperCase()),
            FallingBlock.class,
            (fallingBlock, blockMaterial) ->
                NMS_HACKS.setFallingBlockType(fallingBlock, MaterialData.block((Material) blockMaterial))
        );
        addAttributeFunction(
            attributeFunctions,
            "noGravity", Boolean::parseBoolean,
            Entity.class,
            (entity, noGravity) -> NMS_HACKS.setNoGravity(entity, (boolean) noGravity)
        );

        INSTANCE = new EntityAttributes(attributeFunctions);
    }

    private static void addAttributeFunction(
        final Map<String, EntityAttributeDescription> attributeFunctions,
        final String key, final Function<String, Object> parser,
        final Class<? extends Entity> classRequired,
        final BiConsumer<Entity, Object> function
    ) {
        if (attributeFunctions.containsKey(key)) {
            throw new RuntimeException("Duplicate entity attribute '" + key + "', this must be unique");
        }
        attributeFunctions.put(key, new EntityAttributeDescription(classRequired, parser, function));
    }

    public record EntityAttributeDescription(
        Class<? extends Entity> classRequired,
        Function<String, Object> parser,
        BiConsumer<Entity, Object> function
    ) { }
}
