package net.dzikoysk.funnyguilds.feature.hooks.worldguard;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import java.util.List;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import org.bukkit.Location;

public class WorldGuard7Hook extends WorldGuardHook {

    private WorldGuard worldGuard;
    private StateFlag noPointsFlag;

    public WorldGuard7Hook(String name) {
        super(name);
    }

    @Override
    public HookInitResult earlyInit() {
        worldGuard = WorldGuard.getInstance();
        noPointsFlag = new StateFlag("fg-no-points", false);

        worldGuard.getFlagRegistry().register(noPointsFlag);
        return HookInitResult.SUCCESS;
    }

    @Override
    public boolean isInNonPointsRegion(Location location) {
        ApplicableRegionSet regionSet = getRegionSet(location);
        if (regionSet == null) {
            return false;
        }

        for (ProtectedRegion region : regionSet) {
            if (region.getFlag(noPointsFlag) == StateFlag.State.ALLOW) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isInIgnoredRegion(Location location) {
        PluginConfiguration config = FunnyGuilds.getInstance().getPluginConfiguration();

        ApplicableRegionSet regionSet = getRegionSet(location);
        if (regionSet == null) {
            return false;
        }

        return regionSet.getRegions()
                .stream()
                .anyMatch(region -> config.assistsRegionsIgnored.contains(region.getId()));
    }

    @Override
    public boolean isInRegion(Location location) {
        ApplicableRegionSet regionSet = getRegionSet(location);
        if (regionSet == null) {
            return false;
        }

        return regionSet.size() != 0;
    }

    @Override
    public ApplicableRegionSet getRegionSet(Location location) {
        if (location == null || worldGuard == null) {
            return null;
        }

        RegionManager regionManager = worldGuard.getPlatform().getRegionContainer().get(BukkitAdapter.adapt(location.getWorld()));
        if (regionManager == null) {
            return null;
        }

        return regionManager.getApplicableRegions(BlockVector3.at(location.getX(), location.getY(), location.getZ()));
    }

    @Override
    public List<String> getRegionNames(Location location) {
        ApplicableRegionSet regionSet = getRegionSet(location);

        return regionSet == null ? null : regionSet.getRegions().stream()
                .map(ProtectedRegion::getId)
                .collect(Collectors.toList());
    }
}
