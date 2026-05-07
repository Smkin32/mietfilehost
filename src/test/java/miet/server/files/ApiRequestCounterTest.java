package miet.server.files;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiRequestCounterTest {

    private final ApiRequestCounter counter = new ApiRequestCounter();

    @Test
    void increment_ShouldIncreaseCount() {
        counter.increment();
        counter.increment();

        assertEquals(2, counter.getTotalRequests());
    }

    @Test
    void getTotalRequests_ShouldFilterOldTimestamps() throws InterruptedException {
        counter.increment();
        counter.increment();

        long countBefore = counter.getTotalRequests();
        assertEquals(2, countBefore);

        long countAfter = counter.getTotalRequests();
        assertEquals(2, countAfter);
    }
}