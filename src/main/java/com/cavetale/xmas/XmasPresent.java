package com.cavetale.xmas;

import com.cavetale.mytems.Mytems;
import com.cavetale.xmas.attraction.Attraction;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.bukkit.inventory.ItemStack;

/**
 * For the trading minigame.
 */
@RequiredArgsConstructor
public enum XmasPresent {
    PAINTBRUSH(Mytems.RED_PAINTBRUSH,
               "Paintbrush",
               "My house needs a new coat of paint. I want a brush for Christmas."),
    ;

    public final Mytems mytems;
    public final String itemName;
    public final String request;
    private ItemStack itemStack;

    public ItemStack makeItemStack() {
        if (itemStack == null) {
            itemStack = mytems.createIcon(List.of(Attraction.xmasify(itemName)));
        }
        return itemStack.clone();
    }
}
