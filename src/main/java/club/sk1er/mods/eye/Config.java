package club.sk1er.mods.eye;

import club.sk1er.vigilance.Vigilant;
import club.sk1er.vigilance.data.Property;
import club.sk1er.vigilance.data.PropertyType;
import java.io.File;

public class Config extends Vigilant {

    @Property(
        type = PropertyType.SLIDER,
        category = "General",
        subcategory = "Time",
        name = "Minutes In Between (minutes)",
        description = "Choose how long between breaks.",
        min = 1,
        max = 60
    )
    private int interval = 20;

    @Property(
        type = PropertyType.SLIDER,
        category = "General",
        subcategory = "Time",
        name = "Break Duration (seconds)",
        description = "Choose how long breaks last.",
        min = 1,
        max = 60
    )
    private int duration = 20;

    @Property(
        type = PropertyType.SLIDER,
        category = "General",
        subcategory = "Notification",
        name = "Notification Corner",
        description = "Choose where the break notification is displayed.",
        min = 1,
        max = 4
    )
    private int corner = 1;

    @Property(
        type = PropertyType.SWITCH,
        category = "General",
        subcategory = "General",
        name = "Enabled",
        description = "Toggle the mod entirely."
    )
    private boolean enabled = true;

    @Property(
        type = PropertyType.SWITCH,
        category = "General",
        subcategory = "Notification",
        name = "Chat Message",
        description = "Display a message in chat when ready."
    )
    private boolean chat = true;

    @Property(
        type = PropertyType.SWITCH,
        category = "General",
        subcategory = "Notification",
        name = "Ping When Done",
        description = "Ping when the break is over."
    )
    private boolean pingWhenDone = true;

    @Property(
        type = PropertyType.SWITCH,
        category = "General",
        subcategory = "Notification",
        name = "Ping When Ready",
        description = "Ping when the break is ready."
    )
    private boolean pingWhenReady = true;

    public Config() {
        super(new File("./config/202020.toml"));
        initialize();
    }

    public boolean isPingWhenDone() {
        return pingWhenDone;
    }

    public boolean isPingWhenReady() {
        return pingWhenReady;
    }

    public boolean isChat() {
        return chat;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getCorner() {
        return corner;
    }

    public int getInterval() {
        return interval;
    }

    public int getDuration() {
        return duration;
    }
}
