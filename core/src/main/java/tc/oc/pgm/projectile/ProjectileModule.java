package tc.oc.pgm.projectile;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.FallingBlock;
import org.bukkit.potion.PotionEffect;
import org.jdom2.Document;
import org.jdom2.Element;
import tc.oc.pgm.api.filter.Filter;
import tc.oc.pgm.api.location.MatchLocation;
import tc.oc.pgm.api.map.MapModule;
import tc.oc.pgm.api.map.factory.MapFactory;
import tc.oc.pgm.api.map.factory.MapModuleFactory;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.filters.FilterModule;
import tc.oc.pgm.filters.parse.FilterParser;
import tc.oc.pgm.kits.KitParser;
import tc.oc.pgm.projectile.definition.BaseProjectileDefinition;
import tc.oc.pgm.projectile.definition.IdentifiedFeatureContainer;
import tc.oc.pgm.projectile.definition.ProjectileDefinition;
import tc.oc.pgm.projectile.definition.RealEntityProjectileDefinition;
import tc.oc.pgm.util.material.BlockMaterialData;
import tc.oc.pgm.util.xml.InvalidXMLException;
import tc.oc.pgm.util.xml.Node;
import tc.oc.pgm.util.xml.XMLUtils;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.logging.Logger;

public class ProjectileModule implements MapModule<ProjectileMatchModule> {
    private final ImmutableSet<ProjectileDefinition> projectileDefinitions;

    public ProjectileModule(ImmutableSet<ProjectileDefinition> projectileDefinitions) {
        this.projectileDefinitions = projectileDefinitions;
    }

    @Override
    public ProjectileMatchModule createMatchModule(Match match) {
        return new ProjectileMatchModule(match, this.projectileDefinitions);
    }

    public static class Factory implements MapModuleFactory<ProjectileModule> {
        @Override
        public Collection<Class<? extends MapModule<?>>> getSoftDependencies() {
            return ImmutableList.of(FilterModule.class);
        }

        @Override
        public ProjectileModule parse(MapFactory factory, Logger logger, Document doc)
            throws InvalidXMLException {
            Set<ProjectileDefinition> projectiles = new HashSet<>();

            for (Element projectileElement :
                XMLUtils.flattenElements(doc.getRootElement(), "projectiles", "projectile")) {

                var definition = parseDefinition(factory, projectileElement);
                factory.getFeatures().addFeature(
                    projectileElement,
                    new IdentifiedFeatureContainer(definition.getBaseDefinition().id(), definition)
                );
                projectiles.add(parseDefinition(factory, projectileElement));
            }

            return projectiles.isEmpty() ? null : new ProjectileModule(ImmutableSet.copyOf(projectiles));
        }

        private static ProjectileDefinition parseDefinition(
            final MapFactory factory,
            final Element projectileElement
        ) throws InvalidXMLException {
            var parser = factory.getParser();
            var base = parseBaseProjectileDefinition(factory, projectileElement);

            final String attributeName = "projectile";
            final Class<? extends Entity> def = Arrow.class;
            final Node node = Node.fromAttr(projectileElement, attributeName);
            if (node == null) {
                return new RealEntityProjectileDefinition(
                    def, base, false, null, null
                );
            }
            final String entityText = node.getValue();
            final String normalizedEntity = entityText.toLowerCase(Locale.ROOT);
            return switch (normalizedEntity) {
                case "block", "item" -> {
                    var size = parser.parseFloat(projectileElement, "size").optional(1.0f);
                    var solidBlockCollision = parser.parseBool(
                        projectileElement, "solid-block-collision"
                    ).orTrue();
                    var duration = parser.duration(projectileElement, "max-travel-time")
                        .optional(Duration.ofSeconds(1));
                    var launchOptions = new SimulatedProjectileLauncher.Options(solidBlockCollision, duration, size);
                    // TODO!
                    yield null;
                }
                default -> {
                    var entityType = XMLUtils.parseEntityTypeAttribute(projectileElement, attributeName, def);
                    BlockMaterialData blockMaterial = FallingBlock.class.isAssignableFrom(entityType)
                        ? XMLUtils.parseBlockMaterialData(Node.fromRequiredAttr(projectileElement, "material"))
                        : null;
                    Float power = XMLUtils.parseNumber(
                        Node.fromChildOrAttr(projectileElement, "power"), Float.class, null);
                    boolean precise = XMLUtils.parseBoolean(
                        projectileElement.getAttribute("precise"), true
                    );
                    yield new RealEntityProjectileDefinition(entityType, base, precise, blockMaterial, power);
                }
            };
        }

        private static BaseProjectileDefinition parseBaseProjectileDefinition(
            final MapFactory factory, final Element projectileElement
        ) throws InvalidXMLException {
            KitParser kitParser = factory.getKits();
            FilterParser filterParser = factory.getFilters();

            String id = XMLUtils.getRequiredAttribute(projectileElement, "id").getValue();
            String name = projectileElement.getAttributeValue("name");
            Double damage = XMLUtils.parseNumber(
                projectileElement.getAttribute("damage"), Double.class, (Double) null);
            if (damage != null && damage == 0.0d) {
                damage = null;
            }
            double velocity = XMLUtils.parseNumber(
                Node.fromChildOrAttr(projectileElement, "velocity"), Double.class, 1.0);
            ClickAction clickAction = XMLUtils.parseEnum(
                Node.fromAttr(projectileElement, "click"), ClickAction.class, ClickAction.BOTH);
            List<PotionEffect> potionKit = kitParser.parsePotions(projectileElement);
            Filter destroyFilter =
                filterParser.parseFilterProperty(projectileElement, "destroy-filter");
            Duration coolDown = XMLUtils.parseDuration(projectileElement.getAttribute("cooldown"));
            boolean throwable =
                XMLUtils.parseBoolean(projectileElement.getAttribute("throwable"), true);
            var parser = factory.getParser();
            var onHitBlockAction = parser.action(
                MatchLocation.class, projectileElement, "on-hit-block-action"
            ).orNull();
            var onHitPlayerAction = parser.action(
                MatchPlayer.class, projectileElement, "on-hit-player-action"
            ).orNull();

            return new BaseProjectileDefinition(
                id, name, clickAction, velocity, damage, destroyFilter,
                potionKit, coolDown, throwable, onHitBlockAction, onHitPlayerAction
            );
        }
    }

}
