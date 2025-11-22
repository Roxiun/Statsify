package com.strawberry.statsify;

import com.strawberry.statsify.api.AuroraApi;
import com.strawberry.statsify.api.HypixelApi;
import com.strawberry.statsify.api.MojangApi;
import com.strawberry.statsify.api.NadeshikoApi;
import com.strawberry.statsify.api.PlanckeApi;
import com.strawberry.statsify.api.UrchinApi;
import com.strawberry.statsify.commands.BedwarsCommand;
import com.strawberry.statsify.commands.ClearCacheCommand;
import com.strawberry.statsify.commands.DenickCommand;
import com.strawberry.statsify.commands.StatsifyCommand;
import com.strawberry.statsify.config.StatsifyOneConfig;
import com.strawberry.statsify.events.ChatHandler;
import com.strawberry.statsify.events.WorldLoadHandler;
import com.strawberry.statsify.task.StatsChecker;
import com.strawberry.statsify.util.NickUtils;
import com.strawberry.statsify.util.NumberDenicker;
import com.strawberry.statsify.util.PregameStats;
import com.strawberry.statsify.util.TagUtils;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = Statsify.MODID, name = Statsify.NAME, version = Statsify.VERSION)
public class Statsify {

    public static final String MODID = "statsify";
    public static final String NAME = "Statsify";
    public static final String VERSION = "4.2.3";

    public static StatsifyOneConfig config;
    public static final Map<String, List<String>> playerSuffixes =
        new HashMap<>();
    public static final NickUtils nickUtils = new NickUtils();

    @Mod.EventHandler
    public void init(FMLInitializationEvent event) {
        config = new StatsifyOneConfig();

        // APIs
        MojangApi mojangApi = new MojangApi();
        NadeshikoApi nadeshikoApi = new NadeshikoApi(mojangApi);
        TagUtils tagUtils = new TagUtils(nadeshikoApi);
        HypixelApi hypixelApi = new HypixelApi(nadeshikoApi, tagUtils);
        UrchinApi urchinApi = new UrchinApi();
        PlanckeApi planckeApi = new PlanckeApi();
        AuroraApi auroraApi = new AuroraApi();

        // Utils
        NumberDenicker numberDenicker = new NumberDenicker(
            config,
            nickUtils,
            auroraApi
        );
        PregameStats pregameStats = new PregameStats(
            config,
            nadeshikoApi,
            urchinApi,
            mojangApi
        );

        // Tasks
        StatsChecker statsChecker = new StatsChecker(
            hypixelApi,
            urchinApi,
            nickUtils,
            config,
            playerSuffixes
        );

        // Event Handlers
        MinecraftForge.EVENT_BUS.register(
            new ChatHandler(
                config,
                nickUtils,
                numberDenicker,
                pregameStats,
                planckeApi,
                statsChecker
            )
        );
        MinecraftForge.EVENT_BUS.register(
            new WorldLoadHandler(numberDenicker, pregameStats, nickUtils)
        );

        // Commands
        ClientCommandHandler.instance.registerCommand(
            new BedwarsCommand(config, nadeshikoApi)
        );
        ClientCommandHandler.instance.registerCommand(new StatsifyCommand());
        ClientCommandHandler.instance.registerCommand(
            new ClearCacheCommand(playerSuffixes)
        );
        ClientCommandHandler.instance.registerCommand(
            new DenickCommand(config, auroraApi)
        );
    }
}
