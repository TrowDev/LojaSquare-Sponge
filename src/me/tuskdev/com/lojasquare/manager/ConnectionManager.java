package me.tuskdev.com.lojasquare.manager;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import me.tuskdev.com.lojasquare.LojaSquare;
import me.tuskdev.com.lojasquare.api.ProductPreActiveEvent;
import me.tuskdev.com.lojasquare.objects.Item;
import me.tuskdev.com.lojasquare.objects.Shop;

public class ConnectionManager {
	
	// Fields
	private static List<Integer> produtosEntregues;
	
	private Shop shop;
	
	// Constructor
	public ConnectionManager() {
		super();
		this.shop = new Shop();
		produtosEntregues = new ArrayList<Integer>();
	}
	
	// Get the shop instance
	public Shop getShop() {
		return this.shop;
	}
	
	// Verify if is valid IP
	public Boolean checkIP() {
		String jsonResult = this.shop.get("/v1/sites/extensoes");
		
		if( jsonResult.startsWith("LS-") || !jsonResult.contains("true")) {
			LojaSquare.get().log("§3[LojaSquare] §cO sistema esta sendo desabilitado.");
			LojaSquare.get().log("§3[LojaSquare] §cMotivo: " + jsonResult);
			LojaSquare.get().log("§3[LojaSquare] §cKey-API: " + LojaSquare.get().getConfigManager().KEY_API);
			LojaSquare.get().log("§ePara atualizar o IP, acesse: §ahttps://painel.lojasquare.net/pages/config/site§e e clique em '§aAtivacao Automatica§e'");
			LojaSquare.get().forceDisable();
			return false;
		}
		
		LojaSquare.get().log("§a[LojaSquare] A conexão com sua loja foi estabelecida com sucesso.");
		return true;
	}
	
	// Verify if is valid server
	public Boolean checkServer() {
		String server = LojaSquare.get().getConfigManager().SERVER;
		
		if(server == null || server.equalsIgnoreCase("Nome-Do-Servidor")) {
			LojaSquare.get().log("§3[LojaSquare] §cO sistema está sendo desabilitado.");
			LojaSquare.get().log("§3[LojaSquare] §cMotivo: O servidor atual é inválido.");
			LojaSquare.get().log("§3[LojaSquare] §cServidor: " + (server == null ? "Nenhum" : server) + ".");
			LojaSquare.get().forceDisable();
			return false;
		}
		
		return true;
	}
	
	public void print(String s){
		LojaSquare.get().print(s);
	}
	
	// Check for update in delivery's
	public void checkDelivery(){
		final Task.Builder builder = Sponge.getScheduler().createTaskBuilder();
		print("§3[LojaSquare] §bTempo de checagem de entregas: §a"+LojaSquare.get().getConfigManager().TIME_CHECK_BUYS+" segundos§b.");
		builder.execute(new Runnable() {
			@Override
			public void run() {
				// Get all delivery's and server
				List<Item> items = shop.getAllDelivery();
				String server = LojaSquare.get().getConfigManager().SERVER;
				
				// Inform total delivery's
				LojaSquare.get().log("§3[LojaSquare] §bTotal de Itens: §f" + items.size() + "§b.");
				// Verify if is valid total
				if(items != null && items.size() > 0) {
					for(Item item : items) {
						if(item==null) continue;
						print("§3[LojaSquare] §bItem: §a" + item.toString() 
								+ " §b// SubServer: §a" + item.getSubServidor() 
								+ " §b// Servidor: §a" + server + "§b.");
						
						// Verify if is valid server
						if(!(item.getSubServidor().equalsIgnoreCase(server))) continue;
						
						// Get the player and verify if is valid group
						Player player = null;
						try {
							player = Sponge.getServer().getPlayer(item.getPlayer()).get();
						} catch (Exception e) {
							print("§4[LojaSquare] §cPlayer §a"+item.getPlayer()+"§c nao esta online.");
						}
						
						// Verify if is valid group and player
						if(!(LojaSquare.get().getConfigManager().hasGroup(item.getGrupo())) && player != null) {
							LojaSquare.get().log("§4[LojaSquare] §cProduto §a" + item.getGrupo() + " §cnão configurado.");
							player.sendMessage(Text.of(LojaSquare.get().getConfigManager().PRODUCT_NO_CONFIG.replace("@grupo", item.getGrupo())));
							continue;
						}
						Boolean offlinePlayerGroup = LojaSquare.get().getConfigManager().getGroup(item.getGrupo()).get("Ativar_Com_Player_Offline").getBoolean();
						print("§3[LojaSquare] §bPlayer: §a" + item.getPlayer() 
								+ " §b// P NULL? §a" + (player == null));
						if(player == null && !offlinePlayerGroup) {
							boolean dispute = (item.getProduto().equalsIgnoreCase("DISPUTA") && item.getGrupo().equalsIgnoreCase("DISPUTA"));
							boolean resolved = (item.getProduto().equalsIgnoreCase("RESOLVIDO") && item.getGrupo().equalsIgnoreCase("RESOLVIDO"));
							if(!dispute && !resolved) continue;
						}
						final Player receiver = player;
						// Apply
						builder.execute(new Runnable() {
							@Override
							public void run() {
								if(produtosEntregues.contains(item.getEntregaID())) return;
								print("§3[LojaSquare] §bPre Product Active Event. ID do produto: §a"+item.getEntregaID()+"§b. Codigo: §a"+item.getCodigo());
								ProductPreActiveEvent productPreActiveEvent = new ProductPreActiveEvent(receiver, item, LojaSquare.get().getCause());
								Sponge.getEventManager().post(productPreActiveEvent);
							}
						}).delayTicks(1).submit(LojaSquare.get());
					}
					items.clear();
				} else {
					print("[Loja Square] Sem produtos para entregar.");
				}
			}
			
		}).async()
			.delayTicks(20*10)
			.intervalTicks(20*LojaSquare.get().getConfigManager().TIME_CHECK_BUYS)
			.submit(LojaSquare.get());
	}
	
	public List<Integer> getProdutosEntregues(){
		return produtosEntregues;
	}

}
