package tc.oc.pgm.api.location;

import org.bukkit.Location;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tc.oc.pgm.api.filter.query.MatchQuery;
import tc.oc.pgm.api.match.Match;
import tc.oc.pgm.filters.Filterable;
import tc.oc.pgm.util.Audience;

import java.util.Collection;
import java.util.List;

public class MatchLocation implements Filterable<MatchQuery> {
    private final Match match;
    private final Location location;

    public MatchLocation(Match match, Location location) {
        this.match = match;
        this.location = location;
    }

    @Override
    public @Nullable Filterable<? super MatchQuery> getFilterableParent() {
        return this;
    }

    @Override
    public Collection<? extends Filterable<? extends MatchQuery>> getFilterableChildren() {
        return List.of();
    }

    @Override
    public @NotNull Audience audience() {
        return Audience.console();
    }

    @Override
    public Match getMatch() {
        return match;
    }

    public Location getLocation() {
        return location;
    }
}
