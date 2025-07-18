package me.miki.shindo.management.security;

import java.util.ArrayList;

import me.miki.shindo.management.security.impl.DemoSecurity;
import me.miki.shindo.management.security.impl.ExplosionSecurity;
import me.miki.shindo.management.security.impl.Log4jSecurity;
import me.miki.shindo.management.security.impl.ParticleSecurity;
import me.miki.shindo.management.security.impl.ResourcePackSecurity;
import me.miki.shindo.management.security.impl.TeleportSecurity;

public class SecurityFeatureManager {

	private ArrayList<SecurityFeature> features = new ArrayList<SecurityFeature>();
	
	public SecurityFeatureManager() {
		features.add(new DemoSecurity());
		features.add(new ExplosionSecurity());
		features.add(new Log4jSecurity());
		features.add(new ParticleSecurity());
		features.add(new ResourcePackSecurity());
		features.add(new TeleportSecurity());
	}

	public ArrayList<SecurityFeature> getFeatures() {
		return features;
	}
}
