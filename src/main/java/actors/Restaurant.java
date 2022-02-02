package actors;

import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.Subscribe;
import events.CustomerDoneEvent;
import events.HungryEvent;
import lombok.Getter;
import lombok.SneakyThrows;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class Restaurant {

    private final ExecutorService executor;
    private final AsyncEventBus eventBus;
    private final Pie pie;
    private final Waiter waiter;

    @Getter
    private final ArrayList<Customer> customers;

    private final CountDownLatch countDownLatch;

    public Restaurant(int piePieces, int numCustomers) {
        executor = Executors.newFixedThreadPool(32);
        eventBus = new AsyncEventBus("eventbus", executor);
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

    @SneakyThrows
    void run() {
        for (var customer : customers) {
            eventBus.post(new HungryEvent(customer.getId()));
        }
        assert countDownLatch.await(1, TimeUnit.SECONDS);
    }

    public static void main(String[] args) {
        new Restaurant(10, 10).run();
    }
}
