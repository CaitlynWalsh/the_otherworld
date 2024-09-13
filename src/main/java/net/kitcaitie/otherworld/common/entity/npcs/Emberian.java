package net.kitcaitie.otherworld.common.entity.npcs;

import net.kitcaitie.otherworld.client.Dialogue;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.kitcaitie.otherworld.registry.OtherworldTags;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.MagmaBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.BlockPathTypes;

public class Emberian extends AbstractPerson {

    public Emberian(EntityType<? extends AbstractPerson> type, Level level) {
        super(type, level);
        this.setPathfindingMalus(BlockPathTypes.DAMAGE_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.DANGER_FIRE, 0.0F);
        this.setPathfindingMalus(BlockPathTypes.WATER, -1.0F);
    }

    public Race getRace() {
        return Race.EMBERIAN;
    }

    public static boolean checkEmberianSpawning(EntityType<? extends Mob> type, LevelAccessor accessor, MobSpawnType spawnType, BlockPos blockPos, RandomSource source) {
        BlockState state = accessor.getBlockState(blockPos.below());
        return state.is(OtherworldTags.OTHERAN_SPAWNABLE_ON) || state.is(Blocks.BLACKSTONE) || state.getBlock() instanceof MagmaBlock;
    }

    @Override
    public Item getCurrency() {
        return OtherworldItems.TOPAZ_COIN.get();
    }

    @Override
    public boolean isMarriageItem(ItemStack stack) {
        return stack.is(OtherworldItems.TOPAZ_RING.get());
    }

    @Override
    public void addItemsOnSpawn(MobSpawnType spawnType) {
        super.addItemsOnSpawn(spawnType);
        this.getInventory().addItem(new ItemStack(Items.COOKED_PORKCHOP, random.nextInt(6, 8)));
        this.getInventory().addItem(new ItemStack(OtherworldItems.SPICEROOT.get(), random.nextInt(4, 12)));
        if (this.isSoldier()) {
            if (spawnType == MobSpawnType.EVENT || random.nextInt(6) == 0) {
                this.setItemSlot(EquipmentSlot.HEAD, OtherworldItems.TOPAZ_HELMET.get().getDefaultInstance());
                this.setItemSlot(EquipmentSlot.CHEST, OtherworldItems.TOPAZ_CHESTPLATE.get().getDefaultInstance());
                this.setItemSlot(EquipmentSlot.LEGS, OtherworldItems.TOPAZ_LEGGINGS.get().getDefaultInstance());
                this.setItemSlot(EquipmentSlot.FEET, OtherworldItems.TOPAZ_BOOTS.get().getDefaultInstance());
            }
            if (spawnType == MobSpawnType.NATURAL) {
                if (random.nextBoolean())
                    this.getInventory().addItem(OtherworldItems.TOPAZ_KEY.get().getDefaultInstance());
            }
        }
    }

    @Override
    public ItemStack getSoldierWeapon() {
        return OtherworldItems.TOPAZ_SWORD.get().getDefaultInstance();
    }

    @Override
    public Dialogue.Type getDialogueType() {
        return Dialogue.Type.EMBERIAN;
    }
}
