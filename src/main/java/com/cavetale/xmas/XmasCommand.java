package com.cavetale.xmas;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.xmas.attraction.Attraction;
import org.bukkit.entity.Player;

public final class XmasCommand extends AbstractCommand<XmasPlugin> {
    protected XmasCommand(final XmasPlugin plugin) {
        super(plugin, "xmas");
    }

    @Override
    protected void onEnable() {
        rootNode.addChild("yes").hidden(true).denyTabCompletion()
            .description("Say yes")
            .playerCaller(this::yes);
        rootNode.addChild("no").hidden(true).denyTabCompletion()
            .description("Say no")
            .playerCaller(this::no);
    }

    protected boolean yes(Player player, String[] args) {
        if (args.length != 1) return true;
        Attraction attraction = plugin.attractionsMap.get(args[0]);
        if (attraction == null) return true;
        attraction.onClickYes(player);
        return true;
    }

    protected boolean no(Player player, String[] args) {
        return true;
    }
}
