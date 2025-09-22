/*
 * Copyright 2015-2021 Adrien 'Litarvan' Navratil
 *
 * This file is part of OpenAuth.

 * OpenAuth is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenAuth is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with OpenAuth.  If not, see <http://www.gnu.org/licenses/>.
 */
package me.miki.shindo.libs.openauth.microsoft;

import lombok.Getter;
import me.miki.shindo.libs.openauth.microsoft.model.response.MinecraftProfile;

/**
 * Microsoft authentication result
 *
 * <p>
 * This class contains the result of a successful Microsoft authentication: a player profile and its tokens (both
 * access and refresh token).
 * </p>
 *
 * @author Litarvan
 * @version 1.1.5
 */

@Getter
public class MicrosoftAuthResult {
    /**
     * -- GETTER --
     *
     * @return The player Minecraft profile (contains its UUID and username)
     */
    private final MinecraftProfile profile;
    /**
     * -- GETTER --
     *
     * @return The Minecraft access token
     */
    private final String accessToken;
    /**
     * -- GETTER --
     *
     * @return The Microsoft refresh token that can be used to log the user back silently using
     * {@link MicrosoftAuthenticator#loginWithRefreshToken(String)}
     */
    private final String refreshToken;
    /**
     * -- GETTER --
     *
     * @return The XUID of the player
     */
    private final String xuid;
    /**
     * -- GETTER --
     *
     * @return The client ID of the player
     */
    private final String clientId;

    public MicrosoftAuthResult(MinecraftProfile profile, String accessToken, String refreshToken, String xuid, String clientId)
    {
        this.profile = profile;
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.xuid = xuid;
        this.clientId = clientId;
    }

}
