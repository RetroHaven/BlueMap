## Forked to support Minecraft Beta 1.7.3's McRegion map format
Original project can be found [here](https://github.com/BlueMap-Minecraft/BlueMap)

## Currently supported methods: CLI and Poseidon (Bukkit)
Download Project Poseidon [here](https://github.com/RhysB/Project-Poseidon)

## To get the Beta look:

Make sure you generate the default BlueMap configuration first and accept the Minecraft EULA.

1. Get `Programmer's Art` as a resource pack
- https://resources.download.minecraft.net/6d/6d7a7a95b58a31716699d63b56f9ffff0e89b0a6
- rename it to `01_programmers_art.zip`
- move it to directory `config/resourcepacks/`, relative to your bluemap jar
2. Get `Golden Days` resource pack (I recommend 1.19.4-base, v1.8.2)
- https://github.com/PoeticRainbow/golden-days/releases/tag/1.8.2
- rename it to `02_golden_days.zip`
- move it to directory `config/resourcepacks/`, relative to your bluemap jar
3. Get [Neo-Beta datapack](https://github.com/SkyDeckAGoGo/neo-beta-datapack)
- open the archive & extract files from directory `data/minecraft/worldgen/biome/`
- edit the golden days resourcepack, add the following path: `assets/minecraft/worldgen/biome/`
- put all biome json files into the resourcepack zip

Why?
1. is for texture base
2. overrides modern textures with the old ones
3. makes sure all biomes appear correct
