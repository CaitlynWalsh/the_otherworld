package net.kitcaitie.otherworld.client.layers;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class EmberianGlowLayer<T extends LivingEntity, M extends PlayerModel<T>> extends EyesLayer<T, M> {
    private static final RenderType EMBERIAN_FEMALE = RenderType.eyes(new ResourceLocation(Otherworld.MODID, "textures/entity/glow/emberian_female.png"));
    private static final RenderType EMBERIAN_FEMALE_WEAK = RenderType.eyes(new ResourceLocation(Otherworld.MODID, "textures/entity/glow/emberian_female_weak.png"));
    private static final RenderType EMBERIAN_MALE = RenderType.eyes(new ResourceLocation(Otherworld.MODID, "textures/entity/glow/emberian_male.png"));
    private static final RenderType EMBERIAN_MALE_WEAK = RenderType.eyes(new ResourceLocation(Otherworld.MODID, "textures/entity/glow/emberian_male_weak.png"));
    private static final RenderType EMBERIAN_MALE_SLIM = RenderType.eyes(new ResourceLocation(Otherworld.MODID, "textures/entity/glow/emberian_male_slim.png"));
    private static final RenderType EMBERIAN_MALE_SLIM_WEAK = RenderType.eyes(new ResourceLocation(Otherworld.MODID, "textures/entity/glow/emberian_male_slim_weak.png"));
    private static final RenderType EMBERIAN_BABY_FEMALE = RenderType.eyes(new ResourceLocation(Otherworld.MODID, "textures/entity/glow/emberian_baby_female.png"));
    private static final RenderType EMBERIAN_BABY_FEMALE_WEAK = RenderType.eyes(new ResourceLocation(Otherworld.MODID, "textures/entity/glow/emberian_baby_female_weak.png"));
    private static final RenderType EMBERIAN_BABY_MALE = RenderType.eyes(new ResourceLocation(Otherworld.MODID, "textures/entity/glow/emberian_baby_male.png"));
    private static final RenderType EMBERIAN_BABY_MALE_WEAK = RenderType.eyes(new ResourceLocation(Otherworld.MODID, "textures/entity/glow/emberian_baby_male_weak.png"));

    public EmberianGlowLayer(RenderLayerParent<T, M> p_116981_) {
        super(p_116981_);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int i, T entity, float v, float v1, float v2, float v3, float v4, float v5) {
        if (entity instanceof Player && (!OtherworldConfigs.CLIENT.renderLayersOnPlayer.get() || !OtherworldConfigs.CLIENT.useCharacterTextures.get())) return;
        boolean flag = isWeak(entity);
        RenderType renderType = getRenderLocation(entity, flag);
        if (renderType != null && !entity.isInvisible()) {
            VertexConsumer vertexConsumer = bufferSource.getBuffer(renderType);
            this.getParentModel().renderToBuffer(poseStack, vertexConsumer, flag ? -240 : 15728640, LivingEntityRenderer.getOverlayCoords(entity, 0.0F), 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    public RenderType renderType() {
        return EMBERIAN_MALE;
    }

    public static boolean isWeak(LivingEntity entity) {
        if (entity instanceof IRaces races) return races.isWeak(races, entity);
        if (entity instanceof AbstractClientPlayer player) {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
            return character.isWeak(character, player);
        }
        return false;
    }

    public static RenderType getRenderLocation(LivingEntity entity, boolean isWeak) {
        if (entity instanceof AbstractPerson person && person.isEmberian()) {
            if (isWeak) {
                if (person.isBaby()) {
                    return person.isMale() ? EMBERIAN_BABY_MALE_WEAK : EMBERIAN_BABY_FEMALE_WEAK;
                }
                return person.isMale() ? EMBERIAN_MALE_WEAK : EMBERIAN_FEMALE_WEAK;
            }
            if (person.isBaby()) {
                return person.isMale() ? EMBERIAN_BABY_MALE : EMBERIAN_BABY_FEMALE;
            }
            return person.isMale() ? EMBERIAN_MALE : EMBERIAN_FEMALE;
        }
        else if (entity instanceof AbstractClientPlayer player) {
            PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
            if (character.isEmberian()) {
                if (isWeak) {
                    if (player.getModelName().equals("slim") && character.isMale()) return EMBERIAN_MALE_SLIM_WEAK;
                    return character.isMale() ? EMBERIAN_MALE_WEAK : EMBERIAN_FEMALE_WEAK;
                }
                if (player.getModelName().equals("slim") && character.isMale()) return EMBERIAN_MALE_SLIM;
                return character.isMale() ? EMBERIAN_MALE : EMBERIAN_FEMALE;
            }
        }
        return null;
    }
}
