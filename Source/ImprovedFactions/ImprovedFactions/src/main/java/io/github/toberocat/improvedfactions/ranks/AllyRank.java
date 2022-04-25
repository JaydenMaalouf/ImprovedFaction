package io.github.toberocat.improvedfactions.ranks;

import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.inventory.ItemStack;

public class AllyRank extends Rank{
    public static final String registry = "allyrank";
    public AllyRank() {
        super("Ally rank", registry, false);
    }

    @Override
    public String description(int line) {
        if (line == 0) {
            return Language.format("&8Allies are your");
        } else if (line == 1) {
            return Language.format("&8factions friends");
        }
        return "";
    }
}
