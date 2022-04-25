package io.github.toberocat.improvedfactions.ranks;

import io.github.toberocat.improvedfactions.language.Language;
import org.bukkit.inventory.ItemStack;

public class OwnerRank extends Rank{
    public static final String registry = "Owner";
    public OwnerRank() {
        super("Owner", registry, true);
    }

    @Override
    public String description(int line) {
        if (line == 0) {
            return Language.format("&8Owners have rights");
        } else if (line == 1) {
            return Language.format("&8to delete the faction");
        }
        return "";
    }
}
