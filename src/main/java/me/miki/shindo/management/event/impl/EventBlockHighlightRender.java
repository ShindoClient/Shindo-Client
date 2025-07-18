package me.miki.shindo.management.event.impl;

import me.miki.shindo.management.event.Event;
import net.minecraft.util.MovingObjectPosition;

public class EventBlockHighlightRender extends Event {

	private MovingObjectPosition objectMouseOver;
	private float partialTicks;
	
	public EventBlockHighlightRender(MovingObjectPosition objectMouseOver, float partialTicks) {
		this.objectMouseOver = objectMouseOver;
		this.partialTicks = partialTicks;
	}

	public MovingObjectPosition getObjectMouseOver() {
		return objectMouseOver;
	}

	public float getPartialTicks() {
		return partialTicks;
	}
}
