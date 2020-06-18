package me.tuskdev.com.lojasquare.objects;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import me.tuskdev.com.lojasquare.LojaSquare;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;

public class Config {
	
	// Fields
	private CommentedConfigurationNode configNode;
	private final ConfigurationLoader<CommentedConfigurationNode> configLoader;
	
	// Constructor
	public Config(File configFile, ConfigurationLoader<CommentedConfigurationNode> configLoader) {
		super();
		this.configLoader = configLoader;
		
		if(!(configFile.exists())) this.createFile(configFile);
		else this.load();
	}
	
	// Get the configuration
	public CommentedConfigurationNode getConfig() {
		return this.configNode;
	}
	
	// Save the configuration
	public void save() {
		try {
			this.configLoader.save(this.configNode);
		} catch (IOException e) {
			LojaSquare.get().log("§c[LojaSquare] Não foi possível salvar o arquivo de configurações.");
		}
	}
	
	// Load the configuration
	public void load() {
		try {
			this.configNode = this.configLoader.load();
		} catch (IOException e) {
			LojaSquare.get().log("§c[LojaSquare] Não foi possível carregar o arquivo de configurações.");
		}
	}
	
	// Create the configuration file
	private void createFile(File configFile) {
		try {
			configFile.createNewFile();
			this.load();
			this.generate();
			this.save();
		}catch (IOException e) {
			LojaSquare.get().log("§c[LojaSquare] Não foi possível criar o arquivo de configurações.");
		}
	}
	
	// Generate the configuration file
	private void generate() {
		// LojaSquare configuration
		this.configNode.getNode("LojaSquare", "Debug").setValue(true);
		this.configNode.getNode("LojaSquare", "Key_API").setValue("lojasquare-key-api");
		this.configNode.getNode("LojaSquare", "Servidor").setValue("Nome-Do-Servidor");
		this.configNode.getNode("LojaSquare", "Connection_Timeout").setValue(1500);
		this.configNode.getNode("LojaSquare", "Read_Timeout").setValue(3000);
		
		// CONFIG configuration
		this.configNode.getNode("Config", "HTTPS").setValue(true);
		this.configNode.getNode("Config", "Tempo_Checar_Compras").setValue(60);
		this.configNode.getNode("Config", "Smart_Delivery").setValue(true);
		
		// Groups configuration - DISPUTA
		this.configNode.getNode("Grupos", "DISPUTA", "Ativar_Com_Player_Offline").setValue(true);
		this.configNode.getNode("Grupos", "DISPUTA", "Money").setValue(false);
		this.configNode.getNode("Grupos", "DISPUTA", "Cmds_A_Executar").setValue(Arrays.asList("ban @player &cEncerre o pedido de disputa e voce sera desbanido!"));
		
		// Groups configuration - RESOLVIDO
		this.configNode.getNode("Grupos", "RESOLVIDO", "Ativar_Com_Player_Offline").setValue(true);
		this.configNode.getNode("Grupos", "RESOLVIDO", "Money").setValue(false);
		this.configNode.getNode("Grupos", "RESOLVIDO", "Cmds_A_Executar").setValue(Arrays.asList("desban @player"));
		
		// Groups configuration - VIPFerro
		this.configNode.getNode("Grupos", "VIPFerro", "Ativar_Com_Player_Offline").setValue(false);
		this.configNode.getNode("Grupos", "VIPFerro", "Enviar_Mensagem").setValue(false);
		this.configNode.getNode("Grupos", "VIPFerro", "Mensagem_Receber_Ao_Ativar_Produto").setValue(Arrays.asList("&eOla, &a@player", "&eO produto que voce adquiriu (&a@produto) foi ativado!", "&eDias: &a@dias", "&eQuantidade: &a@qnt"));
		this.configNode.getNode("Grupos", "VIPFerro", "Money").setValue(false);
		this.configNode.getNode("Grupos", "VIPFerro", "Cmds_A_Executar").setValue(Arrays.asList("gerarvip @grupo @dias @qnt @player"));
		
		// Groups configuration - Cash10
		this.configNode.getNode("Grupos", "Cash10", "Ativar_Com_Player_Offline").setValue(false);
		this.configNode.getNode("Grupos", "Cash10", "Enviar_Mensagem").setValue(false);
		this.configNode.getNode("Grupos", "Cash10", "Mensagem_Receber_Ao_Ativar_Produto").setValue(Arrays.asList("&eOla, &a@player", "&eO produto que voce adquiriu (&a@produto) foi ativado!", "&eDias: &a@dias", "&eQuantidade: &a@qnt"));
		this.configNode.getNode("Grupos", "Cash10", "Money").setValue(true);
		this.configNode.getNode("Grupos", "Cash10", "Quantidade_De_Money").setValue(10);
		this.configNode.getNode("Grupos", "Cash10", "Cmds_A_Executar").setValue(Arrays.asList("money give @player @money"));
		
		// MSG configuration
		this.configNode.getNode("Msg", "Produto_Nao_Configurado").setValue("&4[LojaSquare] &cVoce tem um produto para ser entregue, porem, o grupo do produto nao esta configurado. Grupo: &a@grupo");
	}

}
