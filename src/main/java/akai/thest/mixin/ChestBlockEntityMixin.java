package akai.thest.mixin;

import akai.thest.ChestLoot;
import akai.thest.ChestTest;
import com.google.gson.JsonArray;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.loot.LootTables;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(ChestBlockEntity.class)
public abstract class ChestBlockEntityMixin extends LootableContainerBlockEntity implements LidOpenable {
    @Unique
    public boolean used = true;

    protected ChestBlockEntityMixin(BlockEntityType<?> blockEntityType, BlockPos blockPos, BlockState blockState) {
        super(blockEntityType, blockPos, blockState);
    }

    @Inject(at = @At("TAIL"), method = "onOpen")
    public void onOpen(PlayerEntity player, CallbackInfo ci){
        if(used)
            return;

        String biome = getBiomeString(player, this.getPos());
        Random random = player.getWorld().getRandom();

        JsonArray array = ChestLoot.get(biome);
        if(!array.isEmpty()) {
            int randomIndex = array.size() > 1 ? random.nextBetween(0, array.size() - 1) : 0;
            String lootTable = array.get(randomIndex).toString().replace("\"", "");
            if(lootTable.contains(":"))
                this.setLootTable(this.getLootTable(lootTable), random.nextLong());
        }

        used = true;
    }

    @Inject(at = @At("TAIL"), method = "readNbt")
    public void readNbt(NbtCompound nbt, CallbackInfo ci) {
        this.used = nbt.getBoolean("Used");
    }

    @Inject(at = @At("TAIL"), method = "writeNbt")
    protected void writeNbt(NbtCompound nbt, CallbackInfo ci) {
        nbt.putBoolean("Used", used);
    }

    @Unique
    private String getBiomeString(PlayerEntity player, BlockPos pos) {
        return player.getWorld().getBiome(pos).getKeyOrValue().map(
                biomeKey -> biomeKey.getValue().toString(), biome_ -> "[unregistered " + biome_ + "]"
        );
    }

    @Unique
    private Identifier getLootTable(String key) {
        for(Object identifier : LootTables.getAll().toArray()) {
            if(Objects.equals(identifier.toString(), key))
                return (Identifier) identifier;
        }

        ChestTest.LOGGER.warn("Nothing found for {}, return empty", key);
        return LootTables.EMPTY;
    }
}
