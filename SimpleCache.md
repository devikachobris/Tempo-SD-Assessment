## Code Review

You are reviewing the following code submitted as part of a task to implement an item cache in a highly concurrent application. The anticipated load includes: thousands of reads per second, hundreds of writes per second, tens of concurrent threads.
Your objective is to identify and explain the issues in the implementation that must be addressed before deploying the code to production. Please provide a clear explanation of each issue and its potential impact on production behaviour.

```java
import java.util.concurrent.ConcurrentHashMap;

public class SimpleCache<K, V> {
    private final ConcurrentHashMap<K, CacheEntry<V>> cache = new ConcurrentHashMap<>();
    private final long ttlMs = 60000; // 1 minute

    public static class CacheEntry<V> {
        private final V value;
        private final long timestamp;

        public CacheEntry(V value, long timestamp) {
            this.value = value;
            this.timestamp = timestamp;
        }

        public V getValue() {
            return value;
        }

        public long getTimestamp() {
            return timestamp;
        }
    }

    public void put(K key, V value) {
        cache.put(key, new CacheEntry<>(value, System.currentTimeMillis()));
    }

    public V get(K key) {
        CacheEntry<V> entry = cache.get(key);
        if (entry != null) {
            if (System.currentTimeMillis() - entry.getTimestamp() < ttlMs) {
                return entry.getValue();
            }
        }
        return null;
    }

    public int size() {
        return cache.size();
    }
}
```
## Code Review Comments
1. Stale Entries Accumulation (Memory Leak Risk)
   - Issue: The cache never removes expired entries. Reads (`get()`) check the TTL but do not remove expired items, and there is no background cleanup.
   - Impact: Under high write/read load, the ConcurrentHashMap can grow indefinitely, consuming increasing memory. This can lead to OutOfMemoryError in long-running applications.

2. TTL is fixed and not configurable
    - Issue: `ttlMs` is hardcoded with a fixed value.
    - Impact: Cannot tune cache behaviour without code changes.This value may/may not be environment specific.
   
3. Time-Based Expiration is Only Lazy
   - Issue: Expiration is checked only on `get()`. If a key is never accessed again after expiration, it remains in memory forever.
   - Impact: Similar to issue #1. It also means `size()` returns misleading results, counting expired entries as active. This could mislead metrics and monitoring systems.

4. No Null-Value policy defined
    - Issue: `get` returns `null` for missing keys and expired entries.These can be indistinguishable.
    - Impact: Caller method cannot tell whether the value was never cached or expired or null.

5. No Lock-Free Eviction Strategy
   - Issue: The current TTL logic does not support high-performance eviction under concurrent writes. Concurrent writes and reads may contend with each other if eviction were added naively.
   - Impact: Under high write concurrency, adding eviction or cleanup logic without careful design could cause CPU spikes or contention bottlenecks.

6. Potential Inaccuracy in TTL Calculation
   - Issue: TTL is based on `System.currentTimeMillis()`, which can be affected by system clock changes (e.g., NTP adjustments or manual changes).
   - Impact: Entries may appear expired or valid incorrectly, leading to unexpected cache misses or retention beyond TTL.

7. No Atomicity Between `get` and `put`
   - Issue: There is no atomic check-then-act logic (e.g., `get` followed by `put` on a miss). While ConcurrentHashMap provides thread-safe access, complex operations like conditional updates require additional mechanisms.
   - Impact: Race conditions can occur if the cache needs to support lazy-loading or refresh-on-miss logic, which can lead to inconsistent data being returned under high concurrency.

8. Lack of Maximum Size / Eviction Policy
   - Issue: The cache does not limit the number of entries. High write throughput will eventually cause unbounded memory growth.
   - Impact: High-memory pressure can cause JVM GC pauses or crashes under production load. In addition, performance of ConcurrentHashMap may degrade with very large maps.

9. `size()` is Expensive and Misleading Under Concurrency
   - Issue: `ConcurrentHashMap.size()` may traverse internal segments for an accurate count in concurrent scenarios. Expired entries are still counted.
   - Impact: Calling `size()` frequently under high concurrency may introduce unnecessary overhead and give misleading cache usage statistics.

10. No Support for Soft/Weak References
    - Issue: All entries are strongly referenced. If memory becomes tight, the cache cannot automatically release entries.
    - Impact: In a memory-constrained environment, this can increase memory pressure, especially under high load with many entries.