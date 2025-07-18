package me.miki.shindo.management.mods.settings.impl;

import java.util.ArrayList;

import me.miki.shindo.Shindo;
import me.miki.shindo.management.language.TranslateText;
import me.miki.shindo.management.mods.Mod;
import me.miki.shindo.management.mods.settings.Setting;
import me.miki.shindo.management.mods.settings.impl.combo.Option;

public class ComboSetting extends Setting {

	private ArrayList<Option> options;
	
	private Option defaultOption, option;
	
	public ComboSetting(TranslateText text, Mod parent, TranslateText defaultOption, ArrayList<Option> options) {
		super(text, parent);
		
		this.options = options;
		this.option = getOptionByNameKey(defaultOption.getKey());
		this.defaultOption = getOptionByNameKey(defaultOption.getKey());
		
		Shindo.getInstance().getModManager().addSettings(this);
	}

	@Override
	public void reset() {
		this.option = defaultOption;
	}
	
	public Option getOption() {
		return option;
	}

	public void setOption(Option option) {
		this.option = option;
	}

	public ArrayList<Option> getOptions() {
		return options;
	}
	
	public Option getOptionByNameKey(String nameKey) {
		
		for(Option op : options) {
			if(op.getNameKey().equals(nameKey)) {
				return op;
			}
		}
		
		return option;
	}

	public Option getDefaultOption() {
		return defaultOption;
	}
}
