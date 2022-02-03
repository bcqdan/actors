package actors;

import net.jqwik.api.ForAll;
import net.jqwik.api.Property;
import net.jqwik.api.constraints.IntRange;
import net.jqwik.api.lifecycle.AfterProperty;
import net.jqwik.api.lifecycle.BeforeProperty;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

class RestaurantTest {

    private static ExecutorService executor;

    @BeforeProperty
    void setUp() {
        executor = Executors.newFixedThreadPool(32);
    }

    @AfterProperty
    void tearDown() {
        executor.shutdown();
    }

    @Property
    void test(@ForAll @IntRange(min = 1, max = 1000) int piePieces,
              @ForAll @IntRange(min = 1, max = 100) int numCustomers) {
        var restaurant = new Restaurant(executor, piePieces, numCustomers);
        try {
            restaurant.run();
        } catch (InterruptedException e) {
            fail(e);
        }
        var happy = restaurant.getCustomers().stream().map(Customer::isHappy).filter(b->b).count();
        assertEquals(Math.min(piePieces, numCustomers), happy);
    }

}