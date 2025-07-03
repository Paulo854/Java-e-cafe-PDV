package conexao_controle;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class discord_pedidos {

	 public static void enviarEmbed(String titulo, String cafe, String leite, String tipoAcucar, String rodape, String envio) {
	        try {
	            URL url = new URL(envio);
	            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
	            conexao.setRequestMethod("POST");
	            conexao.setRequestProperty("Content-Type", "application/json");
	            conexao.setDoOutput(true);

	            // Criando o JSON com as variáveis dinâmicas
	            String jsonPayload = String.format("""
	            {
	              "username": "Java&Café",
	              "avatar_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png",
	              "embeds": [{
	                "title": "%s",
	                "description": "Olá, um pedido novo foi realizado",
	                "color": 16711680,
	                "fields": [
	                  {
	                    "name": "Café",
	                    "value": "%s",
	                    "inline": true
	                  },
	                  {
	                    "name": "Leite",
	                    "value": "%s",
	                    "inline": true
	                  },
	                  {
	                    "name": "Tipo de Açúcar",
	                    "value": "%s",
	                    "inline": true
	                  }
	                ],
	                "footer": {
	                  "text": "%s",
	                  "icon_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png"
	                }
	              }]
	            }
	            """, titulo,cafe, leite, tipoAcucar, rodape);

	            try (OutputStream output = conexao.getOutputStream()) {
	                output.write(jsonPayload.getBytes());
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
}
