package net.kitcaitie.otherworld.client;

import com.google.gson.stream.JsonReader;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.OtherworldClient;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.Descendant;
import net.kitcaitie.otherworld.common.entity.npcs.data.PersonData;
import net.kitcaitie.otherworld.common.player.PlayerCharacter;
import net.kitcaitie.otherworld.network.NetworkMessages;
import net.kitcaitie.otherworld.network.c2s.DialogueUpdateC2SPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@OnlyIn(Dist.CLIENT)
public class Dialogue {

    public enum Type {
        HUMAN("human.json", new HashMap<>()),
        FAIRIE("fairie.json", new HashMap<>()),
        ROSEIAN("roseian.json", new HashMap<>()),
        ONI("oni.json", new HashMap<>()),
        EMBERIAN("emberian.json", new HashMap<>()),
        ICEIAN("iceian.json", new HashMap<>()),
        GHOUL("ghoul.json", new HashMap<>()),
        DESCENDANT("descendant.json", new HashMap<>());

        private final String file;
        private final Map<String, Map<String, List<String>>> map;

        Type(String file, Map<String, Map<String, List<String>>> map) {
            this.map = map;
            this.file = file;
        }
        public String getFile() {
            return file;
        }
    }

    public final List<Type> dialogue;
    public List<String> languages = new ArrayList<>();
    public List<String> activeWarEvents = new ArrayList<>();

    public Dialogue() {
        this.dialogue = DialogueListener.loadDialouge(this);
    }

    public void say(AbstractPerson person, Player player, @Nullable String event) {
        if (!dialogue.isEmpty()) {
            Type type = dialogue.get(dialogue.indexOf(person.getDialogueType()));

            Map<String, List<String>> map = type.map.get(getLang());

            if (map != null && !map.isEmpty()) {
                NetworkMessages.sendToServer(new DialogueUpdateC2SPacket());

                String warContext = this.activeWarEvents.stream().filter((s) -> s.contains(person.getRace().name().toLowerCase()) && s.contains(OtherworldClient.getPlayerCharacter().getRace().name().toLowerCase())).findAny().orElse("");

                PersonData personData = person.getClientPersonData();

                String relationship = personData.getRelationship(player.getUUID()).relationStatus.name().toLowerCase();

                String familyType = personData.getRelationship(player.getUUID()).familyType != null ?
                        personData.getRelationship(player.getUUID()).familyType.name().toLowerCase() + "." : "";

                String race = type == Type.DESCENDANT ? person.getRace().name().toLowerCase() + "." : "";

                String dialogueLoc = race + familyType + relationship + "." + getPlayerData() + "." + getPersonData(person) + (event != null ? "." + event : "");

                if (!warContext.isBlank()) {
                    dialogueLoc = dialogueLoc + ".war:" + warContext;
                }

                // TODO: FOR TESTING ONLY ERASE THIS
                //System.out.println(dialogueLoc);

                List<String> list = map.getOrDefault(dialogueLoc, null);
                if (list != null && !list.isEmpty()) {
                    player.displayClientMessage(Component.literal(person.getDisplayName().getString() + ": " + list.get(player.getRandom().nextInt(list.size()))), false);
                }
            }
        }
    }

    private String getLang() {
        String currentLang = Minecraft.getInstance().getLanguageManager().getSelected();
        if (languages.contains(currentLang)) {
            return currentLang;
        }
        return LanguageManager.DEFAULT_LANGUAGE_CODE;
    }

    private static String getPersonData(AbstractPerson person) {
        String male = person.isMale() ? "male" : "female";
        String occupation = person.getOccupation().name().toLowerCase();
        return person.isBaby() ? "baby." + male : person instanceof Descendant ? male : occupation + "." + male;
    }

    private static String getPlayerData() {
        PlayerCharacter playerCharacter = OtherworldClient.getPlayerCharacter();
        String race = playerCharacter.getRace().name().toLowerCase();
        String male = playerCharacter.isMale() ? "male" : "female";
        String occupation = playerCharacter.getOccupation().name().toLowerCase();
        return race + "." + male + "." + occupation;
    }

    public static void loadType(Type type, String lang, BufferedReader reader) throws IOException {
        try {
            JsonReader jsonReader = new JsonReader(reader);
            jsonReader.beginObject();
            Map<String, List<String>> map = type.map.getOrDefault(lang, new HashMap<>());

            while (jsonReader.hasNext()) {
                String key = jsonReader.nextName();
                List<String> value = map.getOrDefault(key, new ArrayList<>());
                jsonReader.beginArray();
                if (jsonReader.hasNext() && !value.isEmpty()) value.clear();
                while (jsonReader.hasNext()) {
                    value.add(jsonReader.nextString());
                }
                jsonReader.endArray();
                map.put(key, value);
            }

            type.map.put(lang, map);

            jsonReader.endObject();
            jsonReader.close();
        }
        catch (Exception e) {
            Otherworld.LOGGER.warn("Dialogue for " + type.getFile() + " ran into an error while reading the file: " + e.getLocalizedMessage());
        }
        reader.close();
    }
}
