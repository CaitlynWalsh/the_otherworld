package net.kitcaitie.otherworld.common.story;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.world.OtherworldServerLevel;
import net.kitcaitie.otherworld.registry.OtherworldGameRules;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;

public class Storyline extends SavedData {
    public static final Story EMPTY = new Story("", false);
    public static final String FILE_ID = "otherworld_storyline";
    private Story story;

    public Storyline(Story story) {
        this.story = story;
    }

    public void resetStory(Story story) {
        this.story = story;
    }

    public Story getStory() {
        return story;
    }

    public static Storyline create() {
        return new Storyline(EMPTY);
    }

    public static Storyline load(CompoundTag tag) {
        Storyline storyline = create();
        storyline.story = Story.read(tag.getCompound("storyline"));
        return storyline;
    }

    @Override
    public boolean isDirty() {
        return true;
    }

    @Override
    public CompoundTag save(CompoundTag tag) {
        CompoundTag tag1 = new CompoundTag();
        story.save(tag1);
        tag.put("storyline", tag1);
        return tag;
    }

    public static void saveAndLoadData(LevelAccessor level) {
        if (!level.isClientSide() && level instanceof ServerLevel level1) {
            ServerLevel overworld = level1.getServer().overworld();

            boolean flag = false;

            Storyline storyline = Otherworld.getStoryline(overworld);

            if (storyline == null) flag = true;
            else if (storyline.story.getNameId().isBlank()) flag = true;
            else if (!Otherworld.STORY_MODES.MODES.get(Math.min(OtherworldConfigs.SERVER.storyMode.get(), Otherworld.STORY_MODES.MODES.size() - 1)).getNameId().equals(storyline.story.getNameId())) flag = true;

            overworld.getDataStorage().computeIfAbsent(Storyline::load, Storyline::create, Storyline.FILE_ID);

            if (flag) overrideSavedData(level1);
            else {
                storyline.story.setChanged(false);
                overworld.getDataStorage().set(FILE_ID, storyline);
                ((OtherworldServerLevel)level1).setStoryline(storyline);
            }
        }
    }

    public static void overrideSavedData(ServerLevel level) {
        OtherworldConfigs.SERVER.storyMode.set(Math.min(2, level.getServer().getGameRules().getInt(OtherworldGameRules.RULE_STORYMODE)));
        int storytype = OtherworldConfigs.SERVER.storyMode.get();
        Story story = Otherworld.STORY_MODES.MODES.get(Math.min(storytype, Otherworld.STORY_MODES.MODES.size() - 1));

        Storyline storyline = new Storyline(story);

        level.getServer().overworld().getDataStorage().set(Storyline.FILE_ID, storyline);

        ((OtherworldServerLevel)level).setStoryline(storyline);

        if (level.dimension().equals(Level.OVERWORLD)) Otherworld.LOGGER.warn(Component.translatable("storyline.otherworld.reset").getString());
    }

    public static Storyline accessServerStory(MinecraftServer level) {
        ServerLevel serverlevel = level.overworld();
        return serverlevel.getDataStorage().computeIfAbsent(Storyline::load, Storyline::create, Storyline.FILE_ID);
    }

}
