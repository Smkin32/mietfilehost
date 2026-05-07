package miet.server.files;

import org.springframework.stereotype.Component;
import java.util.concurrent.ConcurrentLinkedDeque;

@Component
public class ApiRequestCounter {

    private final ConcurrentLinkedDeque<Long> timestamps = new ConcurrentLinkedDeque<>();

    public void increment() {
        timestamps.add(System.currentTimeMillis());
    }

    public long getTotalRequests() {
        long cutoff = System.currentTimeMillis() - 24 * 60 * 60 * 1000;
        while (!timestamps.isEmpty() && timestamps.peekFirst() < cutoff) {
            timestamps.pollFirst();
        }
        return timestamps.size();
    }
}