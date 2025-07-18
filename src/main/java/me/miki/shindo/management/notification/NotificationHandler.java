package me.miki.shindo.management.notification;

import java.util.concurrent.LinkedBlockingQueue;

import me.miki.shindo.management.event.EventTarget;
import me.miki.shindo.management.event.impl.EventRenderNotification;

public class NotificationHandler {

	private LinkedBlockingQueue<Notification> notifications;
	private Notification currentNotification;
	
	public NotificationHandler(LinkedBlockingQueue<Notification> notifications) {
		this.notifications = notifications;
	}
	
	@EventTarget
	public void onRenderNotification(EventRenderNotification event) {
		
		if(currentNotification != null && !currentNotification.isShown()) {
			currentNotification = null;
		}
		
		if(currentNotification == null && !notifications.isEmpty()) {
			currentNotification = notifications.poll();
			currentNotification.show();
		}
		
		if(currentNotification != null) {
			currentNotification.draw();
		}
	}
}
