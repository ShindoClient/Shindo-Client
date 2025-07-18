package me.miki.shindo.management.profile.mainmenu.impl;

import me.miki.shindo.utils.animation.simple.SimpleAnimation;

public class Background {

	private SimpleAnimation focusAnimation = new SimpleAnimation();
	private int id;
	private String name;
	
	public Background(int id, String name) {
		this.name = name;
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	public SimpleAnimation getFocusAnimation() {
		return focusAnimation;
	}

	public String getName() {
		return name == null ? "null" : name;
	}
}
