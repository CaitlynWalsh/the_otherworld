package net.kitcaitie.otherworld.common.story;

import net.kitcaitie.otherworld.common.story.global.UndertakerSpawningEvent;
import net.kitcaitie.otherworld.common.story.global.WarEvent;
import net.kitcaitie.otherworld.registry.OtherworldEvents;

import java.util.ArrayList;
import java.util.List;

public class StoryModes {

    public static final StoryModes INSTANCE = new StoryModes();

    // DEFAULT GLOBAL EVENTS //
    public final WarEvent ONI_INVADE_ROSEIAN_WAR;
    public final WarEvent ROSEIAN_INVADE_ONI_WAR;
    public final WarEvent EMBERIAN_INVADE_ICEIAN_WAR;
    public final WarEvent ICEIAN_INVADE_EMBERIAN_WAR;
   // public final WarEvent GHOUL_INVASION_WAR;

    public final UndertakerSpawningEvent UNDERTAKER_SPAWNER;

    // DEFAULT STORY MODE IDS //
    public String ICEvsFIRE_MODE_ID = "ice_vs_fire";
    public String WAR_MODE_ID = "wars";
    public String QUIET_MODE_ID = "quiet";
    public String GHOUL_INVASION_MODE_ID = "ghoul_invasion";

    // DEFAULT STORY MODES //
    public final Story ICEvsFIRE;
    public final Story WARS;
   // public final Story GHOUL_INVASION;

    public final Story QUIET;

    public final List<Story> MODES;

    private StoryModes() {
        this.ONI_INVADE_ROSEIAN_WAR = OtherworldEvents.GlobalStoryEvents.ONI_INVADE_ROSEIAN_WAR.copy();
        this.ROSEIAN_INVADE_ONI_WAR = OtherworldEvents.GlobalStoryEvents.ROSEIAN_INVADE_ONI_WAR.copy();
        this.EMBERIAN_INVADE_ICEIAN_WAR = OtherworldEvents.GlobalStoryEvents.EMBERIAN_INVADE_ICEIAN_WAR.copy();
        this.ICEIAN_INVADE_EMBERIAN_WAR = OtherworldEvents.GlobalStoryEvents.ICEIAN_INVADE_EMBERIAN_WAR.copy();
       // this.GHOUL_INVASION_WAR = OtherworldEvents.GlobalStoryEvents.GHOUL_INVASION_WAR.copy();
        this.UNDERTAKER_SPAWNER = OtherworldEvents.GlobalStoryEvents.UNDERTAKER_SPAWNER.copy();

        ONI_INVADE_ROSEIAN_WAR.setLoaded(true);
        ROSEIAN_INVADE_ONI_WAR.setLoaded(true);
        EMBERIAN_INVADE_ICEIAN_WAR.setLoaded(true);
        ICEIAN_INVADE_EMBERIAN_WAR.setLoaded(true);
       // GHOUL_INVASION_WAR.setLoaded(true);
        //UNDERTAKER_SPAWNER.setLoaded(true);

        this.ICEvsFIRE = iceVsFireMode();
        this.WARS = warMode();
       // this.GHOUL_INVASION = ghoulInvasionMode();
        this.QUIET = quietMode();

        MODES = new ArrayList<>(getModes());
    }

    // DEFAULT STORY MODES //

    public Story quietMode() {
        Story story = new Story(QUIET_MODE_ID, false);
        //story.addGlobalEvent(UNDERTAKER_SPAWNER);
        return story;
    }

    protected Story iceVsFireMode() {
        Story story = new Story(ICEvsFIRE_MODE_ID, false);
        story.addWarEvent(EMBERIAN_INVADE_ICEIAN_WAR);
        story.addWarEvent(ICEIAN_INVADE_EMBERIAN_WAR);
        //story.addGlobalEvent(UNDERTAKER_SPAWNER);
        return story;
    }

    protected Story warMode() {
        Story story = new Story(WAR_MODE_ID, false);
        story.addWarEvent(ONI_INVADE_ROSEIAN_WAR);
        story.addWarEvent(ROSEIAN_INVADE_ONI_WAR);
        story.addWarEvent(EMBERIAN_INVADE_ICEIAN_WAR);
        story.addWarEvent(ICEIAN_INVADE_EMBERIAN_WAR);
        //story.addGlobalEvent(UNDERTAKER_SPAWNER);
        return story;
    }

    protected Story ghoulInvasionMode() {
        Story story = new Story(GHOUL_INVASION_MODE_ID, false);
//        story.addWarEvent(GHOUL_INVASION_WAR);
        //story.addGlobalEvent(UNDERTAKER_SPAWNER);
        return story;
    }

    protected List<Story> getModes() {
        return List.of(WARS, ICEvsFIRE, QUIET);
    }

}
