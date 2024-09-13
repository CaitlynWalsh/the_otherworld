package net.kitcaitie.otherworld.common;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class OtherworldConfigs {

    public static class Client {
        public ForgeConfigSpec.ConfigValue<Boolean> useCharacterTextures;
        public ForgeConfigSpec.ConfigValue<Boolean> renderLayersOnPlayer;
        public ForgeConfigSpec.ConfigValue<Boolean> renderClothesOnPlayer;
        public Client(ForgeConfigSpec.Builder builder) {
            builder.push("otherworld-client");
            builder.comment("--- The Otherworld: Client Configurations ---");
            useCharacterTextures = builder.comment("Use custom textures for the Player to match their character.").define("useCharacterTextures", true);
            renderLayersOnPlayer = builder.comment("Add custom layers to the Player's model, like Oni horns or Fairie wings.").define("renderLayersOnPlayer", true);
            renderClothesOnPlayer = builder.comment("Add clothing layers to the Player's model, depending on their race and profession.").define("renderClothesOnPlayer", true);
            builder.pop();
        }
    }

    public static class Common {
        public Common(ForgeConfigSpec.Builder builder) {
            builder.push("otherworld-common");
            //builder.comment("--- The Otherworld: Common Configurations ---");
            builder.pop();
        }
    }

    public static class Server {
        public ForgeConfigSpec.ConfigValue<Integer> storyMode;
        public ForgeConfigSpec.ConfigValue<String> storyEventChance;
        public ForgeConfigSpec.ConfigValue<Boolean> randomizedCharacters;
        public ForgeConfigSpec.ConfigValue<Integer> permissionLevelToBreakStructures;
        public ForgeConfigSpec.ConfigValue<Boolean> onlyServerOpsBreakStructures;
        public ForgeConfigSpec.ConfigValue<Boolean> allowSoldierArrests;
        public ForgeConfigSpec.ConfigValue<Boolean> allowWarPrisonerArrests;
        public ForgeConfigSpec.ConfigValue<Integer> breedingCooldownAge;
        public ForgeConfigSpec.ConfigValue<Integer> childLimit;
        public ForgeConfigSpec.ConfigValue<Boolean> doNaturalMarriage;
        public ForgeConfigSpec.ConfigValue<Boolean> doNaturalBreeding;
        public ForgeConfigSpec.ConfigValue<Boolean> doNpcTrades;
        public Server(ForgeConfigSpec.Builder builder) {
            builder.push("otherworld-server");
            builder.comment("--- The Otherworld: Server Configurations ---");
            storyMode = builder.comment("Sets a story mode for the server. A number between 0 and 2.").comment("WARNING: Changing this will reset all global data stored in the world!").define("storyMode", 0);
            storyEventChance = builder.comment("Sets the chance for a global event to trigger. If a randomized number between 0 and 1 is smaller than this value, a global event will start.")
                    .comment("WARNING: Setting this number too high will cause story events to trigger constantly! Be very careful increasing this value!").define("storyEventChance", "0.0001");
            randomizedCharacters = builder.comment("Determines whether a player's race and gender are randomized instead of chosen on joining the world for the first time.").define("randomizedCharacters", false);
            permissionLevelToBreakStructures = builder.comment("Players above or at this permission level will be able to destroy villages and structures in The Otherworld. Other dimensions are unaffected. Set to 0 to disable").comment("WARNING: Allowing players to grief naturally generated structures in The Otherworld can negatively impact gameplay!").define("permissionLevelToBreakStructures", 3);
            onlyServerOpsBreakStructures = builder.comment("Only players listed as operators and with the allowed permission level in 'permissionLevelToBreakStructures' can break structures in The Otherworld if true. Only applies to dedicated servers.").define("onlyServerOpsBreakStructures", true);
            allowSoldierArrests = builder.comment("Npc soldiers will be able to arrest and imprison players.").comment("Criminal players will be released from jail after a certain amount of time has passed.").define("allowSoldierArrests", true);
            allowWarPrisonerArrests = builder.comment("Npc soldiers will be able to imprison players if they are at war. This is ignored if 'allowSoldierArrests' is false.").comment("WARNING: This is an experimental feature. Unlike criminals, war prisoners will not be released, and will have to escape in other ways.").define("allowWarPrisonerArrests", false);
            breedingCooldownAge = builder.comment("Sets the NPC's age in ticks after breeding.").define("breedingCooldownAge", 9600);
            childLimit = builder.comment("Sets the limit to the amount of children players can have.").comment("Set to 0 to disable player children, or set to -1 for no limit.").define("childLimit", 3);
            doNaturalMarriage = builder.comment("Determines whether npcs can get married to other npcs.").define("doNaturalMarriage", true);
            doNaturalBreeding = builder.comment("Determines whether npcs can have children on their own with their spouses.").define("doNaturalBreeding", true);
            doNpcTrades = builder.comment("Allows NPC villagers to trade with one another naturally.").comment("This allows the villagers to have an economy and generate food for the village on their own").define("doNpcTrades", true);
            builder.pop();
        }
    }

    public static final ForgeConfigSpec CLIENT_SPEC;
    public static final ForgeConfigSpec COMMON_SPEC;
    public static final ForgeConfigSpec SERVER_SPEC;

    public static final Client CLIENT;
    public static final Common COMMON;
    public static final Server SERVER;

    static {
        Pair<Client, ForgeConfigSpec> client = new ForgeConfigSpec.Builder().configure(Client::new);
        Pair<Common, ForgeConfigSpec> common = new ForgeConfigSpec.Builder().configure(Common::new);
        Pair<Server, ForgeConfigSpec> server = new ForgeConfigSpec.Builder().configure(Server::new);

        CLIENT_SPEC = client.getRight();
        COMMON_SPEC = common.getRight();
        SERVER_SPEC = server.getRight();

        CLIENT = client.getLeft();
        COMMON = common.getLeft();
        SERVER = server.getLeft();
    }
}
