package me.miki.shindo.injection.mixin.mixins.client;

import eu.shoroa.contrib.render.ShBlur;
import me.miki.shindo.Shindo;
import me.miki.shindo.gui.GuiBetterResourcePacks;
import me.miki.shindo.gui.GuiGameMenu;
import me.miki.shindo.gui.GuiSplashScreen;
import me.miki.shindo.injection.interfaces.IMixinEntityLivingBase;
import me.miki.shindo.injection.interfaces.IMixinMinecraft;
import me.miki.shindo.logger.ShindoLogger;
import me.miki.shindo.management.addons.rpo.RPOConfig;
import me.miki.shindo.management.event.impl.*;
import me.miki.shindo.management.mods.impl.*;
import me.miki.shindo.viaversion.fixes.AttackOrder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.*;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.particle.EffectRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultResourcePack;
import net.minecraft.client.resources.ResourcePackRepository;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.stream.IStream;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Session;
import net.minecraft.util.Timer;
import org.apache.commons.lang3.SystemUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.io.File;

@Mixin(Minecraft.class)
public abstract class MixinMinecraft implements IMixinMinecraft {

    @Shadow
    private static int debugFPS;
    @Shadow
    public int displayWidth;
    @Shadow
    public int displayHeight;
    @Shadow
    public PlayerControllerMP playerController;
    @Shadow
    public MovingObjectPosition objectMouseOver;
    @Shadow
    public EffectRenderer effectRenderer;
    @Shadow
    public EntityPlayerSP thePlayer;
    @Shadow
    public WorldClient theWorld;
    @Shadow
    public GameSettings gameSettings;
    @Shadow
    public GuiScreen currentScreen;
    @Shadow
    public EntityRenderer entityRenderer;
    @Shadow
    @Final
    private File fileResourcepacks;
    @Shadow
    private ResourcePackRepository mcResourcePackRepository;
    @Shadow
    private Timer timer = new Timer(20.0F);
    @Shadow
    private Session session;
    @Shadow
    private int leftClickCounter;
    @Shadow
    private boolean fullscreen;
    @Shadow
    @Final
    private DefaultResourcePack mcDefaultResourcePack;
    @Shadow
    private Entity renderViewEntity;
    @Shadow
    private boolean enableGLErrorChecking;

    /**
     * @author
     * @reason
     */
    @Overwrite
    public static int getDebugFPS() {

        if (FPSSpooferMod.getInstance().isToggled()) {
            return debugFPS * FPSSpooferMod.getInstance().getMultiplierSetting().getValueInt();
        }

        return debugFPS;
    }

    @Shadow
    protected abstract void clickMouse();

    @Shadow
    protected abstract void rightClickMouse();

    @Shadow
    public abstract void displayGuiScreen(GuiScreen guiScreenIn);

    @Shadow
    public abstract void updateDisplay();

    @Shadow
    protected abstract void resize(int width, int height);

    @Inject(method = "startGame", at = @At(value = "FIELD", target = "Lnet/minecraft/client/Minecraft;ingameGUI:Lnet/minecraft/client/gui/GuiIngame;", shift = At.Shift.AFTER))
    public void preStartGame(CallbackInfo ci) {
        Shindo.getInstance().start();
    }

    @Inject(method = "startGame", at = @At("TAIL"))
    private void onGameStartCompleted(CallbackInfo ci) {
        RPOConfig.init();
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;next()Z"))
    public boolean nextMouse() {

        boolean next = Mouse.next();

        if (next) {

            EventClickMouse event = new EventClickMouse(Mouse.getEventButton());
            event.call();

            if (event.isCancelled()) {
                next = nextMouse();
            }
        }

        return next;
    }

    @Inject(method = "run", at = @At("HEAD"))
    public void preRun(CallbackInfo callbackInfo) {

        if (displayWidth < 1100) {
            displayWidth = 1100;
        }

        if (displayHeight < 630) {
            displayHeight = 630;
        }
    }

    @Inject(method = "shutdownMinecraftApplet", at = @At("HEAD"))
    public void preShutdown(CallbackInfo ci) {
        Shindo.getInstance().stop();
    }

    /**
     * @param i : exit code
     * @reason let the shutdown sound play before killing the process
     */
    @Redirect(method = "shutdownMinecraftApplet", at = @At(value = "INVOKE", target = "Ljava/lang/System;exit(I)V", remap = false))
    private void ignoreGcCall(int i) {
        try {
            Thread.sleep(2530);
        } catch (Exception ignored) {
        }
        System.exit(i);
    }

    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;dispatchKeypresses()V", shift = At.Shift.AFTER))
    private void onKey(CallbackInfo ci) {
        if (Keyboard.getEventKeyState() && Minecraft.getMinecraft().currentScreen == null) {
            new EventKey(Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey()).call();
        }
    }

    @Inject(method = "clickMouse", at = @At("HEAD"))
    public void fixHitDelay(CallbackInfo ci) {
        if (HitDelayFixMod.getInstance().isToggled()) {
            leftClickCounter = 0;
        }
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/EffectRenderer;updateEffects()V"))
    public void fixEffectRenderer(EffectRenderer effectRenderer) {
        try {
            effectRenderer.updateEffects();
        } catch (Exception e) {
            ShindoLogger.error("An error occurred while updating effects", e);
        }
    }

    @Inject(method = "startGame", at = @At("TAIL"))
    private void disableGlErrorChecking(CallbackInfo ci) {
        this.enableGLErrorChecking = false;
    }

    @Inject(method = "setIngameFocus", at = @At(value = "INVOKE", target = "Lnet/minecraft/util/MouseHelper;grabMouseCursor()V"))
    public void fixKeyBinding(CallbackInfo callback) {
        for (KeyBinding keyBinding : gameSettings.keyBindings) {
            try {
                KeyBinding.setKeyBindState(keyBinding.getKeyCode(), keyBinding.getKeyCode() < 256 && Keyboard.isKeyDown(keyBinding.getKeyCode()));
            } catch (Exception e) {
                ShindoLogger.error("An error occurred while updating keybindings", e);
            }
        }
    }

    @Inject(method = "runTick", at = @At("TAIL"))
    private void onTick(final CallbackInfo ci) {
        new EventTick().call();
    }

    @Inject(method = "sendClickBlockToController", at = @At("HEAD"))
    public void preSendClickBlockToController(boolean leftClick, CallbackInfo ci) {

        AnimationsMod mod = AnimationsMod.getInstance();

        if (mod.isToggled() && mod.getBlockHitSetting().isToggled() && mod.getPushingSetting().isToggled() && gameSettings.keyBindUseItem.isKeyDown()) {
            if (leftClickCounter <= 0 && leftClick && objectMouseOver != null && objectMouseOver.typeOfHit == MovingObjectPosition.MovingObjectType.BLOCK) {
                if (!theWorld.isAirBlock(objectMouseOver.getBlockPos()) && thePlayer.isAllowEdit()) {

                    if (mod.getPushingParticleSetting().isToggled()) {
                        effectRenderer.addBlockHitEffects(objectMouseOver.getBlockPos(), objectMouseOver.sideHit);
                    }

                    if (!thePlayer.isSwingInProgress || thePlayer.swingProgressInt >= ((IMixinEntityLivingBase) thePlayer).getArmSwingAnimation() / 2 || thePlayer.swingProgressInt < 0) {
                        thePlayer.swingProgressInt = -1;
                        thePlayer.isSwingInProgress = true;
                    }
                }
            } else {
                playerController.resetBlockRemoving();
            }
        }
    }

    @Inject(method = "updateDisplay", at = @At("HEAD"))
    public void onUpdateDisplay(CallbackInfo ci) {
        if (Shindo.getInstance().getEventManager() != null) {
            new EventUpdateDisplay().call();
        }
    }

    @Redirect(method = "createDisplay", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setTitle(Ljava/lang/String;)V"))
    public void overrideTitle(String title) {
        Display.setTitle("Shindo Client v" + Shindo.getInstance().getVersion() + " (" + Shindo.getInstance().getVersionIdentifier() + ") for " + title);
    }

    @Inject(method = "updateFramebufferSize", at = @At("HEAD"))
    private void onUpdateFramebufferSize(CallbackInfo ci) {
        if (Shindo.getInstance().getEventManager() != null) {
            new EventUpdateFramebufferSize().call();
        }
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lorg/lwjgl/input/Mouse;getEventDWheel()I"))
    public int onScroll() {

        int dWheel = Mouse.getEventDWheel();

        EventScrollMouse event = new EventScrollMouse(dWheel);
        event.call();

        if (dWheel != 0) {
            if (event.isCancelled()) {
                dWheel = 0;
            }
        }

        return dWheel;
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/achievement/GuiAchievement;updateAchievementWindow()V", shift = At.Shift.BEFORE))
    public void preRenderTick(CallbackInfo ci) {
        new EventPreRenderTick().call();
    }

    @Inject(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/achievement/GuiAchievement;updateAchievementWindow()V", shift = At.Shift.AFTER))
    public void postRenderTick(CallbackInfo ci) {
        new EventRenderTick().call();
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public int getLimitFramerate() {

        FPSLimiterMod limiter = FPSLimiterMod.getInstance();

        if (limiter.isToggled()) {

            if (this.currentScreen == null && limiter.getLimitMaxFpsSetting().isToggled()) {
                return limiter.getMaxFpsSetting().getValueInt();
            } else if (this.currentScreen != null && limiter.getLimitGuiFps().isToggled()) {
                return limiter.getGuiFpsSetting().getValueInt();
            }
        }

        return this.theWorld == null && this.currentScreen != null ? 60 : this.gameSettings.limitFramerate;
    }

    /**
     * @author
     * @reason
     */
    @Overwrite
    public boolean isFramerateLimitBelowMax() {

        FPSLimiterMod limiter = FPSLimiterMod.getInstance();

        if (limiter.isToggled() && limiter.getLimitMaxFpsSetting().isToggled()) {
            return true;
        }

        return (float) this.getLimitFramerate() < GameSettings.Options.FRAMERATE_LIMIT.getValueMax();
    }

    @Inject(method = "drawSplashScreen", at = @At("HEAD"), cancellable = true)
    public void overrideSplash(TextureManager textureManagerInstance, CallbackInfo ci) {
        new GuiSplashScreen().draw();
        ci.cancel();
    }

    @Inject(method = "displayGuiScreen", at = @At("RETURN"))
    public void displayGuiScreenInjectReturn(GuiScreen guiScreenIn, CallbackInfo ci) {
        if (guiScreenIn instanceof GuiMainMenu) {
            displayGuiScreen(Shindo.getInstance().getShindoAPI().getMainMenu());
        }
    }

    @Inject(method = "displayGuiScreen", at = @At("HEAD"), cancellable = true)
    public void displayGuiScreenInjectHead(GuiScreen guiScreenIn, CallbackInfo ci) {

        if (guiScreenIn instanceof GuiScreenResourcePacks && !(guiScreenIn instanceof GuiBetterResourcePacks)) {
            GuiScreen parent = Minecraft.getMinecraft().currentScreen;

            ci.cancel();

            Minecraft.getMinecraft().addScheduledTask(() -> displayGuiScreen(new GuiBetterResourcePacks(parent)));
        }

        if (guiScreenIn instanceof GuiIngameMenu) {

            ci.cancel();

            Minecraft.getMinecraft().addScheduledTask(() -> displayGuiScreen(new GuiGameMenu()));
        }
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void clearLoadedMaps(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
        if (worldClientIn != this.theWorld) {
            this.entityRenderer.getMapItemRenderer().clearLoadedMaps();
        }
    }

    @Redirect(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At(value = "INVOKE", target = "Ljava/lang/System;gc()V"))
    private void optimizedWorldSwapping() {
    }

    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152935_j()V"))
    private void skipTwitchCode1(IStream instance) {
    }

    @Redirect(method = "runGameLoop", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/stream/IStream;func_152922_k()V"))
    private void skipTwitchCode2(IStream instance) {
    }

    @ModifyArg(method = "launchIntegratedServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/Minecraft;displayGuiScreen(Lnet/minecraft/client/gui/GuiScreen;)V", ordinal = 0))
    private GuiScreen displayWorkingScreen(GuiScreen original) {
        return new GuiScreenWorking();
    }

    @Inject(method = "toggleFullscreen", at = @At(value = "INVOKE", target = "Lorg/lwjgl/opengl/Display;setFullscreen(Z)V", remap = false))
    private void resolveScreenState(CallbackInfo ci) {
        if (!this.fullscreen && SystemUtils.IS_OS_WINDOWS) {
            Display.setResizable(false);
            Display.setResizable(true);
        }
    }

    @Inject(method = "toggleFullscreen", at = @At("HEAD"), cancellable = true)
    public void handleToggle(CallbackInfo ci) {

        EventToggleFullscreen event = new EventToggleFullscreen(!fullscreen);
        event.call();

        if (event.isCancelled()) {
            ci.cancel();
            gameSettings.fullScreen = fullscreen;
        } else if (!event.isApplyState()) {
            ci.cancel();
            gameSettings.fullScreen = (fullscreen = !fullscreen);
        }
    }

    @Redirect(method = "clickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;swingItem()V"))
    private void redirectSwing(EntityPlayerSP instance) {
        if (ViaVersionMod.getInstance().isToggled() && ViaVersionMod.getInstance().isLoaded()) {
            AttackOrder.sendConditionalSwing(objectMouseOver);
        } else {
            thePlayer.swingItem();
        }
    }

    @Redirect(method = "clickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;attackEntity(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/entity/Entity;)V"))
    private void redirectAttack(PlayerControllerMP instance, EntityPlayer playerIn, Entity targetEntity) {
        if (ViaVersionMod.getInstance().isToggled() && ViaVersionMod.getInstance().isLoaded()) {
            AttackOrder.sendFixedAttack(thePlayer, objectMouseOver.entityHit);
        } else {
            playerController.attackEntity(thePlayer, objectMouseOver.entityHit);
        }
    }

    @Redirect(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/EntityRenderer;loadEntityShader(Lnet/minecraft/entity/Entity;)V"))
    private void keepShadersOnPerspectiveChange(EntityRenderer entityRenderer, Entity entityIn) {
    }

    @Override
    public Timer getTimer() {
        return timer;
    }

    @Override
    public void setSession(Session session) {
        this.session = session;
    }

    @Override
    public void callClickMouse() {
        clickMouse();
    }

    @Override
    public void callRightClickMouse() {
        rightClickMouse();
    }

    @Override
    public DefaultResourcePack getMcDefaultResourcePack() {
        return this.mcDefaultResourcePack;
    }

    @Override
    public Entity getRenderViewEntity() {
        return renderViewEntity;
    }


    @Override
    @Accessor
    public abstract boolean isRunning();

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/SkinManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Ljava/io/File;Lcom/mojang/authlib/minecraft/MinecraftSessionService;)V"))
    public void splashSkinManager(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/world/chunk/storage/AnvilSaveConverter;<init>(Ljava/io/File;)V"))
    public void splashSaveLoader(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/audio/SoundHandler;<init>(Lnet/minecraft/client/resources/IResourceManager;Lnet/minecraft/client/settings/GameSettings;)V"))
    public void splashSoundHandler(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/audio/MusicTicker;<init>(Lnet/minecraft/client/Minecraft;)V"))
    public void splashMusicTicker(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/FontRenderer;<init>(Lnet/minecraft/client/settings/GameSettings;Lnet/minecraft/util/ResourceLocation;Lnet/minecraft/client/renderer/texture/TextureManager;Z)V"))
    public void splashFontRenderer(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/util/MouseHelper;<init>()V"))
    public void splashMouseHelper(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/texture/TextureMap;<init>(Ljava/lang/String;)V"))
    public void splashTextureMap(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/resources/model/ModelManager;<init>(Lnet/minecraft/client/renderer/texture/TextureMap;)V"))
    public void splashModelManager(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/RenderItem;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/resources/model/ModelManager;)V"))
    public void splashRenderItem(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/entity/RenderManager;<init>(Lnet/minecraft/client/renderer/texture/TextureManager;Lnet/minecraft/client/renderer/entity/RenderItem;)V"))
    public void splashRenderManager(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/ItemRenderer;<init>(Lnet/minecraft/client/Minecraft;)V"))
    public void splashItemRenderer(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/EntityRenderer;<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/resources/IResourceManager;)V"))
    public void splashEntityRenderer(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/BlockRendererDispatcher;<init>(Lnet/minecraft/client/renderer/BlockModelShapes;Lnet/minecraft/client/settings/GameSettings;)V"))
    public void splashBlockRenderDispatcher(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/renderer/RenderGlobal;<init>(Lnet/minecraft/client/Minecraft;)V"))
    public void splashRenderGlobal(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/achievement/GuiAchievement;<init>(Lnet/minecraft/client/Minecraft;)V"))
    public void splashGuiAchivement(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/particle/EffectRenderer;<init>(Lnet/minecraft/world/World;Lnet/minecraft/client/renderer/texture/TextureManager;)V"))
    public void splashEffectRenderer(CallbackInfo callback) {
        updateDisplay();
    }

    @Inject(method = "startGame", at = @At(value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiIngame;<init>(Lnet/minecraft/client/Minecraft;)V"))
    public void splashGuiIngame(CallbackInfo callback) {
        ShBlur.getInstance().init();
        updateDisplay();
    }

    @Override
    public void resizeWindow(int width, int height) {
        resize(width, height);
    }

    @Inject(method = "resize", at = @At("TAIL"))
    public void inject$resize(int width, int height, CallbackInfo ci) {
        ShBlur.getInstance().resize();
    }

    @Override
    public File getFileResourcepacks() {
        return fileResourcepacks;
    }

    @Override
    public ResourcePackRepository getMcResourcePackRepository() {
        return mcResourcePackRepository;
    }

    @Override
    public void setMcResourcePackRepository(ResourcePackRepository repo) {
        this.mcResourcePackRepository = repo;
    }
}
