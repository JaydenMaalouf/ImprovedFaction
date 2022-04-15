package io.github.toberocat.core.utility.gui.slot;

import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public abstract class Slot {
    private ItemStack stack;

    public Slot(ItemStack stack) {
        this.stack = stack;
    }

    public abstract void OnClick(HumanEntity player);

    public ItemStack getStack() {
        return stack;
    }

    public void setStack(ItemStack stack) {
        this.stack = stack;
    }
}
