package me.miki.shindo.management.event.impl;

import me.miki.shindo.management.event.Event;

public class EventToggleFullscreen extends Event {

	private boolean state;
	private boolean applyState;
	
	public EventToggleFullscreen(boolean state) {
		this.state = state;
		this.applyState = true;
	}

	public boolean getState() {
		return state;
	}

	public void setState(boolean state) {
		this.state = state;
	}

	public boolean isApplyState() {
		return applyState;
	}

	public void setApplyState(boolean applyState) {
		this.applyState = applyState;
	}
}
