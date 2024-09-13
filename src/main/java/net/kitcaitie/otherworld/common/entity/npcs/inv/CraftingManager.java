package net.kitcaitie.otherworld.common.entity.npcs.inv;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.recipe.PersonCraftingRecipe;
import net.kitcaitie.otherworld.registry.OtherworldItems;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.LevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class CraftingManager {
    public static final Map<IRaces.Race, List<Supplier<Item>>> CRAFTING_MAP = Map.of(
            IRaces.Race.HUMAN, List.of(()-> Items.BREAD),
            IRaces.Race.ONI, List.of(()-> Items.BREAD, ()->Items.BONE_MEAL, ()->Items.COOKED_MUTTON),
            IRaces.Race.ONIMAN, List.of(()-> Items.BREAD),
            IRaces.Race.ROSEIAN, List.of(OtherworldItems.ROSERYE_BREAD, ()-> Items.COOKED_RABBIT, ()-> Items.BONE_MEAL),
            IRaces.Race.FAIRIE, List.of(OtherworldItems.MULBERRY_SEEDS, ()->Items.BONE_MEAL),
            IRaces.Race.FAIRIAN, List.of(OtherworldItems.MULBERRY_SEEDS, OtherworldItems.ROSERYE_BREAD, ()-> Items.COOKED_RABBIT, ()->Items.BONE_MEAL),
            IRaces.Race.EMBERIAN, List.of(()->Items.BONE_MEAL),
            IRaces.Race.ICEIAN, List.of(()->Items.BONE_MEAL),
            IRaces.Race.OASIAN, List.of(),
            IRaces.Race.GHOUL, List.of()
    );
    private final PersonInventory inventory;
    private int time = 20;
    @Nullable
    private Item wantsToCraft;
    @Nullable
    private Ingredient needsMaterial;
    @Nullable
    private PersonCraftingRecipe currentRecipe;
    private PersonCraftingRecipe wantedRecipe;

    public CraftingManager(PersonInventory inventory) {
        this.inventory = inventory;
    }

    public void tick() {
        if (wantsToCraft != null) {
            if (time <= 0) {
                if (this.currentRecipe == null) {
                    this.currentRecipe = getRecipeForItem(wantsToCraft, true);
                    if (this.currentRecipe == null) {
                        this.wantedRecipe = getRecipeForItem(wantsToCraft, false);
                    }
                }
                if (canCraftItem() && craft()) {
                    time = 20;
                    this.wantsToCraft = null;
                    this.needsMaterial = null;
                    this.currentRecipe = null;
                    this.wantedRecipe = null;
                }
                else {
                    this.needsMaterial = getNeededIngredient(this.currentRecipe == null ? this.wantedRecipe : this.currentRecipe);
                }
                return;
            }
            --time;
        }
    }

    @Nullable
    public Ingredient getNeededMaterial() {
        return this.needsMaterial;
    }

    public Ingredient getNeededIngredient(PersonCraftingRecipe recipe) {
        if (recipe != null) {
            for (Ingredient ingredient : recipe.getIngredients()) {
                if (!hasIngredient(recipe, ingredient)) {
                    return ingredient;
                }
            }
        }
        return null;
    }

    public boolean craft() {
        if (currentRecipe != null) {
            ItemStack resultItem = currentRecipe.output.copy();
            for (int i = 0; i < currentRecipe.getIngredients().size(); i++) {
                for (Ingredient itemsToConsume : currentRecipe.getIngredients()) {
                    for (int j = 0; j < inventory.getContainerSize(); j++) {
                        if (itemsToConsume.test(inventory.getItem(j))) {
                            Item itemToConsume = inventory.getItem(j).getItem();
                            int amount = currentRecipe.getAmounts().get(i);
                            inventory.removeItemType(itemToConsume, amount);
                            break;
                        }
                    }
                }
            }
            inventory.addItem(resultItem);
        }
        return false;
    }

    public boolean canCraftItem() {
        if (this.currentRecipe != null) {
            int flag = 0;
            for (Ingredient ingredient : currentRecipe.getIngredients()) {
                if (hasIngredient(this.currentRecipe, ingredient)) {
                    flag++;
                }
            }
            return inventory.canAddItem(currentRecipe.output) && !inventory.hasAnyMatching((itm) -> itm.is(currentRecipe.output.getItem()) && itm.getCount() >= 16) && flag >= currentRecipe.getIngredients().size();
        }
        return false;
    }

    public boolean hasIngredient(PersonCraftingRecipe recipe, Ingredient ingredient) {
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack itemStack = inventory.getItem(i);
            if (ingredient.test(itemStack) && getAmountNeeded(recipe, ingredient) <= itemStack.getCount())
                return true;
        }
        return false;
    }

    public int getAmountNeeded(PersonCraftingRecipe recipe, Ingredient ingredient) {
        if (recipe != null) {
            for (int i = 0; i < recipe.getIngredients().size(); i++) {
                Ingredient ingredient1 = recipe.getIngredients().get(i);
                if (ingredient == ingredient1) {
                    return recipe.getAmounts().get(i);
                }
            }
        }
        return -1;
    }


    public Item getWantsToCraft() {
        return wantsToCraft;
    }

    public void setWantsToCraft(Item item) {
        this.wantsToCraft = item;
    }

    @Nullable
    public PersonCraftingRecipe getRecipeForItem(Item item, boolean canCraft) {
        if (item != null) {
            Stream<PersonCraftingRecipe> stream = Registry.RECIPES.stream().filter((recipe) -> recipe.output.copy().is(item));
            if (canCraft) {
                return stream.filter((recipe) -> {
                    int flag = 0;
                    for (Ingredient ingredient : recipe.getIngredients()) {
                        for (int i = 0; i < this.inventory.getContainerSize(); i++) {
                            ItemStack itemStack = this.inventory.getItem(i);
                            if (ingredient.test(itemStack)) {
                                flag++;
                            }
                        }
                    }
                    return flag >= recipe.getIngredients().size();
                }).findFirst().orElse(null);
            }
            return stream.findFirst().orElse(null);
        }
        return null;
    }

    public static class Registry {
        public static final ObjectArrayList<PersonCraftingRecipe> RECIPES = new ObjectArrayList<>();

        public static void initRecipes(LevelAccessor level) {
            if (level instanceof ServerLevel level1) {
                RECIPES.addAll(level1.getRecipeManager().getAllRecipesFor(PersonCraftingRecipe.Type.INSTANCE));
            }
        }

    }

}
