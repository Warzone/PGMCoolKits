package tc.oc.pgm.util.entity;

import org.bukkit.Material;

public interface BlockDisplayWrapper {
    void setBlock(Material material);
    void setTeleportationDuration(int duration);
    void setTransformationMatrix(float x, float y, float z);
}
