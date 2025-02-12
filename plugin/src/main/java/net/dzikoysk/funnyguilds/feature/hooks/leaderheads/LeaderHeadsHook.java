package net.dzikoysk.funnyguilds.feature.hooks.leaderheads;

import com.google.common.collect.Maps;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import me.robin.leaderheads.datacollectors.DataCollector;
import me.robin.leaderheads.objects.BoardType;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.feature.hooks.AbstractPluginHook;
import net.dzikoysk.funnyguilds.rank.DefaultTops;

public class LeaderHeadsHook extends AbstractPluginHook {

    private final FunnyGuilds plugin;

    public LeaderHeadsHook(String name, FunnyGuilds plugin) {
        super(name);
        this.plugin = plugin;
    }

    @Override
    public HookInitResult init() {
        new TopRankCollector(plugin);
        return HookInitResult.SUCCESS;
    }

    public static class TopRankCollector extends DataCollector {

        private final FunnyGuilds plugin;

        public TopRankCollector(FunnyGuilds plugin) {
            super(
                    "funnyguilds-top-rank",
                    "FunnyGuilds",
                    BoardType.DEFAULT,
                    "Top rankingu",
                    "/toprank",
                    Collections.emptyList(),
                    true,
                    String.class
            );
            this.plugin = plugin;
        }

        @Override
        public List<Entry<?, Double>> requestAll() {
            List<Entry<?, Double>> topUsers = new ArrayList<>();
            for (int position = 1; position <= 10; position++) {
                this.plugin.getUserRankManager().getUser(DefaultTops.USER_POINTS_TOP, position)
                        .peek(user -> topUsers.add(Maps.immutableEntry(user.getName(), ((double) user.getRank().getPoints()))));
            }
            return topUsers;
        }

    }

}
