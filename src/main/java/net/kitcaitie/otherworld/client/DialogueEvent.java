package net.kitcaitie.otherworld.client;

public enum DialogueEvent {

    HURT("hurt"),
    DEATH("death"),
    WOOING("wooing"),
    WOOING_FAIL("wooing_fail"),
    BREAK_UP("break_up"),
    PROPOSAL("proposal"),
    PROPOSAL_FAIL("proposal_fail"),
    GIFT("gift"),
    GIFTING_FOOD("gifting_food"),
    INV_FULL("inv_full"),
    PRISON_RELEASE("prison_release"),
    QUEST_PROMPT("quest_prompt"),
    QUEST_DENY("quest_deny"),
    COMPLETE("_complete"),
    ASSIGN("_assign"),
    DENY("_denied"),
    QUEST_FULL("quest_full"),
    JOB_PROMPT("job_prompt");

    private final String event;

    DialogueEvent(String event) {
        this.event = event;
    }

    public String getString() {
        return event;
    }

}
