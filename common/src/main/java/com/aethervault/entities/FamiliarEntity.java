package com.aethervault.entities;

import java.util.UUID;

import com.aethervault.AetherVaultEvents;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

/**
 * The semi-sentient storage carrier entity, driven by {@link RuneChipInstruction}s.
 *
 * <p>Right-clicking with an empty hand cycles the familiar's rune-chip role
 * (GATHER -> COURIER -> DEFENDER -> SORT -> none); shift-right-click clears the
 * current task. The task, owner, and internal inventory persist via NBT.</p>
 */
public class FamiliarEntity extends PathfinderMob {

    private final SimpleInventory familiarInventory = new SimpleInventory(9);
    private RuneChipInstruction currentTask;
    private UUID ownerId;

    public FamiliarEntity(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    /** Attributes registered by each platform at startup. */
    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.FOLLOW_RANGE, 24.0);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(1, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(2, new LookAtPlayerGoal(this, Player.class, 8.0F));
        this.goalSelector.addGoal(3, new RandomLookAroundGoal(this));
    }

    // ------------------------------------------------------------ task API ---

    /**
     * Assigns a rune-chip task to this familiar.
     */
    public void assignTask(RuneChipInstruction instruction) {
        this.currentTask = instruction;
        if (!level().isClientSide()) {
            System.out.println("Familiar assigned task: "
                    + (instruction != null ? instruction.getRole() : "none"));
        }
    }

    public RuneChipInstruction getCurrentTask() {
        return currentTask;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }

    public SimpleInventory getFamiliarInventory() {
        return familiarInventory;
    }

    // --------------------------------------------------------- interaction ---

    /**
     * Empty-hand right-click cycles roles; shift-right-click clears the task.
     */
    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand hand) {
        if (!level().isClientSide() && hand == InteractionHand.MAIN_HAND
                && player.getMainHandItem().isEmpty()) {
            if (player.isShiftKeyDown()) {
                currentTask = null;
                System.out.println("Familiar task cleared.");
            } else {
                RuneChipInstruction.Role next = RuneChipInstruction.nextRole(
                        currentTask != null ? currentTask.getRole() : null);
                assignTask(new RuneChipInstruction(next, null, null, 9));
                AetherVaultEvents.onFamiliarSpawned(this); // reuse hook for feedback logging
            }
        }
        return InteractionResult.sidedSuccess(level().isClientSide());
    }

    // ---------------------------------------------------------------- tick ---

    @Override
    public void tick() {
        super.tick();
        if (level().isClientSide()) {
            return;
        }
        if (currentTask != null && tickCount % 40 == 0) {
            executeTaskLogic();
        }
    }

    private void executeTaskLogic() {
        switch (currentTask.getRole()) {
            case GATHER -> attemptGather();
            case COURIER -> navigateToTarget();
            case DEFENDER -> patrolArea();
            case SORT -> sortIncomingItems();
        }
    }

    private void attemptGather() {
        System.out.println("Familiar is attempting to gather: " + currentTask.getTargetTag());
        // TODO: scan nearby ItemEntities matching targetTag and addItem() them.
    }

    private void navigateToTarget() {
        System.out.println("Familiar is navigating to target: " + currentTask.getDestination());
        // TODO: pathfind toward destination via getNavigation().moveTo(...).
    }

    private void patrolArea() {
        System.out.println("Familiar is patrolling its assigned area.");
        // TODO: wander within a radius of the anchor position.
    }

    private void sortIncomingItems() {
        System.out.println("Familiar is sorting incoming items ("
                + familiarInventory.getOccupiedSlots() + "/" + familiarInventory.getSize() + " slots).");
        // TODO: route items through FlowEvaluator rules.
    }

    // ----------------------------------------------------------------- NBT ---

    private static final String TAG_OWNER = "OwnerId";
    private static final String TAG_INVENTORY = "Inventory";

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        if (ownerId != null) {
            tag.putUUID(TAG_OWNER, ownerId);
        }
        Provider registries = registryAccess();
        tag.put(TAG_INVENTORY, familiarInventory.saveAll(registries));
        if (currentTask != null) {
            currentTask.save(tag);
        }
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        if (tag.hasUUID(TAG_OWNER)) {
            ownerId = tag.getUUID(TAG_OWNER);
        }
        Provider registries = registryAccess();
        ListTag inv = tag.getList(TAG_INVENTORY, Tag.TAG_COMPOUND);
        familiarInventory.loadAll(registries, inv);
        currentTask = RuneChipInstruction.load(tag);
    }
}