package me.tuskdev.com.lojasquare.manager;

import java.util.Map;

import me.tuskdev.com.lojasquare.objects.Config;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;

public class ConfigManager {
	
	// Fields - Instance
	private final Config config;
	
	// Fields - String
	public final String KEY_API;
	public final String SERVER;
	public final String PRODUCT_NO_CONFIG;
	
	// Fields - Integer
	public final Integer CONNECTION_TIMEOUT;
	public final Integer READ_TIMEOUT;
	public final Integer TIME_CHECK_BUYS;
	
	// Fields - Boolean
	public final Boolean DEBUG_MODE;
	public final Boolean HTTPS_MODE;
	public final Boolean SMART_DELIVERY;
	
	// Fields - Maps
	private final Map<Object, ? extends CommentedConfigurationNode> GROUPS;
	
	// Constructor
	public ConfigManager(Config config) {
		super();
		
		// Instance
		this.config = config;
		
		// String
		this.KEY_API = config.getConfig().getNode("LojaSquare", "Key_API").getString();
		this.SERVER = config.getConfig().getNode("LojaSquare", "Servidor").getString();
		this.PRODUCT_NO_CONFIG = config.getConfig().getNode("Msg", "Produto_Nao_Configurado").getString().replace("&", "§");
		
		// Integer
		this.CONNECTION_TIMEOUT = config.getConfig().getNode("LojaSquare", "Connection_Timeout").getInt();
		this.READ_TIMEOUT = config.getConfig().getNode("LojaSquare", "Read_Timeout").getInt();
		this.TIME_CHECK_BUYS = config.getConfig().getNode("Config", "Tempo_Checar_Compras").getInt();
		
		// Boolean
		this.DEBUG_MODE = config.getConfig().getNode("LojaSquare", "Debug").getBoolean();
		this.HTTPS_MODE = config.getConfig().getNode("Config", "HTTPS").getBoolean();
		this.SMART_DELIVERY = config.getConfig().getNode("Config", "Smart_Delivery").getBoolean();
		
		// Maps
		this.GROUPS = config.getConfig().getNode("Grupos").getChildrenMap();
		
	}
	
	// Get the configuration instance
	public Config getConfig() {
		return this.config;
	}
	
	// Verify if group is valid
	public Boolean hasGroup(String groupName) {
		return this.GROUPS.containsKey(groupName);
	}
	
	// Get the informations of group
	public Map<Object, ? extends CommentedConfigurationNode> getGroup(String groupName) {
		return this.GROUPS.get(groupName).getChildrenMap();
	}

}
