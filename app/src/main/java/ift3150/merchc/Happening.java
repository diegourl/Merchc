package ift3150.merchc;

/**
 * Created by Diego on 2015-05-18.
 */
public class Happening {
    private static final String TAG = "Happening";
    private final String type;
    private String outcomeMessage;

    public static Happening happen(String type){
        switch (type){
            case Event.PIRATE_ATTACK :
                return pirateAttack();
            case Event.STORM :
                return storm();
            case Event.MONEY_SHORTAGE :
                return moneyShortage();
            case Event.FOOD_SHORTAGE :
                return foodShortage();
            default :
                return none();
        }
    }


    private Happening(String type){
        this.type = type;
    }


///@TODO add and improve
    private static Happening none() {
        return new Happening(Event.NOTHING_HAPPENED);
    }

    private static Happening storm() {
        Happening happening = new Happening(Event.STORM);
        happening.setOutcomeMessage("You braved a storm.");
        return happening;
    }

    private static Happening pirateAttack() {
        Happening happening = new Happening(Event.PIRATE_ATTACK);
        happening.setOutcomeMessage("You were attacked by pirates.");
        return happening;
    }

    private static Happening moneyShortage() {
        Happening happening = new Happening(Event.MONEY_SHORTAGE);
        happening.setOutcomeMessage("You ran out of money.");
        return happening;
    }

    private static Happening foodShortage() {
        Happening happening = new Happening(Event.FOOD_SHORTAGE);
        happening.setOutcomeMessage("You ran out of food.");
        return happening;
    }







    public String getOutcomeMessage() {
        return outcomeMessage;
    }

    private void setOutcomeMessage(String outcomeMessage) {
        this.outcomeMessage = outcomeMessage;
    }

    public String getType() {
        return type;
    }
}
