package me.miki.shindo.injection.mixin.mixins.item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import me.miki.shindo.injection.interfaces.IMixinItemFood;
import net.minecraft.item.ItemFood;

@Mixin(ItemFood.class)
public class MixinItemFood implements IMixinItemFood {
	
	@Shadow
	private int potionId;

	@Override
	public int client$getPotionID() {
		return potionId;
	}
}
