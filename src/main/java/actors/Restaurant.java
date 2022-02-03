package actors;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import events.CustomerDoneEvent;
import events.HungryEvent;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Restaurant {

    private final AsyncEventBus eventBus;
    private final Pie pie;
    private final Waiter waiter;

    @Getter
    private final ArrayList<Customer> customers;

    private final CountDownLatch countDownLatch;

    public Restaurant(ExecutorService executor, int piePieces, int numCustomers) {
        eventBus = new AsyncEventBus(executor, (throwable, subscriberExceptionContext) -> {
            throwable.printStackTrace();
            System.exit(0);
        });
        pie = new Pie(eventBus, piePieces);
        waiter = new Waiter(eventBus);
        customers = new ArrayList<>(numCustomers);
        for (var i = 0; i < numCustomers; i++) {
            var customer = new Customer(String.valueOf(i), eventBus);
            customers.add(customer);
        }
        eventBus.register(this);
        countDownLatch = new CountDownLatch(numCustomers);
    }

    @Subscribe
    void process(CustomerDoneEvent customerDoneEvent) {
        countDownLatch.countDown();
    }

    void run() throws InterruptedException {
        for (var customer : customers) {
            eventBus.post(new HungryEvent(customer.getId()));
        }
        assert countDownLatch.await(1, TimeUnit.SECONDS);
    }

    public static void main(String[] args) throws InterruptedException {
        new Restaurant(Executors.newFixedThreadPool(32), 10, 10).run();
    }
}
