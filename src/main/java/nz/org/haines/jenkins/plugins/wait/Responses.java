package nz.org.haines.jenkins.plugins.wait;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import static java.util.concurrent.TimeUnit.MINUTES;


public class Responses {
    private final Cache<String, Boolean> responses = CacheBuilder.newBuilder().expireAfterWrite(1, MINUTES).build();

    public void confirm(String id) {
        responses.put(id, true);
    }

    public void cancel(String id) {
        responses.put(id, false);
    }

    public Boolean get(String id) {
        return responses.getIfPresent(id);
    }

    private static class Holder {
        private static final Responses INSTANCE = new Responses();
    }

    public static Responses getInstance() {
        return Holder.INSTANCE;
    }
}
