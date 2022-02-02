package actors;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import events.DeliverNoPieEvent;
import events.DeliverPieEvent;
import events.SendPieEvent;

public class Pie {

    private final AsyncEventBus eventBus;

    private volatile int pieces;

    public Pie(AsyncEventBus eventBus, int pieces) {
        this.eventBus = eventBus;
        this.pieces = pieces;
        eventBus.register(this);
    }

    @Subscribe
    public void process(SendPieEvent sendPieEvent) {
        if (pieces > 0) {
            pieces--;
            eventBus.post(new DeliverPieEvent(sendPieEvent.customerId()));
        } else {
            eventBus.post(new DeliverNoPieEvent(sendPieEvent.customerId()));
        }
    }

}
