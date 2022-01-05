package io.github.toberocat.improvedfactions.utility;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import io.github.toberocat.improvedfactions.ImprovedFactionsMain;
import io.github.toberocat.improvedfactions.event.FactionEvent;
import io.github.toberocat.improvedfactions.factions.Faction;
import io.github.toberocat.improvedfactions.factions.FactionUtils;
import io.github.toberocat.improvedfactions.language.LangMessage;
import io.github.toberocat.improvedfactions.language.Language;
import net.minecraft.network.chat.IChatBaseComponent;
import net.minecraft.network.protocol.game.PacketPlayOutChat;
import org.apache.commons.lang.Validate;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.org.apache.commons.codec.binary.Base64;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.util.Vector;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class Utils {
    public static ItemStack createItem(final Material material, final String name, final String[] lore) {
        final ItemStack item = new ItemStack(material, 1);
        final ItemMeta meta = item.getItemMeta();

        meta.setDisplayName(name);
        if (lore != null) meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);

        return item;
    }

    public static ItemStack modiflyItem(ItemStack stack, String title, String... lore) {
        ItemMeta meta = stack.getItemMeta();
        meta.setDisplayName(title);
        meta.setLore(Arrays.asList(lore));
        ItemStack item = new ItemStack(stack);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack getSkull(String url, int count, String name, String... lore) {
        if (Bukkit.getVersion().contains("1.18")) {
            return createItem(Material.BARRIER, name, lore);
        }
        ItemStack head = new ItemStack(Material.PLAYER_HEAD, count);
        if(url.isEmpty())return head;


        SkullMeta headMeta = (SkullMeta) head.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        byte[] encodedData = Base64.encodeBase64(String.format("{textures:{SKIN:{url:\"%s\"}}}", url).getBytes());
        profile.getProperties().put("textures", new Property("textures", new String(encodedData)));
        Field profileField = null;
        try {
            assert headMeta != null;
            profileField = headMeta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(headMeta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e1) {
            e1.printStackTrace();
        }
        head.setItemMeta(headMeta);

        ItemMeta meta = head.getItemMeta();
        assert meta != null;
        meta.setDisplayName(name);
        if (lore != null) meta.setLore(Arrays.asList(lore));
        head.setItemMeta(meta);
        return head;
    }

    public static <T> List<String> listToStringList(List<T> list) {
        List<String> strList = new ArrayList<>();
        for (T type : list) {
            strList.add(type.toString());
        }
        return strList;
    }

    public static void ClaimChunk(Player player) {
        if (FactionUtils.getFaction(player) != null) {
            Chunk chunk = player.getLocation().getChunk();
            Faction faction = ImprovedFactionsMain.playerData.get(player.getUniqueId()).playerFaction;

            faction.ClaimChunk(chunk, status -> {
                if (status == null) { //No power left
                    Language.sendMessage(LangMessage.CLAIM_ONE_NO_POWER, player);
                } else if (status.getClaimStatus() == ClaimStatus.Status.SUCCESS) { //Claimed chunk successfully
                    Language.sendMessage(LangMessage.CLAIM_ONE_SUCCESS, player);
                } else if (status.getClaimStatus() == ClaimStatus.Status.NEED_CONNECTION) { //Chunk needs to be connected
                    Language.sendMessage(LangMessage.CLAIM_ONE_NOT_CONNECTED, player);
                } else if (status.getClaimStatus() == ClaimStatus.Status.ALREADY_CLAIMED) { //Chunk claimed by another faction
                    if (status.getFactionClaim().getRegistryName().equals(faction.getRegistryName())) {
                        Language.sendMessage(LangMessage.CLAIM_ONE_ALREADY_PROPERTY, player);
                    } else {
                        Language.sendMessage(LangMessage.CLAIM_ONE_OWNED_BY_OTHERS, player);
                    }
                } else {
                    player.sendMessage(Language.getPrefix() + "§cError: "  + status.getClaimStatus().toString());
                }
            });
        } else {
            player.sendMessage(Language.getPrefix() + "§cYou need to be in a faction");
        }
    }

    public static void UnClaimChunk(Player player) {
        if (FactionUtils.getFaction(player) != null) {
            Chunk chunk = player.getLocation().getChunk();
            Faction faction = ImprovedFactionsMain.playerData.get(player.getUniqueId()).playerFaction;

            faction.UnClaimChunk(chunk, status -> {
                if (status == null) { //No power left
                    Language.sendMessage(LangMessage.UNCLAIM_ONE_SOMETHING_WRONG, player);
                } else if (status.getClaimStatus() == ClaimStatus.Status.SUCCESS) { //Claimed chunk successfully
                    Language.sendMessage(LangMessage.UNCLAIM_ONE_SUCCESS, player);
                } else if (status.getClaimStatus() == ClaimStatus.Status.NEED_CONNECTION) { //Chunk needs to be connected
                    Language.sendMessage(LangMessage.UNCLAIM_ONE_DISCONNECTED, player);
                } else if (status.getClaimStatus() == ClaimStatus.Status.NOT_CLAIMED) {
                    Language.sendMessage(LangMessage.UNCLAIM_ONE_ALREADY_WILDNESS, player);
                } else if (status.getClaimStatus() == ClaimStatus.Status.NOT_PROPERTY) {
                    Language.sendMessage(LangMessage.UNCLAIM_ONE_NOT_YOUR_PROPERTY, player);
                } else if (status.getClaimStatus() == ClaimStatus.Status.ALREADY_CLAIMED) { //Chunk claimed by another faction
                    if (status.getFactionClaim().getRegistryName().equals(faction.getRegistryName())) {
                        player.sendMessage(Language.getPrefix() + "§cThis chunk is already your property");
                    } else {
                        Language.sendMessage(LangMessage.UNCLAIM_ONE_NOT_YOUR_PROPERTY, player);
                    }
                }
            });
        } else {
            player.sendMessage(Language.getPrefix() + "§cYou need to be in a faction");
        }
    }

    public static boolean CallEvent(Class<FactionEvent> eventClazz, Faction faction, Object[] objects, boolean cancellable)
            throws NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException {
        FactionEvent event = eventClazz.getConstructor(Faction.class, Object[].class).newInstance(faction, objects);
        Bukkit.getPluginManager().callEvent(event);

        return !cancellable || !event.isCancelled();
    }

}
