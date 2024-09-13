package net.kitcaitie.otherworld.client;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.OtherworldClient;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@OnlyIn(Dist.CLIENT)
public class DialogueListener implements ResourceManagerReloadListener {
    public static DialogueListener INSTANCE = new DialogueListener();
    public static final String DIALOGUE_LOCATION = "dialogue/";

    private DialogueListener() {}

    public static List<Dialogue.Type> loadDialouge(Dialogue dialogue) {
        try {
            Set<String> langs = Minecraft.getInstance().getLanguageManager().getLanguages().keySet();

            List<Dialogue.Type> list = new ArrayList<>();

            for (Dialogue.Type dialogueType : Dialogue.Type.values()) {

                for (String lang : langs) {
                    try {
                        BufferedReader bufferedReader = Minecraft.getInstance().getResourceManager().openAsReader(new ResourceLocation(Otherworld.MODID,DIALOGUE_LOCATION + lang + "/" + dialogueType.getFile()));
                        Dialogue.loadType(dialogueType, lang, bufferedReader);
                        if (!dialogue.languages.contains(lang)) {
                            dialogue.languages.add(lang);
                        }
                    }
                    catch (IOException ignored) {}
                }

                list.add(dialogueType);

            }
            return list;
        }
        catch (Exception e) {
            Otherworld.LOGGER.error("DialogueListener: Failed to load dialogue due to exception: " + e.getLocalizedMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        List<Dialogue.Type> types = loadDialouge(OtherworldClient.DIALOGUE);
        if (!types.isEmpty()) {
            OtherworldClient.DIALOGUE.dialogue.clear();
            OtherworldClient.DIALOGUE.dialogue.addAll(types);
        }
    }
}
