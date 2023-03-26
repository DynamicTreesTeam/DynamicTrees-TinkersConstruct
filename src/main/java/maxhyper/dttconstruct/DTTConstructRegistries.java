package maxhyper.dttconstruct;

import com.ferreusveritas.dynamictrees.api.registry.TypeRegistryEvent;
import com.ferreusveritas.dynamictrees.blocks.leaves.LeavesProperties;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class DTTConstructRegistries {

    @SubscribeEvent
    public static void registerLeavesPropertiesTypes (final TypeRegistryEvent<LeavesProperties> event) {
    }

    public static void setup() {
    }

}
