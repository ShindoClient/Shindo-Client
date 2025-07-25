package me.miki.shindo.management.roles;

import me.miki.shindo.Shindo;
import me.miki.shindo.ShindoAPI;
import me.miki.shindo.logger.ShindoLogger;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class ClientRoleManager {

    private static final Map<UUID, ClientRole> roleCache = new HashMap<>();

    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    private static final List<ClientRole> ROLE_HIERARCHY = Arrays.asList(
            ClientRole.STAFF,
            ClientRole.DIAMOND,
            ClientRole.GOLD,
            ClientRole.MEMBER
    );

    public static void start(){
        executor.scheduleAtFixedRate(ClientRoleManager::clearCache, 0, 30, TimeUnit.SECONDS);
    }

    public static void stop(){
        executor.shutdownNow();
    }

    public static ClientRole getRole(UUID uuid) {
        if (roleCache.containsKey(uuid)) {
            return roleCache.get(uuid);
        }

        ShindoAPI api = Shindo.getInstance().getShindoAPI();
        String uuidStr = uuid.toString();

        ClientRole detectedRole = ClientRole.MEMBER;

        ShindoLogger.info("Checking role " + uuidStr + " for " + detectedRole);
        for (ClientRole role : ROLE_HIERARCHY) {
            if (api.hasRole(uuidStr, role.name().toLowerCase(Locale.ROOT))) {
                detectedRole = role;
                ShindoLogger.info("Detected role: " + detectedRole.name());
                break;
            } else {
                detectedRole = ClientRole.MEMBER;
                ShindoLogger.info("Detected role: " + detectedRole.name());
                break;
            }
        }

        roleCache.put(uuid, detectedRole);
        return detectedRole;
    }

    public static boolean hasRole(UUID uuid, ClientRole required) {
        return getRole(uuid).hasPermission(required);
    }

    public static void clearCache() {
        roleCache.clear();
    }
}
