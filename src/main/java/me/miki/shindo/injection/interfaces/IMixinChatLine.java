package me.miki.shindo.injection.interfaces;

import java.lang.ref.WeakReference;
import java.util.HashSet;

import net.minecraft.client.gui.ChatLine;
import net.minecraft.client.network.NetworkPlayerInfo;

public interface IMixinChatLine {
    HashSet<WeakReference<ChatLine>> chatLines = new HashSet<>();
    NetworkPlayerInfo client$getPlayerInfo();
}
