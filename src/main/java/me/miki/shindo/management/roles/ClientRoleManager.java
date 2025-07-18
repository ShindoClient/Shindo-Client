package me.miki.shindo.management.roles;

import me.miki.shindo.Shindo;
import me.miki.shindo.ShindoAPI;

import java.util.*;

public class ClientRoleManager {

    private static final Map<UUID, ClientRole> roleCache = new HashMap<>();

    private static final List<ClientRole> ROLE_HIERARCHY = Arrays.asList(
            ClientRole.STAFF,
            ClientRole.DIAMOND,
            ClientRole.GOLD,
            ClientRole.MEMBER
    );

    public static ClientRole getRole(UUID uuid) {
        if (roleCache.containsKey(uuid)) {
            return roleCache.get(uuid);
        }

        ShindoAPI api = Shindo.getInstance().getShindoAPI();
        String uuidStr = uuid.toString();

        ClientRole detectedRole = ClientRole.MEMBER;

        for (ClientRole role : ROLE_HIERARCHY) {
            if (!role.equals(ClientRole.MEMBER) &&
                    api.hasPrivilege(uuidStr, role.name().toLowerCase(Locale.ROOT))) {
                detectedRole = role;
                break;
            }
        }

        roleCache.put(uuid, detectedRole);
        return detectedRole;
    }

    public static boolean hasPermission(UUID uuid, ClientRole required) {
        return getRole(uuid).hasPermission(required);
    }

    public static void clearCache() {
        roleCache.clear();
    }
}
