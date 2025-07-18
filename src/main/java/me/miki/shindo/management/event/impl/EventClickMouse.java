package me.miki.shindo.management.event.impl;

import me.miki.shindo.management.event.Event;

public class EventClickMouse extends Event {

	private int button;
	
	public EventClickMouse(int button) {
		this.button = button;
	}

	public int getButton() {
		return button;
	}
}
