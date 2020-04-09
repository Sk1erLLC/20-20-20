package club.sk1er.mods.eye;

import club.sk1er.mods.core.util.MinecraftUtils;
import club.sk1er.mods.core.util.Multithreading;
import club.sk1er.mods.modcore.ModCoreInstaller;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.MathHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

@Mod(modid = TwentyTwentyTwentyMod.MODID, name = "20 20 20", version = TwentyTwentyTwentyMod.VERSION)
public class TwentyTwentyTwentyMod {

    public static final String MODID = "20_20_20";
    public static final String VERSION = "1.3";
    private final ResourceLocation textureLoc = new ResourceLocation("20_20_20", "break.png");
    private final KeyBinding keyBinding = new KeyBinding("Start Break", Keyboard.KEY_J, "20 20 20");

    private int breakTicks;
    private int ticks;
    private boolean breaking;
    private boolean timeForBreak;
    private int warnedTicks;

    private boolean shouldPing;
    private Config config;

    @Instance(MODID)
    public static TwentyTwentyTwentyMod instance;

    public Config getConfig() {
        return config;
    }

    @Mod.EventHandler
    public void init(FMLPreInitializationEvent event) {
        ModCoreInstaller.initializeModCore(Minecraft.getMinecraft().mcDataDir);
        config = new Config();
        config.preload();
        MinecraftForge.EVENT_BUS.register(this);
        ClientRegistry.registerKeyBinding(keyBinding);
        ClientCommandHandler.instance.registerCommand(new Command20Config());
    }

    @SubscribeEvent
    public void tick(TickEvent.ClientTickEvent event) {
        EntityPlayerSP thePlayer = Minecraft.getMinecraft().thePlayer;
        if (!this.config.isEnabled() || event.phase != TickEvent.Phase.START || thePlayer == null) {
            return;
        }

        timeForBreak = ++ticks >= config.getInterval() * 20 * 60;
        if (timeForBreak) {
            if (warnedTicks % (20 * 30) == 0 && config.isChat()) {
                MinecraftUtils.sendMessage(EnumChatFormatting.GOLD + "[20 20 20] ",
                    "Time to take a break. Press " + Keyboard.getKeyName(keyBinding.getKeyCode()) + " to start. ");
                if (config.isPingWhenReady()) {
                    ping();
                }
            }

            warnedTicks++;
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

                if (config.isPingWhenDone()) {
                    ping();
                }
            }
        }

        if (shouldPing) {
            SoundHandler soundHandler = Minecraft.getMinecraft().getSoundHandler();
            if (soundHandler != null) {
                soundHandler
                    .playSound(PositionedSoundRecord.create(new ResourceLocation("note.pling"),
                        (float) Minecraft.getMinecraft().thePlayer.posX,
                        (float) Minecraft.getMinecraft().thePlayer.posY,
                        (float) Minecraft.getMinecraft().thePlayer.posZ));
                shouldPing = false;
            }
        }
    }

    private void ping() {
        if (Minecraft.getMinecraft().theWorld != null) {
            Multithreading.runAsync(() -> {
                long[] times = {0, 50, 50, 50, 400, 100, 100};
                for (long time : times) {
                    try {
                        Thread.sleep(time);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    shouldPing = true;
                }
            });
        }
    }

    @SubscribeEvent
    public void renderTickEvent(RenderGameOverlayEvent.Post event) {
        if (!config.isEnabled() || !(event.type.equals(RenderGameOverlayEvent.ElementType.ALL))) {
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
            switch (corner) {
                case 2:  //Top right
                    GlStateManager.translate(scaledResolution.getScaledWidth() - width / scaledResolution.getScaleFactor(),
                            0,
                            0);
                    break;

                case 3:  //bottom left
                    GlStateManager.translate(0,
                            scaledResolution.getScaledHeight() - height / scaledResolution.getScaleFactor(),
                            0);
                    break;

                case 4:  //bottom right
                    GlStateManager.translate(scaledResolution.getScaledWidth() - width / scaledResolution.getScaleFactor(),
                            scaledResolution.getScaledHeight() - height / scaledResolution.getScaleFactor(),
                            0);
                    break;
            }

            GlStateManager.color(1.0F, 1.0F, 1.0F, (float) (.4 + .6 * animationFactor));
            GlStateManager.scale(1.0 + .25D * animationFactor, 1.0 + .25D * animationFactor, 0);
            Gui.drawScaledCustomSizeModalRect(0, 0, 0, 0, 128, 128, 16, 16, 128, 128);
            GlStateManager.popMatrix();
        } else if (breaking) {
            int totalTime = 20 * config.getDuration();
            double percent = (double) breakTicks / (double) totalTime;
            ScaledResolution current = new ScaledResolution(Minecraft.getMinecraft());
            float radius = current.getScaledHeight() * 2F / 5F;
            int centerY = current.getScaledHeight() / 2;
            int centerX = current.getScaledWidth() / 2;

            GlStateManager.pushMatrix();
            GlStateManager.enableBlend();
            GlStateManager.disableTexture2D();
            GlStateManager.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            GL11.glEnable(GL11.GL_LINE_SMOOTH);
            GL11.glHint(GL11.GL_PERSPECTIVE_CORRECTION_HINT, GL11.GL_NICEST);
            GL11.glBegin(GL11.GL_TRIANGLE_FAN);
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
            GlStateManager.enableTexture2D();
            GlStateManager.disableBlend();
            GL11.glDisable(GL11.GL_LINE_SMOOTH);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

            drawScaledText(String.valueOf((config.getDuration() * 20 - breakTicks) / 20),
                    centerX,
                    centerY - 10,
                    2.0,
                    Color.YELLOW.getRGB()
            );

            drawScaledText("Press " + Keyboard.getKeyName(keyBinding.getKeyCode()) + " to cancel. ",
                    current.getScaledWidth() / 2,
                    5,
                    2,
                    Color.WHITE.getRGB()
            );

            GlStateManager.popMatrix();
        }
    }

    private void drawScaledText(String text, int trueX, int trueY, double scaleFac, int color) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scaleFac, scaleFac, scaleFac);
        Minecraft.getMinecraft().fontRendererObj.drawString(text,
            (float) (((double) trueX) / scaleFac) - (Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) >> 1),
            (float) (((double) trueY) / scaleFac),
            color,
            true);
        GlStateManager.scale(1 / scaleFac, 1 / scaleFac, 1 / scaleFac);
        GlStateManager.popMatrix();
    }
}
