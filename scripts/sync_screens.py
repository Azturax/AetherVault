#!/usr/bin/env python3
"""Wire Rune Program Tablet screen openers on all platforms + lang keys."""

import json
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]

NEOFORGE_ENTRY = '''package com.aethervault.neoforge;

import com.aethervault.AetherVault;
import com.aethervault.client.RuneProgramScreen;
import com.aethervault.core.RuneProgramTabletItem;

import net.minecraft.client.Minecraft;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;

/**
 * NeoForge entry point for AetherVault (Minecraft 1.21.1).
 */
@Mod(AetherVault.MOD_ID)
public final class AetherVaultNeoForge {

    public AetherVaultNeoForge(IEventBus modEventBus) {
        ModRegistry.register(modEventBus);
        modEventBus.addListener(this::onCommonSetup);
        if (FMLEnvironment.dist.isClient()) {
            // Referenced lazily inside the lambda so dedicated servers never load client classes.
            RuneProgramTabletItem.setScreenOpener((player, stack) ->
                    Minecraft.getInstance().setScreen(new RuneProgramScreen()));
        }
    }

    private void onCommonSetup(FMLCommonSetupEvent event) {
        AetherVault.init();
    }
}
'''

FABRIC_CLIENT = '''package com.aethervault.fabric;

import com.aethervault.client.RuneProgramScreen;
import com.aethervault.core.RuneProgramTabletItem;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;

/**
 * Fabric client entry point: wires client-only hooks for AetherVault.
 */
@Environment(EnvType.CLIENT)
public final class AetherVaultClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        RuneProgramTabletItem.setScreenOpener((player, stack) ->
                Minecraft.getInstance().setScreen(new RuneProgramScreen()));
    }
}
'''

# 1. Write NeoForge entries (both lines).
for rel in ["neoforge-1.21.1/src/main/java/com/aethervault/neoforge/AetherVaultNeoForge.java",
            "neoforge-26.x/src/main/java/com/aethervault/neoforge/AetherVaultNeoForge.java"]:
    p = ROOT / rel
    p.write_text(NEOFORGE_ENTRY, encoding="utf-8", newline="\n")
    print("wrote", rel)

# 2. Write Fabric client classes.
for rel in ["fabric-1.21.1/src/main/java/com/aethervault/fabric/AetherVaultClient.java",
            "fabric-26.x/src/main/java/com/aethervault/fabric/AetherVaultClient.java"]:
    p = ROOT / rel
    p.parent.mkdir(parents=True, exist_ok=True)
    p.write_text(FABRIC_CLIENT, encoding="utf-8", newline="\n")
    print("wrote", rel)

# 3. Patch fabric.mod.json files to add the client entrypoint.
for rel in ["fabric-1.21.1/src/main/resources/fabric.mod.json",
            "fabric-26.x/src/main/resources/fabric.mod.json"]:
    p = ROOT / rel
    data = json.loads(p.read_text(encoding="utf-8-sig"))
    eps = data.setdefault("entrypoints", {})
    if "client" not in eps:
        eps["client"] = []
    if "com.aethervault.fabric.AetherVaultClient" not in eps["client"]:
        eps["client"].append("com.aethervault.fabric.AetherVaultClient")
    p.write_text(json.dumps(data, indent=2) + "\n", encoding="utf-8", newline="\n")
    print("patched", rel)
