package me.miki.shindo.management.cape.impl;

import me.miki.shindo.management.cape.CapeCategory;
import me.miki.shindo.management.roles.ClientRole;
import me.miki.shindo.utils.animation.simple.SimpleAnimation;
import net.minecraft.util.ResourceLocation;

public class Cape {

	private String name;
	private ResourceLocation cape;
	private CapeCategory category;
	private ClientRole requiredRole;

	private SimpleAnimation animation = new SimpleAnimation();

	public Cape(String name, ResourceLocation cape, CapeCategory category, ClientRole requiredRole) {
		this.name = name;
		this.category = category;
		this.cape = cape;
		this.requiredRole = requiredRole;
	}

	public String getName() {
		return name;
	}

	public CapeCategory getCategory() {
		return category;
	}

	public SimpleAnimation getAnimation() {
		return animation;
	}

	public ResourceLocation getCape() {
		return cape;
	}

	public ClientRole getRequiredRole() {
		return requiredRole;
	}
}
