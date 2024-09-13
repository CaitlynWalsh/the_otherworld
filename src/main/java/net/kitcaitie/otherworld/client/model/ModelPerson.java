package net.kitcaitie.otherworld.client.model;

import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.client.model.AnimationUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class ModelPerson<T extends AbstractPerson> extends PlayerModel<T> {
    public ModelPerson(ModelPart p_170821_, boolean p_170822_) {
        super(p_170821_, p_170822_);
    }

    @Override
    public void setupAnim(T t, float p_102867_, float p_102868_, float p_102869_, float p_102870_, float p_102871_) {
        if (t.isBlind() && !t.isCrouching() && !t.isVisuallySwimming() && !t.isPassenger()) {
            this.head.yRot = p_102870_ * ((float)Math.PI / 180F);
            this.head.xRot = p_102871_ * ((float)Math.PI / 180F);

            this.body.yRot = 0.0F;
            this.rightArm.z = 0.0F;
            this.rightArm.x = -5.0F;
            this.leftArm.z = 0.0F;
            this.leftArm.x = 5.0F;

            float f = 1.0F;

            AnimationUtils.animateZombieArms(leftArm, rightArm, false, attackTime, p_102869_);

            this.rightLeg.xRot = Mth.cos(p_102867_ * 0.6662F) * 1.4F * p_102868_ / f;
            this.leftLeg.xRot = Mth.cos(p_102867_ * 0.6662F + (float)Math.PI) * 1.4F * p_102868_ / f;
            this.rightLeg.yRot = 0.005F;
            this.leftLeg.yRot = -0.005F;
            this.rightLeg.zRot = 0.005F;
            this.leftLeg.zRot = -0.005F;

            this.hat.copyFrom(this.head);
            this.leftPants.copyFrom(this.leftLeg);
            this.rightPants.copyFrom(this.rightLeg);
            this.leftSleeve.copyFrom(this.leftArm);
            this.rightSleeve.copyFrom(this.rightArm);
            this.jacket.copyFrom(this.body);
        }
        else super.setupAnim(t, p_102867_, p_102868_, p_102869_, p_102870_, p_102871_);
    }
}
