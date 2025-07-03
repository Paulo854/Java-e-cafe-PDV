package status_pagamento;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import produto.Produtos;

public class PedidosDiscord {

    public void enviarPedido(String cliente, List<Object[][]> tabelaPedidos, String webhookUrl) {
        try {
            URL url = new URL(webhookUrl);
            HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
            conexao.setRequestMethod("POST");
            conexao.setRequestProperty("Content-Type", "application/json");
            conexao.setRequestProperty("User-Agent", "Java-Discord-Webhook");
            conexao.setDoOutput(true);

            StringBuilder itensBuilder = new StringBuilder();
            double total = 0.0;

            for (Object[][] item : tabelaPedidos) {
                Produtos produto = Produtos.fromArray(item);
                String nome = escapeJson(produto.getNome());
                int quantidade = produto.getQuantidade();
                double preco = produto.getPreco();
                String detalhes = escapeJson(produto.getDetalhes());

                if(detalhes == "--") {
                	detalhes = "Sem detalhes";
                }
                
                itensBuilder.append(String.format("🍽️ %s — Qtd: %d, R$ %.2f\\n📝 %s\\n\\n",
                    nome, quantidade, preco, detalhes));

                total += quantidade * preco;
            }


            // CORREÇÃO PRINCIPAL: usando vírgula entre propriedades do JSON
            String jsonPayload = String.format(
            	    "{\"username\":\"Pedidos\",\"content\":\"📋 **Cliente:** %s\\n\\n🛍️ **Itens:**\\n%s\\n\\n💵 **Total:** R$ %.2f\"}",
            	    escapeJson(cliente),
            	    itensBuilder.toString(),
            	    total
            	);


            System.out.println("JSON a enviar: " + jsonPayload);

            try (OutputStream os = conexao.getOutputStream()) {
                os.write(jsonPayload.getBytes("UTF-8"));
            }

            int statusCode = conexao.getResponseCode();
            if (statusCode == 204) {
                System.out.println("Pedido enviado com sucesso!");
            } else {
                System.out.println("Erro HTTP: " + statusCode);
                try (java.io.InputStream is = conexao.getErrorStream()) {
                    if (is != null) {
                        String errorResponse = new String(is.readAllBytes());
                        System.out.println("Resposta de erro: " + errorResponse);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao enviar pedido: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                   .replace("\"", "\\\"")
                   .replace("\n", "\\n")
                   .replace("\r", "\\r");
    }
}