package net.kitcaitie.otherworld.common.story.events;

import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IWorlds;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.common.story.Story;
import net.kitcaitie.otherworld.common.util.PowerUtils;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.function.Supplier;

public class Bounty implements Event {
    @Nullable
    protected UUID criminalUuid;
    protected int criminalId;
    private final String id;
    private final Impact impact;
    private List<Pair<Supplier<Item>, Integer>> rewardItems;
    private int experience;

    public Bounty(String id, UUID criminal, Impact impact, List<Pair<Supplier<Item>, Integer>> rewards, int exp) {
        this.criminalUuid = criminal;
        this.id = id;
        this.impact = impact;
        this.rewardItems = rewards;
        this.experience = exp;
    }

    public Bounty(String id, int entity, Impact impact, List<Pair<Supplier<Item>, Integer>> rewards, int exp) {
        this.id = id;
        this.criminalId = entity;
        this.impact = impact;
        this.rewardItems = rewards;
        this.experience = exp;
    }

    public void complete(Player player, LivingEntity criminalEntity, ServerLevel level) {
        if (!rewardItems.isEmpty()) {
            for (Pair<Supplier<Item>, Integer> pair : rewardItems) {
                player.addItem(new ItemStack(pair.getFirst().get(), pair.getSecond()));
            }
        }
        player.giveExperiencePoints(this.experience);

        PlayerCharacter chr = PowerUtils.accessPlayerCharacter(player);
        chr.setCurrentBounty(null);
        chr.sendPacket(player);

        Story story = Otherworld.getStoryline(level).getStory();

        if (criminalEntity instanceof ServerPlayer criminalPlayer) {
            PlayerCharacter criminalChr = PowerUtils.accessPlayerCharacter(criminalPlayer);
            criminalChr.releaseFromJail(criminalPlayer);
            criminalChr.sendPacket(criminalPlayer);
        }

        for (IWorlds.Worlds worlds : IWorlds.Worlds.values()) {
            if (story.getChapters().getOrDefault(worlds, null) != null) {
                if (getCriminalId() != 0) story.getMostWantedNpcs(worlds).removeIf(id -> id == getCriminalId());
                if (getCriminalUuid() != null) story.getMostWantedPlayers(worlds).removeIf((uuid) -> uuid.equals(getCriminalUuid()));
                story.getBounties(worlds).removeIf((b) -> Objects.equals(b, this));
            }
        }
    }

    @Nullable
    public Entity getCriminal(Level level) {
        if (this.criminalId != 0) {
            return level.getEntity(this.criminalId);
        }
        return getCriminalPlayer(level);
    }

    @Nullable
    public Player getCriminalPlayer(Level level) {
        if (this.criminalUuid != null) {
            return level.getPlayerByUUID(this.criminalUuid);
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Bounty bounty) {
            if (bounty.getCriminalId() != 0) return this.getCriminalId() == bounty.getCriminalId();
            else if (bounty.getCriminalUuid() != null) return Objects.equals(this.criminalUuid, bounty.criminalUuid);
            return super.equals(bounty);
        }
        return false;
    }

    @Nullable
    public UUID getCriminalUuid() {
        return criminalUuid;
    }

    public int getCriminalId() {
        return criminalId;
    }

    @Override
    public String getID() {
        return id;
    }

    @Override
    public Impact getImpact() {
        return impact;
    }

    @Override
    public Type getType() {
        return Type.HELPFUL;
    }

    @Override
    public CompoundTag save() {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", this.id);
        if (criminalUuid != null)
            tag.putUUID("criminalUuid", this.criminalUuid);
        tag.putInt("criminalId", this.criminalId);
        tag.putString("impact", this.impact.name());

        if (!rewardItems.isEmpty()) {
            ListTag items = new ListTag();
            for (Pair<Supplier<Item>, Integer> pair : rewardItems) {
                CompoundTag tag1 = new CompoundTag();
                Item item = pair.getFirst().get();
                int size = pair.getSecond();

                tag1.putString("item", ForgeRegistries.ITEMS.getKey(item).toString());
                tag1.putInt("amount", size);

                items.add(tag1);
            }
            tag.put("rewardItems", items);
        }

        tag.putInt("exp", experience);

        return tag;
    }

    public static Bounty load(CompoundTag tag) {
        String id = tag.getString("id");
        UUID uuid = null;
        if (tag.hasUUID("criminalUuid"))
            uuid = tag.getUUID("criminalUuid");
        int criminalId = tag.getInt("criminalId");
        Impact impact = Impact.valueOf(tag.getString("impact"));

        List<Pair<Supplier<Item>, Integer>> items = new ArrayList<>();

        ListTag list = tag.getList("rewardItems", 10);
        for (int i=0; i<list.size(); i++) {
            CompoundTag tag1 = list.getCompound(i);
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(tag1.getString("item")));
            if (item == null) continue;
            items.add(Pair.of(() -> item, tag1.getInt("amount")));
        }

        int exp = tag.getInt("exp");

        Bounty bounty;

        if (criminalId != 0)
            bounty = new Bounty(id, criminalId, impact, items, exp);
        else if (uuid != null)
            bounty = new Bounty(id, uuid, impact, items, exp);
        else throw new NullPointerException("Otherworld: Error loading bounty: " + id + ": criminalId and criminalUuid are both invalid");

        return bounty;
    }

    public List<Pair<Item, Integer>> getRewards() {
        List<Pair<Item, Integer>> retval = new ArrayList<>();
        this.rewardItems.forEach((pair) -> retval.add(Pair.of(pair.getFirst().get(), pair.getSecond())));
        return retval;
    }
}
