package club.sk1er.mods.eye;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumChatFormatting;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.config.GuiSlider;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;

public class ConfigGui extends GuiScreen {

    private List<GuiButton> sliders = new ArrayList<>();
    private HashMap<GuiButton, Consumer<GuiButton>> clicks = new HashMap<>();
    private HashMap<GuiButton, Consumer<GuiButton>> ticks = new HashMap<>();

    private HashMap<Integer, Runnable> ids = new HashMap<>();
    private TwentyTwentyTwentyMod mod;

    public ConfigGui(TwentyTwentyTwentyMod mod) {
        this.mod = mod;
    }

    public static void drawScaledText(String text, int trueX, int trueY, double scaleFac, int color, boolean shadow, boolean centered) {
        GlStateManager.pushMatrix();
        GlStateManager.scale(scaleFac, scaleFac, scaleFac);
        Minecraft.getMinecraft().fontRendererObj.drawString(text, (float) (((double) trueX) / scaleFac) - (centered ? Minecraft.getMinecraft().fontRendererObj.getStringWidth(text) / 2 : 0), (float) (((double) trueY) / scaleFac), color, shadow);
        GlStateManager.scale(1 / scaleFac, 1 / scaleFac, 1 / scaleFac);
        GlStateManager.popMatrix();
    }

    public void show() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void tickEvent(TickEvent.ClientTickEvent tickEvent) {
        MinecraftForge.EVENT_BUS.unregister(this);
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    @Override
    public void confirmClicked(boolean result, int id) {
        super.confirmClicked(result, id);
        if (result) {
            Runnable runnable = ids.get(id);
            if (runnable != null) {
                runnable.run();
            }
        }
        Minecraft.getMinecraft().displayGuiScreen(this);
    }

    @Override
    protected void actionPerformed(GuiButton button) {
        Consumer<GuiButton> guiButtonConsumer = clicks.get(button);
        if (guiButtonConsumer != null) {
            guiButtonConsumer.accept(button);
        }
    }

    private void regSlider(net.minecraftforge.fml.client.config.GuiSlider slider, Consumer<GuiButton> but, Consumer<GuiButton> but1) {
        reg(slider, but, but1);
        sliders.add(slider);

    }

    @Override
    public void initGui() {
        super.initGui();
        reg(new GuiButton(1, width / 2 - 100, 70, "Mod Status"), guiButton -> mod.getConfig().setEnabled(!mod.getConfig().isEnabled()), button -> button.displayString = EnumChatFormatting.YELLOW + "Mod Status: " + (mod.getConfig().isEnabled() ? EnumChatFormatting.GREEN + "Enabled" : EnumChatFormatting.RED + "Disabled"));
        regSlider(new GuiSlider(2, width / 2 - 100, 120, 200, 20, "", " Minutes", 1, 60, mod.getConfig().getInterval(), false, true), guiButton -> {

        }, guiButton -> {
            mod.getConfig().setInterval(((GuiSlider) guiButton).getValueInt());
        });
        regSlider(new GuiSlider(3, width / 2 - 100, 170, 200, 20, "", " Seconds", 1, 60, mod.getConfig().getDuration(), false, true), guiButton -> {

        }, guiButton -> {
            mod.getConfig().setDuration(((GuiSlider) guiButton).getValueInt());
        });
        reg(new GuiButton(4, width / 2 - 100, 220, "Chat Alerts"), guiButton -> mod.getConfig().setChat(!mod.getConfig().isChat()), button -> button.displayString = EnumChatFormatting.YELLOW + "Chat Alerts: " + (mod.getConfig().isChat() ? EnumChatFormatting.GREEN + "Yes" : EnumChatFormatting.RED + "No"));

    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        super.drawScreen(mouseX, mouseY, partialTicks);

        drawScaledText(EnumChatFormatting.YELLOW + EnumChatFormatting.BOLD.toString() + EnumChatFormatting.UNDERLINE + "20 20 20", width / 2, 3, 3.0, Color.WHITE.getRGB(), true, true);
        drawScaledText(EnumChatFormatting.AQUA + "By Sk1er LLC", width / 2, 35, 2.0, Color.WHITE.getRGB(), true, true);
        drawScaledText("Notification Interval", width / 2, 100, 2.0, Color.WHITE.getRGB(), true, true);
        drawScaledText("Break Duration", width / 2, 150, 2.0, Color.WHITE.getRGB(), true, true);
        drawScaledText("Chat Alerts", width / 2, 200, 2.0, Color.WHITE.getRGB(), true, true);


    }

    @Override
    public void updateScreen() {
        super.updateScreen();
        for (GuiButton guiButton : ticks.keySet()) {
            ticks.get(guiButton).accept(guiButton);
        }
    }

    private void reg(GuiButton button, Consumer<GuiButton> consumer, Consumer<GuiButton> consumer1) {
        this.buttonList.removeIf(button1 -> button1.id == button.id);
        this.clicks.keySet().removeIf(button1 -> button1.id == button.id);

        this.buttonList.add(button);
        this.clicks.put(button, consumer);
        this.ticks.put(button, consumer1);
    }
}
