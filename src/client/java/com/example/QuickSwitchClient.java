package com.example;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.ConnectScreen;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.client.sound.PositionedSoundInstance;
import net.minecraft.client.toast.SystemToast;
import net.minecraft.sound.SoundEvents;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.text.Text;
import static com.mojang.brigadier.arguments.StringArgumentType.word;

public class QuickSwitchClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            
            //Register the quickswitch command
            dispatcher.register(literal("quickswitch")
                
            //Add an argument, that represents the server users will join
            .then(argument("serverIP", word())    
                .executes(context -> {
                    String serverIP = getString(context, "serverIP");
                    MinecraftClient client = context.getSource().getClient();
                    
                    //Disconnect from the current server/world only if there is a server/world loaded
                    if (client.world != null) {
                        client.world.disconnect();
                    }
                    
                    
                    //Connect to the command-specified server
                    ServerAddress address = ServerAddress.parse(serverIP);
                    ServerInfo info = new ServerInfo("", serverIP, ServerInfo.ServerType.OTHER);
                    ConnectScreen.connect(new MultiplayerScreen(new TitleScreen()), client, address, info, false, null);
                    
                    //Play a sound and display a message to the user to indicate that the client has been moved to the specified server
                    client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_PORTAL_TRAVEL, 2.0F, 2.0F));
                    client.getToastManager().add(new SystemToast(SystemToast.Type.CHUNK_LOAD_FAILURE, Text.literal("§7[§bQuick Switch§7]§3 Moving Client..."), Text.literal("§7You are now being redirected to: §f" + serverIP + "§7...")));
                    
                    
                    return 1;
                })));

                
                //Register the disconnect command
                dispatcher.register(literal("quickswitch:disconnect")
                .executes(context -> {
                    MinecraftClient client = context.getSource().getClient();
                        
                        //Disconnect the user from the current server/world
                        client.world.disconnect();
                        
                        //Attempt to play a sound and display a message to the user to indicate that the client has been disconnected from the server/world
                        client.getSoundManager().play(PositionedSoundInstance.master(SoundEvents.BLOCK_CHEST_CLOSE, 2.0F, 2.0F));
                        client.getToastManager().add(new SystemToast(SystemToast.Type.CHUNK_LOAD_FAILURE, Text.literal("§7[§bQuick Switch§7]§3 Disconnected"), Text.literal("§7You are now disconnected from the §fserver§8/§fworld§7.")));
                         
                    return 1;
                }));
        });
    }
}
