package net.dzikoysk.funnyguilds.guild;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import net.dzikoysk.funnyguilds.FunnyGuilds;
import net.dzikoysk.funnyguilds.concurrency.requests.prefix.PrefixGlobalRemoveGuildRequest;
import net.dzikoysk.funnyguilds.config.PluginConfiguration;
import net.dzikoysk.funnyguilds.data.database.DatabaseGuild;
import net.dzikoysk.funnyguilds.data.database.SQLDataModel;
import net.dzikoysk.funnyguilds.data.flat.FlatDataModel;
import net.dzikoysk.funnyguilds.feature.hooks.HookManager;
import net.dzikoysk.funnyguilds.nms.BlockDataChanger;
import net.dzikoysk.funnyguilds.nms.GuildEntityHelper;
import net.dzikoysk.funnyguilds.user.User;
import org.apache.commons.lang3.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import panda.std.Option;
import panda.std.stream.PandaStream;

public class GuildManager {

    private final PluginConfiguration pluginConfiguration;

    private final Map<UUID, Guild> guildsMap = new ConcurrentHashMap<>();

    public GuildManager(PluginConfiguration pluginConfiguration) {
        this.pluginConfiguration = pluginConfiguration;
    }

    public int countGuilds() {
        return this.guildsMap.size();
    }

    /**
     * Gets the copied set of guilds.
     *
     * @return set of guild
     */
    public Set<Guild> getGuilds() {
        return new HashSet<>(this.guildsMap.values());
    }

    /**
     * Gets the set of guilds from collection of strings (names).
     *
     * @param names collection of names
     * @return set of guild
     */
    public Set<Guild> findByNames(Collection<String> names) {
        return PandaStream.of(names)
                .flatMap(this::findByName)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the set of guilds from collection of strings (tags).
     *
     * @param tags collection of tags
     * @return set of guild
     */
    public Set<Guild> findByTags(Collection<String> tags) {
        return PandaStream.of(tags)
                .flatMap(this::findByTag)
                .collect(Collectors.toSet());
    }

    /**
     * Gets the guild.
     *
     * @param uuid the uuid of guild
     * @return the guild
     */
    public Option<Guild> findByUuid(UUID uuid) {
        return Option.of(guildsMap.get(uuid));
    }

    /**
     * Gets the guild.
     *
     * @param name       the name of guild
     * @param ignoreCase ignore the case of the name
     * @return the guild
     */
    public Option<Guild> findByName(String name, boolean ignoreCase) {
        return PandaStream.of(guildsMap.entrySet())
                .find(entry -> ignoreCase
                        ? entry.getValue().getName().equalsIgnoreCase(name)
                        : entry.getValue().getName().equals(name))
                .map(Map.Entry::getValue);
    }

    /**
     * Gets the guild.
     *
     * @param name the name of guild
     * @return the guild
     */
    public Option<Guild> findByName(String name) {
        return this.findByName(name, false);
    }

    /**
     * Gets the guild.
     *
     * @param tag        the tag of guild
     * @param ignoreCase ignore the case of the tag
     * @return the guild
     */
    public Option<Guild> findByTag(String tag, boolean ignoreCase) {
        return PandaStream.of(guildsMap.entrySet())
                .find(entry -> ignoreCase
                        ? entry.getValue().getTag().equalsIgnoreCase(tag)
                        : entry.getValue().getTag().equals(tag))
                .map(Map.Entry::getValue);
    }

    /**
     * Gets the guild.
     *
     * @param tag the tag of guild
     * @return the guild
     */
    public Option<Guild> findByTag(String tag) {
        return this.findByTag(tag, false);
    }

    /**
     * Create the guild and add it to storage. If you think you should use this method you probably shouldn't - instead use {@link GuildManager#findByUuid(UUID)}, {@link GuildManager#findByName(String)} etc.
     *
     * @param name the name which will be assigned to guild
     * @return the guild
     */
    /*
    public Guild create(String name) {
        Validate.notNull(name, "name can't be null!");
        Validate.notBlank(name, "name can't be blank!");
        Validate.isTrue(GuildUtils.validateName(pluginConfiguration, name), "name is not valid!");

        Guild guild = new Guild(name);
        addGuild(guild);

        return guild;
    }
     */

    /**
     * Create the guild and add it to storage. If you think you should use this method you probably shouldn't - instead use {@link GuildManager#findByUuid(UUID)}, {@link GuildManager#findByName(String)} etc.
     *
     * @param name the name which will be assigned to guild
     * @param tag the tag which will be assigned to guild
     * @return the guild
     */
    /*
    public Guild create(String name, String tag) {
        Validate.notNull(tag, "tag can't be null!");
        Validate.notBlank(tag, "tag can't be blank!");
        Validate.isTrue(GuildUtils.validateTag(pluginConfiguration, tag), "tag is not valid!");

        Guild guild = create(name);
        guild.setTag(tag);

        return guild;
    }
    */

    /**
     * Create the guild and add it to storage. If you think you should use this method you probably shouldn't - instead use {@link GuildManager#findByUuid(UUID)}, {@link GuildManager#findByName(String)} etc.
     *
     * @param uuid the uuid which will be assigned to guild
     * @param name the name which will be assigned to guild
     * @param tag the tag which will be assigned to guild
     * @return the guild
     */
    /*
    public Guild create(UUID uuid, String name, String tag) {
        Validate.notNull(uuid, "uuid can't be null!");
        Validate.notNull(name, "name can't be null!");
        Validate.notBlank(name, "name can't be blank!");
        Validate.isTrue(GuildUtils.validateName(pluginConfiguration, name), "name is not valid!");
        Validate.notNull(tag, "tag can't be null!");
        Validate.notBlank(tag, "tag can't be blank!");
        Validate.isTrue(GuildUtils.validateTag(pluginConfiguration, tag), "tag is not valid!");

        Guild guild = new Guild(uuid, name, tag);
        addGuild(guild);

        return guild;
    }
    */

    /**
     * Add guild to storage. If you think you should use this method you probably shouldn't.
     *
     * @param guild guild to add
     */
    public Guild addGuild(Guild guild) {
        Validate.notNull(guild, "guild can't be null!");
        guildsMap.put(guild.getUUID(), guild);
        return guild;
    }

    /**
     * Remove guild from storage. If you think you should use this method you probably shouldn't - instead use {@link GuildManager#deleteGuild(FunnyGuilds, Guild)}.
     *
     * @param guild guild to remove
     */
    public void removeGuild(Guild guild) {
        Validate.notNull(guild, "guild can't be null!");
        guildsMap.remove(guild.getUUID());
    }

    /**
     * Delete guild in every possible way.
     *
     * @param guild guild to delete
     */
    public void deleteGuild(FunnyGuilds plugin, Guild guild) {
        if (guild == null) {
            return;
        }

        if (this.pluginConfiguration.regionsEnabled) {
            guild.getRegion()
                    .peek(region -> {
                        if (this.pluginConfiguration.heart.createEntityType != null) {
                            GuildEntityHelper.despawnGuildHeart(guild);
                        }
                        else if (this.pluginConfiguration.heart.createMaterial != null &&
                                this.pluginConfiguration.heart.createMaterial.getLeft() != Material.AIR) {
                            Location center = region.getCenter().clone();

                            Bukkit.getScheduler().runTask(plugin, () -> {
                                Block block = center.getBlock().getRelative(BlockFace.DOWN);

                                if (block.getLocation().getBlockY() > 1) {
                                    block.setType(Material.AIR);
                                }
                            });
                        }

                        plugin.getRegionManager().deleteRegion(plugin.getDataModel(), region);
                    });
        }

        plugin.getConcurrencyManager().postRequests(new PrefixGlobalRemoveGuildRequest(guild));

        guild.getMembers().forEach(User::removeGuild);

        for (Guild ally : guild.getAllies()) {
            ally.removeAlly(guild);
        }

        for (Guild globalGuild : getGuilds()) {
            globalGuild.removeEnemy(guild);
        }

        if (plugin.getDataModel() instanceof FlatDataModel) {
            FlatDataModel dataModel = ((FlatDataModel) plugin.getDataModel());
            dataModel.getGuildFile(guild).delete();
        }
        else if (plugin.getDataModel() instanceof SQLDataModel) {
            DatabaseGuild.delete(guild);
        }

        removeGuild(guild);
        HookManager.HOLOGRAPHIC_DISPLAYS.peek(hologramManager -> hologramManager.deleteHologram(guild));
    }

    /**
     * Checks if guild with given name exists.
     *
     * @param name the guild name to check if exists
     * @return if guild with given name exists
     */
    public boolean nameExists(String name) {
        return this.findByName(name).isPresent();
    }

    /**
     * Checks if guild with given tag exists.
     *
     * @param tag the guild tag to check if exists
     * @return if guild with given tag exists
     */
    public boolean tagExists(String tag) {
        return this.findByTag(tag).isPresent();
    }

    /**
     * Spawn heart for guild. You probably shouldn't use this method.
     *
     * @param guild the guild for which heart should be spawned
     */
    public void spawnHeart(Guild guild) {
        if (this.pluginConfiguration.heart.createMaterial != null && this.pluginConfiguration.heart.createMaterial.getLeft() != Material.AIR) {
            guild.getRegion()
                    .flatMap(Region::getHeartBlock)
                    .peek(heart -> {
                        heart.setType(this.pluginConfiguration.heart.createMaterial.getLeft());
                        BlockDataChanger.applyChanges(heart, this.pluginConfiguration.heart.createMaterial.getRight());
                    });
        }
        else if (this.pluginConfiguration.heart.createEntityType != null) {
            GuildEntityHelper.spawnGuildHeart(guild);
        }
    }

}
