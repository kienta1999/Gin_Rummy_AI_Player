package players.handeval;

import java.util.ArrayList;
import ginrummy.Card;
import players.StateTracker;
import players.ParamList;

public interface DrawDecider {
    public boolean shouldDraw(ArrayList<Card> hand, Card faceUpCard, StateTracker myTracker);
    public boolean hasDifferentParamList(ParamList otherParams);
}