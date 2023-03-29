package maxhyper.dttconstruct.replacement;

import com.ferreusveritas.dynamictrees.DynamicTrees;
import com.ferreusveritas.dynamictrees.init.DTConfigs;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import maxhyper.dttconstruct.DTTConstructConfigs;
import maxhyper.dttconstruct.DTTConstructRegistries;
import maxhyper.dttconstruct.DynamicTreesTConstruct;
import maxhyper.dttconstruct.replacement.structures.*;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.DimensionSettings;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.settings.DimensionStructuresSettings;
import net.minecraft.world.gen.settings.StructureSeparationSettings;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import slimeknights.tconstruct.common.config.Config;

import javax.annotation.Nullable;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber(modid = DynamicTreesTConstruct.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SlimeIslandReplacement {

    public static RegistryObject<Structure<NoFeatureConfig>> earthSlimeIsland;
    public static RegistryObject<Structure<NoFeatureConfig>> skySlimeIsland;
    public static RegistryObject<Structure<NoFeatureConfig>> clayIsland;
    public static RegistryObject<Structure<NoFeatureConfig>> bloodSlimeIsland;
    public static RegistryObject<Structure<NoFeatureConfig>> endSlimeIsland;

    static {
        earthSlimeIsland = DTTConstructRegistries.STRUCTURE_FEATURES.register("earth_slime_island", EarthSlimeIslandStructureDynamic::new);
        skySlimeIsland = DTTConstructRegistries.STRUCTURE_FEATURES.register("overworld_slime_island", SkySlimeIslandStructureDynamic::new);
        clayIsland = DTTConstructRegistries.STRUCTURE_FEATURES.register("clay_island", ClayIslandStructureDynamic::new);
        bloodSlimeIsland = DTTConstructRegistries.STRUCTURE_FEATURES.register("nether_slime_island", BloodSlimeIslandStructureDynamic::new);
        endSlimeIsland = DTTConstructRegistries.STRUCTURE_FEATURES.register("end_slime_island", EnderSlimeIslandStructureDynamic::new);
    }

    public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> EARTH_SLIME_ISLAND;
    public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> SKY_SLIME_ISLAND;
    public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> CLAY_ISLAND;
    public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> BLOOD_SLIME_ISLAND;
    public static StructureFeature<NoFeatureConfig, ? extends Structure<NoFeatureConfig>> END_SLIME_ISLAND;

    public static final SingleTreeFeature SINGLE_TREE_FEATURE = new SingleTreeFeature(SingleTreeFeatureConfig.CODEC);
    public static final ConfiguredFeature<SingleTreeFeatureConfig, ?> SINGLE_TREE_BIOME_CONFIGURED_FEATURE = SINGLE_TREE_FEATURE.configured(new SingleTreeFeatureConfig(DynamicTrees.resLoc("oak"),true, new ResourceLocation("grass_block")));
    public static final ConfiguredFeature<SingleTreeFeatureConfig, ?> SINGLE_TREE_GREENHEART_CONFIGURED_FEATURE = SINGLE_TREE_FEATURE.configured(new SingleTreeFeatureConfig(DynamicTreesTConstruct.resLoc("greenheart"), new ResourceLocation("tconstruct","earth_sky_slime_grass")));
    public static final ConfiguredFeature<SingleTreeFeatureConfig, ?> SINGLE_TREE_SKYROOT_CONFIGURED_FEATURE = SINGLE_TREE_FEATURE.configured(new SingleTreeFeatureConfig(DynamicTreesTConstruct.resLoc("skyroot"), new ResourceLocation("tconstruct","sky_earth_slime_grass")));
    public static final ConfiguredFeature<SingleTreeFeatureConfig, ?> SINGLE_TREE_ENDERSLIME_CONFIGURED_FEATURE = SINGLE_TREE_FEATURE.configured(new SingleTreeFeatureConfig(DynamicTreesTConstruct.resLoc("enderslime"), new ResourceLocation("tconstruct","ender_ender_slime_grass")));
    public static final ConfiguredFeature<SingleTreeFeatureConfig, ?> SINGLE_TREE_BLOODSHROOM_CONFIGURED_FEATURE = SINGLE_TREE_FEATURE.configured(new SingleTreeFeatureConfig(DynamicTreesTConstruct.resLoc("bloodshroom"), new ResourceLocation("tconstruct","blood_ichor_slime_grass")));

    public static void commonSetup (final FMLCommonSetupEvent event){
        Config.COMMON.generateEarthSlimeIslands.set(false);
        Config.COMMON.generateSkySlimeIslands.set(false);
        Config.COMMON.generateClayIslands.set(false);
        Config.COMMON.generateBloodIslands.set(false);
        Config.COMMON.generateEndSlimeIslands.set(false);

        event.enqueueWork(() -> {
            addStructureToMap(earthSlimeIsland.get());
            addStructureToMap(skySlimeIsland.get());
            addStructureToMap(clayIsland.get());
            addStructureToMap(bloodSlimeIsland.get());
            addStructureToMap(endSlimeIsland.get());
        });
        event.enqueueWork(SlimeIslandReplacement::addStructureSeparation);
        event.enqueueWork(() -> {
            EARTH_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, DynamicTreesTConstruct.resLoc("earth_slime_island"), (earthSlimeIsland.get()).configured(NoFeatureConfig.INSTANCE));
            SKY_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, DynamicTreesTConstruct.resLoc("sky_slime_island"), (skySlimeIsland.get()).configured(NoFeatureConfig.INSTANCE));
            CLAY_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, DynamicTreesTConstruct.resLoc("clay_island"), (clayIsland.get()).configured(NoFeatureConfig.INSTANCE));
            BLOOD_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, DynamicTreesTConstruct.resLoc("blood_slime_island"), (bloodSlimeIsland.get()).configured(NoFeatureConfig.INSTANCE));
            END_SLIME_ISLAND = WorldGenRegistries.register(WorldGenRegistries.CONFIGURED_STRUCTURE_FEATURE, DynamicTreesTConstruct.resLoc("end_slime_island"), (endSlimeIsland.get()).configured(NoFeatureConfig.INSTANCE));

            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, DynamicTreesTConstruct.resLoc("clay_island_tree"), SINGLE_TREE_BIOME_CONFIGURED_FEATURE);

            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, DynamicTreesTConstruct.resLoc("slime_island_greenheart_tree"), SINGLE_TREE_GREENHEART_CONFIGURED_FEATURE);
            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, DynamicTreesTConstruct.resLoc("slime_island_skyroot_tree"), SINGLE_TREE_SKYROOT_CONFIGURED_FEATURE);
            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, DynamicTreesTConstruct.resLoc("slime_island_enderslime_tree"), SINGLE_TREE_ENDERSLIME_CONFIGURED_FEATURE);
            Registry.register(WorldGenRegistries.CONFIGURED_FEATURE, DynamicTreesTConstruct.resLoc("slime_island_bloodshroom_tree"), SINGLE_TREE_BLOODSHROOM_CONFIGURED_FEATURE);
        });

    }

    @SubscribeEvent
    public static void onFeatureRegistry(final RegistryEvent.Register<Feature<?>> event) {
        event.getRegistry().register(SINGLE_TREE_FEATURE);
    }

    @SubscribeEvent
    static void onBiomeLoad(BiomeLoadingEvent event) {
        if (!DTConfigs.WORLD_GEN.get()) return;

        BiomeGenerationSettingsBuilder generation = event.getGeneration();
        Biome.Category category = event.getCategory();

        if (category == Biome.Category.NETHER) {
            if (DTTConstructConfigs.GENERATE_BLOOD_ISLANDS.get()) {
                generation.addStructureStart(BLOOD_SLIME_ISLAND);
            }
        } else if (category != Biome.Category.THEEND) {

            if (DTTConstructConfigs.GENERATE_SKY_SLIME_ISLANDS.get()) {
                generation.addStructureStart(SKY_SLIME_ISLAND);
            }

            if (DTTConstructConfigs.GENERATE_CLAY_ISLANDS.get() && category != Biome.Category.TAIGA && category != Biome.Category.JUNGLE && category != Biome.Category.FOREST && category != Biome.Category.OCEAN && category != Biome.Category.SWAMP) {
                generation.addStructureStart(CLAY_ISLAND);
            }

            if (DTTConstructConfigs.GENERATE_EARTH_SLIME_ISLANDS.get() && category == Biome.Category.OCEAN) {
                generation.addStructureStart(EARTH_SLIME_ISLAND);
            }

        } else if (!doesNameMatchBiomes(event.getName(), Biomes.THE_END, Biomes.THE_VOID)) {

            if (DTTConstructConfigs.GENERATE_END_SLIME_ISLANDS.get()) {
                generation.addStructureStart(END_SLIME_ISLAND);
            }
        }

    }

    private static boolean doesNameMatchBiomes(@Nullable ResourceLocation name, RegistryKey<?>... biomes) {
        int biomeCount = biomes.length;

        for(int i = 0; i < biomeCount; ++i) {
            RegistryKey<?> biome = ((RegistryKey<?>[]) biomes)[i];
            if (biome.location().equals(name)) {
                return true;
            }
        }

        return false;
    }

    private static void addStructureSettings(RegistryKey<DimensionSettings> key, Structure<?> structure, StructureSeparationSettings settings) {
        DimensionSettings dimensionSettings = WorldGenRegistries.NOISE_GENERATOR_SETTINGS.get(key);
        if (dimensionSettings != null) {
            dimensionSettings.structureSettings().structureConfig().put(structure, settings);
        }
    }
    private static void addStructureToMap(Structure<?> structure) {
        Structure.STRUCTURES_REGISTRY.put((Objects.requireNonNull(structure.getRegistryName())).toString(), structure);
    }
    public static void addStructureSeparation() {

        StructureSeparationSettings earthSettings = new StructureSeparationSettings(Config.COMMON.earthSlimeIslandSeparation.get(), 5, 25988585);
        Map<Structure<?>, StructureSeparationSettings> defaultStructures = DimensionSettings.bootstrap().structureSettings().structureConfig();
        defaultStructures.put(earthSlimeIsland.get(), earthSettings);
        addStructureSettings(DimensionSettings.AMPLIFIED, earthSlimeIsland.get(), earthSettings);
        addStructureSettings(DimensionSettings.FLOATING_ISLANDS, earthSlimeIsland.get(), earthSettings);
        StructureSeparationSettings skySettings = new StructureSeparationSettings(Config.COMMON.skySlimeIslandSeparation.get(), 5, 14357800);
        defaultStructures.put(skySlimeIsland.get(), skySettings);
        addStructureSettings(DimensionSettings.AMPLIFIED, skySlimeIsland.get(), skySettings);
        addStructureSettings(DimensionSettings.FLOATING_ISLANDS, skySlimeIsland.get(), skySettings);
        StructureSeparationSettings claySettings = new StructureSeparationSettings(Config.COMMON.clayIslandSeparation.get(), 5, 162976988);
        defaultStructures.put(clayIsland.get(), claySettings);
        addStructureSettings(DimensionSettings.AMPLIFIED, clayIsland.get(), claySettings);
        addStructureSettings(DimensionSettings.FLOATING_ISLANDS, clayIsland.get(), claySettings);
        StructureSeparationSettings netherSettings = new StructureSeparationSettings(Config.COMMON.bloodIslandSeparation.get(), 5, 65245622);
        addStructureSettings(DimensionSettings.NETHER, bloodSlimeIsland.get(), netherSettings);
        StructureSeparationSettings endSettings = new StructureSeparationSettings(Config.COMMON.endSlimeIslandSeparation.get(), 5, 368963602);
        addStructureSettings(DimensionSettings.END, endSlimeIsland.get(), endSettings);
        ImmutableMap.Builder<Structure<?>, StructureSeparationSettings> builder = ImmutableMap.builder();
        Set<Structure<?>> ignore = Sets.newHashSet(new Structure[]{earthSlimeIsland.get(), skySlimeIsland.get(), clayIsland.get(), bloodSlimeIsland.get(), endSlimeIsland.get()});
        builder.putAll(DimensionStructuresSettings.DEFAULTS.entrySet().stream().filter((entry) -> !ignore.contains(entry.getKey())).collect(Collectors.toList()));
        builder.put(earthSlimeIsland.get(), earthSettings);
        builder.put(skySlimeIsland.get(), skySettings);
        builder.put(clayIsland.get(), claySettings);
        builder.put(bloodSlimeIsland.get(), netherSettings);
        builder.put(endSlimeIsland.get(), endSettings);
        DimensionStructuresSettings.DEFAULTS = builder.build();

    }

}
