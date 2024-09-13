package net.kitcaitie.otherworld.common.items;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.LazyLoadedValue;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum Armors implements ArmorMaterial {

    ROSEGOLD("rosegold", 7, new int[]{1, 3, 5, 2}, 25, SoundEvents.ARMOR_EQUIP_GOLD, 0.0F, 0.0F, () -> {
        return Ingredient.of(OtherworldItems.ROSEGOLD_INGOT.get());
    }),
    SAPPHIRE("sapphire", 15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_DIAMOND, 0.0F, 0.0F, () -> {
        return Ingredient.of(OtherworldItems.SAPPHIRE.get());
    }),
    TOPAZ("topaz",15, new int[]{2, 5, 6, 2}, 9, SoundEvents.ARMOR_EQUIP_DIAMOND, 0.0F, 0.0F, () -> {
        return Ingredient.of(OtherworldItems.TOPAZ.get());
    });

    private static final int[] HEALTH_PER_SLOT = new int[]{13, 15, 16, 11};
    private final String name;
    private final int durabilityMultiplier;
    private final int[] slotProtections;
    private final int enchantmentValue;
    private final SoundEvent sound;
    private final float toughness;
    private final float knockbackResistance;
    private final LazyLoadedValue<Ingredient> repairIngredient;

    Armors(String s, int i, int[] ints, int i1, SoundEvent event, float v, float v1, Supplier<Ingredient> supplier) {
        this.name = s;
        this.durabilityMultiplier = i;
        this.slotProtections = ints;
        this.enchantmentValue = i1;
        this.sound = event;
        this.toughness = v;
        this.knockbackResistance = v1;
        this.repairIngredient = new LazyLoadedValue<>(supplier);
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        return HEALTH_PER_SLOT[type.getSlot().getIndex()] * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        return this.slotProtections[type.getSlot().getIndex()];
    }

    public int getEnchantmentValue() {
        return this.enchantmentValue;
    }

    public SoundEvent getEquipSound() {
        return this.sound;
    }

    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    public String getName() {
        return Otherworld.MODID + ":" + this.name;
    }

    public float getToughness() {
        return this.toughness;
    }

    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}
