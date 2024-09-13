package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import com.mojang.datafixers.util.Pair;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.kitcaitie.otherworld.common.entity.npcs.data.PersonData;
import net.minecraft.world.entity.LivingEntity;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class FamilyInteractionAction extends Action {
    @Nullable protected UUID spouse;
    protected List<Long> parents = new ArrayList<>();
    protected List<UUID> playerParents = new ArrayList<>();
    protected List<Long> children = new ArrayList<>();
    private int checkRefresh = 0;
    @Nullable protected LivingEntity interactTarget;

    public FamilyInteractionAction(AbstractPerson person) {
        super(person);
        this.refresh();
    }

    @Override
    public boolean canStart() {
        checkRefresh();
        return true;
    }

    @Override
    public void tick() {
        checkRefresh();
        super.tick();
    }

    @Override
    public void stop() {
        super.stop();
        this.interactTarget = null;
    }

    protected void checkRefresh() {
        if (--checkRefresh <= 0) {
            refresh();
            checkRefresh = 300;
        }
    }

    private void refresh() {
        PersonData data = person.getPersonData();
        this.spouse = data.getSpouse() != null ? data.getSpouse().getFirst() : null;
        if (data.hasParents()) {
            this.parents.clear();
            this.playerParents.clear();
            for (Pair<Long, ?> pair : data.getParents()) {
                this.parents.add(pair.getFirst());
            }
            for (Pair<UUID, ?> pair : data.getPlayerParents()) {
                this.playerParents.add(pair.getFirst());
            }
        }
        if (!data.getChildren().isEmpty()) {
            this.children.clear();
            this.children.addAll(data.getChildren().keySet());
        }
    }


}
