package conexao_controle;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.format.DateTimeFormatter;

public class discord_erro_pdv {
	public void enviarEmbed(String titulo, String erro, String componente, String gravidade, String formatter, String rodape, String envio) {
        try {
            URL url = new URL(envio);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("POST");
            conexao.setRequestProperty("Content-Type", "application/json");
            conexao.setDoOutput(true);

            // Criando o JSON com as variáveis dinâmicas
            String jsonPayload = String.format("""
                    {
                      "username": "Java&Café • PDV Monitor",
                      "avatar_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png",
                      "embeds": [{
                        "title": "🚨 Alerta de Erro: %s",
                        "description": "O sistema PDV identificou uma falha durante a operação. Abaixo estão os detalhes técnicos:",
                        "color": 16733525,
                        "fields": [
                          {
                            "name": "🧠 Descrição do Erro",
                            "value": "```%s```",
                            "inline": false
                          },
                          {
                            "name": "📦 Componente Atingido",
                            "value": "%s",
                            "inline": true
                          },
                          {
                            "name": "🔥 Nível de Gravidade",
                            "value": "%s",
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
                    """, titulo, erro, componente, gravidade, formatter, rodape);


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
