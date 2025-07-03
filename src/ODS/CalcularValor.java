package ODS;

import java.sql.*;
import java.text.NumberFormat;
import java.util.Locale;

import io.github.cdimascio.dotenv.Dotenv;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class CalcularValor {
	    private static final Dotenv dotenv = Dotenv.configure()
	        .directory("./src")
	        .filename(".env")
	        .load();

	    public static void calcularEDisparar(String link) {
	        String url = "jdbc:mysql://" + dotenv.get("HOST_BD") + ":3306/" + dotenv.get("BACODEDADOS_BD");
	        String usuario = dotenv.get("USER_BD");
	        String senha = dotenv.get("PASSWORD_BD");

	        try (Connection conn = DriverManager.getConnection(url, usuario, senha)) {
	            String sql = "SELECT SUM(valor) AS total_doacoes FROM donates";
	            PreparedStatement stmt = conn.prepareStatement(sql);
	            ResultSet rs = stmt.executeQuery();

	            if (rs.next()) {
	                double total = rs.getDouble("total_doacoes");

	                // Formata como moeda BRL
	                NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
	                String totalFormatado = formato.format(total);

	                // Envia embed para o Discord
	                enviarParaDiscord(totalFormatado, link);
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	    }

	    private static void enviarParaDiscord(String valorFormatado, String link) {
	        try {
	            String webhookUrl = link; 
	            URL url = new URL(webhookUrl);
	            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();

	            conexao.setRequestMethod("POST");
	            conexao.setRequestProperty("Content-Type", "application/json");
	            conexao.setDoOutput(true);

	            String payload = """
	            	    {
	            	        "username": "Java&Café — Impactando Vidas",
	            	         "avatar_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png",
	            	        "embeds": [
	            	            {
	            	                "title": "💖 Total de Doações",
	            	                "description": "O valor total arrecadado em doações foi de: **%s**",
	            	                "color": 65280,
	            	                "footer": {
	            	                    "text": "%s",
	            	                    "icon_url": "https://media.discordapp.net/attachments/1225809616978313348/1352467083266625680/logo1.png"
	            	                }
	            	            }
	            	        ]
	            	    }
	            	    """.formatted(valorFormatado, "JavamosUnidos");


	            try (OutputStream os = conexao.getOutputStream()) {
	                byte[] input = payload.getBytes("utf-8");
	                os.write(input, 0, input.length);
	            }

	            conexao.getInputStream(); // força a execução da requisição

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	   }
}
