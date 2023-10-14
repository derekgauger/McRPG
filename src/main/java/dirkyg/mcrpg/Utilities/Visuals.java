package dirkyg.mcrpg.Utilities;

import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class Visuals {

    public static String colorText(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }


    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(colorText(message)));
    }

    public static void launchLevelUpFirework(Location location) {
        Firework firework = location.getWorld().spawn(location, Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.ORANGE)
                .withColor(Color.GREEN)
                .withFade(Color.BLUE) // Fade color
                .withFade(Color.YELLOW) // Fade color
                .with(FireworkEffect.Type.BALL_LARGE) // Shape
                .flicker(true) // Flicker effect
                .trail(true) // Trail effect
                .build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(1); // Set the launch power (height)
        firework.setFireworkMeta(fireworkMeta);
    }
}
