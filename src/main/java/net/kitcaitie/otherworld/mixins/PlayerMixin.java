package net.kitcaitie.otherworld.mixins;

import net.kitcaitie.otherworld.common.player.IOtherworldPlayer;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Player.class)
public abstract class PlayerMixin implements IOtherworldPlayer {
    @Unique @Final
    private static final EntityDataAccessor<CompoundTag> PLAYER_CHARACTER_TAG = SynchedEntityData.defineId(Player.class, EntityDataSerializers.COMPOUND_TAG);
    @Unique
    private PlayerCharacter otherworld_playerCharacter;

    @Inject(method= "defineSynchedData", at= @At("TAIL"))
    protected void otherworld_defineSynchedData(CallbackInfo ci) {
        ((Player)(Object)this).getEntityData().define(PLAYER_CHARACTER_TAG, new PlayerCharacter().writeNBT());
    }

    @Inject(method = "addAdditionalSaveData", at=@At("TAIL"))
    protected void otherworld_addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.put("otherworld:character_data", ((Player)(Object)this).getEntityData().get(PLAYER_CHARACTER_TAG));
    }

    @Inject(method= "readAdditionalSaveData", at=@At("TAIL"))
    protected void otherworld_readAdditionalSaveData(CompoundTag tag, CallbackInfo info) {
        if (tag.contains("otherworld:character_data")) ((Player)(Object)this).getEntityData().set(PLAYER_CHARACTER_TAG, tag.getCompound("otherworld:character_data"));
    }

    @Inject(method = "travel", at=@At("TAIL"))
    protected void otherworld_travel(Vec3 vec3, CallbackInfo ci) {
        PowerUtils.powerTick(((IOtherworldPlayer)(Object)this).getPlayerCharacter(), (Player)(Object)this);
    }

    @Inject(method = "remove", at=@At("HEAD"))
    protected void otherworld_remove(Entity.RemovalReason reason, CallbackInfo ci) {
        setPlayerCharacterTag(getPlayerCharacter().writeNBT());
    }

    @Unique
    @Override
    public PlayerCharacter getPlayerCharacter() {
        if (this.otherworld_playerCharacter == null) {
            this.otherworld_playerCharacter = PlayerCharacter.load(getPlayerCharacterTag());
        }
        return this.otherworld_playerCharacter;
    }

    @Unique
    @Override
    public CompoundTag getPlayerCharacterTag() {
        return ((Player)(Object)this).getEntityData().get(PLAYER_CHARACTER_TAG);
    }

    @Unique
    @Override
    public void setPlayerCharacterTag(CompoundTag tag) {
        ((Player)(Object)this).getEntityData().set(PLAYER_CHARACTER_TAG, tag);
    }

    @Unique
    @Override
    public void setPlayerCharacter(PlayerCharacter character) {
        this.otherworld_playerCharacter = character;
        ((Player)(Object)this).getEntityData().set(PLAYER_CHARACTER_TAG, character.writeNBT());
    }

}
