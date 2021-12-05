package com.cavetale.xmas;

import com.cavetale.area.struct.AreasFile;
import com.cavetale.area.struct.Cuboid;
import com.cavetale.core.font.GuiOverlay;
import com.cavetale.mytems.Mytems;
import com.cavetale.mytems.util.Items;
import com.cavetale.mytems.util.Text;
import com.cavetale.xmas.attraction.Attraction;
import com.cavetale.xmas.attraction.AttractionType;
import com.cavetale.xmas.util.Gui;
import java.io.File;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.Month;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.logging.Level;
import lombok.Getter;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public final class XmasPlugin extends JavaPlugin {
    @Getter protected static XmasPlugin instance;
    protected static final String WORLD = "winter_woods";
    protected static final String ATTRACTION_AREAS = "XmasAttractions";
    protected static final String TRADER_AREAS = "XmasTraders";
    XmasCommand xmasCommand = new XmasCommand(this);
    AdminCommand adminCommand = new AdminCommand(this);
    EventListener eventListener = new EventListener(this);
    protected final Map<String, Attraction> attractionsMap = new HashMap<>();
    protected final Map<UUID, Session> sessionsMap = new HashMap<>();
    @Getter protected File attractionsFolder;
    @Getter protected File playersFolder;
    public static final int YEAR = 2021;
    public static final YearMonth XMAS_MONTH = YearMonth.of(YEAR, Month.DECEMBER);
    protected final Random random = ThreadLocalRandom.current();
    protected int currentDayOfChristmas = 0;

    @Override
    public void onEnable() {
        instance = this;
        xmasCommand.enable();
        adminCommand.enable();
        eventListener.enable();
        attractionsFolder = new File(getDataFolder(), "attractions");
        playersFolder = new File(getDataFolder(), "players");
        attractionsFolder.mkdirs();
        playersFolder.mkdirs();
        loadAttractions();
        Bukkit.getScheduler().runTaskTimer(this, this::tick, 0L, 0L);
        Bukkit.getScheduler().runTaskTimer(this, this::updateDayOfChristmas, 0L, 20L * 60L);
        Gui.enable(this);
    }

    @Override
    public void onDisable() {
        clearSessions();
        clearAttractions();
    }

    public World getWorld() {
        return Bukkit.getWorld(WORLD);
    }

    protected void tick() {
        for (Attraction attraction : new ArrayList<>(attractionsMap.values())) {
            if (attraction.isPlaying()) {
                try {
                    attraction.tick();
                } catch (Exception e) {
                    getLogger().log(Level.SEVERE, attraction.getName(), e);
                }
            }
        }
    }

    protected void updateDayOfChristmas() {
        LocalDate now = LocalDate.now();
        for (int i = 25; i > 0; i -= 1) {
            LocalDate date = XMAS_MONTH.atDay(i);
            if (now.isAfter(date) || now.isEqual(date)) {
                currentDayOfChristmas = i;
                break;
            }
        }
        for (Attraction attraction : attractionsMap.values()) {
            attraction.wakeUp(currentDayOfChristmas);
        }
    }

    protected void clearAttractions() {
        for (Attraction attraction : attractionsMap.values()) {
            try {
                attraction.save();
                attraction.disable();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
        }
        attractionsMap.clear();
    }

    protected void clearSessions() {
        for (Session session : sessionsMap.values()) {
            session.save();
        }
        sessionsMap.clear();
    }

    protected void clearSession(UUID uuid) {
        Session session = sessionsMap.remove(uuid);
        if (session != null) session.save();
    }

    protected void loadAttractions() {
        World world = getWorld();
        if (world == null) {
            getLogger().warning("World not found: " + WORLD);
            return;
        }
        AreasFile areasFile = AreasFile.load(world, ATTRACTION_AREAS);
        if (areasFile == null) throw new IllegalStateException("Areas file not found: " + ATTRACTION_AREAS);
        Set<Booth> unusedBooths = EnumSet.allOf(Booth.class);
        for (Map.Entry<String, List<Cuboid>> entry : areasFile.areas.entrySet()) {
            String name = entry.getKey();
            Booth booth = Booth.forName(name);
            if (booth == null) {
                getLogger().warning(name + ": No Booth found!");
                continue;
            } else {
                unusedBooths.remove(booth);
            }
            List<Cuboid> areaList = entry.getValue();
            Attraction attraction = Attraction.of(this, name, areaList, booth);
            if (attraction == null) {
                getLogger().warning(name + ": No Attraction!");
            }
            try {
                attraction.enable();
                attraction.load();
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            attractionsMap.put(name, attraction);
        }
        for (Booth booth : unusedBooths) {
            getLogger().warning(booth + ": Booth unused");
        }
        Map<AttractionType, Integer> counts = new EnumMap<>(AttractionType.class);
        for (AttractionType type : AttractionType.values()) counts.put(type, 0);
        for (Attraction attraction : attractionsMap.values()) {
            AttractionType type = AttractionType.of(attraction);
            counts.put(type, counts.get(type) + 1);
        }
        List<AttractionType> rankings = new ArrayList<>(List.of(AttractionType.values()));
        Collections.sort(rankings, (a, b) -> Integer.compare(counts.get(a), counts.get(b)));
        for (AttractionType type : rankings) {
            getLogger().info(counts.get(type) + " " + type);
        }
        getLogger().info(attractionsMap.size() + " Total");
    }

    public List<Player> getPlayersIn(Cuboid area) {
        List<Player> result = new ArrayList<>();
        for (Player player : getWorld().getPlayers()) {
            if (area.contains(player.getLocation())) {
                result.add(player);
            }
        }
        return result;
    }

    public Session sessionOf(Player player) {
        return sessionsMap.computeIfAbsent(player.getUniqueId(), u -> {
                Session newSession = new Session(this, player);
                newSession.load();
                return newSession;
            });
    }

    protected <T extends Attraction> void applyActiveAttraction(Class<T> type, Consumer<T> consumer) {
        for (Attraction attraction : attractionsMap.values()) {
            if (attraction.isPlaying() && type.isInstance(attraction)) {
                consumer.accept(type.cast(attraction));
            }
        }
    }

    public void openCalendar(Player player) {
        Session session = sessionOf(player);
        final int size = 6 * 9;
        Gui gui = new Gui(this).size(size);
        GuiOverlay.Builder builder = GuiOverlay.builder(size)
            .layer(GuiOverlay.BLANK, TextColor.color(0x8080FF))
            .layer(GuiOverlay.TOP_BAR, TextColor.color(0xFFFFFF))
            .title(Attraction.xmasify("Advent Calendar " + YEAR).decorate(TextDecoration.BOLD));
        int weekNumber = 1;
        final int openedUntil = session.tag.doorsOpened;
        final int keyAmount = session.tag.keys;
        for (int i = 0; i < 25; i += 1) {
            final int dayOfChristmas = i + 1;
            LocalDate date = XMAS_MONTH.atDay(dayOfChristmas);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (i > 0 && dayOfWeek == DayOfWeek.MONDAY) weekNumber += 1;
            int dayNumber = dayOfWeek.getValue();
            int guiIndex = weekNumber * 9 + (dayNumber - 1) + 1;
            boolean open = openedUntil >= dayOfChristmas;
            ItemStack item;
            final boolean canOpen = i == openedUntil && currentDayOfChristmas >= dayOfChristmas;
            if (open) {
                item = Mytems.CROSSED_CHECKBOX.createItemStack(dayOfChristmas);
            } else {
                item = canOpen
                    ? Mytems.GOLDEN_KEYHOLE.createItemStack(dayOfChristmas)
                    : Mytems.CHECKBOX.createItemStack(dayOfChristmas);
            }
            TextColor dayColor = i % 2 == 0 ? TextColor.color(0xE40010) : TextColor.color(0x00B32C);
            builder.highlightSlot(guiIndex, TextColor.color(0x8080FF));
            item = Items.text(item, List.of(Component.text(Text.toCamelCase(dayOfWeek)
                                                           + " " + dayOfChristmas, dayColor)));
            gui.setItem(guiIndex, item, keyAmount > 0 && canOpen
                        ? (click -> {
                                if (!click.isLeftClick()) return;
                                player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK,
                                                 SoundCategory.MASTER, 0.5f, 2.0f);
                            })
                        : (click -> {
                                if (!click.isLeftClick()) return;
                                player.playSound(player.getLocation(), Sound.ENTITY_ITEM_FRAME_REMOVE_ITEM,
                                                 SoundCategory.MASTER, 0.5f, 0.75f);
                            }));
        }
        gui.setItem(8, keyAmount == 0
                    ? Items.text(Mytems.COPPER_KEY.createItemStack(),
                                 List.of(Component.text("No keys!", NamedTextColor.DARK_GRAY)))
                    : Items.text(Mytems.GOLDEN_KEY.createItemStack(keyAmount),
                                 List.of(Component.text("You have " + keyAmount + " key" + (keyAmount > 1 ? "s" : ""),
                                                        NamedTextColor.GOLD))));
        gui.title(builder.build());
        gui.open(player);
    }

    public void openPresentInventory(Player player) {
        Session session = sessionOf(player);
        final int size = 6 * 9;
        Gui gui = new Gui(this).size(size);
        GuiOverlay.Builder builder = GuiOverlay.builder(size)
            .layer(GuiOverlay.BLANK, TextColor.color(0x8080FF))
            .title(Attraction.xmasify("Secret Santa Inventory").decorate(TextDecoration.BOLD));
        gui.title(builder.build());
        int index = 0;
        for (XmasPresent xmasPresent : session.getTag().getPresentList()) {
            gui.setItem(index++, xmasPresent.makeItemStack());
        }
        gui.open(player);
    }
}
