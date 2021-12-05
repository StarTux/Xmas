package com.cavetale.xmas;

import com.cavetale.core.command.AbstractCommand;
import com.cavetale.core.command.CommandArgCompleter;
import com.cavetale.core.command.CommandWarn;
import com.cavetale.mytems.item.music.MelodyReplay;
import com.cavetale.xmas.attraction.Attraction;
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
        rootNode.addChild("wakeup").arguments("<day>")
            .description("Wakeup attractions")
            .senderCaller(this::wakeUp);
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

    protected boolean wakeUp(CommandSender sender, String[] args) {
        if (args.length != 1) return false;
        int day;
        try {
            day = Integer.parseInt(args[0]);
        } catch (IllegalArgumentException iae) {
            throw new CommandWarn("Invalid day: " + args[0]);
        }
        int count = 0;
        for (Attraction attraction : plugin.attractionsMap.values()) {
            attraction.wakeUp(day);
            count += 1;
        }
        sender.sendMessage("Woke up " + count + " attractions with day " + day);
        return true;
    }
}
