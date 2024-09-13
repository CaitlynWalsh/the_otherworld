package net.kitcaitie.otherworld.common.util;

import com.google.gson.stream.JsonReader;
import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.IRaces;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.network.chat.Component;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NameGenerator {
    public static final Map<String, List<String>> NAMES = new HashMap<>();

    public NameGenerator() {
        loadNames();
    }

    public void createName(AbstractPerson person) {
        if (person.canHaveName()) {
            List<String> name = NAMES.get(person.getRace().name().toLowerCase() + (person.isMale() ? "_male" : "_female"));
            if (name != null && !name.isEmpty()) {
                person.setCustomName(Component.literal(name.get(person.getRandom().nextInt(name.size()))));
            }
        }
    }

    private void loadNames() {
        for (IRaces.Race race : IRaces.Race.values()) {
            try {
                NAMES.put(race.name().toLowerCase() + "_male", this.parseNames(race, true));
                NAMES.put(race.name().toLowerCase() + "_female", this.parseNames(race, false));
            }
            catch (IOException e) {
                NAMES.put(race.name().toLowerCase() + "_male", new ArrayList<>());
                NAMES.put(race.name().toLowerCase() + "_female", new ArrayList<>());
                Otherworld.LOGGER.warn("NameGenerator: Unable to load name for: " + race.name().toLowerCase() + ": " + e.getMessage());
            }
        }
    }

    private List<String> parseNames(IRaces.Race race, boolean male) throws IOException {
        ArrayList<String> names = new ArrayList<>();
        String gender = male ? "male" : "female";
        ClassLoader classLoader = this.getClass().getClassLoader();

        InputStream inputStream = null;
        InputStreamReader inputStreamReader = null;

        try {
            inputStream = classLoader.getResourceAsStream("names.json");
            inputStreamReader = new InputStreamReader(inputStream);
            JsonReader json = new JsonReader(inputStreamReader);
            json.beginObject();
            while (json.hasNext()) {
                String info = json.nextName();
                if (info.equals(race.name().toLowerCase())) {
                    json.beginObject();
                    while (json.hasNext()) {
                        String nameArray = json.nextName();
                        if (nameArray.equals(gender)) {
                            json.beginArray();
                            while (json.hasNext()) {
                                names.add(json.nextString());
                            }
                            json.close();
                            inputStreamReader.close();
                            inputStream.close();
                            return names;
                        }
                        json.skipValue();
                    }
                    json.endObject();
                }
                json.skipValue();
            }
            json.close();
        }
        catch (IOException ignored) {
        }
        finally {
            if (inputStreamReader != null) {
                inputStreamReader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return new ArrayList<>();
    }

}
