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
package de.bluecolored.bluemap.common.api;

import de.bluecolored.bluemap.api.WebApp;
import de.bluecolored.bluemap.common.plugin.Plugin;
import de.bluecolored.bluemap.core.util.FileHelper;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Stream;

public class WebAppImpl implements WebApp {
    private static final Path IMAGE_ROOT_PATH = Paths.get("data", "images");

    private final Plugin plugin;

    public WebAppImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public Path getWebRoot() {
        return plugin.getConfigs().getWebappConfig().getWebroot();
    }

    @Override
    public void setPlayerVisibility(UUID player, boolean visible) {
        if (visible) {
            plugin.getPluginState().removeHiddenPlayer(player);
        } else {
            plugin.getPluginState().addHiddenPlayer(player);
        }
    }

    @Override
    public boolean getPlayerVisibility(UUID player) {
        return !plugin.getPluginState().isPlayerHidden(player);
    }

    @Override
    public void registerScript(String url) {
        plugin.getBlueMap().getWebFilesManager().getScripts().add(url);
    }

    @Override
    public void registerStyle(String url) {
        plugin.getBlueMap().getWebFilesManager().getStyles().add(url);
    }

    @Override
    @Deprecated()
    public String createImage(BufferedImage image, String path) throws IOException {
        path = path.replaceAll("[^a-zA-Z0-9_.\\-/]", "_");

        Path webRoot = getWebRoot().toAbsolutePath();
        String separator = webRoot.getFileSystem().getSeparator();

        Path imageRootFolder = webRoot.resolve(IMAGE_ROOT_PATH);
        Path imagePath = imageRootFolder.resolve(Paths.get(path.replace("/", separator) + ".png")).toAbsolutePath();

        FileHelper.createDirectories(imagePath.getParent());
        Files.deleteIfExists(imagePath);
        Files.createFile(imagePath);

        if (!ImageIO.write(image, "png", imagePath.toFile()))
            throw new IOException("The format 'png' is not supported!");

        return webRoot.relativize(imagePath).toString().replace(separator, "/");
    }

    @Override
    public Map<String, String> availableImages() throws IOException {
        Path webRoot = getWebRoot().toAbsolutePath();
        String separator = webRoot.getFileSystem().getSeparator();

        Path imageRootPath = webRoot.resolve("data").resolve(IMAGE_ROOT_PATH).toAbsolutePath();

        Map<String, String> availableImagesMap = new HashMap<>();

        if (Files.exists(imageRootPath)) {
            try (Stream<Path> fileStream = Files.walk(imageRootPath)) {
                fileStream
                        .filter(p -> !Files.isDirectory(p))
                        .filter(p -> p.getFileName().toString().endsWith(".png"))
                        .map(Path::toAbsolutePath)
                        .forEach(p -> {
                            try {
                                String key = imageRootPath.relativize(p).toString();
                                key = key
                                        .substring(0, key.length() - 4) //remove .png
                                        .replace(separator, "/");

                                String value = webRoot.relativize(p).toString()
                                        .replace(separator, "/");

                                availableImagesMap.put(key, value);
                            } catch (IllegalArgumentException ignore) {
                            }
                        });
            }
        }

        return availableImagesMap;
    }

}
