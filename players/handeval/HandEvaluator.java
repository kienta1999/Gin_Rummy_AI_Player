package players.handeval;

import java.util.ArrayList;

import players.ParamList;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.Map;
import players.StateTracker;
import ginrummy.Card;

public interface HandEvaluator {

    public double evalHand(ArrayList<Card> handOf10, StateTracker myTracker, Card excludedCard);

    /**
     * Should ignore the argument if this HandEvaluator does not use a ParamList;
     * @param params
     */
    public void setParamList(ParamList params);

    /**
     * Should just return false all the time if this HandEvaluator does not
     * use a ParamList.
     */
    public boolean hasDifferentParamList(ParamList otherParams);
}