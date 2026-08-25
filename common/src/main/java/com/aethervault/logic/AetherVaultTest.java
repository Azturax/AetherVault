package com.aethervault.logic;

import com.aethervault.storage.lattice.LatticeAnchorBlockEntity;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

/**
 * A simulation demonstrating the full functionality of the RuneProgram and FlowEvaluator.
 *
 * <p>Graph topology (connection order defines success/failure branches):</p>
 * <pre>
 *   ItemIngest -> IsDamaged ──success──> RepairStation
 *                     └────failure──> IsTool ──success──> MainLattice
 *                                              └────failure──> Discard
 * </pre>
 */
public class AetherVaultTest {
    public static void main(String[] args) {
        // 1. Setup storage target (the output destination). Type/pos are mocked.
        LatticeAnchorBlockEntity lattice = new LatticeAnchorBlockEntity(null, BlockPos.ZERO);

        // 2. Define the program structure.
        RuneProgram program = new RuneProgram("TestFlow_001");

        program.addNode(new FlowEvaluator.InputNode("ItemIngest"));

        // Filter 1: route damaged items to the repair station.
        RuneCondition repairCheck = new DurabilityThresholdCondition(50);
        program.addNode(new FlowEvaluator.FilterNode("IsDamaged", repairCheck));

        // Filter 2: route intact tools into the lattice.
        RuneCondition toolCheck = new ItemTagCondition("minecraft:tools");
        program.addNode(new FlowEvaluator.FilterNode("IsTool", toolCheck));

        // Output nodes.
        program.addNode(new FlowEvaluator.OutputNode("RepairStation", lattice));
        program.addNode(new FlowEvaluator.OutputNode("MainLattice", lattice));
        program.addNode(new FlowEvaluator.InputNode("Discard")); // end-of-flow placeholder

        // Connections: first outgoing = success path, second = failure path.
        program.connectNodes("ItemIngest", "IsDamaged");
        program.connectNodes("IsDamaged", "RepairStation");  // success: damaged
        program.connectNodes("IsDamaged", "IsTool");         // failure: intact
        program.connectNodes("IsTool", "MainLattice");       // success: tool
        program.connectNodes("IsTool", "Discard");           // failure: neither

        // 3. Initialize the evaluator and run test cases.
        FlowEvaluator evaluator = new FlowEvaluator(program);

        System.out.println("--- Running AetherVault Program Simulation ---");

        // TEST CASE 1: Damaged tool (should route to RepairStation).
        ItemStack damagedTool = createTestItem(true, 30); // 30% durability remaining
        System.out.println("\n[TEST] Running Damaged Tool...");
        evaluator.evaluate(damagedTool);

        // TEST CASE 2: Pristine item (should route to MainLattice or Discard).
        ItemStack pristineItem = createTestItem(false, 100);
        System.out.println("\n[TEST] Running Pristine Item...");
        evaluator.evaluate(pristineItem);
    }

    /** Helper method to simulate an item with a given durability state. */
    private static ItemStack createTestItem(boolean isDamaged, int durabilityPercent) {
        ItemStack stack = new ItemStack(Items.DIAMOND_SWORD); // durable base item
        int maxDamage = stack.getMaxDamage();
        int currentDamage = isDamaged ? (int) ((100 - durabilityPercent) / 100.0 * maxDamage) : 0;
        stack.setDamageValue(currentDamage);
        return stack;
    }
}