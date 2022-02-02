package actors;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestaurantTest {

    @Test
    void test() {
        var restaurant = new Restaurant(4, 10);
        restaurant.run();
        var happy = restaurant.getCustomers().stream().map(Customer::isHappy).filter(b->b).count();
        assertEquals(4, happy);
    }

}