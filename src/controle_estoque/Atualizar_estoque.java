package controle_estoque;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import conect_banco.valida_login;
import conexao_controle.discord_erro_pdv;
import controladores.controlador_operador;
import io.github.cdimascio.dotenv.Dotenv;

public class Atualizar_estoque {

    private final String URL;
    private final String USUARIO;
    private final String SENHA;
    valida_login validarFilial = new valida_login();
    controlador_operador op = new controlador_operador();
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public Dotenv dotenv = Dotenv.configure()
            .directory("./src")
            .filename(".env")
            .load();


    public Atualizar_estoque() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./src")
                .filename(".env")
                .load();

        this.URL = "jdbc:mysql://" + dotenv.get("HOST_BD") + ":3306/" + dotenv.get("BACODEDADOS_BD");
        this.USUARIO = dotenv.get("USER_BD");
        this.SENHA = dotenv.get("PASSWORD_BD");
    }

    
    public void retirarEstoque(String produto, int quantidade) {
        Runnable tarefa = () -> {
            String sql = "UPDATE controle_estoque SET quantidade = quantidade - ? WHERE produto = ? and filial = ?";

            try (Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

            	int filial = op.getFilial();
            	
                stmt.setInt(1, quantidade);
                stmt.setString(2, produto);
                stmt.setInt(3, filial);
                
                System.out.println("Produto buscado: [" + produto + "]");
                
                int linhasAfetadas = stmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    System.out.println("Estoque atualizado: " + produto + " Quantidade = (-" + quantidade + ")");
                } else {
                    System.out.println("Produto não encontrado: " + produto);
                }

            } catch (SQLException e) {
                System.err.println("Erro ao atualizar estoque de " + produto);
                e.printStackTrace();
            }
        };

        // Executa em segundo plano
        Thread thread = new Thread(tarefa);
        thread.setDaemon(true);
        thread.start();
    }
    
    public void addEstoque(String produto, int quantidade) {
        Runnable tarefa = () -> {
            String sql = "UPDATE controle_estoque SET quantidade = quantidade + ? WHERE produto = ? and filial = ?";

            try (Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

            	int filial = op.getFilial();
            	
                stmt.setInt(1, quantidade);
                stmt.setString(2, produto);
                stmt.setInt(3, filial);
                
                System.out.println("Produto buscado: [" + produto + "]");
                int linhasAfetadas = stmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    System.out.println("Estoque atualizado: " + produto + " Quantidade = (+" + quantidade + ")");
                } else {
                    System.out.println("Produto não encontrado: " + produto);
                }

            } catch (SQLException e) {
                System.err.println("Erro ao atualizar estoque de " + produto);
                discord_erro_pdv erroDiscord = new discord_erro_pdv();
                String tipoErro = e.getClass().getSimpleName();
                String mensagemErro = e.getMessage() != null ? e.getMessage() : "Sem mensagem específica.";
                erroDiscord.enviarEmbed("Erro desconhecido", mensagemErro, "login() -> validarLogin()", "Alta", LocalDateTime.now().format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
                e.printStackTrace();
            }
        };

        // Executa em segundo plano
        Thread thread = new Thread(tarefa);
        thread.setDaemon(true);
        thread.start();
    }
}
