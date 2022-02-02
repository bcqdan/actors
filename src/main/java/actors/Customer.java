package actors;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import events.*;
import lombok.Getter;

public final class Customer {

    @Getter
    private final String id;
    private final AsyncEventBus eventBus;

    @Getter
    private boolean happy;

    public Customer(String id, AsyncEventBus eventBus) {
        this.id = id;
        this.eventBus = eventBus;
        eventBus.register(this);
    }

    @Subscribe
    public void process(HungryEvent hungryEvent) {
        if (!hungryEvent.customerId().equals(id)) {
            return;
        }
        eventBus.post(new OrderPieEvent(id));
    }

    @Subscribe
    public void process(PlacePieOnTableEvent placePieOnTableEvent) {
        if (!placePieOnTableEvent.customerId().equals(id)) {
            return;
        }
        happy = true;
        eventBus.post(new CustomerDoneEvent(id));
    }

    @Subscribe
    public void process(SorryEvent sorryEvent) {
        if (!sorryEvent.customerId().equals(id)) {
            return;
        }
        happy = false;
        eventBus.post(new CustomerDoneEvent(id));
    }

    @Override
    public String toString() {
        return "Customer " + id;
    }
}
