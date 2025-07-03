package seguranca;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.*;
import java.util.*;

import io.github.cdimascio.dotenv.Dotenv;

public class ControleDiscordEstoque {
	public static final Dotenv dotenv = Dotenv.configure()
	        .directory("./src") 
	        .filename(".env")
	        .load();

	
	
    private static final String WEBHOOK_URL = dotenv.get("WEBHOOK_ESTOQUE");
    private static final int LIMITE_DISCORD = 4000;

    public static void enviarRelatorio(int filialId) {
        try {
            // 1. Buscar dados
            List<String> linhas = buscarProdutosDaFilial(filialId);

            // 2. Montar conteúdo respeitando o limite
            StringBuilder conteudo = new StringBuilder();
            for (String linha : linhas) {
                if (conteudo.length() + linha.length() + 1 > LIMITE_DISCORD) break;
                conteudo.append(linha).append("\n");
            }

            // 3. Montar JSON do Embed
            String json = "{\n" +
                    "  \"embeds\": [\n" +
                    "    {\n" +
                    "      \"title\": \"📦 Relatório de Estoque - Filial " + filialId + "\",\n" +
                    "      \"description\": \"" + escapeJson(conteudo.toString()) + "\",\n" +
                    "      \"color\": 5814783\n" +
                    "    }\n" +
                    "  ]\n" +
                    "}";

            // 4. Enviar para o Discord
            enviarParaWebhook(json);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static List<String> buscarProdutosDaFilial(int filialId) throws Exception {
        List<String> linhas = new ArrayList<>();

        Connection conn = DriverManager.getConnection(
        	    "jdbc:mysql://" + dotenv.get("HOST_BD") + ":3306/" + dotenv.get("BACODEDADOS_BD"),
        	    dotenv.get("USER_BD"),
        	    dotenv.get("PASSWORD_BD")
        	);
        PreparedStatement stmt = conn.prepareStatement(
                "SELECT produto, quantidade FROM controle_estoque WHERE filial = ?");
        stmt.setInt(1, filialId);

        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String nome = rs.getString("produto");
            int quantidade = rs.getInt("quantidade");
            linhas.add("🔹 **" + nome + "** - Estoque: `" + quantidade + "`");
        }

        rs.close();
        stmt.close();
        conn.close();

        return linhas;
    }

    private static void enviarParaWebhook(String json) throws Exception {
        URL url = new URL(WEBHOOK_URL);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        conn.setDoOutput(true);

        try (OutputStream os = conn.getOutputStream()) {
            os.write(json.getBytes("UTF-8"));
        }

        int responseCode = conn.getResponseCode();
        System.out.println("Resposta do Discord: " + responseCode);
    }

    // Evita problemas com aspas e quebras de linha no JSON
    private static String escapeJson(String s) {
        return s.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n");
    }

}
