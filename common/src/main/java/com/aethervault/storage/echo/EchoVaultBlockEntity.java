package com.aethervault.storage.echo;

import com.aethervault.AetherVaultEvents;
import com.aethervault.core.IAetherStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Block entity powering the temporal Echo Vault.
 *
 * <p>Storing an item does not move it into a slot; instead the vault records a
 * {@link TemporalSnapshot} - an "echo" - of the item's state. Retrieving an echo
 * materializes the item in its decayed state unless the echo was maintained.</p>
 */
public class EchoVaultBlockEntity extends BlockEntity implements IAetherStorage {

    /** Maximum number of echoes this vault can hold (v1.0 placeholder limit). */
    public static final int MAX_TOTAL_ECHOES = 500;
    /** Maximum number of echoes retained per distinct item. */
    public static final int MAX_ECHOES_PER_ITEM = 16;

    private final Map<UUID, List<TemporalSnapshot>> echoSlots = new LinkedHashMap<>();

    public EchoVaultBlockEntity(BlockEntityType<?> type, BlockPos worldPosition) {
        super(type, worldPosition);
    }

    // -------------------------------------------------------- storage API ---

    /**
     * Records the item's current state as a new temporal echo.
     */
    @Override
    public boolean store(ItemStack item) {
        if (item == null || item.isEmpty() || !hasSpace(item)) {
            playFeedback(false);
            return false;
        }

        UUID itemId = TemporalSnapshot.itemIdFor(item);
        List<TemporalSnapshot> history = echoSlots.computeIfAbsent(itemId, k -> new ArrayList<>());

        if (history.size() >= MAX_ECHOES_PER_ITEM) {
            history.remove(0); // Evict the oldest echo to make room.
        }

        TemporalSnapshot snapshot = new TemporalSnapshot(item);
        history.add(snapshot);
        setChanged();
        playFeedback(true);
        AetherVaultEvents.onSnapshotCaptured(snapshot);
        return true;
    }

    /**
     * Materializes the most recent echo of the requested item, applying decay
     * unless the echo was maintained. The echo itself is kept (peek, not pop).
     */
    @Override
    public Optional<ItemStack> retrieve(UUID uniqueId) {
        List<TemporalSnapshot> snapshots = echoSlots.get(uniqueId);
        if (snapshots == null || snapshots.isEmpty()) {
            playFeedback(false);
            return Optional.empty();
        }

        TemporalSnapshot latest = snapshots.get(snapshots.size() - 1);
        long now = System.currentTimeMillis();
        ItemStack decayed = latest.getDecayedStack(now);

        if (ItemStack.isSameItemSameComponents(latest.getStack(), decayed)) {
            System.out.println("Echo retrieved intact: " + uniqueId);
        } else if (decayed.isEmpty()) {
            System.out.println("Echo fully degraded: " + uniqueId);
        } else {
            System.out.println("Echo decayed on retrieval: " + uniqueId);
        }

        playFeedback(!decayed.isEmpty());
        return Optional.of(decayed);
    }

    /**
     * Marks the most recent echo of the requested item as maintained, making it
     * immune to decay.
     */
    public boolean maintainEcho(UUID uniqueId) {
        List<TemporalSnapshot> snapshots = echoSlots.get(uniqueId);
        if (snapshots == null || snapshots.isEmpty()) {
            return false;
        }
        snapshots.get(snapshots.size() - 1).maintain();
        setChanged();
        return true;
    }

    @Override
    public boolean hasSpace(ItemStack item) {
        long totalEchoes = echoSlots.values().stream().mapToLong(List::size).sum();
        return totalEchoes < MAX_TOTAL_ECHOES;
    }

    @Override
    public void clear() {
        echoSlots.clear();
        setChanged();
    }

    public Map<UUID, List<TemporalSnapshot>> getEchoSlots() {
        return echoSlots;
    }

    public int getTotalEchoCount() {
        return (int) echoSlots.values().stream().mapToLong(List::size).sum();
    }

    private void playFeedback(boolean success) {
        if (success) {
            System.out.println("Echo Vault: storage action succeeded.");
            // TODO: particle + sound feedback (mana orbs, storage chime).
        } else {
            System.out.println("Echo Vault: storage action failed.");
        }
    }

    // ------------------------------------------------------------- NBT ---

    private static final String TAG_ECHO_SLOTS = "EchoSlots";

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag slotList = new ListTag();
        for (Map.Entry<UUID, List<TemporalSnapshot>> entry : echoSlots.entrySet()) {
            CompoundTag slotTag = new CompoundTag();
            slotTag.putUUID("ItemId", entry.getKey());
            ListTag echoList = new ListTag();
            for (TemporalSnapshot snapshot : entry.getValue()) {
                echoList.add(snapshot.save(registries));
            }
            slotTag.put("Echoes", echoList);
            slotList.add(slotTag);
        }
        tag.put(TAG_ECHO_SLOTS, slotList);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        echoSlots.clear();
        ListTag slotList = tag.getList(TAG_ECHO_SLOTS, Tag.TAG_COMPOUND);
        for (int i = 0; i < slotList.size(); i++) {
            CompoundTag slotTag = slotList.getCompound(i);
            if (!slotTag.hasUUID("ItemId")) {
                continue;
            }
            UUID itemId = slotTag.getUUID("ItemId");
            ListTag echoList = slotTag.getList("Echoes", Tag.TAG_COMPOUND);
            List<TemporalSnapshot> history = new ArrayList<>(echoList.size());
            for (int j = 0; j < echoList.size(); j++) {
                history.add(TemporalSnapshot.load(registries, echoList.getCompound(j)));
            }
            echoSlots.put(itemId, history);
        }
    }
}