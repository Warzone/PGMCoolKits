package tc.oc.pgm.projectile.definition;

import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.action.Action;
import tc.oc.pgm.api.filter.Filter;
import tc.oc.pgm.api.location.MatchLocation;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.features.SelfIdentifyingFeatureDefinition;
import tc.oc.pgm.projectile.ClickAction;
import tc.oc.pgm.util.material.BlockMaterialData;

import java.time.Duration;
import java.util.List;

public class OldProjectileDefinition extends SelfIdentifyingFeatureDefinition {
  protected @Nullable String name;
  protected @Nullable Double damage;
  protected @Nullable Float power;
  protected double velocity;
  protected ClickAction clickAction;
  protected List<PotionEffect> potion;
  protected Filter destroyFilter;
  protected Duration coolDown;
  protected boolean throwable;
  protected boolean precise;
  protected BlockMaterialData blockMaterial;
  protected final Action<? super MatchLocation> onHitBlockAction;
  protected final Action<? super MatchPlayer> onHitPlayerAction;

  public OldProjectileDefinition(
      @Nullable String id,
      @Nullable String name,
      @Nullable Double damage,
      @Nullable Float power,
      double velocity,
      ClickAction clickAction,
      List<PotionEffect> potion,
      Filter destroyFilter,
      Duration coolDown,
      boolean throwable,
      boolean precise,
      BlockMaterialData blockMaterial,
      Action<? super MatchLocation> onHitBlockAction,
      Action<? super MatchPlayer> onHitPlayerAction) {
    super(id);
    this.name = name;
    this.damage = damage;
    this.power = power;
    this.velocity = velocity;
    this.clickAction = clickAction;
    this.potion = potion;
    this.destroyFilter = destroyFilter;
    this.coolDown = coolDown;
    this.throwable = throwable;
    this.precise = precise;
    this.blockMaterial = blockMaterial;
    this.onHitBlockAction = onHitBlockAction;
    this.onHitPlayerAction = onHitPlayerAction;
  }

  public @Nullable String getName() {
    return name;
  }
}
