package players.handeval;

import java.util.ArrayList;

import ginrummy.Card;
import players.StateTracker;
import players.ParamList;

public interface KnockDecider {
    /**
     * Returns final melds, or null if the player should not knock.
     */
    public ArrayList<ArrayList<Card>> shouldKnock(ArrayList<Card> hand, StateTracker myTracker);

    public boolean hasDifferentParamList(ParamList otherParams);

    public void setParamList(ParamList newParams);
}