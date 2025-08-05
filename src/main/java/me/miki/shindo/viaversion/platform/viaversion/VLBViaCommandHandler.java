package me.miki.shindo.viaversion.platform.viaversion;

import com.viaversion.viaversion.commands.ViaCommandHandler;
import me.miki.shindo.viaversion.command.impl.LeakDetectSubCommand;

public class VLBViaCommandHandler extends ViaCommandHandler {

    public VLBViaCommandHandler() {
        super();
        this.registerVLBDefaults();
    }

    public void registerVLBDefaults() {
        this.registerSubCommand(new LeakDetectSubCommand());
    }
}
