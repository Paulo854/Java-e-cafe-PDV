package ODS;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class DonateDiscord {
	public void enviarEmbedDoacao(String cliente, double valor, String dataHora, String rodape, String envio) {
	    try {
	        System.out.println("Debug de parâmetros:");
	        System.out.println("Cliente: " + cliente);
	        System.out.println("Valor: " + valor);
	        System.out.println("Data/Hora: " + dataHora);
	        System.out.println("Rodapé: " + rodape);
	        System.out.println("URL de envio: " + envio);

	        if (cliente == null || dataHora == null || rodape == null || envio == null) {
	            System.out.println("Erro: Um ou mais parâmetros são nulos.");
	            return;
	        }

	        URL url = new URL(envio.trim());
	        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
	        conexao.setRequestMethod("POST");
	        conexao.setRequestProperty("Content-Type", "application/json");
	        conexao.setDoOutput(true);

	        String valorFormatado = String.format("%.2f", valor);


	        String jsonPayload = String.format("""
	            {
	             "username": "Java&Café • PDV Monitor",
                  "avatar_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png",
	              "embeds": [{
	                "title": "🎉 Nova Doação Recebida!",
	                "description": "Uma nova contribuição foi registrada. Detalhes abaixo:",
	                "color": 3066993,
	                "fields": [
	                  { "name": "👤 Cliente", "value": "%s", "inline": true },
	                  { "name": "🎁 Valor Doado", "value": "R$ %s", "inline": true },
	                  { "name": "📅 Data", "value": "%s", "inline": false }
	                ],
	                "footer": {
	                  "text": "%s",
	                  "icon_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png"
	                }
	              }]
	            }
	            """, cliente, valorFormatado, dataHora, rodape);

	        System.out.println("JSON enviado:\n" + jsonPayload);

	        try (OutputStream output = conexao.getOutputStream()) {
	            output.write(jsonPayload.getBytes("UTF-8"));
	            output.flush();
	        }

	        int resposta = conexao.getResponseCode();
	        if (resposta == 204) {
	            System.out.println("Embed de doação enviado com sucesso!");
	        } else {
	            System.out.println("Erro ao enviar Embed de doação: Código " + resposta);
	        }

	    } catch (Exception e) {
	        System.out.println("Erro: " + e.getMessage());
	    }
	}
	public void enviarEmbedDoacaoFalha(String cliente, double valor, String dataHora, String rodape, String envio) {
	    try {
	        System.out.println("Debug de parâmetros:");
	        System.out.println("Cliente: " + cliente);
	        System.out.println("Valor: " + valor);
	        System.out.println("Data/Hora: " + dataHora);
	        System.out.println("Rodapé: " + rodape);
	        System.out.println("URL de envio: " + envio);

	        if (cliente == null || dataHora == null || rodape == null || envio == null) {
	            System.out.println("Erro: Um ou mais parâmetros são nulos.");
	            return;
	        }

	        URL url = new URL(envio.trim());
	        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
	        conexao.setRequestMethod("POST");
	        conexao.setRequestProperty("Content-Type", "application/json");
	        conexao.setDoOutput(true);

	        String valorFormatado = String.format("%.2f", valor);


	        String jsonPayload = String.format("""
	            {
	              "username": "PDV Monitor",
	              "avatar_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png",
	              "embeds": [{
	                "title": "❌ Erro ao Registrar Doação",
	                "description": "Uma tentativa de doação falhou e **uma tentativa foi feita para conveter em pontos ;)**.",
	                "color": 15158332,
	                "fields": [
	                  { "name": "👤 Cliente", "value": "%s", "inline": true },
	                  { "name": "🎁 Valor Tentado", "value": "R$ %s", "inline": true },
	                  { "name": "📅 Data", "value": "%s", "inline": false },
	                ],
	                "footer": {
	                  "text": "%s",
	                  "icon_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png"
	                }
	              }]
	            }
	            """, cliente, valorFormatado, dataHora, rodape);

	        System.out.println("JSON enviado:\n" + jsonPayload);

	        try (OutputStream output = conexao.getOutputStream()) {
	            output.write(jsonPayload.getBytes("UTF-8"));
	            output.flush();
	        }

	        int resposta = conexao.getResponseCode();
	        if (resposta == 204) {
	            System.out.println("Embed de falha na doação enviado com sucesso!");
	        } else {
	            System.out.println("Erro ao enviar Embed: Código " + resposta);
	        }

	    } catch (Exception e) {
	        System.out.println("Erro ao tentar enviar embed de falha: " + e.getMessage());
	    }
	}

}
