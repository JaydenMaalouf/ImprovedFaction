package io.github.toberocat.core.utility.settings.type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.toberocat.core.utility.Utility;
import io.github.toberocat.core.utility.async.AsyncCore;
import io.github.toberocat.core.utility.callbacks.Callback;
import io.github.toberocat.core.utility.gui.slot.Slot;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Setting<T> {
    protected T selected;
    protected ItemStack display;

    public Setting() {
    }

    public Setting(T t, ItemStack display) {
        this.selected = t;
        this.display = display;
    }

    public static Map<String, Setting> populateSettings(Map<String, Setting> defaulted, Map<String, Setting> current) {
        for (String key : defaulted.keySet()) {
            Setting defaultSettings = defaulted.get(key);
            Object selected = current.get(key).getSelected();
            current.replace(key, defaultSettings);
            current.get(key).setSelected(selected);

            current.get(key).setDisplay(defaultSettings.getDisplay());

            if (defaultSettings instanceof EnumSetting enumDefaults) {
                ((EnumSetting) current.get(key)).setValues(enumDefaults.getValues());
            } else if (defaultSettings instanceof CallbackSettings cbSettings) {
                ((CallbackSettings) current.get(key)).setType(cbSettings.getType());
                ((CallbackSettings) current.get(key)).setCallback(cbSettings.getCallback());
            }
        }

        return current;
    }

    public static Slot getSlot(Setting setting, Callback render) {
        if (setting instanceof HiddenSetting) return null;

        if (setting instanceof BoolSetting boolSetting) {
            StringBuilder enabled = new StringBuilder("&a");
            StringBuilder disabled = new StringBuilder("&c");

            if (boolSetting.getSelected()) enabled.append("&n");
            else disabled.append("&n");

            List<String> defaultLore = Utility.getLore(boolSetting.getDisplay());
            defaultLore.add(0, "&7Type: &eBoolean");
            if (defaultLore.size() > 1) {
                defaultLore.add(1, "");
                defaultLore.add("");
            }
            defaultLore.addAll(List.of(enabled + "enabled", disabled + "disabled"));
            defaultLore.add("");
            defaultLore.add("§8Click to toggle selected");

            return new Slot(Utility.setLore(boolSetting.getDisplay(), defaultLore.toArray(String[]::new))) {
                @Override
                public void OnClick(HumanEntity entity) {
                    setting.setSelected(!(Boolean) setting.getSelected());
                    render.callback();
                }
            };
        } else if (setting instanceof EnumSetting enumSetting) {
            String[] values = enumSetting.getValues();
            List<String> lore = Utility.getLore(enumSetting.getDisplay());
            lore.add(0, "&7Type: &eSelector");

            if (lore.size() > 1) {
                lore.add(1, "");
                lore.add("");
            }

            for (int i = 0; i < values.length; i++) {
                lore.add((enumSetting.getSelected() == i ? "&f&n" + ChatColor.stripColor(values[i]) :
                        "&7" + values[i]));
            }
            lore.add("");
            lore.add("§8Click to switch selected");

            return new Slot(Utility.setLore(enumSetting.getDisplay(), lore.toArray(String[]::new))) {
                @Override
                public void OnClick(HumanEntity entity) {
                    enumSetting.rotateSelection();
                    render.callback();
                }
            };
        } else if (setting instanceof CallbackSettings callbackSettings) {
            List<String> lore = Utility.getLore(callbackSettings.getDisplay());
            lore.add(0, "&7Type: &e" + callbackSettings.getType());

            if (lore.size() > 1) {
                lore.add(1, "");
                lore.add("");
            }

            return new Slot(Utility.setLore(callbackSettings.getDisplay(), lore.toArray(String[]::new))) {
                @Override
                public void OnClick(HumanEntity clicker) {
                    AsyncCore.runLaterSync(0, () -> callbackSettings.execute((Player) clicker));
                }
            };
        }

        return new Slot(setting.getDisplay()) {
            @Override
            public void OnClick(HumanEntity entity) {
                render.callback();
            }
        };
    }

    public T getSelected() {
        return selected;
    }

    public void setSelected(T selected) {
        this.selected = selected;
    }

    @JsonIgnore
    public ItemStack getDisplay() {
        return display;
    }

    @JsonIgnore
    public void setDisplay(ItemStack display) {
        this.display = display;
    }

    /*    @JsonIgnore
    public String[] getEnumValues() {
        return enumValues;
    }

    @JsonIgnore
    public Setting<T> setEnumValues(String[] enumValues) {
        this.enumValues = enumValues;
        return this;
    }*/
}
