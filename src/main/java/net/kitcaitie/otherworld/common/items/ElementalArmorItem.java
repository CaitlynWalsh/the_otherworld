package net.kitcaitie.otherworld.common.items;

import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ElementalArmorItem extends ArmorItem {

    public ElementalArmorItem(ArmorMaterial material, ArmorItem.Type slot, Properties properties) {
        super(material, slot, properties);
    }

    @Override
    public void inventoryTick(ItemStack stack, Level level, Entity entity, int i, boolean b) {
        super.inventoryTick(stack, level, entity, i, b);
        if (!level.isClientSide && level instanceof ServerLevel) {
            boolean flag = false;
            for (ItemStack stack2 : entity.getArmorSlots()) {
                if (stack2.equals(stack)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) return;

            if (entity instanceof Player player) {
                PlayerCharacter character = PowerUtils.accessPlayerCharacter(player);
                manageHurtEntity(stack, character, player);
            } else if (entity instanceof AbstractPerson person) {
                manageHurtEntity(stack, person, person);
            }
        }
    }

    protected void manageHurtEntity(ItemStack stack, IRaces races, Entity entity) {
        if (entity instanceof LivingEntity) {
            ElementalArmorItem armorItem = (ElementalArmorItem) stack.getItem();
            if (!races.hasFireResistance() && armorItem.getMaterial().equals(Armors.TOPAZ)) {
                entity.hurt(entity.level.damageSources().onFire(), 1.0F);
            }
            else if (!races.hasFreezeResistance() && armorItem.getMaterial().equals(Armors.SAPPHIRE)) {
                entity.setTicksFrozen(300);
            }
        }
    }
}
