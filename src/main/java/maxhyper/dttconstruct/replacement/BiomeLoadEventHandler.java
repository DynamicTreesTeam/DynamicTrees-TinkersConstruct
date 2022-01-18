package maxhyper.dttconstruct.replacement;

import maxhyper.dttconstruct.DTTConstructConfigs;
import maxhyper.dttconstruct.DynamicTreesTConstruct;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraftforge.common.world.BiomeGenerationSettingsBuilder;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import javax.annotation.Nullable;

@Mod.EventBusSubscriber(modid = DynamicTreesTConstruct.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class BiomeLoadEventHandler {

//    @SubscribeEvent
//    static void onBiomeLoad(BiomeLoadingEvent event) {
//        BiomeGenerationSettingsBuilder generation = event.getGeneration();
//
//        Biome.Category category = event.getCategory();
//        if (category == Biome.Category.NETHER) {
//            if (DTTConstructConfigs.GENERATE_BLOOD_ISLANDS.get()) {
//                generation.addStructureStart(SlimeIslandReplacement.BLOOD_SLIME_ISLAND);
//            }
//        }
//        else if (category != Biome.Category.THEEND) {
//            // normal sky islands - anywhere
//            if (DTTConstructConfigs.GENERATE_SKY_SLIME_ISLANDS.get()) {
//                generation.addStructureStart(SlimeIslandReplacement.SKY_SLIME_ISLAND);
//            }
//            // clay islands - no forest like biomes
//            if (DTTConstructConfigs.GENERATE_CLAY_ISLANDS.get() && category != Biome.Category.TAIGA && category != Biome.Category.JUNGLE && category != Biome.Category.FOREST && category != Biome.Category.OCEAN && category != Biome.Category.SWAMP) {
//                generation.addStructureStart(SlimeIslandReplacement.CLAY_ISLAND);
//            }
//            // ocean islands - ocean
//            if (category == Biome.Category.OCEAN && DTTConstructConfigs.GENERATE_EARTH_SLIME_ISLANDS.get()) {
//                generation.addStructureStart(SlimeIslandReplacement.EARTH_SLIME_ISLAND);
//            }
//        }
//        else if (!doesNameMatchBiomes(event.getName(), Biomes.THE_END, Biomes.THE_VOID)) {
//            if (DTTConstructConfigs.GENERATE_END_SLIME_ISLANDS.get()) {
//                generation.addStructureStart(SlimeIslandReplacement.END_SLIME_ISLAND);
//            }
//        }
//    }
//    private static boolean doesNameMatchBiomes(@Nullable ResourceLocation name, RegistryKey<?>... biomes) {
//        for (RegistryKey<?> biome : biomes) {
//            if (biome.getRegistryName().equals(name)) {
//                return true;
//            }
//        }
//        return false;
//    }

}
