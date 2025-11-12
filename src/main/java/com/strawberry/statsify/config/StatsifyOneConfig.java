package com.strawberry.statsify.config;

import cc.polyfrost.oneconfig.config.Config;
import cc.polyfrost.oneconfig.config.annotations.Dropdown;
import cc.polyfrost.oneconfig.config.annotations.Slider;
import cc.polyfrost.oneconfig.config.annotations.Switch;
import cc.polyfrost.oneconfig.config.annotations.Text;
import cc.polyfrost.oneconfig.config.data.Mod;
import cc.polyfrost.oneconfig.config.data.ModType;
import com.strawberry.statsify.Statsify;

public class StatsifyOneConfig extends Config {

    @Slider(
            name = "Minimum FKDR",
            min = -1, max = 50,
            step = 1
    )
    public int minFkdr = -1;

    @Switch(
            name = "Show Tags"
    )
    public boolean tags = false;

    @Switch(
            name = "Show Tab Stats"
    )
    public boolean tabStats = true;

    @Switch(
            name = "Enable Urchin"
    )
    public boolean urchin = false;

    @Text(
            name = "Urchin API Key"
    )
    public String urchinKey = "";

    @Switch(
            name = "Auto /who"
    )
    public boolean autoWho = true;

    @Dropdown(
            name = "Tab Format",
            options = {"[Star] Name • FKDR", "Star • Name • FKDR", "Name • FKDR"}
    )
    public int tabFormat = 0;

    public StatsifyOneConfig() {
        super(new Mod(Statsify.NAME, ModType.HYPIXEL), Statsify.MODID + ".json");
        initialize();
    }
}
