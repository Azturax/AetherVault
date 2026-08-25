package com.aethervault.logic;

import com.aethervault.core.*;
import com.aethervault.storage.lattice.LatticeAnchorBlockEntity;
import net.minecraft.world.item.ItemStack;
import java.util.UUID;

/**
 * A test class to demonstrate the full functionality of the RuneProgram and FlowEvaluator.
 */
public class AetherVaultTest {
    public static void main(String[] args) {
        // 1. Setup Storage Target (The Output Destination)
        LatticeAnchorBlockEntity lattice = new LatticeAnchorBlockEntity(null); // Mock BlockEntity for testing

        // 2. Define the Program Structure
        RuneProgram program = new RuneProgram("TestFlow_001");

        // Create Nodes
        StorageNode inputNode = new FlowEvaluator.InputNode("ItemIngest");
        
        // Filter 1: Check if item has a "RepairNeeded" tag (e.g., durability < 50%)
        RuneCondition repairCheck = new DurabilityThresholdCondition(50);
        FlowEvaluator.FilterNode repairFilter = new FlowEvaluator.FilterNode("IsDamaged", repairCheck, null, null);

        // Filter 2: Check if item is a "Tool" (e.g., has the "Tool" tag)
        RuneCondition toolCheck = new ItemTagCondition("Tool");
        FlowEvaluator.FilterNode toolFilter = new FlowEvaluator.FilterNode("IsTool", toolCheck, null, null);

        // Output Nodes
        // Path A: Repair Node (Placeholder for a repair mechanism)
        StorageNode repairOutput = new FlowEvaluator.OutputNode("RepairStation", lattice); 
        // Path B: Storage Node (The main Lattice storage)
        StorageNode storageOutput = new FlowEvaluator.OutputNode("MainLattice", lattice);

        // Connect Nodes to form the graph logic:
        // Input -> Repair Check (If damaged, go to repair; otherwise, check if it's a tool)
        program.connectNodes(inputNode.getNodeId(), repairFilter.getNodeId()); 
        
        // Repair Check Success Path (Damaged item) -> Repair Station
        program.connectNodes(repairFilter.getNodeId(), repairOutput.getNodeId());

        // Repair Check Failure Path (Not damaged, proceed to check if it's a tool)
        program.connectNodes(repairFilter.getNodeId(), toolFilter.getNodeId()); 

        // Tool Check Success Path -> Main Lattice Storage
        program.connectNodes(toolFilter.getNodeId(), storageOutput.getNodeId());

        // Tool Check Failure Path (Not damaged AND not a tool) -> Overflow/Discard (Placeholder)
        StorageNode discard = new FlowEvaluator.InputNode("Discard"); // Reusing InputNode as placeholder for end of flow
        program.connectNodes(toolFilter.getNodeId(), discard.getNodeId());


        // 3. Initialize the Evaluator and Run Test Cases
        FlowEvaluator evaluator = new FlowEvaluator(program);

        System.out.println("--- Running AetherVault Program Simulation ---");

        // TEST CASE 1: Damaged Tool (Should go to RepairStation)
        ItemStack damagedTool = createTestItem("Sword", "Tool", true, 30); // 30% durability remaining
        System.out.println("\n[TEST] Running Damaged Tool...");
        evaluator.evaluate(damagedTool);

        // TEST CASE 2: Pristine Item (Should go to MainLattice)
        ItemStack pristineItem = createTestItem("AetherShard", "Resource", false, 100); // 100% durability remaining
        System.out.println("\n[TEST] Running Pristine Resource...");
        evaluator.evaluate(pristineItem);

        // TEST CASE 3: Undefined Item (Should go to Discard)
        ItemStack undefinedItem = createTestItem("MysteryBox", "Unknown", false, 100);
        System.out.println("\n[TEST] Running Undefined Item...");
        evaluator.evaluate(undefinedItem);
    }

    /** Helper method to simulate item creation with tags and durability */
    private static ItemStack createTestItem(String name, String tag, boolean isDamaged, int durability) {
        ItemStack stack = new ItemStack(net.minecraft.world.item.Items.DIAMOND_SWORD); // Using a base item for simplicity
        stack.setCustomTag("Name", net.minecraft.nbt.CompoundTag.valueOf(name));
        if (tag != null) {
            stack.setCustomTag(tag, net.minecraft.nbt.CompoundTag.valueOf(tag));
        }

        // Simulate durability damage
        int maxDamage = 100;
        int currentDamage = isDamaged ? (maxDamage - durability) : 0;
        stack.setDamage(currentDamage);
        return stack;
    }
}