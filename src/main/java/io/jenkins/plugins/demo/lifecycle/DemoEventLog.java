package io.jenkins.plugins.demo.lifecycle;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 内存事件日志，供「演示中心」页面展示各扩展点的触发情况。
 */
public final class DemoEventLog {

    private static final int MAX_EVENTS = 200;
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());

    private static final List<Event> EVENTS = Collections.synchronizedList(new ArrayList<>());
    private static final Map<String, AtomicInteger> COUNTERS = new ConcurrentHashMap<>();

    private DemoEventLog() {}

    public static void record(String extensionPoint, String phase, String detail) {
        COUNTERS.computeIfAbsent(extensionPoint, key -> new AtomicInteger()).incrementAndGet();
        Event event = new Event(Instant.now(), extensionPoint, phase, detail);
        synchronized (EVENTS) {
            EVENTS.add(0, event);
            while (EVENTS.size() > MAX_EVENTS) {
                EVENTS.remove(EVENTS.size() - 1);
            }
        }
    }

    public static List<Event> getRecentEvents() {
        synchronized (EVENTS) {
            return List.copyOf(EVENTS);
        }
    }

    public static Map<String, Integer> getTriggerCounts() {
        Map<String, Integer> counts = new LinkedHashMap<>();
        COUNTERS.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .forEach(entry -> counts.put(entry.getKey(), entry.getValue().get()));
        return counts;
    }

    public static void clear() {
        synchronized (EVENTS) {
            EVENTS.clear();
        }
        COUNTERS.clear();
    }

    public record Event(Instant time, String extensionPoint, String phase, String detail) {
        public String formattedTime() {
            return FORMATTER.format(time);
        }
    }
}
