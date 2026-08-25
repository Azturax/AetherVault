package com.aethervault.storage.lattice;

import com.aethervault.core.IAetherStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Block entity for the Voxel Lattice storage anchored by this block.
 *
 * <p>The lattice is a bounded three-dimensional grid of cells (see
 * {@link BoundingBox}). Each cell holds at most one item stack; items are
 * addressed by a stable {@link UUID} assigned on insertion.</p>
 */
public class LatticeAnchorBlockEntity extends BlockEntity implements IAetherStorage {

    /** Lattice edge length in cells; the grid holds GRID_EDGE^3 items. */
    public static final int GRID_EDGE = 4;
    /** Total cell capacity of the lattice. */
    public static final int CAPACITY = GRID_EDGE * GRID_EDGE * GRID_EDGE;

    /** Cell index -> stored stack. */
    private final Map<Integer, ItemStack> cells = new HashMap<>();
    /** Item id -> cell index. */
    private final Map<UUID, Integer> idToCell = new HashMap<>();

    public LatticeAnchorBlockEntity(BlockEntityType<?> type, BlockPos worldPosition) {
        super(type, worldPosition);
    }

    /**
     * The local-space bounds of the lattice volume anchored by this block.
     * The anchor sits at the lattice origin; the volume extends +X/+Y/+Z.
     */
    public BoundingBox getBounds() {
        return new BoundingBox(new Vector3d(0, 0, 0), new Vector3d(GRID_EDGE, GRID_EDGE, GRID_EDGE));
    }

    /**
     * Stores the item in the first free lattice cell.
     */
    @Override
    public boolean store(ItemStack item) {
        if (item == null || item.isEmpty() || !hasSpace(item)) {
            return false;
        }
        int cell = firstFreeCell();
        if (cell < 0) {
            return false;
        }
        UUID id = UUID.randomUUID();
        cells.put(cell, item.copy());
        idToCell.put(id, cell);
        setChanged();
        System.out.println("Lattice Anchor: stored item in cell " + cell + " (" + id + ").");
        return true;
    }

    /**
     * Removes and returns the item stored under the given id.
     */
    @Override
    public Optional<ItemStack> retrieve(UUID uniqueId) {
        Integer cell = idToCell.remove(uniqueId);
        if (cell == null) {
            return Optional.empty();
        }
        ItemStack stack = cells.remove(cell);
        setChanged();
        System.out.println("Lattice Anchor: retrieved item from cell " + cell + ".");
        return Optional.ofNullable(stack);
    }

    @Override
    public boolean hasSpace(ItemStack item) {
        return cells.size() < CAPACITY;
    }

    @Override
    public void clear() {
        cells.clear();
        idToCell.clear();
        setChanged();
    }

    /** Number of occupied cells. */
    public int getOccupiedCells() {
        return cells.size();
    }

    /** The stack occupying a cell, or empty. */
    public Optional<ItemStack> getCell(int index) {
        return Optional.ofNullable(cells.get(index));
    }

    private int firstFreeCell() {
        for (int i = 0; i < CAPACITY; i++) {
            if (!cells.containsKey(i)) {
                return i;
            }
        }
        return -1;
    }

    // ------------------------------------------------------------- NBT ---

    private static final String TAG_CELLS = "Cells";

    @Override
    protected void saveAdditional(CompoundTag tag, Provider registries) {
        super.saveAdditional(tag, registries);
        ListTag cellList = new ListTag();
        for (Map.Entry<UUID, Integer> entry : idToCell.entrySet()) {
            ItemStack stack = cells.get(entry.getValue());
            if (stack == null || stack.isEmpty()) {
                continue;
            }
            CompoundTag cellTag = new CompoundTag();
            cellTag.putUUID("Id", entry.getKey());
            cellTag.putInt("Cell", entry.getValue());
            Tag stackTag = stack.save(registries);
            if (stackTag != null) {
                cellTag.put("Stack", stackTag);
            }
            cellList.add(cellTag);
        }
        tag.put(TAG_CELLS, cellList);
    }

    @Override
    public void loadAdditional(CompoundTag tag, Provider registries) {
        super.loadAdditional(tag, registries);
        cells.clear();
        idToCell.clear();
        ListTag cellList = tag.getList(TAG_CELLS, Tag.TAG_COMPOUND);
        for (int i = 0; i < cellList.size(); i++) {
            CompoundTag cellTag = cellList.getCompound(i);
            if (!cellTag.hasUUID("Id") || !cellTag.contains("Cell")) {
                continue;
            }
            ItemStack stack = ItemStack.parse(registries, cellTag.getCompound("Stack"))
                    .orElse(ItemStack.EMPTY);
            if (stack.isEmpty()) {
                continue;
            }
            int cell = cellTag.getInt("Cell");
            if (cell < 0 || cell >= CAPACITY || cells.containsKey(cell)) {
                continue;
            }
            cells.put(cell, stack);
            idToCell.put(cellTag.getUUID("Id"), cell);
        }
    }
}