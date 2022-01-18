package maxhyper.dttconstruct.replacement;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import maxhyper.dttconstruct.DynamicTreesTConstruct;
import net.minecraft.entity.EntityType;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.registry.DynamicRegistries;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.gen.feature.template.TemplateManager;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.common.TinkerModule;
import slimeknights.tconstruct.common.config.Config;
import slimeknights.tconstruct.world.TinkerStructures;
import slimeknights.tconstruct.world.TinkerWorld;
import slimeknights.tconstruct.world.worldgen.islands.AbstractIslandStructure;
import slimeknights.tconstruct.world.worldgen.islands.SlimeIslandPiece;
import slimeknights.tconstruct.world.worldgen.islands.variants.IIslandVariant;
import slimeknights.tconstruct.world.worldgen.islands.variants.IslandVariants;

import java.util.*;
import java.util.stream.Collectors;

public class SlimeIslandReplacement {
//    private static boolean structureSettingsReady = false;
//
//    public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> EARTH_SLIME_ISLAND;
//    public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> SKY_SLIME_ISLAND;
//    public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> CLAY_ISLAND;
//    public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> BLOOD_SLIME_ISLAND;
//    public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> END_SLIME_ISLAND;
//
//    private static final Structure<NoFeatureConfig> earthSlimeIsland = new DynamicAbstractIslandStructure(DynamicTreesTConstruct.resLoc("earth_slime_island")){
//        private final List<MobSpawnInfo.Spawners> monsters = ImmutableList.of(new MobSpawnInfo.Spawners(TinkerWorld.earthSlimeEntity.get(), 30, 4, 4));;
//        public List<MobSpawnInfo.Spawners> getDefaultSpawnList() {
//            return this.monsters;
//        }
//        public IIslandVariant getVariant(Random random) { return random.nextBoolean() ? IslandVariants.EARTH_BLUE : IslandVariants.EARTH_GREEN; }
//        protected int getHeight(ChunkGenerator generator, Rotation rotation, int x, int z, Random random) { return Math.max(generator.getSeaLevel() - 7, 0); }
//    };
//    private static final Structure<NoFeatureConfig> skySlimeIsland = new DynamicAbstractIslandStructure(DynamicTreesTConstruct.resLoc("sky_slime_island")){
//        private final List<MobSpawnInfo.Spawners> monsters = ImmutableList.of(new MobSpawnInfo.Spawners(TinkerWorld.skySlimeEntity.get(), 30, 4, 4));
//        public List<MobSpawnInfo.Spawners> getDefaultSpawnList() {
//            return this.monsters;
//        }
//        public IIslandVariant getVariant(Random random) { return random.nextBoolean() ? IslandVariants.SKY_BLUE : IslandVariants.SKY_GREEN; }
//    };
//    private static final Structure<NoFeatureConfig> clayIsland = new DynamicAbstractIslandStructure(DynamicTreesTConstruct.resLoc("clay_island")){
//        private final List<MobSpawnInfo.Spawners> monsters =ImmutableList.of(new MobSpawnInfo.Spawners(TinkerWorld.terracubeEntity.get(), 30, 4, 4));
//        public List<MobSpawnInfo.Spawners> getDefaultSpawnList() {
//            return this.monsters;
//        }
//        protected IIslandVariant getVariant(Random random) {
//            return IslandVariants.SKY_CLAY;
//        }
//    };
//    private static final Structure<NoFeatureConfig> bloodSlimeIsland = new DynamicAbstractIslandStructure(DynamicTreesTConstruct.resLoc("blood_slime_island")){
//        private final List<MobSpawnInfo.Spawners> STRUCTURE_MONSTERS = ImmutableList.of(new MobSpawnInfo.Spawners(EntityType.MAGMA_CUBE, 150, 4, 6));
//        public GenerationStage.Decoration step() {
//            return GenerationStage.Decoration.UNDERGROUND_DECORATION;
//        }
//        public IIslandVariant getVariant(Random random) {
//            return IslandVariants.BLOOD;
//        }
//        public List<MobSpawnInfo.Spawners> getDefaultSpawnList() {
//            return STRUCTURE_MONSTERS;
//        }
//        protected int getHeight(ChunkGenerator generator, Rotation rotation, int x, int z, Random random) { return Math.max(generator.getSeaLevel() - 7, 0); }
//    };
//    private static final Structure<NoFeatureConfig> endSlimeIsland = new DynamicAbstractIslandStructure(DynamicTreesTConstruct.resLoc("end_slime_island")){
//        private final List<MobSpawnInfo.Spawners> monsters = ImmutableList.of(new MobSpawnInfo.Spawners(TinkerWorld.enderSlimeEntity.get(), 30, 4, 4));
//        public List<MobSpawnInfo.Spawners> getDefaultSpawnList() {
//            return this.monsters;
//        }
//        public IIslandVariant getVariant(Random random) {
//            return IslandVariants.ENDER;
//        }
//    };
//
//    private static void addStructureSettings(RegistryKey<DimensionSettings> key, Structure<?> structure, StructureSeparationSettings settings) {
//        DimensionSettings dimensionSettings = WorldGenRegistries.NOISE_GENERATOR_SETTINGS.get(key);
//        if (dimensionSettings != null) { dimensionSettings.structureSettings().structureConfig().put(structure, settings); }
//    }
//
//    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
//    public static void addStructureSeparation() {
//        if (structureSettingsReady) {
//            StructureSeparationSettings earthSettings = new StructureSeparationSettings(Config.COMMON.earthSlimeIslandSeparation.get(), 5, 25988585);
//            Map<Structure<?>, StructureSeparationSettings> defaultStructures = DimensionSettings.bootstrap().structureSettings().structureConfig();
//            defaultStructures.put(earthSlimeIsland, earthSettings);
//            addStructureSettings(DimensionSettings.AMPLIFIED, earthSlimeIsland, earthSettings);
//            addStructureSettings(DimensionSettings.FLOATING_ISLANDS, earthSlimeIsland, earthSettings);
//            StructureSeparationSettings skySettings = new StructureSeparationSettings(Config.COMMON.skySlimeIslandSeparation.get(), 5, 14357800);
//            defaultStructures.put(skySlimeIsland, skySettings);
//            addStructureSettings(DimensionSettings.AMPLIFIED, skySlimeIsland, skySettings);
//            addStructureSettings(DimensionSettings.FLOATING_ISLANDS, skySlimeIsland, skySettings);
//            StructureSeparationSettings claySettings = new StructureSeparationSettings(Config.COMMON.clayIslandSeparation.get(), 5, 162976988);
//            defaultStructures.put(clayIsland, claySettings);
//            addStructureSettings(DimensionSettings.AMPLIFIED, clayIsland, claySettings);
//            addStructureSettings(DimensionSettings.FLOATING_ISLANDS, clayIsland, claySettings);
//            StructureSeparationSettings netherSettings = new StructureSeparationSettings(Config.COMMON.bloodIslandSeparation.get(), 5, 65245622);
//            addStructureSettings(DimensionSettings.NETHER, bloodSlimeIsland, netherSettings);
//            StructureSeparationSettings endSettings = new StructureSeparationSettings(Config.COMMON.endSlimeIslandSeparation.get(), 5, 368963602);
//            addStructureSettings(DimensionSettings.END, endSlimeIsland, endSettings);
//            ImmutableMap.Builder<Structure<?>, StructureSeparationSettings> builder = ImmutableMap.builder();
//            Set<Structure<?>> ignore = Sets.newHashSet(new Structure[]{earthSlimeIsland, skySlimeIsland, clayIsland, bloodSlimeIsland, endSlimeIsland});
//            builder.putAll(DimensionStructuresSettings.DEFAULTS.entrySet().stream().filter((entry) -> !ignore.contains(entry.getKey())).collect(Collectors.toList()));
//            builder.put(earthSlimeIsland, earthSettings);
//            builder.put(skySlimeIsland, skySettings);
//            builder.put(clayIsland, claySettings);
//            builder.put(bloodSlimeIsland, netherSettings);
//            builder.put(endSlimeIsland, endSettings);
//            DimensionStructuresSettings.DEFAULTS = builder.build();
//        }
//    }
//
//    private static void addStructureToMap(Structure<?> structure) {
//        Structure.STRUCTURES_REGISTRY.put((Objects.requireNonNull(structure.getRegistryName())).toString(), structure);
//    }

//    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void commonSetup (final FMLCommonSetupEvent event){
//        event.enqueueWork(() -> {
//            addStructureToMap(earthSlimeIsland);
//            addStructureToMap(skySlimeIsland);
//            addStructureToMap(clayIsland);
//            addStructureToMap(bloodSlimeIsland);
//            addStructureToMap(endSlimeIsland);
//        });
//        structureSettingsReady = true;
//        event.enqueueWork(SlimeIslandReplacement::addStructureSeparation);
//        event.enqueueWork(() -> {
//            EARTH_SLIME_ISLAND =   (StructureFeature)WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, DynamicTreesTConstruct.resLoc("earth_slime_island"),    ((Structure) earthSlimeIsland).configured(NoFeatureConfig.INSTANCE));
//            SKY_SLIME_ISLAND =     (StructureFeature)WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, DynamicTreesTConstruct.resLoc("sky_slime_island"),      ((Structure) skySlimeIsland).configured(NoFeatureConfig.INSTANCE));
//            CLAY_ISLAND =          (StructureFeature)WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, DynamicTreesTConstruct.resLoc("clay_island"),           ((Structure) clayIsland).configured(NoFeatureConfig.INSTANCE));
//            BLOOD_SLIME_ISLAND =   (StructureFeature)WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, DynamicTreesTConstruct.resLoc("blood_slime_island"),    ((Structure) bloodSlimeIsland).configured(NoFeatureConfig.INSTANCE));
//            END_SLIME_ISLAND =     (StructureFeature)WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, DynamicTreesTConstruct.resLoc("end_slime_island"),      ((Structure) endSlimeIsland).configured(NoFeatureConfig.INSTANCE));
//        });
//        ((AbstractIslandStructure)TinkerStructures.earthSlimeIsland.get()).startFactory = DefaultStart::new;
    }

//    public static class DefaultStart extends StructureStart<NoFeatureConfig> {
//        public DefaultStart(Structure<NoFeatureConfig> structureIn, int chunkPosX, int chunkPosZ, MutableBoundingBox bounds, int references, long seed) {
//            super(structureIn, chunkPosX, chunkPosZ, bounds, references, seed);
//        }
//
//        public void generatePieces(DynamicRegistries registries, ChunkGenerator generator, TemplateManager templateManagerIn, int chunkX, int chunkZ, Biome biomeIn, NoFeatureConfig config) {
////            // determine orientation
////            Rotation rotation = Rotation.values()[this.random.nextInt(Rotation.values().length)];
////            // determine coords
////            int x = chunkX * 16 + 4 + this.random.nextInt(8);
////            int z = chunkZ * 16 + 4 + this.random.nextInt(8);
////            int y = getHeight(generator, rotation, x, z, this.random);
////
////            IIslandVariant variant = getVariant(random);
////            // fetch the tree now so its consistent on the whole island
////            SlimeIslandPiece slimeIslandPiece = new SlimeIslandPiece(templateManagerIn, variant, SIZES[this.random.nextInt(SIZES.length)], new BlockPos(x, y, z), variant.getTreeFeature(random), rotation);
////            this.pieces.add(slimeIslandPiece);
////            this.calculateBoundingBox();
//            System.out.println(" ASAFASA : " + this);
//        }
//    }

}
