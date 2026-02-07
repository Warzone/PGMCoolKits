package tc.oc.pgm.platform.modern.impl;

import io.papermc.paper.registry.RegistryAccess;
import io.papermc.paper.registry.RegistryKey;
import tc.oc.pgm.util.entity.EntityAttributeDescription;
import tc.oc.pgm.util.entity.PlatformEntityAttributes;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Function;

import static tc.oc.pgm.util.nms.NMSHacks.NMS_HACKS;

public class ModernEntityAttributes implements PlatformEntityAttributes {
    public static final ModernEntityAttributes INSTANCE;
    public final Map<String, EntityAttributeDescription> attributeData;

    private ModernEntityAttributes(Map<String, EntityAttributeDescription> attributeData) {
        this.attributeData = Collections.unmodifiableMap(attributeData);
    }

    static {
        final Map<String, EntityAttributeDescription> attributeFunctions = new HashMap<>();

        addAttributeFunction(
            attributeFunctions,
            "block", (s) -> Material.valueOf(s.replace(" ", "_").toUpperCase()),
            BlockDisplay.class,
            (blockDisplay, blockMaterial) ->
            ((BlockDisplay) blockDisplay).setBlock(Bukkit.createBlockData((Material) blockMaterial)),
            Material.STONE
        );
        addAttributeFunction(
            attributeFunctions,
            "teleportation-duration", Integer::parseInt,
            BlockDisplay.class,
            (blockDisplay, teleportationDuration) ->
            ((BlockDisplay) blockDisplay).setTeleportDuration((int) teleportationDuration),
            null
        );
        
        addAttributeFunction(
            attributeFunctions,
            "noGravity", Boolean::parseBoolean,
            Entity.class,
            (entity, noGravity) -> entity.setGravity(!((boolean) noGravity)),
            false
        );
        addAttributeFunction(
            attributeFunctions,
            "autoExpire", Boolean::parseBoolean,
            FallingBlock.class,
            (entity, autoExpire) -> NMS_HACKS.setFallingBlockAutoExpire(entity, (boolean) autoExpire), // TANK SAID IT COULDN'T BE DONE, YET HERE WE ARE
            true
        );

        // Common Entity attributes
        addAttributeFunction(
            attributeFunctions,
            "silent", Boolean::parseBoolean,
            Entity.class,
            (entity, silent) -> entity.setSilent((boolean) silent),
            false
        );
        addAttributeFunction(
            attributeFunctions,
            "glowing", Boolean::parseBoolean,
            Entity.class,
            (entity, glowing) -> entity.setGlowing((boolean) glowing),
            false
        );
        addAttributeFunction(
            attributeFunctions,
            "invisible", Boolean::parseBoolean,
            Entity.class,
            (entity, invisible) -> entity.setInvisible((boolean) invisible),
            false
        );
        addAttributeFunction(
            attributeFunctions,
            "scale", Double::parseDouble,
            LivingEntity.class,
            (entity, scale) -> ((LivingEntity) entity).getAttribute(Attribute.SCALE).setBaseValue((double) scale),
            1.0
        );

        // Phantom
        addAttributeFunction(
            attributeFunctions,
            "phantomSize", Integer::parseInt,
            Phantom.class,
            (entity, size) -> ((Phantom) entity).setSize((int) size),
            0
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
            "wolfVariant", (s) -> RegistryAccess.registryAccess().getRegistry(RegistryKey.WOLF_VARIANT).get(NamespacedKey.minecraft(s.toLowerCase())),
            Wolf.class,
            (entity, variant) -> ((Wolf) entity).setVariant((Wolf.Variant) variant),
            Wolf.Variant.PALE
        );

        // Villager
        addAttributeFunction(
            attributeFunctions,
            "profession", (s) -> Registry.VILLAGER_PROFESSION.get(NamespacedKey.minecraft(s.toLowerCase())),
            Villager.class,
            (entity, profession) -> ((Villager) entity).setProfession((Villager.Profession) profession),
            Villager.Profession.NONE
        );
        addAttributeFunction(
            attributeFunctions,
            "villagerType", (s) -> Registry.VILLAGER_TYPE.get(NamespacedKey.minecraft(s.toLowerCase())),
            Villager.class,
            (entity, type) -> ((Villager) entity).setVillagerType((Villager.Type) type),
            Villager.Type.PLAINS
        );
        addAttributeFunction(
            attributeFunctions,
            "villagerLevel", Integer::parseInt,
            Villager.class,
            (entity, level) -> ((Villager) entity).setVillagerLevel((int) level),
            1
        );

        // Snowman
        addAttributeFunction(
            attributeFunctions,
            "derp", Boolean::parseBoolean,
            Snowman.class,
            (entity, derp) -> ((Snowman) entity).setDerp((boolean) derp),
            false
        );

        // Bee
        addAttributeFunction(
            attributeFunctions,
            "hasNectar", Boolean::parseBoolean,
            Bee.class,
            (entity, nectar) -> ((Bee) entity).setHasNectar((boolean) nectar),
            false
        );

        // Enderman
        addAttributeFunction(
            attributeFunctions,
            "carriedBlock", (s) -> Material.valueOf(s.replace(" ", "_").toUpperCase()).createBlockData(),
            Enderman.class,
            (entity, block) -> ((Enderman) entity).setCarriedBlock((org.bukkit.block.data.BlockData) block),
            null
        );

        // Cat
        addAttributeFunction(
            attributeFunctions,
            "catType", (s) -> RegistryAccess.registryAccess().getRegistry(RegistryKey.CAT_VARIANT).get(NamespacedKey.minecraft(s.toLowerCase())),
            Cat.class,
            (entity, type) -> ((Cat) entity).setCatType((Cat.Type) type),
            Cat.Type.TABBY
        );
        

        // Parrot
        addAttributeFunction(
            attributeFunctions,
            "parrotVariant", (s) -> Parrot.Variant.valueOf(s.toUpperCase()),
            Parrot.class,
            (entity, variant) -> ((Parrot) entity).setVariant((Parrot.Variant) variant),
            Parrot.Variant.RED
        );
        

        // Axolotl
        addAttributeFunction(
            attributeFunctions,
            "axolotlVariant", (s) -> Axolotl.Variant.valueOf(s.toUpperCase()),
            Axolotl.class,
            (entity, variant) -> ((Axolotl) entity).setVariant((Axolotl.Variant) variant),
            Axolotl.Variant.LUCY
        );
        

        // Frog
        addAttributeFunction(
            attributeFunctions,
            "frogVariant", (s) -> RegistryAccess.registryAccess().getRegistry(RegistryKey.FROG_VARIANT).get(NamespacedKey.minecraft(s.toLowerCase())),
            Frog.class,
            (entity, variant) -> ((Frog) entity).setVariant((Frog.Variant) variant),
            Frog.Variant.TEMPERATE
        );
        

        // PufferFish
        addAttributeFunction(
            attributeFunctions,
            "puffState", Integer::parseInt,
            PufferFish.class,
            (entity, state) -> ((PufferFish) entity).setPuffState((int) state),
            0
        );

        // TropicalFish
        addAttributeFunction(
            attributeFunctions,
            "fishPattern", (s) -> TropicalFish.Pattern.valueOf(s.toUpperCase()),
            TropicalFish.class,
            (entity, pattern) -> ((TropicalFish) entity).setPattern((TropicalFish.Pattern) pattern),
            TropicalFish.Pattern.KOB
        );
        addAttributeFunction(
            attributeFunctions,
            "fishBodyColor", (s) -> DyeColor.valueOf(s.toUpperCase()),
            TropicalFish.class,
            (entity, color) -> ((TropicalFish) entity).setBodyColor((DyeColor) color),
            DyeColor.WHITE
        );
        addAttributeFunction(
            attributeFunctions,
            "fishPatternColor", (s) -> DyeColor.valueOf(s.toUpperCase()),
            TropicalFish.class,
            (entity, color) -> ((TropicalFish) entity).setPatternColor((DyeColor) color),
            DyeColor.WHITE
        );

        // Llama
        addAttributeFunction(
            attributeFunctions,
            "llamaColor", (s) -> Llama.Color.valueOf(s.toUpperCase()),
            Llama.class,
            (entity, color) -> ((Llama) entity).setColor((Llama.Color) color),
            Llama.Color.BROWN
        );
        

        // Fox
        addAttributeFunction(
            attributeFunctions,
            "foxType", (s) -> Fox.Type.valueOf(s.toUpperCase()),
            Fox.class,
            (entity, type) -> ((Fox) entity).setFoxType((Fox.Type) type),
            Fox.Type.RED
        );
        

        // MushroomCow
        addAttributeFunction(
            attributeFunctions,
            "mooshroomVariant", (s) -> MushroomCow.Variant.valueOf(s.toUpperCase()),
            MushroomCow.class,
            (entity, variant) -> ((MushroomCow) entity).setVariant((MushroomCow.Variant) variant),
            MushroomCow.Variant.RED
        );

        // Goat
        addAttributeFunction(
            attributeFunctions,
            "screaming", Boolean::parseBoolean,
            Goat.class,
            (entity, screaming) -> ((Goat) entity).setScreaming((boolean) screaming),
            false
        );
        

        // Shulker
        addAttributeFunction(
            attributeFunctions,
            "shulkerColor", (s) -> DyeColor.valueOf(s.toUpperCase()),
            Shulker.class,
            (entity, color) -> ((Shulker) entity).setColor((DyeColor) color),
            DyeColor.PURPLE
        );

        INSTANCE = new ModernEntityAttributes(attributeFunctions);
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

    @Override
    public Map<String, EntityAttributeDescription> getAttributeFunctions() {
        return attributeData;
    }
}
