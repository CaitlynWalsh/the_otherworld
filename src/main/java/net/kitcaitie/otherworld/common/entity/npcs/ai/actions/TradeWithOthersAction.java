package net.kitcaitie.otherworld.common.entity.npcs.ai.actions;

import net.kitcaitie.otherworld.common.IOccupation;
import net.kitcaitie.otherworld.common.OtherworldConfigs;
import net.kitcaitie.otherworld.common.entity.npcs.AbstractPerson;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.trading.MerchantOffer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

public class TradeWithOthersAction extends Action {
    protected ItemStack personOffer = ItemStack.EMPTY;
    protected ItemStack traderOffer = ItemStack.EMPTY;
    protected AbstractPerson trader;
    protected MerchantOffer trade;
    protected boolean trading;
    protected int tradingTimer;
    protected int tradingCooldown;
    public TradeWithOthersAction(AbstractPerson person) {
        super(person);
    }

    @Override
    public boolean canStart() {
        if (OtherworldConfigs.SERVER.doNpcTrades.get()) {
            if (tradingCooldown <= 0) {
                if (person.getRandom().nextFloat() < 0.01F) {
                    if (person.level.isDay() && person.getInvolvedWar() == null) {
                        if (person.getItemInInventory(person::isEdibleItem).isEmpty() || person.getInventory().getNeededIngredient() != null) {
                            this.personOffer = person.getItemInInventory((stack) -> stack.is(person.getCurrency()));
                            List<AbstractPerson> people = new ArrayList<>();
                            if (person.getItemInInventory(person::isEdibleItem).isEmpty() && person.getJobType() != IOccupation.VillagerType.COOK && person.getJobType() != IOccupation.VillagerType.FARMER) {
                                people = ActionUtils.findGroupOfPeople(person, (p) -> isValidTrader(this.personOffer).test(p), 40.0D);
                            } else if (person.getInventory().getNeededIngredient() != null && !person.isVillagerWorkingHours()) {
                                if (person.getOffers() != null && person.getOffers().stream().anyMatch((offer) -> person.getInventory().getNeededIngredient().test(offer.getResult())))
                                    return false;
                                people = ActionUtils.findGroupOfPeople(person, (p) -> isValidTrader(person.getInventory().getNeededIngredient(), this.personOffer).test(p), 40.0D);
                            }
                            if (people.isEmpty()) {
                                this.tradingCooldown = 100;
                                return false;
                            }
                            this.trader = people.get(0);
                            return true;
                        }
                    }
                }
            } else --tradingCooldown;
        }
        return false;
    }

    @Override
    public boolean canContinue() {
        return this.trader != null && trader.isAlive() && tradingCooldown <= 0 && person.getInvolvedWar() == null && !person.getCombatTracker().isInCombat();
    }

    @Override
    public void start() {
        super.start();
        person.setItemInHand(InteractionHand.MAIN_HAND, this.personOffer);
        ActionUtils.stopMoving(person);
    }

    @Override
    public Priority getPriority() {
        return Priority.P3;
    }

    @Override
    public boolean stopLowerPriorities() {
        return true;
    }

    @Override
    public void tick() {
        if (this.person.distanceToSqr(this.trader) < 4.0D) {
            ActionUtils.stopMoving(trader);
            ActionUtils.lookAt(this.trader, this.person);
            if (!trading) {
                this.trader.getAi().startMindControl(this.person);
                this.tradingTimer = 80;
                this.trading = true;
            } else {
                --tradingTimer;
                if (tradingTimer <= 40 && !trader.getItemInHand(InteractionHand.MAIN_HAND).is(this.traderOffer.getItem())) {
                    this.trader.setItemInHand(InteractionHand.MAIN_HAND, this.traderOffer);
                }
                if (tradingTimer <= 0) {
                    person.swing(InteractionHand.MAIN_HAND);
                    trader.swing(InteractionHand.MAIN_HAND);

                    person.getInventory().removeItemType(this.personOffer.getItem(), trade.getCostA().getCount());
                    trader.getInventory().removeItemType(this.traderOffer.getItem(), trade.getResult().getCount());

                    trader.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

                    person.getInventory().addItem(this.traderOffer.copy());
                    trader.getInventory().addItem(this.personOffer.copy());

                    person.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

                    this.trader.getAi().stopMindControl();
                    this.trading = false;
                    this.tradingCooldown = 60;
                }
            }
        } else {
            ActionUtils.lookAndMoveTo(this.person, this.trader, 0.85D);
        }
    }

    @Override
    public void stop() {
        super.stop();
        person.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        if (this.trader != null) {
            this.trader.getAi().stopMindControl();
            this.trader.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);
        }
        this.trader = null;
        this.personOffer = ItemStack.EMPTY;
        this.traderOffer = ItemStack.EMPTY;
    }

    protected Predicate<AbstractPerson> isValidTrader(Ingredient ingredient, ItemStack stack) {
        return (p) -> {
            if (p.is(person)) return false;
            if (stack.isEmpty()) return false;
            if (p.getAi() == null || p.getAi().isMindControlled()) return false;
            if (p.getRace() == person.getRace() && p.getOffers() != null) {
                MerchantOffer merchantOffer = p.getOffers().stream().filter((offer) -> ingredient.test(offer.getResult())).filter((offer) ->
                        offer.getBaseCostA().is(stack.getItem()) && stack.getCount() >= offer.getBaseCostA().getCount()
                        && p.getInventory().hasAnyMatching((i) -> offer.getResult().is(i.getItem()) && i.getCount() >= offer.getResult().getCount())).findAny().orElse(null);
                if (merchantOffer != null) {
                    this.trade = merchantOffer;
                    this.traderOffer = merchantOffer.getResult().copy();
                    return true;
                }
            }
            return false;
        };
    }

    protected Predicate<AbstractPerson> isValidTrader(ItemStack stack) {
        return (p) -> {
            if (p.is(person)) return false;
            if (stack.isEmpty()) return false;
            if (p.getAi() == null || p.getAi().isMindControlled()) return false;
            if (p.getRace() == person.getRace() && p.getOffers() != null) {
                MerchantOffer merchantOffer = p.getOffers().stream().filter((offer) -> person.isEdibleItem(offer.getResult())).filter((offer) ->
                        offer.getBaseCostA().is(stack.getItem()) && stack.getCount() >= offer.getBaseCostA().getCount()
                        && p.getInventory().hasAnyMatching((i) -> offer.getResult().is(i.getItem()) && i.getCount() >= offer.getResult().getCount())).findAny().orElse(null);
                if (merchantOffer != null) {
                    this.trade = merchantOffer;
                    this.traderOffer = merchantOffer.getResult().copy();
                    return true;
                }
            }
            return false;
        };
    }

}
