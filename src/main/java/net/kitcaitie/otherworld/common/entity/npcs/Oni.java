package net.kitcaitie.otherworld.common.entity.npcs;

import net.kitcaitie.otherworld.client.Dialogue;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

public class Oni extends AbstractPerson {
    protected static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(Oni.class, EntityDataSerializers.INT);

    public Oni(EntityType<? extends AbstractPerson> type, Level level) {
        super(type, level);
        PowerUtils.createToughAttributes(this, this);
    }

    public Race getRace() {
        return Race.ONI;
    }

    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor accessor, DifficultyInstance instance, MobSpawnType type, @Nullable SpawnGroupData data, @Nullable CompoundTag tag) {
        if (type != MobSpawnType.BREEDING) this.setVariant(this.random.nextInt(PlayerCharacter.MAX_VARIANTS));
        return super.finalizeSpawn(accessor, instance, type, data, tag);
    }


    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(VARIANT, 0);
    }

    @Override
    public void writeData(CompoundTag tag) {
        super.writeData(tag);
        tag.putInt("Variant", this.getVariant());
    }

    @Override
    public void readData(CompoundTag tag) {
        super.readData(tag);
        if (tag.contains("Variant")) this.setVariant(tag.getInt("Variant"));
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    @Override
    public Oni create(ServerLevel level, LivingEntity partner) {
        Oni oni = (Oni) super.create(level, partner);
        if (partner instanceof Oni oni1)
            oni.setVariant(random.nextBoolean() ? this.getVariant() : oni1.getVariant());
        else
            oni.setVariant(this.getVariant());
        return oni;
    }

    @Override
    public Item getCurrency() {
        return Items.IRON_INGOT;
    }

    @Override
    public boolean isMarriageItem(ItemStack stack) {
        return stack.is(OtherworldItems.IRON_RING.get());
    }

    @Override
    public void addItemsOnSpawn(MobSpawnType spawnType) {
        super.addItemsOnSpawn(spawnType);
        this.getInventory().addItem(new ItemStack(Items.COOKED_MUTTON, random.nextInt(6, 12)));
        if (this.isSoldier() && spawnType == MobSpawnType.NATURAL) {
            if (random.nextBoolean())
                this.getInventory().addItem(OtherworldItems.IRON_KEY.get().getDefaultInstance());
        }
    }

    @Override
    protected void dropCustomDeathLoot(DamageSource source, int amount, boolean b) {
        super.dropCustomDeathLoot(source, amount, b);
        if (this.isSoldier() && random.nextFloat() < 0.5F) {
            this.spawnAtLocation(OtherworldItems.ONI_HORN.get().getDefaultInstance());
        }
    }

    @Override
    public Dialogue.Type getDialogueType() {
        return Dialogue.Type.ONI;
    }

}
