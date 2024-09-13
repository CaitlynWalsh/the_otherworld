package net.kitcaitie.otherworld.common.story.events;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.function.Function;

public class EventTrigger {
    private Function<Context, Trigger> function;
    private @Nullable Object obj;

    public EventTrigger(Function<Context, Trigger> function) {
        this.function = function;
    }

    public EventTrigger of(Object object) {
        EventTrigger trigger = new EventTrigger(function);
        trigger.obj = object;
        return trigger;
    }

    public void trigger(Player player, @Nullable LivingEntity entity, @Nullable Event cause) {
        Trigger result = this.function.apply(new Context(player, entity, cause, obj));
        result.run();
    }

    public static class Context {
        public final Player player;
        public final @Nullable LivingEntity person;
        public final @Nullable Event event;
        public final @Nullable Object object;

        public Context(Player player, @Nullable LivingEntity person, @Nullable Event event, @Nullable Object o) {
            this.player = player;
            this.person = person;
            this.event = event;
            this.object = o;
        }
    }

    public static class Trigger implements Runnable {
        public final Context context;
        private final Runnable runnable;

        public Trigger(Context context, Runnable runnable) {
            this.context = context;
            this.runnable = runnable;
        }

        @Override
        public void run() {
            this.runnable.run();
        }
    }

}
