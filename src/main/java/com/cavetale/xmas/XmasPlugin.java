package com.cavetale.xmas;

import com.cavetale.area.struct.Area;
import com.cavetale.area.struct.AreasFile;
import com.cavetale.core.font.DefaultFont;
import com.cavetale.core.font.GuiOverlay;
import com.cavetale.core.struct.Cuboid;
import com.cavetale.core.struct.Vec3i;
import com.cavetale.mytems.Mytems;
import com.cavetale.mytems.util.Items;
import com.cavetale.mytems.util.Text;
import com.cavetale.resident.PluginSpawn;
import com.cavetale.resident.ZoneType;
import com.cavetale.resident.save.Loc;
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
import net.kyori.adventure.text.JoinConfiguration;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.title.Title;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.SoundCategory;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.java.JavaPlugin;

public final class XmasPlugin extends JavaPlugin {
    @Getter protected static XmasPlugin instance;
    protected static final String WORLD = "winter_woods";
    protected static final String ATTRACTION_AREAS = "Xmas";
    protected static final String TRADER_AREAS = ATTRACTION_AREAS;
    XmasCommand xmasCommand = new XmasCommand(this);
    AdminCommand adminCommand = new AdminCommand(this);
    EventListener eventListener = new EventListener(this);
    protected final Map<String, Attraction> attractionsMap = new HashMap<>();
    protected final Map<UUID, Session> sessionsMap = new HashMap<>();
    protected final List<PluginSpawn> traderSpawns = new ArrayList<>();
    @Getter protected File attractionsFolder;
    @Getter protected File playersFolder;
    public static final int YEAR = 2021;
    public static final YearMonth XMAS_MONTH = YearMonth.of(YEAR, Month.DECEMBER);
    protected final Random random = ThreadLocalRandom.current();
    protected int currentDayOfChristmas = 0;
    protected int forcedDayOfChristmas = 0;
    protected List<ItemStack> dailyPrizes;
    protected Vec3i calendarBlock = Vec3i.ZERO;
    protected Vec3i presentBlock = Vec3i.ZERO;

    @Override
    public void onEnable() {
        instance = this;
        dailyPrizes = List.of(new ItemStack[] {
                new ItemStack(Material.DIAMOND, 20), // 1
                Mytems.KITTY_COIN.createItemStack(2), // 2
                new ItemStack(Material.TNT, 64), // 3
                Mytems.RUBY.createItemStack(3), // 4
                Mytems.STAR.createItemStack(), // 5
                Mytems.MOB_CATCHER.createItemStack(16), // 6
                new ItemStack(Material.GLOWSTONE, 64), // 7
                new ItemStack(Material.BONE_BLOCK, 64), // 8
                new ItemStack(Material.AMETHYST_SHARD, 64), // 9
                new ItemStack(Material.TOTEM_OF_UNDYING), // 10
                new ItemStack(Material.GOLDEN_CARROT, 64), // 11
                new ItemStack(Material.ANCIENT_DEBRIS, 16), // 12
                new ItemStack(Material.GOLD_INGOT, 64), // 13
                new ItemStack(Material.POINTED_DRIPSTONE, 64), // 14
                new ItemStack(Material.MOSS_BLOCK, 64), // 15
                new ItemStack(Material.COPPER_BLOCK, 64), // 16
                new ItemStack(Material.SPORE_BLOSSOM, 64), // 17
                new ItemStack(Material.COOKIE, 64), // 18
                new ItemStack(Material.GLOW_BERRIES, 64), // 19
                new ItemStack(Material.GHAST_TEAR, 64), // 20
                new ItemStack(Material.BUDDING_AMETHYST), // 21
                Mytems.SANTA_BOOTS.createItemStack(), // 22
                Mytems.SANTA_PANTS.createItemStack(), // 23
                Mytems.SANTA_JACKET.createItemStack(), // 24
                Mytems.SANTA_HAT.createItemStack(), // 25
            });
        xmasCommand.enable();
        adminCommand.enable();
        eventListener.enable();
        attractionsFolder = new File(getDataFolder(), "attractions");
        playersFolder = new File(getDataFolder(), "players");
        attractionsFolder.mkdirs();
        playersFolder.mkdirs();
        loadAttractions();
        loadTraders();
        Bukkit.getScheduler().runTaskTimer(this, this::tick, 0L, 0L);
        Bukkit.getScheduler().runTaskTimer(this, this::updateDayOfChristmas, 0L, 20L * 60L);
        Gui.enable(this);
    }

    @Override
    public void onDisable() {
        clearSessions();
        clearAttractions();
        clearTraders();
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
        if (forcedDayOfChristmas > 0) {
            currentDayOfChristmas = forcedDayOfChristmas;
        } else {
            LocalDate now = LocalDate.now();
            for (int i = 25; i > 0; i -= 1) {
                LocalDate date = XMAS_MONTH.atDay(i);
                if (now.isAfter(date) || now.isEqual(date)) {
                    currentDayOfChristmas = i;
                    break;
                }
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
        for (Map.Entry<String, List<Area>> entry : areasFile.areas.entrySet()) {
            String name = entry.getKey();
            List<Area> areaList = entry.getValue();
            if (areaList.isEmpty()) continue;
            if (name.equals("ClickCalendar")) {
                calendarBlock = areaList.get(0).min;
                continue;
            } else if (name.equals("ClickPresents")) {
                presentBlock = areaList.get(0).min;
                continue;
            } else if (name.equals("Traders")) {
                continue;
            }
            Booth booth = Booth.forName(name);
            if (booth == null) {
                getLogger().warning(name + ": No Booth found!");
                continue;
            } else {
                unusedBooths.remove(booth);
            }
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

    protected void clearTraders() {
        for (PluginSpawn pluginSpawn : traderSpawns) {
            pluginSpawn.unregister();
        }
        traderSpawns.clear();
    }

    protected void loadTraders() {
        clearTraders();
        World world = getWorld();
        AreasFile areasFile = AreasFile.load(world, TRADER_AREAS);
        if (areasFile == null) {
            throw new IllegalStateException("Areas file not found: " + TRADER_AREAS);
        }
        List<Area> traderAreas = areasFile.areas.get("Traders");
        if (traderAreas == null) {
            throw new IllegalStateException("Traders list not found!");
        }
        for (XmasPresent xmasPresent : XmasPresent.values()) {
            if (xmasPresent.ordinal() >= traderAreas.size()) {
                getLogger().warning("Trader list too short for " + xmasPresent);
                break;
            }
            Area area = traderAreas.get(xmasPresent.ordinal());
            PluginSpawn traderSpawn = PluginSpawn.register(this, ZoneType.CHRISTMAS, Loc.of(area.min.toLocation(world)));
            traderSpawns.add(traderSpawn);
            traderSpawn.setOnPlayerClick(player -> onClickTrader(player, xmasPresent));
            traderSpawn.setOnMobSpawning(mob -> {
                    mob.setCollidable(false);
            });
        }
    }

    public List<Player> getPlayersIn(Cuboid cuboid) {
        List<Player> result = new ArrayList<>();
        for (Player player : getWorld().getPlayers()) {
            if (cuboid.contains(player.getLocation())) {
                result.add(player);
            }
        }
        return result;
    }

    public List<Player> getPlayersIn(Area area) {
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
        final int doorsOpened = session.tag.doorsOpened;
        final int keyAmount = session.tag.keys;
        for (int i = 0; i < 25; i += 1) {
            final int prizeIndex = i;
            final int dayOfChristmas = i + 1;
            LocalDate date = XMAS_MONTH.atDay(dayOfChristmas);
            DayOfWeek dayOfWeek = date.getDayOfWeek();
            if (i > 0 && dayOfWeek == DayOfWeek.MONDAY) weekNumber += 1;
            int dayNumber = dayOfWeek.getValue();
            int guiIndex = weekNumber * 9 + (dayNumber - 1) + 1;
            boolean open = doorsOpened >= dayOfChristmas;
            ItemStack item;
            final boolean canOpen = i == doorsOpened && currentDayOfChristmas >= dayOfChristmas
                && dailyPrizes.size() > prizeIndex;
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
                                session.getTag().setKeys(keyAmount - 1);
                                session.getTag().setDoorsOpened(doorsOpened + 1);
                                openPrize(player, dailyPrizes.get(prizeIndex), true);
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

    public void openPresentInventory(Player player, XmasPresent highlight, XmasPresent trader) {
        Session session = sessionOf(player);
        final int size = 6 * 9;
        Gui gui = new Gui(this).size(size);
        GuiOverlay.Builder builder = GuiOverlay.builder(size)
            .layer(GuiOverlay.BLANK, TextColor.color(0x8080FF))
            .title(Attraction.xmasify("Secret Santa Inventory").decorate(TextDecoration.BOLD));
        int i = 0;
        for (XmasPresent xmasPresent : session.getTag().getPresentList()) {
            final int index = i++;
            gui.setItem(index, xmasPresent.makeItemStack(), click -> {
                    if (!click.isLeftClick()) return;
                    onClickPresentInventory(player, xmasPresent, trader);
                });
            if (xmasPresent == highlight) {
                builder.highlightSlot(index, NamedTextColor.GOLD);
            }
        }
        gui.title(builder.build());
        gui.open(player);
    }

    public void openPresentInventory(Player player) {
        openPresentInventory(player, null, null);
    }

    public void openPrize(Player player, ItemStack prize, boolean hidden) {
        Session session = sessionOf(player);
        final int size = 3 * 9;
        final int slot = 13;
        Gui gui = new Gui(this).size(size);
        GuiOverlay.Builder builder = GuiOverlay.builder(size)
            .layer(GuiOverlay.BLANK, TextColor.color(0xFF0000))
            .title(Attraction.xmasify("Merry Christmas!").decorate(TextDecoration.BOLD));
        if (hidden) {
            List<Component> tooltip = List.of(Attraction.xmasify("Open Present"));
            ItemStack icon = Mytems.QUESTION_MARK.createIcon(tooltip);
            gui.setItem(slot, icon, click -> {
                    if (!click.isLeftClick()) return;
                    player.closeInventory();
                });
            gui.onClose(evt -> {
                    Music.DECK_THE_HALLS.melody.play(this, player);
                    openPrize(player, prize, false);
                });
        } else {
            gui.setItem(slot, prize);
            gui.setEditable(true);
            gui.onClose(evt -> {
                    for (ItemStack item : gui.getInventory()) {
                        if (item == null || item.getType() == Material.AIR) continue;
                        for (ItemStack drop : player.getInventory().addItem(item).values()) {
                            player.getWorld().dropItem(player.getEyeLocation(), drop);
                        }
                    }
                    if (session.tag.doorsOpened == 25) {
                        String cmd = "titles unlockset " + player.getName() + " Santa";
                        getLogger().info("Running command: " + cmd);
                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
                        session.tag.doorsOpened += 1;
                        session.save();
                    }
                });
            builder.highlightSlot(slot, NamedTextColor.GOLD);
        }
        gui.title(builder.build());
        gui.open(player);
    }

    protected void onClickTrader(Player player, XmasPresent xmasPresent) {
        Session session = sessionOf(player);
        session.lastClickedPresent = xmasPresent;
        if (session.tag.presentsGiven.contains(xmasPresent)) {
            player.sendMessage(Attraction.xmasify("Thank you so much!"));
            player.sendActionBar(Attraction.xmasify("Thank you so much!"));
            return;
        }
        ItemStack book = new ItemStack(Material.WRITTEN_BOOK);
        Component page = Component.join(JoinConfiguration.noSeparators(), new Component[] {
                Attraction.xmasify("Winter Woods Villager"),
                Component.newline(),
                Component.newline(),
                Component.text(xmasPresent.request),
                Component.newline(),
                Component.newline(),
                (DefaultFont.OK_BUTTON.component
                 .hoverEvent(HoverEvent.showText(Attraction.xmasify("Help this Villager")))
                 .clickEvent(ClickEvent.runCommand("/xmas trader " + xmasPresent.name()))),
            });
        book.editMeta(m -> {
                BookMeta meta = (BookMeta) m;
                meta.setAuthor("Cavetale");
                meta.title(Component.text("Christmas"));
                meta.pages(List.of(page));
            });
        player.openBook(book);
    }

    protected void onClickPresentInventory(Player player, XmasPresent clicked, XmasPresent trader) {
        if (clicked != trader) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 0.5f, 0.5f);
            return;
        }
        Session session = sessionOf(player);
        if (session.tag.presentsGiven.contains(trader) || !session.tag.presentList.contains(trader)) {
            player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 0.5f, 0.5f);
            return;
        }
        session.tag.presentsGiven.add(trader);
        session.tag.presentList.remove(trader);
        session.tag.keys += 1;
        session.save();
        player.closeInventory();
        Music.DECK_THE_HALLS.melody.play(this, player);
        player.showTitle(Title.title(Mytems.GOLDEN_KEY.component,
                                     Attraction.xmasify("You received a golden key!")));
    }
}
