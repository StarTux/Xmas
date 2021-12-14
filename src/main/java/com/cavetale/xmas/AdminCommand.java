package com.cavetale.xmas;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandArgCompleter;
import com.cavetale.core.command.CommandWarn;
import com.cavetale.mytems.item.music.MelodyReplay;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public final class AdminCommand extends AbstractCommand<XmasPlugin> {
    private MelodyReplay melodyReplay;

    protected AdminCommand(final XmasPlugin plugin) {
        super(plugin, "xmasadmin");
    }

    @Override
    protected void onEnable() {
        rootNode.addChild("cal").denyTabCompletion()
            .description("Open calendar")
            .playerCaller(this::cal);
        rootNode.addChild("music").arguments("<melody>")
            .description("Play music")
            .completers(CommandArgCompleter.enumLowerList(Music.class))
            .playerCaller(this::music);
        rootNode.addChild("stopmusic").denyTabCompletion()
            .description("Stop music")
            .playerCaller(this::stopMusic);
        rootNode.addChild("day").arguments("<day>")
            .description("Force day of Christmas")
            .senderCaller(this::day);
        rootNode.addChild("present").arguments("<player> <present>")
            .completers(CommandArgCompleter.NULL,
                        CommandArgCompleter.enumLowerList(XmasPresent.class))
            .description("Add item to player's present inventory")
            .senderCaller(this::present);
        rootNode.addChild("key").arguments("<player> <amount>")
            .completers(CommandArgCompleter.NULL,
                        CommandArgCompleter.integer(i -> true))
            .description("Add keys to player's inventory")
            .senderCaller(this::key);
    }

    protected boolean cal(Player player, String[] args) {
        if (args.length != 0) return false;
        plugin.openCalendar(player);
        return true;
    }

    protected boolean music(Player player, String[] args) {
        if (args.length != 1) return false;
        Music music;
        try {
            music = Music.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException iae) {
            throw new CommandWarn("Invalid melody: " + args[0]);
        }
        melodyReplay = music.melody.play(plugin, player);
        player.sendMessage("Playing melody: " + music);
        return true;
    }

    protected boolean stopMusic(Player player, String[] args) {
        if (args.length != 0) return false;
        if (melodyReplay == null) throw new CommandWarn("Music not playing!");
        melodyReplay.stop();
        melodyReplay = null;
        player.sendMessage("Music stopped");
        return true;
    }

    protected boolean day(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        int day;
        try {
            day = Integer.parseInt(args[0]);
        } catch (IllegalArgumentException iae) {
            throw new CommandWarn("Invalid day: " + args[0]);
        }
        plugin.forcedDayOfChristmas = day;
        plugin.updateDayOfChristmas();
        sender.sendMessage("Forced day of Christmas to " + day);
        return true;
    }

    protected boolean present(CommandSender sender, String[] args) {
        if (args.length != 2) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        List<XmasPresent> xmasPresents;
        if (args[1].equals("*")) {
            xmasPresents = List.of(XmasPresent.values());
        } else {
            try {
                xmasPresents = List.of(XmasPresent.valueOf(args[1].toUpperCase()));
            } catch (IllegalArgumentException iae) {
                throw new CommandWarn("Present not found: " + args[1]);
            }
        }
        Session session = plugin.sessionOf(target);
        int count = 0;
        for (XmasPresent xmasPresent : xmasPresents) {
            boolean has = session.tag.presentList.contains(xmasPresent);
            if (!has) {
                session.tag.presentList.add(xmasPresent);
                count += 1;
            }
        }
        if (count > 0) {
            session.save();
        }
        plugin.openPresentInventory(target, xmasPresents.get(0), null);
        if (count > 0) {
            sender.sendMessage("Given " + count + " presents to " + target.getName());
        } else {
            sender.sendMessage("Fake given " + xmasPresents.size() + " presents to " + target.getName());
        }
        return true;
    }

    protected boolean key(CommandSender sender, String[] args) {
        if (args.length != 2) return false;
        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) throw new CommandWarn("Player not found: " + args[0]);
        int amount;
        try {
            amount = Integer.parseInt(args[1]);
        } catch (IllegalArgumentException iae) {
            throw new CommandWarn("Invalid amount: " + args[1]);
        }
        Session session = plugin.sessionOf(target);
        session.tag.keys += amount;
        session.save();
        sender.sendMessage("Given " + amount + " keys to " + target.getName());
        return true;
    }
}
