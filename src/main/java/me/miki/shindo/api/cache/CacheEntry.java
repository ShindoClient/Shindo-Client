package me.miki.shindo.api.cache;

import com.google.gson.JsonObject;

public class CacheEntry {

    private static final long CACHE_EXPIRATION_MS = 30_000; // 30 segundos

    public JsonObject data;
    public long timestamp;

    public CacheEntry(JsonObject data) {
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return System.currentTimeMillis() - timestamp > CACHE_EXPIRATION_MS;
    }
}
