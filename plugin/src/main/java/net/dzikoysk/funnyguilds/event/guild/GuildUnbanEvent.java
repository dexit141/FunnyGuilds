package net.dzikoysk.funnyguilds.event.guild;

import net.dzikoysk.funnyguilds.guild.Guild;
import net.dzikoysk.funnyguilds.user.User;
import org.bukkit.event.HandlerList;

public class GuildUnbanEvent extends GuildEvent {

    private static final HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public GuildUnbanEvent(EventCause eventCause, User doer, Guild guild) {
        super(eventCause, doer, guild);
    }

    @Override
    public String getDefaultCancelMessage() {
        return "[FunnyGuilds] Guild unban has been cancelled by the server!";
    }

}
