package dirkyg.mcrpg.WorldGeneration;


import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.bukkit.BukkitWorld;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import dirkyg.mcrpg.McRPG;
import dirkyg.mcrpg.Utils;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class SchematicWorldGenerator implements CommandExecutor {

    private final WorldEditPlugin worldEdit;
    McRPG plugin;
    private static final String createdWorldNamesFile = "created_world_names.ser";
    private static List<String> createdWorldNames = new ArrayList<>();

    public SchematicWorldGenerator(McRPG plugin) {
        this.plugin = plugin;
        this.worldEdit = (WorldEditPlugin) Bukkit.getServer().getPluginManager().getPlugin("WorldEdit");
        if (this.worldEdit == null) {
            throw new RuntimeException("WorldEdit not found!");
        }
        try {
            deserializeWorldNames();
            for (String worldName : createdWorldNames) {
                if (Bukkit.getWorld(worldName) == null) {
                    WorldCreator creator = new WorldCreator(worldName);
                    Bukkit.createWorld(creator);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        Bukkit.getServer().getPluginCommand("tpworld").setExecutor(this);
        Bukkit.getServer().getPluginCommand("genworld").setExecutor(this);
        Bukkit.getServer().getPluginCommand("listworlds").setExecutor(this);
        Bukkit.getServer().getPluginCommand("loadworld").setExecutor(this);
    }

    public static void serializeWorldNames() throws IOException {
        FileOutputStream fileOut = new FileOutputStream(createdWorldNamesFile);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(createdWorldNames);
        out.close();
        fileOut.close();
        System.out.println("Saved information to " + createdWorldNamesFile);
    }

    public static void deserializeWorldNames() throws IOException, ClassNotFoundException {
        FileInputStream fileIn = new FileInputStream(createdWorldNamesFile);
        ObjectInputStream in = new ObjectInputStream(fileIn);
        createdWorldNames = (List<String>) in.readObject();
        in.close();
        fileIn.close();
        System.out.println("Loaded information from " + createdWorldNamesFile);
    }

    public static class EmptyChunkGenerator extends ChunkGenerator {
        @Override
        public ChunkData generateChunkData(World world, Random random, int x, int z, BiomeGrid biome) {
            return createChunkData(world);
        }

        @Override
        public Location getFixedSpawnLocation(World world, Random random) {
            return new Location(world, 0, 100, 0);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            System.out.println("Only players in game can do that!");
            return false;
        }
        if (label.equalsIgnoreCase("genworld")) {
            createVoidWorldWithSchematic(args[0], World.Environment.NORMAL, args[1]);
        }
        if (label.equalsIgnoreCase("loadworld")) {
            WorldCreator worldCreator = new WorldCreator(args[0]);
            Bukkit.getServer().createWorld(worldCreator);
        }
        if (label.equalsIgnoreCase("tpworld")) {
            String worldName = args[0];
            player.teleport(new Location(Bukkit.getWorld(worldName), 0, 100, 0));
        }
        if (label.equalsIgnoreCase("listworlds")) {
            for (World world : Bukkit.getServer().getWorlds()) {
                player.sendMessage(Utils.chat("&d" + world.getName()));
            }
        }
        return false;
    }

    public void createVoidWorldWithSchematic(String worldName, World.Environment environment, String schematicName) {
        World voidWorld = createWorld(worldName, environment);
        File schematicFile = new File(plugin.getDataFolder(), schematicName);
        pasteSchematicIntoWorld(schematicFile, voidWorld, new Location(voidWorld, 0, 100, 0));
    }

    public World createWorld(String worldName, World.Environment worldEnvironment) {
        try {
            WorldCreator worldCreator = new WorldCreator(worldName);
            worldCreator.generator(new EmptyChunkGenerator());
            worldCreator.environment(worldEnvironment);
            World world = Bukkit.getServer().createWorld(worldCreator);
            createdWorldNames.add(worldName);
            return world;
        } catch (Exception e) {
            System.out.println("There was an error while creating the world '" + worldName + "'!");
        }
        return null;
    }

    public void pasteSchematicIntoWorld(File schematic, World world, Location pasteLocation) {
        try {
            ClipboardFormat format = ClipboardFormats.findByFile(schematic);
            if (format == null) {
                throw new RuntimeException("Unknown schematic format!");
            }
            try (ClipboardReader reader = format.getReader(new FileInputStream(schematic))) {
                Clipboard clipboard = reader.read();
                ClipboardHolder holder = new ClipboardHolder(clipboard);

                try (EditSession editSession = worldEdit.getWorldEdit().newEditSession(new BukkitWorld(world))) {
                    Operation operation = holder
                            .createPaste(editSession)
                            .to(BlockVector3.at(pasteLocation.getX(), pasteLocation.getY(), pasteLocation.getZ()))
                            .ignoreAirBlocks(false)
                            .build();
                    Operations.complete(operation);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
