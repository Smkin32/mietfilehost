package miet.server.files;

import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class RequestCountingInterceptor implements HandlerInterceptor {

    private final ApiRequestCounter counter;

    public RequestCountingInterceptor(ApiRequestCounter counter) {
        this.counter = counter;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        counter.increment();
        return true;
    }
}