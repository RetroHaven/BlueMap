/*
 * This file is part of BlueMap, licensed under the MIT License (MIT).
 *
 * Copyright (c) Blue (Lukas Rieger) <https://bluecolored.de>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package de.bluecolored.bluemap.bukkit;

import com.flowpowered.math.vector.Vector3d;
import de.bluecolored.bluemap.common.serverinterface.Gamemode;
import de.bluecolored.bluemap.common.serverinterface.Player;
import de.bluecolored.bluemap.common.plugin.text.Text;
import org.bukkit.Bukkit;
import org.bukkit.Location;

import java.io.IOException;
import java.util.UUID;

public class BukkitPlayer implements Player {

    private final UUID uuid;
    private final String playerName;
    private Text name;
    private String world;
    private Vector3d position;
    private Vector3d rotation;
    private int skyLight;
    private int blockLight;
    private boolean online;
    private boolean sneaking;
    private boolean invisible;
    private boolean vanished;
    private Gamemode gamemode;

    public BukkitPlayer(UUID uuid, String playerName) {
        this.uuid = uuid;
        this.playerName = playerName;
        update();
    }

    @Override
    public UUID getUuid() {
        return this.uuid;
    }

    @Override
    public Text getName() {
        return this.name;
    }

    @Override
    public String getWorld() {
        return this.world;
    }

    @Override
    public Vector3d getPosition() {
        return this.position;
    }

    @Override
    public Vector3d getRotation() {
        return rotation;
    }

    @Override
    public int getSkyLight() {
        return skyLight;
    }

    @Override
    public int getBlockLight() {
        return blockLight;
    }

    @Override
    public boolean isOnline() {
        return this.online;
    }

    @Override
    public boolean isSneaking() {
        return this.sneaking;
    }

    @Override
    public boolean isInvisible() {
        return this.invisible;
    }

    @Override
    public boolean isVanished() {
        return vanished;
    }

    @Override
    public Gamemode getGamemode() {
        return this.gamemode;
    }

    /**
     * API access, only call on server thread!
     */
    public void update() {
        org.bukkit.entity.Player player = Bukkit.getServer().getPlayer(this.playerName);
        if (player == null) {
            this.online = false;
            return;
        }

        this.gamemode = Gamemode.SURVIVAL;

        this.invisible = this.vanished = false; // TODO add support for vanish;

        //also check for "vanished" players
        /*
                boolean vanished = false;
        for (MetadataValue meta : player.getMetadata("vanished")) {
            if (meta.asBoolean()) vanished = true;
        }
         */

        this.name = Text.of(player.getName());
        this.online = player.isOnline();

        Location location = player.getLocation();
        this.position = new Vector3d(location.getX(), location.getY(), location.getZ());
        this.rotation = new Vector3d(location.getPitch(), location.getYaw(), 0);
        this.sneaking = player.isSneaking();

        this.skyLight = //player.getLocation().getBlock().getLightLevel();
        this.blockLight = player.getLocation().getBlock().getLightLevel();

        try {
            var world = BukkitPlugin.getInstance().getWorld(player.getWorld());
            this.world = BukkitPlugin.getInstance().getPlugin().getBlueMap().getWorldId(world.getSaveFolder());
        } catch (IOException | NullPointerException e) { // NullPointerException -> the plugin isn't fully loaded
            this.world = "unknown";
        }
    }

}
