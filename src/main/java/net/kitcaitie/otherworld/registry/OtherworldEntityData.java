package net.kitcaitie.otherworld.registry;

import net.kitcaitie.otherworld.Otherworld;
import net.kitcaitie.otherworld.common.entity.npcs.data.PersonData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class OtherworldEntityData {
    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS =
            DeferredRegister.create(ForgeRegistries.Keys.ENTITY_DATA_SERIALIZERS, Otherworld.MODID);

    public static final RegistryObject<EntityDataSerializer<PersonData>> PERSON_DATA = DATA_SERIALIZERS.register("person_data", () ->
            new EntityDataSerializer<>() {
                @Override
                public void write(FriendlyByteBuf byteBuf, PersonData data) {
                    byteBuf.writeNbt(data.saveData());
                }

                @Override
                public PersonData read(FriendlyByteBuf byteBuf) {
                    return PersonData.readData(byteBuf.readNbt());
                }

                @Override
                public PersonData copy(PersonData data) {
                    return PersonData.copyFrom(data);
                }
            }
    );

    public static void register(IEventBus eventBus) {
        DATA_SERIALIZERS.register(eventBus);
    }
}
