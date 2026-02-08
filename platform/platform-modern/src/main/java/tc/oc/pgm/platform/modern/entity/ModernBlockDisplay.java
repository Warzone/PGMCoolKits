package tc.oc.pgm.platform.modern.entity;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.BlockDisplay;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;
import tc.oc.pgm.platform.modern.material.ModernBlockMaterialData;
import tc.oc.pgm.util.entity.BlockDisplayWrapper;
import tc.oc.pgm.util.material.BlockMaterialData;

public class ModernBlockDisplay implements BlockDisplayWrapper {
    private final BlockDisplay blockDisplay;

    public ModernBlockDisplay(BlockDisplay blockDisplay) {
        this.blockDisplay = blockDisplay;
    }

    @Override
    public void setBlock(Material material) {
        blockDisplay.setBlock(Bukkit.createBlockData(material));
    }

    @Override
    public void setBlock(BlockMaterialData blockMaterialData) {
        blockDisplay.setBlock(((ModernBlockMaterialData) blockMaterialData).getBlock());
    }

    @Override
    public void setTeleportationDuration(int duration) {
        blockDisplay.setTeleportDuration(duration);
    }

    @Override
    public void setTranslation(float x, float y, float z) {
        final Matrix4f translation =
            new Matrix4f().translate(new Vector3f(x, y, z));
        final Matrix4f transformationMatrix = translation;
        blockDisplay.setTransformationMatrix(transformationMatrix);
    }

    @Override
    public void alignToFacing(float pitch, float yaw, float size) {
        final Matrix4f translation =
            new Matrix4f().translate(new Vector3f(-0.5f * size, -0.5f * size, -0.5f * size));

        final Matrix4f rotationMatrix = new Matrix4f();
        final Quaternionf rotation = new Quaternionf();
        rotation.rotateLocalX((float) Math.toRadians(-pitch));
        rotation.rotateLocalY((float) Math.toRadians(180 - yaw));
        rotation.get(rotationMatrix);

        final Matrix4f scaleMatrix = new Matrix4f().scale(size);
        final Matrix4f transformationMatrix = rotationMatrix.mul(translation.mul(scaleMatrix));
        blockDisplay.setTransformationMatrix(transformationMatrix);
    }
}
