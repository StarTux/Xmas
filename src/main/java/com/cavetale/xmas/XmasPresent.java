package com.cavetale.xmas;

import com.cavetale.mytems.Mytems;
import com.cavetale.xmas.attraction.Attraction;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

/**
 * Items for the trading minigame.
 *
 * Each present has an item, and a trader that hangs out on the map,
 * on every day of the event.
 *
 * The actual presents are stored in the Session.Tag and presented in
 * the PresentInventory (see XmasPlugin).
 */
@RequiredArgsConstructor
public enum XmasPresent {
    PAINTBRUSH(Mytems.RED_PAINTBRUSH,
               "Paintbrush",
               "My house needs a new coat of paint. I asked Santa for a brush for Christmas."),
    BROOM(Mytems.WITCH_BROOM,
          "Broom",
          "This place needs a good clean. Too bad my last broom broke on me."),
    DICE(Mytems.DICE,
         "Dice",
         "I can never decide how to sort my chests. If only I had an item to choose for me..."),
    WARM_SOCKS(Mytems.SANTA_BOOTS,
               "Warm Socks",
               "I wish I could go out visit my parents, but my feet always get cold."),
    DIAMOND_RING(Mytems.WEDDING_RING,
                 "Diamond Ring",
                 "I can't believe I lost my diamond ring. What am I supposed to do now?"),
    ARMCHAIR(Mytems.RED_ARMCHAIR,
             "Comfy Armchair",
             "I hope Santa brings me something comfortable to sit in and relax."),
    KNITTED_HAT(Mytems.SANTA_HAT,
                "Knitted Hat",
                "The cold outside hurts my ears. Have you seen my hat?"),
    CHRISTMAS_BALL(Mytems.BLUE_CHRISTMAS_BALL, // 8
                   "Christmas Ball",
                   "I'm decorating my Christmas tree, but something's missing..."),
    SNOW_SHOVEL(Mytems.SNOW_SHOVEL, // 9
                "Snow Shovel",
                "I desperately need to clear my front yard of all this new snow!"),
    PRESENT(Mytems.CHRISTMAS_TOKEN, // 10
            "Wrapped Present",
            "I'm so late on my Christmas shopping this year!"
            + " I'm missing a wrapped present for my sister."),
    SANTAS_LIST(Mytems.MAGIC_MAP, // 11
                "Santa's List",
                "Can you keep a secret? I'll be Santa this year,"
                + " but I lost my list somewhere..."),
    ONION(Mytems.ORANGE_ONION, // 12
          "Festive Onion",
          "I'm making my delicious Christmas stew but ran out of onions!"),
    DODO_EGG(Mytems.EASTER_EGG, // 13
             "Dodo Egg",
             "For my next cake, I need the egg from a dodo."
             + " Unfortunately, not many dodos have been around here lately."),
    SCARY_PUMPKIN(Mytems.KINGS_PUMPKIN, // 14
                  "Scary Pumpkin",
                  "I want to make an even better snowman,"
                  + " but my last pumpkin with a face got lost around Halloween.");

    public final Mytems mytems;
    public final String itemName;
    public final String request;
    private ItemStack itemStack;

    public ItemStack makeItemStack() {
        if (itemStack == null) {
            itemStack = mytems.createIcon(List.of(Attraction.xmasify(itemName)));
            itemStack.editMeta(meta -> {
                    meta.addItemFlags(ItemFlag.values());
                    for (Enchantment ench : Enchantment.values()) {
                        meta.removeEnchant(ench);
                    }
                });
        }
        return itemStack.clone();
    }
}
