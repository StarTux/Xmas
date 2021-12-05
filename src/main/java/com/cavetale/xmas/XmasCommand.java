package com.cavetale.xmas;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.xmas.attraction.Attraction;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
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
        rootNode.addChild("trader").hidden(true).denyTabCompletion()
            .description("Open trader")
            .playerCaller(this::trader);
        rootNode.addChild("cal").hidden(true).denyTabCompletion()
            .description("Open calendar")
            .playerCaller(this::cal);
        rootNode.addChild("inv").hidden(true).denyTabCompletion()
            .description("Open Secret Santa Inventory")
            .playerCaller(this::inv);
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

    protected boolean trader(Player player, String[] args) {
        if (args.length != 1) return true;
        XmasPresent trader;
        try {
            trader = XmasPresent.valueOf(args[0]);
        } catch (IllegalArgumentException iae) {
            return true;
        }
        if (plugin.sessionOf(player).lastClickedPresent != trader) return true;
        plugin.openPresentInventory(player, null, trader);
        player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 0.5f, 2.0f);
        return true;
    }

    protected boolean cal(Player player, String[] args) {
        if (args.length != 0) return true;
        plugin.openCalendar(player);
        return true;
    }

    protected boolean inv(Player player, String[] args) {
        if (args.length != 0) return true;
        plugin.openPresentInventory(player);
        return true;
    }
}
