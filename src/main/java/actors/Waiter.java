package actors;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import events.*;

public class Waiter {

    private final AsyncEventBus eventBus;

    public Waiter(AsyncEventBus eventBus) {
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Subscribe
    public void process(OrderPieEvent orderPieEvent) {
        eventBus.post(new SendPieEvent(orderPieEvent.customerId()));
    }

    @Subscribe
    public void process(DeliverPieEvent deliverPieEvent) {
        eventBus.post(new PlacePieOnTableEvent(deliverPieEvent.customerId()));
//        eventBus.post(new BillCustomerEvent(deliverPieEvent.customerId(), 1));
    }

    @Subscribe
    public void process(DeliverNoPieEvent deliverNoPieEvent) {
        eventBus.post(new SorryEvent(deliverNoPieEvent.customerId()));
    }

}
