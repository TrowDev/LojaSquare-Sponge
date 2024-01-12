package me.tuskdev.com.lojasquare;

import java.io.File;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;
import org.spongepowered.api.event.cause.EventContextKeys;
import org.spongepowered.api.event.game.state.GameStartedServerEvent;
import org.spongepowered.api.event.game.state.GameStartingServerEvent;
import org.spongepowered.api.event.game.state.GameStoppedServerEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import com.google.inject.Inject;

import me.tuskdev.com.lojasquare.listener.ProductListener;
import me.tuskdev.com.lojasquare.manager.ConfigManager;
import me.tuskdev.com.lojasquare.manager.ConnectionManager;
import me.tuskdev.com.lojasquare.objects.Config;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

@Plugin(id = "lojasquare", name = "Loja Square", version = "1.0")
public class LojaSquare {
	
	// Instance
	private static LojaSquare i;
	public static LojaSquare get() { return i; }
	
	// Fields
	private ConfigManager configManager;
	public ConfigManager getConfigManager() { return this.configManager; }
	private ConnectionManager connectionManager;
	public ConnectionManager getConnectionManager() { return this.connectionManager; }
	
	// Logger
	@Inject
	private Logger logger;
	public Logger getLogger() { return this.logger; }
	public void log(String message) { Sponge.getServer().getConsole().sendMessage(Text.of(message)); }
	
	// Configuration
	@Inject
	@DefaultConfig(sharedRoot = true)
	private File defaultConfig;

	@Inject
	@DefaultConfig(sharedRoot = true)
	private ConfigurationLoader<CommentedConfigurationNode> configLoader;

	@Inject
	@ConfigDir(sharedRoot = false)
	private Path privateConfigDir;
	
	// On server load event
	@Listener
	public void onLoad(GameStartingServerEvent event) {
		this.log("§3[LojaSquare] §bAtivando plugin...");
		
		i = this;
		this.configManager = new ConfigManager(new Config(this.defaultConfig, this.configLoader));
	}
	
	// On server enable event
	@Listener
	public void onEnable(GameStartedServerEvent event) {
		// Connect for server API
		this.connectionManager = new ConnectionManager();
		
		// Check if server is correct
		if(!(this.connectionManager.checkServer())) return;
		
		// Check if IP is correct
		if(!(this.connectionManager.checkIP())) return;
		
		// Inform
		this.log("§3[LojaSquare] §bPlugin ativado.");
		this.log("§3Criador: §bTuskDev (Corrigido/Atualizado por TrowDev)");
		this.log("§bDesejo a voce uma otima experiencia com a §dLojaSquare§b.");
		
		// Register the listeners
		Sponge.getEventManager().registerListeners(this, new ProductListener());
		
		// Start the task for check delivery's
		this.connectionManager.checkDelivery();
	}
	
	// On server disable event
	@Listener
	public void onDisable(GameStoppedServerEvent event) {
		this.log("§c[LojaSquare] Sistema desativado.");
	}
	
	// Force disable PLUGIN
	public void forceDisable() {
		Sponge.getGame().getEventManager().unregisterPluginListeners(this);
		Sponge.getGame().getCommandManager().getOwnedBy(this).forEach(Sponge.getGame().getCommandManager()::removeMapping);
		Sponge.getGame().getScheduler().getScheduledTasks(this).forEach(Task::cancel);
	}
	
	public void print(String s){
		if(getConfigManager().DEBUG_MODE){
			LojaSquare.get().log(s);
		}
	}
	
	// Get new cause for custom event
	public Cause getCause() {
		PluginContainer plugin = Sponge.getPluginManager().getPlugin("lojasquare").get();
		EventContext eventContext = EventContext.builder().add(EventContextKeys.PLUGIN, plugin).build();
		return Cause.of(eventContext, plugin);
	}
	
}
