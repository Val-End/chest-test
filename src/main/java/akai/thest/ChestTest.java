package akai.thest;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ChestTest implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("chest-test");

	@Override
	public void onInitialize() {
		LOGGER.info("Chest Test Initilized!!!");
		ChestLoot.loadEntries();
		CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> ReloadCommand.register(dispatcher));
	}
}