package me.miki.shindo.management.quickplay;

import java.util.ArrayList;

import me.miki.shindo.management.quickplay.impl.ArcadeQuickPlay;
import me.miki.shindo.management.quickplay.impl.BedwarsQuickPlay;
import me.miki.shindo.management.quickplay.impl.DuelsQuickPlay;
import me.miki.shindo.management.quickplay.impl.MainLobbyQuickPlay;
import me.miki.shindo.management.quickplay.impl.MurderMysteryQuickPlay;
import me.miki.shindo.management.quickplay.impl.SkywarsQuickPlay;
import me.miki.shindo.management.quickplay.impl.TNTQuickPlay;
import me.miki.shindo.management.quickplay.impl.UHCQuickPlay;

public class QuickPlayManager {

	private ArrayList<QuickPlay> quickPlays = new ArrayList<QuickPlay>();
	
	public QuickPlayManager() {
		quickPlays.add(new ArcadeQuickPlay());
		quickPlays.add(new BedwarsQuickPlay());
		quickPlays.add(new DuelsQuickPlay());
		quickPlays.add(new MainLobbyQuickPlay());
		quickPlays.add(new MurderMysteryQuickPlay());
		quickPlays.add(new SkywarsQuickPlay());
		quickPlays.add(new TNTQuickPlay());
		quickPlays.add(new UHCQuickPlay());
	}

	public ArrayList<QuickPlay> getQuickPlays() {
		return quickPlays;
	}
}
