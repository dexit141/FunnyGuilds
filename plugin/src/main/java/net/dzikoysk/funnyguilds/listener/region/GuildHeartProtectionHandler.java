package net.dzikoysk.funnyguilds.listener.region;

import net.dzikoysk.funnyguilds.listener.AbstractFunnyListener;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockBurnEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockFadeEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;

public class GuildHeartProtectionHandler extends AbstractFunnyListener {

    @EventHandler
    public void onPistonExtend(BlockPistonExtendEvent event) {
        for (Block block : event.getBlocks()) {
            if (this.regionManager.isGuildHeart(block)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPistonRetract(BlockPistonRetractEvent event) {
        for (Block block : event.getBlocks()) {
            if (this.regionManager.isGuildHeart(block)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockBurn(BlockBurnEvent event) {
        if (this.regionManager.isGuildHeart(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockFade(BlockFadeEvent event) {
        if (this.regionManager.isGuildHeart(event.getBlock())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockExplode(BlockExplodeEvent event) {
        if (this.regionManager.isGuildHeart(event.getBlock())) {
            event.setCancelled(true);
        }
    }

}
