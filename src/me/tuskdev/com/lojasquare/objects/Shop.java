package me.tuskdev.com.lojasquare.objects;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.HttpsURLConnection;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.tuskdev.com.lojasquare.LojaSquare;

public class Shop {
	
	private String ipMaquina;
	
	// Update the delivery
	public Boolean updateDelivery(Item item) {
		if(item == null) return false;
		return this.update(String.format("/v1/queue/%s/%d", item.getPlayer(), item.getIDEntrega()));
	}
	
	// Get all delivery's
	public List<Item> getAllDelivery() {
		List<Item> result = new ArrayList<>();

		String jsonResult = this.get("/v1/queue/*");
		if(jsonResult.startsWith("LS-")) return result;
		try {
			JsonObject jsonObject = new JsonParser().parse(jsonResult).getAsJsonObject();
			for (Integer index = 1; index <= jsonObject.entrySet().size(); index++) {
				Item item = new Gson().fromJson(jsonObject.getAsJsonObject(index.toString()), Item.class);
				result.add(item);
			}
		} catch (Exception e) {
			System.out.println(jsonResult);
		}
		
		return result;
	}
	
	// Get all delivery's of player
	public List<Item> getAllDelivery(String player) {
		List<Item> result = new ArrayList<>();
		
		String jsonResult = this.get("/v1/queue/" + player);
		if(jsonResult.startsWith("LS-")) return result;
		try {
			JsonObject jsonObject = new JsonParser().parse(jsonResult).getAsJsonObject();
			for (Integer index = 1; index <= jsonObject.entrySet().size(); index++) {
				Item item = new Gson().fromJson(jsonObject.getAsJsonObject(index.toString()), Item.class);
				result.add(item);
			}
		} catch (Exception e) {
			System.out.println(jsonResult);
		}
		
		return result;
	}
	
	// Get the JsonObject in string by API
	public String get(String endpoint) {
		// URLConnection and status code
		HttpsURLConnection urlConnection = null;
		Integer statusCode = 0;
		
		// Trying
		try {
			// Connect in API server
			URL url = new URL("https://api.lojasquare.com.br/" + endpoint);
			urlConnection = (HttpsURLConnection) url.openConnection();
			urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0) lojasquare");
			urlConnection.setRequestMethod("GET");
			urlConnection.setRequestProperty("Authorization", LojaSquare.get().getConfigManager().KEY_API);
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setUseCaches(false);
			urlConnection.setAllowUserInteraction(false);
			urlConnection.setConnectTimeout(LojaSquare.get().getConfigManager().CONNECTION_TIMEOUT);
			urlConnection.setReadTimeout(LojaSquare.get().getConfigManager().READ_TIMEOUT);
			urlConnection.connect();
			
			// Get the response code and apply
			statusCode = urlConnection.getResponseCode();
			if (statusCode == 200 || statusCode == 201 || statusCode == 204) {
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
				
				StringBuilder stringBuilder = new StringBuilder();
				String line;
				while ((line = bufferedReader.readLine()) != null) stringBuilder.append(line).append("\n");
				
				bufferedReader.close();
				
				return stringBuilder.toString();
			}
		}
		
		// Catching
		catch (IOException exception) {
			if (LojaSquare.get().getConfigManager().DEBUG_MODE) exception.printStackTrace();
			else LojaSquare.get().log("§c[LojaSquare] Não foi possível fazer conexão com o site. Motivo: " + exception.getMessage());
			
			if (urlConnection != null) urlConnection.disconnect();
		}
		
		// Finally
		finally {
			if (urlConnection != null) urlConnection.disconnect();
		}
		
		// Return the response by status code
		return "LS-"+this.getResponseByCode(statusCode);
	}
	
	// Update the JsonObject in API
	public Boolean update(String endpoint){
		// URLConnection and status code
		HttpsURLConnection urlConnection = null;
		Integer statusCode = 0;
		
		// Trying
		try {
			// Connect in API server
			URL url = new URL("https://api.lojasquare.com.br/" + endpoint);
			urlConnection = (HttpsURLConnection) url.openConnection();
			urlConnection.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0) lojasquare");
			urlConnection.setRequestMethod("PUT");
			urlConnection.setRequestProperty("Authorization", LojaSquare.get().getConfigManager().KEY_API);
			urlConnection.setRequestProperty("Content-Type", "application/json");
			urlConnection.setUseCaches(false);
			urlConnection.setAllowUserInteraction(false);
			urlConnection.setConnectTimeout(LojaSquare.get().getConfigManager().CONNECTION_TIMEOUT);
			urlConnection.setReadTimeout(LojaSquare.get().getConfigManager().READ_TIMEOUT);
			urlConnection.connect();
			
			// Get the response code and apply
			statusCode = urlConnection.getResponseCode();
			if (statusCode == 200 || statusCode == 201 || statusCode == 204) return true;
		}
		
		// Catching
		catch (IOException exception) {
			if (LojaSquare.get().getConfigManager().DEBUG_MODE) exception.printStackTrace();
			else LojaSquare.get().log("§c[LojaSquare] Não foi possível fazer conexão com o site. Motivo: " + exception.getMessage());
			
			if (urlConnection != null) urlConnection.disconnect();
		}
		
		// Finally
		finally {
			if (urlConnection != null) urlConnection.disconnect();
		}
		
		// Inform and return false
		LojaSquare.get().log(this.getResponseByCode(statusCode));
		return false;
	}
	
	// Get the response by status code

	public String getResponseByCode(int i) {
		String msg = "";
		switch (i) {
		case 0:
			msg = "[LojaSquare] §cServidor sem conexao com a internet.";
			break;
		case 401:
			msg = "[LojaSquare] §cConexao nao autorizada! Por favor, confira se a sua credencial esta correta.";
			break;
		case 404:
			msg = "[LojaSquare] §cNao foi encontrado nenhum registro para a requisicao efetuada.";
			break;
		case 405:
			msg = "[LojaSquare] §cErro ao autenticar sua loja! Verifique se sua assinatura e credencial estao ativas!";
			break;
		case 406:
			msg = "[LojaSquare] §cNao foi executada nenhuma atualizacao referente ao requerimento efetuado.";
			break;
		case 409:
			msg = "[LojaSquare] §cO IP enviado e diferente do que temos em nosso Banco de Dados. IP da sua Maquina: §a" + getIpMaquina();
			break;
		case 423:
			msg = "[LojaSquare] §cO IP da maquina do seu servidor ou a sua key-api foram bloqueados.";
			break;
		default:
			msg = "[LojaSquare] §cProvavel falha causada por entrada de dados incompativeis com o requerimento efetuado. Status Code: " + i;
			break;
		}
		return msg;
	}

	public String getIpMaquina() {
		if (ipMaquina == null) {
			String getIP = this.get("/v1/autenticar/ip");
			// checagem criada para evitar erros de repetição como este:
			// https://prnt.sc/vql2p3
			if (getIP.length() > 20) {
				ipMaquina = "não identificado.";
			} else {
				ipMaquina = getIP;
			}
		}
		return ipMaquina;
	}

}
