package net.kitcaitie.otherworld.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.OtherworldClient;
import net.kitcaitie.otherworld.client.model.ModelPerson;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.data.PersonData;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class PersonEntityRenderer<T extends AbstractPerson> extends LivingEntityRenderer<T, ModelPerson<T>> {
    public PersonEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new ModelPerson<>(context.bakeLayer(ModelLayers.PLAYER), true), 0.5F);
        this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel(context.bakeLayer(ModelLayers.PLAYER_OUTER_ARMOR)), context.getModelManager()));
        this.addLayer(new PlayerItemInHandLayer(this, context.getItemInHandRenderer()));
        this.addLayer(new ArrowLayer(context, this));
        this.addLayer(new CustomHeadLayer(this, context.getModelSet(), context.getItemInHandRenderer()));
        this.addLayer(new ElytraLayer(this, context.getModelSet()));
        this.addLayer(new BeeStingerLayer(this));
    }

    @Override
    public void render(T entity, float f, float f1, PoseStack stack, MultiBufferSource source, int i) {
        setModelProperties(entity);
        super.render(entity, f, f1, stack, source, i);
    }

    private void setModelProperties(AbstractPerson person) {
        PlayerModel<T> playerModel = this.getModel();
        playerModel.setAllVisible(true);
        playerModel.crouching = person.isCrouching();
        HumanoidModel.ArmPose armPose = getArmPose(person, InteractionHand.MAIN_HAND);
        HumanoidModel.ArmPose armPose1 = getArmPose(person, InteractionHand.OFF_HAND);
        if (armPose.isTwoHanded()) {
            armPose1 = person.getOffhandItem().isEmpty() ? HumanoidModel.ArmPose.EMPTY : HumanoidModel.ArmPose.ITEM;
        }
        if (person.getMainArm() == HumanoidArm.RIGHT) {
            playerModel.rightArmPose = armPose;
            playerModel.leftArmPose = armPose1;
        }
        else {
            playerModel.rightArmPose = armPose1;
            playerModel.leftArmPose = armPose;
        }
    }

    private static HumanoidModel.ArmPose getArmPose(AbstractPerson person, InteractionHand hand) {
        if (person.hasEffect(MobEffects.BLINDNESS)) return HumanoidModel.ArmPose.EMPTY;
        ItemStack itemStack = person.getItemInHand(hand);
        if (itemStack.isEmpty()) {
            return HumanoidModel.ArmPose.EMPTY;
        }
        else {
            if (person.getUsedItemHand() == hand && person.getUseItemRemainingTicks() > 0) {
                UseAnim useAnim = itemStack.getUseAnimation();
                if (useAnim == UseAnim.BLOCK) {
                    return HumanoidModel.ArmPose.BLOCK;
                }
                if (useAnim == UseAnim.BOW) {
                    return HumanoidModel.ArmPose.BOW_AND_ARROW;
                }
                if (useAnim == UseAnim.SPEAR) {
                    return HumanoidModel.ArmPose.THROW_SPEAR;
                }
                if (useAnim == UseAnim.CROSSBOW && hand == person.getUsedItemHand()) {
                    return HumanoidModel.ArmPose.CROSSBOW_CHARGE;
                }
                if (useAnim == UseAnim.SPYGLASS) {
                    return HumanoidModel.ArmPose.SPYGLASS;
                }
                if (useAnim == UseAnim.TOOT_HORN) {
                    return HumanoidModel.ArmPose.TOOT_HORN;
                }
            } else if (!person.swinging && itemStack.getItem() instanceof CrossbowItem && CrossbowItem.isCharged(itemStack)) {
                return HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
            return HumanoidModel.ArmPose.ITEM;
        }
    }

    protected void setupRotations(T person, PoseStack poseStack, float v, float v1, float v2) {
        float f = person.getSwimAmount(v2);
        if (person.isFallFlying()) {
            super.setupRotations(person, poseStack, v, v1, v2);
            float f1 = (float) person.getFallFlyingTicks() + v2;
            float f2 = Mth.clamp(f1 * f1 / 100.0F, 0.0F, 1.0F);
            if (!person.isAutoSpinAttack()) {
                poseStack.mulPose(Axis.XP.rotationDegrees(f2 * (-90.0F - person.getXRot())));
            }

            Vec3 vec3 = person.getViewVector(v2);
            Vec3 vec31 = person.getDeltaMovement();
            double d0 = vec31.horizontalDistanceSqr();
            double d1 = vec3.horizontalDistanceSqr();
            if (d0 > 0.0D && d1 > 0.0D) {
                double d2 = (vec31.x * vec3.x + vec31.z * vec3.z) / Math.sqrt(d0 * d1);
                double d3 = vec31.x * vec3.z - vec31.z * vec3.x;
                poseStack.mulPose(Axis.YP.rotation((float) (Math.signum(d3) * Math.acos(d2))));
            }
        } else if (f > 0.0F) {
            super.setupRotations(person, poseStack, v, v1, v2);
            float f3 = person.isInWater() || person.isInFluidType((fluidType, height) -> person.canSwimInFluidType(fluidType)) ? -90.0F - person.getXRot() : -90.0F;
            float f4 = Mth.lerp(f, 0.0F, f3);
            poseStack.mulPose(Axis.XP.rotationDegrees(f4));
            if (person.isVisuallySwimming()) {
                poseStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            super.setupRotations(person, poseStack, v, v1, v2);
        }
    }

    protected void scale(AbstractPerson person, PoseStack poseStack, float f) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
        if (!person.isMale()) {
            poseStack.scale(0.95F, 0.95F, 0.95F);
        }
        if (person.isOni()) {
            poseStack.scale(1.05F, 1.05F, 1.05F);
        }
    }

    @Override
    public ResourceLocation getTextureLocation(AbstractPerson person) {
        String ismale = person.isMale() ? "male" : "female";
        if (person.isBaby()) {
            return new ResourceLocation(Otherworld.MODID, "textures/entity/" + person.getRace().name().toLowerCase() + "_baby_" + ismale + ".png");
        }
        return new ResourceLocation(Otherworld.MODID, "textures/entity/" + person.getRace().name().toLowerCase() + "_" + person.getOccupation().name().toLowerCase() + "_" + ismale + ".png");
    }

    @Override
    protected void renderNameTag(T entity, Component tag, PoseStack poseStack, MultiBufferSource bufferSource, int i) {
        if (!entity.canHaveName()) return;
        super.renderNameTag(entity, getNameTag(entity, tag), poseStack, bufferSource, i);
    }

    private Component getNameTag(T entity, Component tag) {
        Player player = Minecraft.getInstance().player;
        PersonData data = entity.getClientPersonData();
        if (data.isFamilyWith(player)) {
            Style style = tag.getStyle().withColor(ChatFormatting.GOLD);
            if (data.isMarriedTo(player)) style = style.withBold(true);
            return tag.copy().setStyle(style);
        } else if (OtherworldClient.getPlayerCharacter().receivedQuestFrom(entity)) {
            return tag.copy().setStyle(tag.getStyle().withColor(ChatFormatting.AQUA));
        } else if (data.isLoverOf(player)) {
            return tag.copy().setStyle(tag.getStyle().withColor(ChatFormatting.LIGHT_PURPLE));
        } else if (data.isFriendlyTowards(player)) {
            return tag.copy().setStyle(tag.getStyle().withColor(ChatFormatting.GREEN));
        } else if (data.isHostileTowards(player)) {
            return tag.copy().setStyle(tag.getStyle().withColor(ChatFormatting.RED));
        }
        return tag;
    }
}
