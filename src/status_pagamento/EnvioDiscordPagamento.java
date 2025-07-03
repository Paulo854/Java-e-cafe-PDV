package status_pagamento;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class EnvioDiscordPagamento {
	public void enviarEmbed(String cliente, double valor, String formaPagamento, String formatter, String rodape, String envio) {
	    try {
	    	
	        System.out.println("Debug de parâmetros:");
	        System.out.println("Cliente: " + cliente);
	        System.out.println("Valor: " + valor);
	        System.out.println("Forma de Pagamento: " + formaPagamento);
	        System.out.println("Data/Hora: " + formatter);
	        System.out.println("Rodapé: " + rodape);
	        System.out.println("URL de envio: " + envio);
	        
	        if (cliente == null || formaPagamento == null || formatter == null || rodape == null || envio == null) {
	            System.out.println("Erro: Um ou mais parâmetros são nulos.");
	            return;
	        }
	        

	        URL url = new URL(envio.trim());
	        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
	        conexao.setRequestMethod("POST");
	        conexao.setRequestProperty("Content-Type", "application/json");
	        conexao.setDoOutput(true);

	        String valorFormatado = String.format("%.2f", valor);

	        // Escape básico
	        cliente = escapeJson(cliente);
	        formaPagamento = escapeJson(formaPagamento);
	        formatter = escapeJson(formatter);
	        rodape = escapeJson(rodape);

	        String jsonPayload = String.format("""
	            {
	              "username": "PDV Monitor",
	              "avatar_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png",
	              "embeds": [{
	                "title": "✅ Pagamento Confirmado com Sucesso!",
	                "description": "Uma nova venda foi concluída com êxito. Detalhes abaixo:",
	                "fields": [
	                  { "name": "👤 Cliente", "value": "%s", "inline": true },
	                  { "name": "💰 Valor Pago", "value": "R$ %s", "inline": true },
	                  { "name": "💳 Forma de Pagamento", "value": "%s", "inline": false },
	                  { "name": "🕒 Data e Hora", "value": "%s", "inline": false }
	                ],
	                "footer": {
	                  "text": "%s",
	                  "icon_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png"
	                }
	              }]
	            }
	            """, cliente, valorFormatado, formaPagamento, formatter, rodape);

	        System.out.println("JSON enviado:\n" + jsonPayload);

	        try (OutputStream output = conexao.getOutputStream()) {
	            output.write(jsonPayload.getBytes("UTF-8"));
	            output.flush();
	        }

	        int resposta = conexao.getResponseCode();
	        if (resposta == 204) {
	            System.out.println("Embed enviado com sucesso!");
	        } else {
	            System.out.println("Erro ao enviar Embed: Código " + resposta);
	        }

	    } catch (Exception e) {
	        System.out.println("Erro: " + e.getMessage());
	    }
	}

	private String escapeJson(String text) {
	    return text.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n");
	}
}
