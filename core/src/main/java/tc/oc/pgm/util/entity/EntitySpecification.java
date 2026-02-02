package tc.oc.pgm.util.entity;

public sealed interface EntitySpecification permits SimpleEntitySpecification, ProxiedPhysicsEntitySpecification {
    boolean showNametag();
}