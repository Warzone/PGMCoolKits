package tc.oc.pgm.projectile.definition;

import org.bukkit.potion.PotionEffect;
import tc.oc.pgm.action.Action;
import tc.oc.pgm.api.filter.Filter;
import tc.oc.pgm.api.location.MatchLocation;
import tc.oc.pgm.api.player.MatchPlayer;
import tc.oc.pgm.projectile.ClickAction;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

public record BaseProjectileDefinition(
    String id, String name, ClickAction clickAction,
    double velocity, Double damage, Filter destroyFilter,
    List<PotionEffect> potion, Duration coolDown, boolean throwable,
    Action<? super MatchLocation> onHitBlockAction, Action<? super MatchPlayer> onHitPlayerAction
) {
    public static class Builder {
        private String id;
        private String name;
        private ClickAction clickAction;
        private double velocity = 0.1;
        private Double damage;
        private Filter destroyFilter;
        private List<PotionEffect> potion;
        private Duration coolDown;
        private boolean throwable;
        private Action<? super MatchLocation> onHitBlockAction;
        private Action<? super MatchPlayer> onHitPlayerAction;

        public void setId(String id) {
            this.id = id;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setClickAction(ClickAction clickAction) {
            this.clickAction = clickAction;
        }

        public Builder setVelocity(double velocity) {
            this.velocity = velocity;
            return this;
        }

        public Builder setDamage(Double damage) {
            this.damage = damage;
            return this;
        }

        public void setDestroyFilter(Filter destroyFilter) {
            this.destroyFilter = destroyFilter;
        }

        public void setPotion(List<PotionEffect> potion) {
            this.potion = potion;
        }

        public void setCoolDown(Duration coolDown) {
            this.coolDown = coolDown;
        }

        public void setThrowable(boolean throwable) {
            this.throwable = throwable;
        }

        public void setOnHitBlockAction(Action<? super MatchLocation> onHitBlockAction) {
            this.onHitBlockAction = onHitBlockAction;
        }

        public void setOnHitPlayerAction(Action<? super MatchPlayer> onHitPlayerAction) {
            this.onHitPlayerAction = onHitPlayerAction;
        }

        public BaseProjectileDefinition build() {
            return new BaseProjectileDefinition(
                Objects.requireNonNull(id), Objects.requireNonNull(name),
                Objects.requireNonNull(clickAction),
                velocity, damage, destroyFilter,
                potion, coolDown, throwable,
                onHitBlockAction, onHitPlayerAction
            );
        }
    }
}
