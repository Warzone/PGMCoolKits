package tc.oc.pgm.util.entity;

public record ProxiedPhysicsEntitySpecification(
    SimpleEntitySpecification modelEntitySpec,
    int slimeSize
) implements EntitySpecification {
    @Override
    public boolean showNametag() {
        return modelEntitySpec.showNametag();
    }
}
