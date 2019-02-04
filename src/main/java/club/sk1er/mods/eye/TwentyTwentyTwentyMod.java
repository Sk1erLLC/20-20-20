package club.sk1er.mods.eye;


import club.sk1er.mods.eye.utils.Sk1erMod;
import com.google.gson.Gson;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.io.FileUtils;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.awt.Color;
import java.io.File;
import java.io.IOException;

@Mod(modid = TwentyTwentyTwentyMod.MODID, version = TwentyTwentyTwentyMod.VERSION)
public class TwentyTwentyTwentyMod {

    public static final String MODID = "20_20_20";
    public static final String VERSION = "1.0";
    private final Gson gson = new Gson();
    private int breakTicks = 0;
    private Config config;
    private int ticks = 0;
    private boolean breaking = false;
    private boolean timeForBreak = false;
    private ResourceLocation textureLoc = new ResourceLocation("20_20_20", "break.png");
    private KeyBinding keyBinding = new KeyBinding("Start Break", Keyboard.KEY_J, "20 20 20");
    private int warnedTicks = 0;
    private Sk1erMod sk1erMod;
    ;

    public Config getConfig() {
        return config;
    }

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        File suggestedConfigurationFile = event.getSuggestedConfigurationFile();
        try {
            config = gson.fromJson(FileUtils.readFileToString(suggestedConfigurationFile, "UTF-8"), Config.class);
        } catch (Exception e) {
            System.out.println("No config file");
        }
        if (this.config == null) {
            this.config = new Config();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                FileUtils.write(suggestedConfigurationFile, gson.toJson(config));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }));
        MinecraftForge.EVENT_BUS.register(this);
        ClientRegistry.registerKeyBinding(keyBinding);
        sk1erMod = new Sk1erMod(MODID, VERSION, "20 20 20");
        sk1erMod.checkStatus();
        ClientCommandHandler.instance.registerCommand(new Command20Config(this));
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        if (!this.config.isEnabled()) {
            return;
        }
        if (event.phase != TickEvent.Phase.START) {
            return;
        }
        EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        if (thePlayer == null)
            return;
        timeForBreak = ++ticks >= config.getInterval() * 20 * 60;
        if (timeForBreak) {
            warnedTicks++;
            if (warnedTicks % 100 == 0 && config.isChat()) {
                sk1erMod.sendMessage("Time to take a break. Press " + Keyboard.getKeyName(keyBinding.getKeyCode()) + " to start. ");
            }
        }
        if (keyBinding.isPressed()) {
            if (breaking) {
                breaking = false;
            } else {
                ticks = 0;
                breaking = true;
                breakTicks = 0;
                warnedTicks = 0;
            }
        }
        if (breaking) {
            timeForBreak = false;
            breakTicks++;
            if (breakTicks > config.getDuration() * 20) {
                breaking = false;
            }
        }


    }

    @SubscribeEvent
    public void renderTickEvent(TickEvent.RenderTickEvent event) {
        if (!this.config.isEnabled()) {
            return;
        }
        if (event.phase != TickEvent.Phase.END) {
            return;
        }
        if (timeForBreak) {
            GlStateManager.pushMatrix();
            ScaledResolution scaledResolution = new ScaledResolution(Minecraft.getMinecraft());
            int corner = config.getCorner();
            double width = 64;
            double height = 64;


            GlStateManager.enableTexture2D();
            double v = 4000D;
            long l = System.currentTimeMillis() % (int) v;
            double per = ((double) l / v);

            float animationFactor = (float) ((1F - Math.cos(per * Math.PI * 2)) / 2F);

            //Adjust for breathing effect
            width += animationFactor * width * .25D;
            height += animationFactor * height * .25D;


            Minecraft.getMinecraft().getTextureManager().bindTexture(textureLoc);
            //No translation needed for corner 1
            if (corner == 2) { //Top right
                GlStateManager.translate(scaledResolution.getScaledWidth() - width / scaledResolution.getScaleFactor(), 0, 0);
            } else if (corner == 3) { //bottom left
                GlStateManager.translate(0, scaledResolution.getScaledHeight() - height / scaledResolution.getScaleFactor(), 0);
            } else if (corner == 4) { //bottom right
                GlStateManager.translate(scaledResolution.getScaledWidth() - width / scaledResolution.getScaleFactor(), scaledResolution.getScaledHeight() - height, 0);
            }
            GlStateManager.color(1.0F, 1.0F, 1.0F, (float) (.4 + .6 * animationFactor));
            GlStateManager.scale(1.0 + .25D * animationFactor, 1.0 + .25D * animationFactor, 0);
            Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, 128, 128, 16, 16, 128, 128);
            GlStateManager.popMatrix();

        } else if (breaking) {
            int totalTime = 20 * config.getDuration();
            double percent = (double) breakTicks / (double) totalTime;
            ScaledResolution current = new ScaledResolution(Minecraft.getMinecraft());
            float radius = current.getScaledHeight() * 2 / 5;
            int centerY = current.getScaledHeight() / 2;
            int centerX = current.getScaledWidth() / 2;
            GlStateManager.pushMatrix();
            GL11.glEnable(3042);
            GL11.glDisable(3553);
            GL11.glBlendFunc(770, 771);
            GL11.glEnable(2848);
            GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
            GL11.glBegin(6);
            GlStateManager.resetColor();
            GL11.glVertex3d(centerX, centerY, 0);
            float startTheta = 0;
            float endTheta = (float) (Math.PI * 2);
            float diff = endTheta - startTheta;

            int i = 150;
            for (float j = 0; j <= i; j++) {
                Color tmp = new Color(97, 132, 249, percent > j / (float) i ? 50 : 255);
                GlStateManager.color(tmp.getRed() / 255F, tmp.getGreen() / 255F, tmp.getBlue() / 255F, tmp.getAlpha() / 255F);
                float x = centerX + radius * MathHelper.sin(startTheta + (diff * j / ((float) i)));
                float y = centerY + radius * MathHelper.cos(startTheta + (diff * j / ((float) i)));
                GL11.glVertex2f(x, y);
            }
            GL11.glEnd();
            GL11.glEnable(3553);
            GL11.glDisable(3042);
            GL11.glDisable(2848);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            ConfigGui.drawScaledText(String.valueOf((config.getDuration() * 20 - breakTicks) / 20), centerX, centerY - 10, 2.0, Color.YELLOW.getRGB(), true, true);
            ConfigGui.drawScaledText("Press " + Keyboard.getKeyName(keyBinding.getKeyCode()) + " to cancel. ", current.getScaledWidth() / 2, 5, 2, Color.WHITE.getRGB(), true, true);
            GlStateManager.popMatrix();

        }
    }


}
