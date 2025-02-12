package net.dzikoysk.funnyguilds.feature.security.cheat;

import java.text.DecimalFormat;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.MessageConfiguration;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.feature.security.SecurityUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.MinecraftServerUtils;
import net.dzikoysk.funnyguilds.shared.bukkit.PingUtils;
import net.dzikoysk.funnyguilds.user.UserUtils;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class SecurityReach {

    private static final DecimalFormat FORMAT = new DecimalFormat("##.##");
    private static final double CREATIVE_REACH = 4.5;
    private static final double SURVIVAL_REACH = 3.0;
    private static final double IMPORTANCE_OF_PING = 0.93;
    private static final double IMPORTANCE_OF_TPS = 10.0;

    private SecurityReach() {}

    public static void on(Player player, double distance) {
        MessageConfiguration messages = FunnyGuilds.getInstance().getMessageConfiguration();
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();
        double ping = PingUtils.getPing(player);
        double tpsDelayMs = (1000.0 / MinecraftServerUtils.getTpsInLastMinute() - 50.0);
        double compensation = player.getGameMode().equals(GameMode.CREATIVE) ? CREATIVE_REACH : SURVIVAL_REACH;

        compensation += config.reachCompensation;
        compensation += SecurityUtils.compensationMs(IMPORTANCE_OF_PING * ping);
        compensation += SecurityUtils.compensationMs(IMPORTANCE_OF_TPS * tpsDelayMs);

        if (distance < compensation) {
            return;
        }

        String message = messages.securitySystemReach.replace("{DISTANCE}", FORMAT.format(distance));
        SecurityUtils.addViolationLevel(UserUtils.get(player.getUniqueId()));
        SecurityUtils.sendToOperator(player, "Reach", message);
    }
}
