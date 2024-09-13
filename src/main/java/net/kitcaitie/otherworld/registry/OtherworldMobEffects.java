package net.kitcaitie.otherworld.registry;

import com.mojang.blaze3d.vertex.PoseStack;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.blocks.teleporters.PrisonerTeleportBlock;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.village.poi.PoiManager;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientMobEffectExtensions;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class OtherworldMobEffects {
    public static final DeferredRegister<MobEffect> MOB_EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, Otherworld.MODID);
    public static final DeferredRegister<Potion> POTIONS =
            DeferredRegister.create(ForgeRegistries.POTIONS, Otherworld.MODID);

    public static final RegistryObject<MobEffect> UNCONSCIOUS = MOB_EFFECTS.register("unconscious", () ->
            new OtherworldMobEffect(MobEffectCategory.HARMFUL, 5526612, (entity, integer) -> {
                if (entity instanceof Player) {
                    entity.addEffect(new MobEffectInstance(MobEffects.DARKNESS, 300, integer * 4, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.BLINDNESS, 300, integer * 4, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.DIG_SLOWDOWN, 300, integer + 3, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300, integer + 3, false, false, false));
                    entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, integer + 3, false, false, false));
                }
            }, (entity, integer) -> {
                if (entity instanceof Mob mob) {
                    mob.getNavigation().stop();
                    mob.getLookControl().setLookAt(Vec3.atBottomCenterOf(mob.blockPosition()));
                    mob.setTarget(null);
                    mob.setAggressive(false);
                }
            }, (entity, integer) -> {
                if (entity instanceof Player) {
                    entity.removeEffect(MobEffects.DARKNESS);
                    entity.removeEffect(MobEffects.BLINDNESS);
                    entity.removeEffect(MobEffects.DIG_SLOWDOWN);
                    entity.removeEffect(MobEffects.WEAKNESS);
                    entity.removeEffect(MobEffects.MOVEMENT_SLOWDOWN);
                }
            }, false, false));

    public static final RegistryObject<MobEffect> CAPTURED = MOB_EFFECTS.register("captured", () ->
        new OtherworldMobEffect(MobEffectCategory.HARMFUL, 5526612, (entity, integer) -> {
            entity.addEffect(new MobEffectInstance(OtherworldMobEffects.UNCONSCIOUS.get(), 300, 0, false, false, false));
            if (entity instanceof ServerPlayer player) {
                player.displayClientMessage(Component.translatable("event.otherworld.captured").withStyle(ChatFormatting.BOLD, ChatFormatting.ITALIC, ChatFormatting.RED), true);
            }
        }, (entity, integer) -> {
            if (entity instanceof ServerPlayer player) {
                if (player.tickCount % 60 == 1) {
                    PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
                    if (!character.isImprisoned()) {
                        ServerLevel level = player.getLevel();
                        Optional<BlockPos> pos = level.getPoiManager().findClosest((holder) -> {
                            PoiType type = holder.value();
                            BlockState state = type.matchingStates().stream().findFirst().get();
                            if (state.getBlock() instanceof PrisonerTeleportBlock block) {
                                return block.validTargets().test(player);
                            }
                            return false;
                        }, player.blockPosition(), 2000, PoiManager.Occupancy.ANY);
                        pos.ifPresent(bp -> PrisonerTeleportBlock.teleport(level.getBlockState(bp), bp, player));
                    }
                }
            }
        }, (entity, integer) -> {
            if (entity instanceof ServerPlayer player) {
                PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
                character.removePrisonStatus();
                character.sendPacket(player);
                if (player.isInvulnerable()) {
                    player.setInvulnerable(false);
                }
            }
        }, false, false));


    public static final RegistryObject<Potion> SEDATIVE = POTIONS.register("sedative", () -> new Potion(new MobEffectInstance(UNCONSCIOUS.get(), 2000, 0, false, false, false)));


    public static void register(IEventBus eventBus) {
        MOB_EFFECTS.register(eventBus);
        POTIONS.register(eventBus);
    }

    protected static class OtherworldMobEffect extends MobEffect {
        private final BiConsumer<? super LivingEntity, Integer> onAdded;
        private final BiConsumer<? super LivingEntity, Integer> onTick;
        private final BiConsumer<? super LivingEntity, Integer> onRemoved;
        private final boolean shouldRender;
        private final boolean instant;
        public OtherworldMobEffect(MobEffectCategory category, int color, BiConsumer<? super LivingEntity, Integer> onAdded, BiConsumer<? super LivingEntity, Integer> onTick, BiConsumer<? super LivingEntity, Integer> onRemoved, boolean shouldRender, boolean instant) {
            super(category, color);
            this.onAdded = onAdded;
            this.onTick = onTick;
            this.onRemoved = onRemoved;
            this.shouldRender = shouldRender;
            this.instant = instant;
        }

        @Override
        public boolean isInstantenous() {
            return instant;
        }

        @Override
        public boolean isDurationEffectTick(int p_19455_, int p_19456_) {
            return !instant;
        }

        @Override
        public void applyInstantenousEffect(@Nullable Entity p_19462_, @Nullable Entity p_19463_, LivingEntity p_19464_, int p_19465_, double p_19466_) {
            onAdded.accept(p_19464_, p_19465_);
        }

        @Override
        public void initializeClient(Consumer<IClientMobEffectExtensions> consumer) {
            if (!shouldRender) {
                consumer.accept(new IClientMobEffectExtensions() {
                    @Override
                    public boolean isVisibleInInventory(MobEffectInstance instance) {
                        return false;
                    }

                    @Override
                    public boolean isVisibleInGui(MobEffectInstance instance) {
                        return false;
                    }

                    @Override
                    public boolean renderInventoryIcon(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, PoseStack poseStack, int x, int y, int blitOffset) {
                        return false;
                    }

                    @Override
                    public boolean renderInventoryText(MobEffectInstance instance, EffectRenderingInventoryScreen<?> screen, PoseStack poseStack, int x, int y, int blitOffset) {
                        return false;
                    }

                    @Override
                    public boolean renderGuiIcon(MobEffectInstance instance, Gui gui, PoseStack poseStack, int x, int y, float z, float alpha) {
                        return false;
                    }
                });
            }
        }

        @Override
        public void addAttributeModifiers(LivingEntity entity, AttributeMap map, int i) {
            onAdded.accept(entity, i);
        }

        @Override
        public void applyEffectTick(LivingEntity entity, int i) {
            onTick.accept(entity, i);
        }

        @Override
        public void removeAttributeModifiers(LivingEntity p_19469_, AttributeMap p_19470_, int p_19471_) {
            onRemoved.accept(p_19469_, p_19471_);
        }
    }

}
