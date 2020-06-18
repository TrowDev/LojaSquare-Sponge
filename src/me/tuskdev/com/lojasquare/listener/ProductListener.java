package me.tuskdev.com.lojasquare.listener;

import java.util.List;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import me.tuskdev.com.lojasquare.LojaSquare;
import me.tuskdev.com.lojasquare.api.ProductActiveEvent;
import me.tuskdev.com.lojasquare.api.ProductPreActiveEvent;
import me.tuskdev.com.lojasquare.objects.Item;

public class ProductListener {
	
	public void print(String s){
		LojaSquare.get().print(s);
	}
	
	// On PRE active product event
	@Listener
	public void onPreActiveEvent(ProductPreActiveEvent event) {
		// Inform
		print("§3[LojaSquare] §bPreActiveEvent sendo executado.");
		
		// Verify if is cancelled
		if(event.isCancelled()) return;
		
		// Create the task
		final Task.Builder builder = Sponge.getScheduler().createTaskBuilder();
		builder.execute(new Runnable() {
			
			@Override
			public void run() {
				print("§3[LojaSquare] §bAntes do update delivery.");
				Item item = event.getItem();
				if(LojaSquare.get().getConnectionManager().getShop().updateDelivery(item)) {
					LojaSquare.get().getConnectionManager().getProdutosEntregues().add(item.getIDEntrega());
					print("§3[LojaSquare] §bPreparando a entrega do produto: §a" + item.toString() + "§b.");
					
					// Apply
					builder.execute(new Runnable() {
						
						@Override
						public void run() {
							ProductActiveEvent productActiveEvent = new ProductActiveEvent(event.getPlayer(), item, LojaSquare.get().getCause());
							Sponge.getEventManager().post(productActiveEvent);
						}
						
					}).submit(LojaSquare.get());
				}else LojaSquare.get().log("§c[LojaSquare] Não foi possível atualizar o status da compra: " + item.toString() + " para: 'Entregue'. Pontanto, a entrega nao foi feita!");
			}
			
		}).submit(LojaSquare.get());
	}
	
	// On active product event with smart delivery
	@Listener
	public void onActiveSmartDelivery(ProductActiveEvent event) {
		if(!LojaSquare.get().getConfigManager().SMART_DELIVERY) return;
		Item item = event.getItem();
		
		// Verify if is cancelled
		if(event.isCancelled()) {
			LojaSquare.get().log("§4[LojaSquare] §cAtivacao da compra: §a" + item.toString() + "§c foi cancelada por meio do evento ProductActiveEvent, mas ja foi marcado no site com status 'Entregue'.");
			return;
		}
		
		// Get the fields of product
		Boolean hasMoney = LojaSquare.get().getConfigManager().getGroup(item.getGrupo()).get("Money").getBoolean();
		Double moneyValue = 0D;
		if(hasMoney){
			moneyValue = LojaSquare.get().getConfigManager().getGroup(item.getGrupo()).get("Quantidade_De_Money").getDouble() * item.getQuantidade();
		}
		
		// Execute the list of commands
		List<String> commands = LojaSquare.get().getConfigManager().getGroup(item.getGrupo()).get("Cmds_A_Executar").getList(object -> (String)object);
		for(String command : commands) {
			command = command
					.replace("@grupo", item.getGrupo())
					.replace("@dias", item.getDias()+"")
					.replace("@player", item.getPlayer())
					.replace("@qnt", item.getQuantidade()+"")
					.replace("@moneyInteiro", moneyValue.intValue()+"")
					.replace("@money", moneyValue.toString());
			Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
		}
		
		// Inform the player
		if(LojaSquare.get().getConfigManager().getGroup(item.getGrupo()).get("Enviar_Mensagem").getBoolean()) {
			List<String> messages = LojaSquare.get().getConfigManager().getGroup(item.getGrupo()).get("Mensagem_Receber_Ao_Ativar_Produto").getList(object -> (String)object);
			for(String message : messages) {
				message = message.replace("&", "§")
						.replace("@grupo", item.getGrupo())
						.replace("@produto", item.getProduto())
						.replace("@dias", item.getDias()+"")
						.replace("@qnt", item.getQuantidade()+"")
						.replace("@player", event.getPlayer().getName());
				event.getPlayer().sendMessage(Text.of(message));
			}
		}
		
		// Inform
		LojaSquare.get().log("§3[LojaSquare] §bEntrega do produto §a" + item.toString() + "§b concluida com sucesso.");
	}
	
	// On active product event
	@Listener
	public void onActiveWithoutSmartDelivery(ProductActiveEvent event) {
		if(LojaSquare.get().getConfigManager().SMART_DELIVERY) return;
		Item item = event.getItem();
		
		// Verify if is cancelled
		if(event.isCancelled()) {
			LojaSquare.get().log("§4[LojaSquare] §cAtivacao da compra: §a" + item.toString() + "§c foi cancelada por meio do evento ProductActiveEvent, mas ja foi marcado no site com status 'Entregue'.");
			return;
		}
		
		// Get the fields of product
		Boolean hasMoney = LojaSquare.get().getConfigManager().getGroup(item.getGrupo()).get("Money").getBoolean();
		Double moneyValue = 0D;
		if(hasMoney){
			moneyValue = LojaSquare.get().getConfigManager().getGroup(item.getGrupo()).get("Quantidade_De_Money").getDouble();
		}
		
		// Execute the list of commands
		List<String> commands = LojaSquare.get().getConfigManager().getGroup(item.getGrupo()).get("Cmds_A_Executar").getList(object -> (String)object);
		for(int i = 0; i < item.getQuantidade(); i++){
			for(String command : commands) {
				command = command
						.replace("@grupo", item.getGrupo())
						.replace("@dias", item.getDias()+"")
						.replace("@player", item.getPlayer())
						.replace("@qnt", item.getQuantidade()+"")
						.replace("@moneyInteiro", moneyValue.intValue()+"")
						.replace("@money", moneyValue.toString());
				Sponge.getCommandManager().process(Sponge.getServer().getConsole(), command);
			}
		}
		
		// Inform the player
		if(LojaSquare.get().getConfigManager().getGroup(item.getGrupo()).get("Enviar_Mensagem").getBoolean()) {
			List<String> messages = LojaSquare.get().getConfigManager().getGroup(item.getGrupo()).get("Mensagem_Receber_Ao_Ativar_Produto").getList(object -> (String)object);
			for(String message : messages) {
				message = message.replace("&", "§")
						.replace("@grupo", item.getGrupo())
						.replace("@produto", item.getProduto())
						.replace("@dias", item.getDias()+"")
						.replace("@qnt", item.getQuantidade()+"")
						.replace("@player", event.getPlayer().getName());
				event.getPlayer().sendMessage(Text.of(message));
			}
		}
		
		// Inform
		LojaSquare.get().log("§3[LojaSquare] §bEntrega do produto §a" + item.toString() + "§b concluida com sucesso.");
	}
}
