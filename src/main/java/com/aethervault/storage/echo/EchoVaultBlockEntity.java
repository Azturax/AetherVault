package com.aethervault.storage.echo;

import com.aethervault.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import java.util.ArrayList;
import java.util.List;

/**
 * Block Entity responsible for managing the temporal Echo Vault storage.
 */
public class EchoVaultBlockEntity extends BlockEntity implements IAetherStorage {
    // Maps a unique item ID (UUID) to a list of its recorded snapshots over time.
    private final Map<UUID, List<TemporalSnapshot>> echoSlots = new HashMap<>();

    public EchoVaultBlockEntity(BlockEntity master) {
        super(master);
    }

    @Override
    protected void addEchoSlot(UUID uniqueId, TemporalSnapshot snapshot) {
        // Add the new snapshot to the list for this item ID.
        echoSlots.computeIfAbsent(uniqueId, k -> new ArrayList<>()).add(snapshot);
    }

    public Map<UUID, List<TemporalSnapshot>> getEchoSlots() {
        return echoSlots;
    }

    @Override
    public void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        // Serialize the map of echoes to NBT for chunk saving/loading.
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        // Deserialize the map from NBT upon chunk loading.
    }

    /**
     * Stores an item by recording its current state as a new temporal echo.
     */
    @Override
    public boolean store(ItemStack item) {
        if (!hasSpace(item)) return false;

        // In Echo Storage, the unique ID is derived from the item's UUID/metadata for tracking.
        UUID itemId = generateItemIdFromStack(item); 
        TemporalSnapshot snapshot = new TemporalSnapshot(item);
// Logic to trigger visual feedback (particles) and audio cues
    private void playFeedback(boolean success) {
        if (success) {
            System.out.println("Playing successful storage/retrieval sound event.");
            // TODO: Implement particle effect generation for mana orbs here
            // ParticleEffectManager.spawnManaOrbs(this); 
        } else {
            System.out.println("Playing failure sound event.");
        }
    }
System.out.println("Storage action success: Storing item."); 
// ParticleEffectUtils.spawnParticles(this, ItemEffects.STORE_SUCCESS);
// SoundManager.playSound(SoundEvents.STORAGE_SAVE);
    }

    /**
     * Retrieves a specific temporal echo and materializes it (returns the item).
     */
    @Override
    public Optional<ItemStack> retrieve(UUID uniqueId) {
        List<TemporalSnapshot> snapshots = echoSlots.get(uniqueId);
        if (snapshots == null || snapshots.isEmpty()) return Optional.empty();

        // Get the most recent snapshot
        TemporalSnapshot latestSnapshot = snapshots.get(snapshots.size() - 1);
        long currentTime = System.currentTimeMillis(); // Placeholder for actual game time retrieval

        ItemStack originalStack = latestSnapshot.getStack();
        ItemStack decayedStack = latestSnapshot.getDecayedStack(currentTime);

        // Check if decay occurred and was not maintained
        boolean isDegraded = !latestSnapshot.isMaintained() && !originalStack.equals(decayedStack) && !decayedStack.isEmpty();

        if (isDegraded) {
            // TODO: Implement visual feedback for item decay here (e.g., particle effects, sound).
            System.out.println("Echo decayed! Item ID: " + uniqueId); 
        } else if (!originalStack.equals(decayedStack)) {
             // This handles the case where maintenance was applied but it still changed state for other reasons (e.g., durability loss not covered by decay logic)
             // TODO: Implement visual feedback for item change here.
        }

        // Trigger particle effect and play retrieval sound on successful retrieve
        System.out.println("Storage action success: Retrieving item."); 
        // ParticleEffectUtils.spawnParticles(this, ItemEffects.STORE_SUCCESS);
        // SoundManager.playSound(SoundEvents.STORAGE_LOAD);

        return Optional.of(decayedStack);
    }

    /**
     * Checks if there is space available (i.e., total number of echoes across all slots is below limit).
     */
    @Override
    public boolean hasSpace(ItemStack item) {
        // Simple capacity check: total snapshots vs max allowed.
        long totalEchoes = echoSlots.values().stream()
                .mapToLong(List::size)
                .sum();
        return totalEchoes < 500; // Placeholder limit for v1.0
    }

    /**
     * Clears all echoes from the vault.
     */
    @Override
    public void clear() {
        echoSlots.clear();
    }

    private UUID generateItemIdFromStack(ItemStack stack) {
        // In a real mod, this would be more complex (e.g., combining item ID and NBT data).
        return java.util.UUID.randomUUID(); 
    }
}