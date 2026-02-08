package tc.oc.pgm.util.entity;

import org.bukkit.Material;
import tc.oc.pgm.util.material.BlockMaterialData;

public interface BlockDisplayWrapper {
    void setBlock(Material material);
    void setBlock(BlockMaterialData blockMaterialData);
    void setTeleportationDuration(int duration);
    void setTranslation(float x, float y, float z);
    void alignToFacing(float pitch, float yaw, float size);
}
