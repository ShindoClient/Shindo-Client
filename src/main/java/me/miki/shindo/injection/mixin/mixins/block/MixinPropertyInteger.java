package me.miki.shindo.injection.mixin.mixins.block;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import me.miki.shindo.injection.interfaces.ICachedHashcode;
import net.minecraft.block.properties.PropertyInteger;

@Mixin(PropertyInteger.class)
public class MixinPropertyInteger {

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int hashCode() {
        return ((ICachedHashcode)((Object)this)).getCachedHashcode();
    }
}