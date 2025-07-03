package controladores;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class FechamentoCaixa {
	public void enviarFechamentoCaixa(String operador, double pix, double credito, double debito, double dinheiro, String formatter, String rodape, String envio) {
	    try {
	        URL url = new URL(envio);
	        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
	        conexao.setRequestMethod("POST");
	        conexao.setRequestProperty("Content-Type", "application/json");
	        conexao.setDoOutput(true);

	        // Formata todos os valores para duas casas decimais
	        String valorDinheiro = String.format("%.2f", dinheiro);
	        String valorDebito = String.format("%.2f", debito);
	        String valorCredito = String.format("%.2f", credito);
	        String valorPix = String.format("%.2f", pix);        
	        

	        String jsonPayload = String.format("""
	            {
	              "username": "Java&Café • PDV Monitor",
	              "avatar_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png",
	              "embeds": [{
	                "title": "📦 Fechamento de Caixa",
	                "description": "Confira abaixo os valores do fechamento de caixa:",
	                "color": 5763719,
	                "fields": [
	                  {
	                    "name": "👨‍💼 Operador",
	                    "value": "%s",
	                    "inline": false
	                  },
	                  {
	                    "name": "💰 Dinheiro",
	                    "value": "R$ %s",
	                    "inline": true
	                  },
	                  {
	                    "name": "🏧 Débito",
	                    "value": "R$ %s",
	                    "inline": true
	                  },
	                  {
	                    "name": "💳 Crédito",
	                    "value": "R$ %s",
	                    "inline": true
	                  },
	                  {
	                    "name": "🔁 PIX",
	                    "value": "R$ %s",
	                    "inline": true
	                  },
	                  {
	                    "name": "🕒 Data e Hora",
	                    "value": "%s",
	                    "inline": false
	                  }
	                ],
	                "footer": {
	                  "text": "%s",
	                  "icon_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png"
	                }
	              }]
	            }
	            """, operador, valorDinheiro, valorDebito, valorCredito, valorPix, formatter, rodape);

	        try (OutputStream output = conexao.getOutputStream()) {
	            output.write(jsonPayload.getBytes());
	            output.flush();
	        }

	        int resposta = conexao.getResponseCode();
	        if (resposta == 204) {
	            System.out.println("Fechamento de caixa enviado com sucesso!");
	        } else {
	            System.out.println("Erro ao enviar fechamento: Código " + resposta);
	        }

	    } catch (Exception e) {
	        System.out.println("Erro: " + e.getMessage());
	    }
	}

}
