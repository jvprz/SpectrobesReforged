# Fossils and exploration

Fossil ores generate underground in selected Overworld biomes. Use an **iron pickaxe or better**: the bundled mining tags require an iron-tier tool.

## Where to look

| Element | Biome group | Stone variant height | Deepslate variant height |
| --- | --- | --- | --- |
| Corona | Hot | Y 0 to 72 | Y -64 to -1 |
| Aurora | Temperate | Y 0 to 72 | Y -64 to -1 |
| Flash | Cold | Y 0 to 72 | Y -64 to -1 |

These are configured placement ranges, not guaranteed ore locations: terrain and replaceable stone also affect the result. Explore newly generated chunks when adding the mod to an existing world.

<!-- BEGIN AUTO:BIOMES -->

| Element | Included biomes |
| --- | --- |
| Corona | Desert, Savanna, Savanna Plateau, Badlands, Wooded Badlands, Eroded Badlands, Jungle, Sparse Jungle |
| Aurora | Plains, Sunflower Plains, Forest, Birch Forest, Dark Forest, Meadow, Flower Forest |
| Flash | Snowy Plains, Snowy Taiga, Taiga, Grove, Snowy Slopes, Frozen Peaks, Jagged Peaks, Ice Spikes |

<!-- END AUTO:BIOMES -->

## Mining and drops

Without Silk Touch, a fossil ore drops one of its element’s fossil items. The entries within each bundled element pool have equal weight. With Silk Touch, the block drops its ore block instead.

<!-- BEGIN AUTO:FOSSIL_DROPS -->

| Ore element | Possible fossil items |
| --- | --- |
| Corona | Vilipod (`vilar_fossil`), Inkapod (`inkana_fossil`), Dongopod (`dongor_fossil`), Komapod (`komainu_fossil`), Zozapod (`zoza_fossil`), Danapod (`danawa_fossil`), Tenkropod (`tenkro_fossil`), Mesapod (`mesa_fossil`) |
| Aurora | Segupod (`segu_fossil`), Spipod (`spiko_fossil`), Bartopod (`bartor_fossil`), Grilpod (`grilda_fossil`), Gejipod (`gejio_fossil`), Kubapod (`kubaku_fossil`), Masepod (`masetto_fossil`) |
| Flash | Harupod (`harumi_fossil`), Nagupod (`nagu_fossil`), Aopod (`aoi_fossil`), Shapod (`shakin_fossil`), Kasupod (`kasumi_fossil`), Samupod (`samukabu_fossil`), Mossapod (`mossari_fossil`) |

<!-- END AUTO:FOSSIL_DROPS -->

## Which fossils can awaken?

**Only six species currently have bundled species definitions:** Inkana, Komainu, Segu, Shakin, Spiko, and Vilar. Other fossil items can drop and be collected, but cannot currently complete awakening with the default species data.

If a definition is missing, the incubator reports the issue and aborts without consuming the fossil. See the [supported roster](spectrobes.md).

## Excavation status

The current discovery interaction is Minecraft ore mining. A separate fossil-cleaning or excavation minigame is not implemented. Analysis and purification belong to the future progression.

Once you have a supported fossil, continue to [the incubator](awakening.md).
