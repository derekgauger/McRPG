package dirkyg.mcrpg.Utilities;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.Particle.DustOptions;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import dirkyg.mcrpg.McRPG;
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
        Firework firework = location.getWorld().spawn(location.add(0, 2, 0), Firework.class);
        FireworkMeta fireworkMeta = firework.getFireworkMeta();
        FireworkEffect effect = FireworkEffect.builder()
                .withColor(Color.WHITE, Color.SILVER, Color.YELLOW)
                .withFade(Color.BLUE, Color.PURPLE, Color.LIME)
                .with(FireworkEffect.Type.BALL_LARGE) // Shape
                .flicker(true) // Flicker effect
                .trail(true) // Trail effect
                .build();
        fireworkMeta.addEffect(effect);
        fireworkMeta.setPower(1); // Set the launch power (height)
        firework.setFireworkMeta(fireworkMeta);
    }

    public static void drawParticleCircle(Location center, double radius, int particleCount, DustOptions dustOptions) {
        World world = center.getWorld();
        double increment = (2 * Math.PI) / particleCount;

        // Loop through all the points in the circle
        for (int i = 0; i < particleCount; i++) {
            double angle = i * increment;
            double x = center.getX() + (radius * Math.cos(angle));
            double z = center.getZ() + (radius * Math.sin(angle));
            // Create the particle at the position, with an offset of 1 in the Y direction to make it visible
            world.spawnParticle(Particle.REDSTONE, x, center.getY() + 1, z, particleCount, dustOptions);
        }
    }

    public static void drawLine(Location start, Location end, Particle particle, int count, long duration) {
        Vector vector = end.toVector().subtract(start.toVector()).normalize().multiply(0.1); // Adjust the multiplier for spacing
        int steps = (int) (start.distance(end) / 0.1);
        
        new BukkitRunnable() {
            int step = 0;
            Location current = start.clone();
    
            @Override
            public void run() {
                current.getWorld().spawnParticle(particle, current, count);
                current.add(vector);
                step++;
    
                if (step >= steps) {
                    cancel();
                }
            }
        }.runTaskTimer(McRPG.plugin, 0L, duration / steps);
    }

    public static void createSphere(Location center, double radius, Particle particle, int count) {
        // Adjust the denominators for a larger gap between particles
        DustOptions dustOptions = new DustOptions(Color.fromRGB(0, 255, 0), 1);

        // For instance, changing 10 to 5 or 20 to 10 will double the gap
        for (double theta = 0; theta <= Math.PI; theta += Math.PI / 5) {
            double r = Math.sin(theta) * radius;
            double y = Math.cos(theta) * radius;
            for (double phi = 0; phi <= 2 * Math.PI; phi += Math.PI / 10) {
                double x = Math.cos(phi) * r;
                double z = Math.sin(phi) * r;
                center.getWorld().spawnParticle(particle, center.clone().add(x, y, z), count, dustOptions);
            }
        }
    }

    public static void pulseSphere(Entity entity, double maxRadius, Particle particle, int count, long durationInSeconds) {
        final double[] radius = {1}; // start with a tiny radius
        final boolean[] expanding = {true};

        // Reference to entity and particle for use within the runnable
        final Entity referenceEntity = entity;
        final Particle referenceParticle = particle;

        new BukkitRunnable() {
            int iterations;
            @Override
            public void run() {
                if (iterations == durationInSeconds) {
                    this.cancel();
                }
                createSphere(referenceEntity.getLocation(), radius[0], referenceParticle, count);
                if (expanding[0]) {
                    radius[0] += 1;
                    if (radius[0] >= maxRadius) {
                        expanding[0] = false;
                    }
                } else {
                    radius[0] -= 1;
                    if (radius[0] <= 0.1) {
                        expanding[0] = true;
                    }
                }
                iterations++;
            }
        }.runTaskTimer(McRPG.plugin, 0L, 10L);
    }
}
