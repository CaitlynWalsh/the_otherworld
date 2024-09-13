package net.kitcaitie.otherworld.common.recipe;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.entity.npcs.inv.PersonInventory;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class PersonCraftingRecipe implements Recipe<PersonInventory> {
    private final ResourceLocation id;
    public final ItemStack output;
    private final NonNullList<Ingredient> ingredients;
    private final NonNullList<Integer> amounts;

    public PersonCraftingRecipe(ResourceLocation id, ItemStack output, NonNullList<Ingredient> ingredients, NonNullList<Integer> amounts) {
        this.id = id;
        this.output = output;
        this.ingredients = ingredients;
        this.amounts = amounts;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public NonNullList<Integer> getAmounts() {
        return amounts;
    }

    @Override
    public boolean matches(PersonInventory container, Level level) {
        if (level.isClientSide) return false;

        int flag = 0;

        for (int i = 0; i < getIngredients().size(); i++) {
            for (int j = 0; j < container.getContainerSize(); j++) {
                ItemStack item = container.getItem(i);
                if (getIngredients().get(i).test(item)) {
                    if (item.getCount() >= getAmounts().get(i)) {
                        flag++;
                        break;
                    }
                }
            }
        }
        return flag >= getIngredients().size();
    }

    @Override
    public ItemStack assemble(PersonInventory personInventory, RegistryAccess registryAccess) {
        return output;
    }

    @Override
    public boolean canCraftInDimensions(int x, int y) {
        return true;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess registryAccess) {
        return output.copy();
    }

    @Override
    public ResourceLocation getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return Serializer.INSTANCE;
    }

    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }

    public static class Type implements RecipeType<PersonCraftingRecipe> {
        private Type() {
        }

        public static final Type INSTANCE = new Type();
        public static final ResourceLocation ID = new ResourceLocation(Otherworld.MODID, "inventory_crafting");
    }

    public static class Serializer implements RecipeSerializer<PersonCraftingRecipe> {
        public static final Serializer INSTANCE = new Serializer();
        public static final ResourceLocation ID = new ResourceLocation(Otherworld.MODID, "inventory_crafting");

        @Override
        public PersonCraftingRecipe fromJson(ResourceLocation location, JsonObject json) {
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));

            JsonArray ingredients = GsonHelper.getAsJsonArray(json, "ingredients");

            NonNullList<Ingredient> inputs = NonNullList.withSize(ingredients.size(), Ingredient.EMPTY);

            for (int i = 0; i < ingredients.size(); i++) {
                inputs.set(i, Ingredient.fromJson(ingredients.get(i)));
            }

            JsonArray amounts = GsonHelper.getAsJsonArray(json, "amounts");
            NonNullList<Integer> amountList = NonNullList.withSize(amounts.size(), 1);

            for (int i = 0; i < amounts.size(); i++) {
                amountList.set(i, amounts.get(i).getAsInt());
            }

            return new PersonCraftingRecipe(location, output, inputs, amountList);
        }

        @Override
        public @Nullable PersonCraftingRecipe fromNetwork(ResourceLocation location, FriendlyByteBuf byteBuf) {
            NonNullList<Ingredient> inputs = NonNullList.withSize(byteBuf.readInt(), Ingredient.EMPTY);
            NonNullList<Integer> amounts = NonNullList.withSize(byteBuf.readInt(), 1);

            for (int i = 0; i < inputs.size(); i++) {
                inputs.set(i, Ingredient.fromNetwork(byteBuf));
            }

            for (int i = 0; i < amounts.size(); i++) {
                amounts.set(i, byteBuf.readInt());
            }

            ItemStack output = byteBuf.readItem();

            return new PersonCraftingRecipe(location, output, inputs, amounts);
        }

        @Override
        public void toNetwork(FriendlyByteBuf byteBuf, PersonCraftingRecipe recipe) {
            byteBuf.writeInt(recipe.getIngredients().size());
            byteBuf.writeInt(recipe.getAmounts().size());

            for (Ingredient ing : recipe.getIngredients()) {
                ing.toNetwork(byteBuf);
            }

            for (Integer in : recipe.getAmounts()) {
                byteBuf.writeInt(in);
            }

            byteBuf.writeItemStack(recipe.output.copy(), false);
        }
    }
}
