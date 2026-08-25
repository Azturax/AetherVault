package com.aethervault.storage.echo;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

/**
 * Represents a single temporal snapshot of an item's state at a specific point in time.
 *
 * <p>Echo storage records items as snapshots rather than moving them; retrieving an
 * echo materializes the item in its <em>decayed</em> state unless the snapshot has
 * been actively maintained.</p>
 */
public class TemporalSnapshot {

    /** One decay interval equals one real-time hour. */
    public static final long DECAY_INTERVAL_MS = 3_600_000L;
    /** Fraction of maximum durability lost per decay interval (10%). */
    public static final double DECAY_FRACTION_PER_INTERVAL = 0.10;

    private final ItemStack stack;
    private final long timestamp;
    private boolean maintained;

    public TemporalSnapshot(ItemStack stack) {
        this(stack, System.currentTimeMillis(), false);
    }

    public TemporalSnapshot(ItemStack stack, long timestamp, boolean maintained) {
        this.stack = stack.copy();
        this.stack.setCount(1);
        this.timestamp = timestamp;
        this.maintained = maintained;
    }

    /** The pristine item state as recorded. Never modified in place. */
    public ItemStack getStack() {
        return stack;
    }

    /** Time (ms since epoch) at which this echo was recorded. */
    public long getTimestamp() {
        return timestamp;
    }

    /** Maintained echoes do not decay over time. */
    public boolean isMaintained() {
        return maintained;
    }

    /**
     * Maintains this echo: marks it decay-immune and resets its age.
     */
    public void maintain() {
        this.maintained = true;
    }

    /**
     * Calculates the current state of the item after time-based decay.
     *
     * @param currentTime the current time in milliseconds
     * @return a copy of the stack in its decayed state, or {@link ItemStack#EMPTY}
     *         if the echo has fully degraded
     */
    public ItemStack getDecayedStack(long currentTime) {
        long elapsedTime = Math.max(0L, currentTime - timestamp);
        if (maintained || elapsedTime == 0L) {
            return stack.copy();
        }

        long decayIntervals = elapsedTime / DECAY_INTERVAL_MS;
        if (decayIntervals <= 0L) {
            return stack.copy();
        }

        // Items without durability do not decay; they persist as pure echoes.
        int maxDurability = stack.getMaxDamage();
        if (maxDurability <= 0) {
            return stack.copy();
        }

        int damagePerInterval = (int) Math.ceil(maxDurability * DECAY_FRACTION_PER_INTERVAL);
        int totalDecay = (int) Math.min((long) damagePerInterval * decayIntervals, maxDurability);

        ItemStack decayed = stack.copy();
        int currentDamage = decayed.getDamageValue();
        int newDamage = currentDamage + totalDecay;

        if (newDamage >= maxDurability) {
            return ItemStack.EMPTY; // The echo has fully degraded.
        }
        decayed.setDamageValue(newDamage);
        return decayed;
    }

    // ------------------------------------------------------------- NBT ---

    private static final String TAG_STACK = "Stack";
    private static final String TAG_TIMESTAMP = "Timestamp";
    private static final String TAG_MAINTAINED = "Maintained";

    /**
     * Serializes this snapshot into a compound tag.
     */
    public CompoundTag save(Provider registries) {
        CompoundTag tag = new CompoundTag();
        Tag stackTag = stack.save(registries);
        if (stackTag != null) {
            tag.put(TAG_STACK, stackTag);
        }
        tag.putLong(TAG_TIMESTAMP, timestamp);
        tag.putBoolean(TAG_MAINTAINED, maintained);
        return tag;
    }

    /**
     * Deserializes a snapshot from a compound tag.
     */
    public static TemporalSnapshot load(Provider registries, CompoundTag tag) {
        ItemStack stack = ItemStack.parse(registries, tag.getCompound(TAG_STACK))
                .orElse(ItemStack.EMPTY);
        long timestamp = tag.contains(TAG_TIMESTAMP) ? tag.getLong(TAG_TIMESTAMP) : System.currentTimeMillis();
        boolean maintained = tag.getBoolean(TAG_MAINTAINED);
        return new TemporalSnapshot(stack, timestamp, maintained);
    }

    /**
     * Convenience for deriving a stable per-item echo key from a stack.
     * Echoes of the same item kind share a slot history.
     */
    public static UUID itemIdFor(ItemStack stack) {
        String fingerprint = stack.getItem() + "|" + stack.getCount();
        return UUID.nameUUIDFromBytes(fingerprint.getBytes());
    }
}