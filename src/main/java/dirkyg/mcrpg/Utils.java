package dirkyg.mcrpg;

import dirkyg.mcrpg.Skills.SkillManager;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;


public class Utils implements Listener {

    public Utils (McRPG plugin) {
        Bukkit.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public static String chat(String s) {
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    @EventHandler
    public void onEntityHit(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player || !(event.getEntity() instanceof LivingEntity)) {
            return;
        }
        if (event.getEntity().getCustomName() !=  null && !event.getEntity().getCustomName().equalsIgnoreCase("")) {
            if (!event.getEntity().getCustomName().contains("❤")) {
                return;
            }
        }
        Entity entity = event.getEntity();
        double health = ((LivingEntity) entity).getHealth() - event.getFinalDamage();
        health = Math.round(health * 10.0) / 10.0;
        if (health < 0.0) {
            health = 0.0;
        }
        entity.setCustomNameVisible(true);
        entity.setCustomName(Utils.chat((String.format("%.1f", health) + " &c❤ &f" + entity.getType())));
    }

    public static ItemStack createGuiItem(final Material material, final String name, final String... lore) {
        final ItemStack item = new ItemStack(material, 1);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName(name);
        meta.setLore(Arrays.asList(lore));
        if (item.getType() == Material.DIRT || item.getType() == Material.STONE || item.getType() == Material.NETHERRACK) {
            meta.addEnchant(Enchantment.MENDING, 1, true);
        }
        item.setItemMeta(meta);
        return item;
    }

    public static ItemStack createItem(final Material material, final String name, int amount, Enchantment[] enchants, int[] levels, final String... lore) {
        final ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (!name.equalsIgnoreCase("")) {
            meta.setDisplayName(name);
        }
        meta.setLore(Arrays.asList(lore));

        item.setItemMeta(meta);
        if (enchants != null) {
            for (int i = 0; i < enchants.length; i++) {
                if (material == Material.ENCHANTED_BOOK) {
                    EnchantmentStorageMeta esm = (EnchantmentStorageMeta) item.getItemMeta();
                    esm.addStoredEnchant(enchants[i], levels[i],true);
                    item.setItemMeta(esm);
                } else {
                    ItemMeta eMeta = item.getItemMeta();
                    eMeta.addEnchant(enchants[i], levels[i], true);
                    item.setItemMeta(eMeta);
                }
            }
        }
        return item;
    }

    public static void addItemToInventory(ItemStack item, Player player) {
        HashMap<Integer, ItemStack> addedItem = player.getInventory().addItem(item);
        if (!addedItem.isEmpty()) {
            player.getWorld().dropItemNaturally(player.getLocation(), item);
            player.sendMessage(Utils.chat("&dItem has been dropped at your feet"));
        }
    }

    public static int getRandomNumber(int lower, int upper) {
        Random rand = new Random();
        return rand.nextInt(lower, upper);
    }

    public static boolean isPickaxe(Material material) {
        return switch (material) {
            case WOODEN_PICKAXE, STONE_PICKAXE, IRON_PICKAXE, GOLDEN_PICKAXE, DIAMOND_PICKAXE, NETHERITE_PICKAXE -> true;
            default -> false;
        };
    }

    public static boolean isShovel(Material material) {
        return switch (material) {
            case WOODEN_SHOVEL, STONE_SHOVEL, IRON_SHOVEL, GOLDEN_SHOVEL, DIAMOND_SHOVEL, NETHERITE_SHOVEL -> true;
            default -> false;
        };
    }

    public static boolean isAxe(Material material) {
        return switch (material) {
            case WOODEN_AXE, STONE_AXE, IRON_AXE, GOLDEN_AXE, DIAMOND_AXE, NETHERITE_AXE -> true;
            default -> false;
        };
    }

    public static boolean isSword(Material material) {
        return switch (material) {
            case WOODEN_SWORD , STONE_SWORD, IRON_SWORD, GOLDEN_SWORD, DIAMOND_SWORD, NETHERITE_SWORD -> true;
            default -> false;
        };
    }

    public static boolean isCrop(Material material) {
        return switch (material) {
            case WHEAT_SEEDS, WHEAT, CARROTS, POTATOES, BEETROOTS, BEETROOT_SEEDS, MELON_SEEDS, PUMPKIN_SEEDS, TORCHFLOWER, TORCHFLOWER_CROP, TORCHFLOWER_SEEDS ->
                    // Add more crops as needed
                    true;
            default -> false;
        };
    }

    public static boolean isWood(Material material) {
        boolean retVal = false;
        switch (material) {
            case OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG, CRIMSON_STEM, WARPED_STEM, OAK_WOOD, SPRUCE_WOOD, BIRCH_WOOD, JUNGLE_WOOD, ACACIA_WOOD, DARK_OAK_WOOD, CRIMSON_HYPHAE, WARPED_HYPHAE -> {
                retVal = true;
            }
            default -> {}
        };
        if (!retVal && (material.name().contains("LOG")) || material.name().contains("WOOD")){
            retVal = true;
        }
        return retVal;
    }

    public static boolean isMineMat(Material material) {
        boolean retVal = false;
        switch (material) {
            case COAL_ORE, COPPER_ORE, STONE, COBBLESTONE, DIORITE, ANDESITE, GRANITE, IRON_ORE, GOLD_ORE, DIAMOND_ORE, LAPIS_ORE, REDSTONE_ORE, NETHER_QUARTZ_ORE, EMERALD_ORE, OBSIDIAN, END_STONE, ANCIENT_DEBRIS, CRYING_OBSIDIAN, BLACKSTONE, DEEPSLATE, TUFF, CALCITE, DEEPSLATE_LAPIS_ORE, DEEPSLATE_COAL_ORE, DEEPSLATE_IRON_ORE, DEEPSLATE_GOLD_ORE, DEEPSLATE_EMERALD_ORE, DEEPSLATE_DIAMOND_ORE, DEEPSLATE_REDSTONE_ORE, DEEPSLATE_COPPER_ORE, NETHER_GOLD_ORE, BASALT, SMOOTH_BASALT, SANDSTONE, RED_SANDSTONE, COBBLED_DEEPSLATE -> {
                retVal = true;
            }
            default -> {}
        }
        if (!retVal && (material.name().contains("CONCRETE") || material.name().contains("TERRACOTTA"))) {
            if (!material.name().contains("POWDER")) {
                retVal = true;
            }
        }
        return retVal;
    }

    public static boolean isDigMat(Material material) {
        boolean retVal = false;
        switch (material) {
            case SAND, RED_SAND, GRAVEL, DIRT, COARSE_DIRT, MYCELIUM, SNOW, POWDER_SNOW, SNOW_BLOCK, SOUL_SAND, SOUL_SOIL, ROOTED_DIRT, FARMLAND, MUD, CLAY, DIRT_PATH, PODZOL, GRASS_BLOCK -> {
                retVal = true;
            }
            default -> {}
        };
        if (!retVal && material.name().contains("POWDER")) {
            retVal = true;
        }
        return retVal;
    }

    public static boolean isLog(Material material) {
        return switch (material) {
            case OAK_LOG, SPRUCE_LOG, BIRCH_LOG, JUNGLE_LOG, ACACIA_LOG, DARK_OAK_LOG, CRIMSON_STEM, WARPED_STEM -> true;
            default -> false;
        };
    }

    public static boolean isBreedable(EntityType entityType) {
        return switch (entityType) {
            case WOLF, CAT, HORSE, DONKEY, LLAMA, SHEEP, COW, GOAT, MUSHROOM_COW, PIG, CHICKEN, RABBIT, TURTLE, PANDA, FOX, BEE, FROG, AXOLOTL, CAMEL, STRIDER, HOGLIN, SNIFFER -> true;
            default -> false;
        };
    }

    public static Entity getTargetEntity(Player player, double maxDistance) {
        Vector start = player.getEyeLocation().toVector();
        Vector direction = player.getLocation().getDirection().normalize();
        RayTraceResult rayTrace = player.getWorld().rayTraceEntities(player.getEyeLocation(), direction, maxDistance, 0.5, (entity) -> !entity.equals(player));
        return (rayTrace != null) ? rayTrace.getHitEntity() : null;
    }

    public static void sendActionBar(Player player, String message) {
        player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(message));
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

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        System.out.println(event.getCause());
        if (event.getEntity() instanceof Player player) {
            if (event.getEntity().getNearbyEntities(2, 2, 2).stream().anyMatch(e -> e.getType().toString().equals("FIREWORK"))) {
                event.setCancelled(true);
            }
        }
    }
}
