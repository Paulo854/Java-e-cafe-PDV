package controle_estoque;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;

import conect_banco.valida_login;
import conexao_controle.discord_erro_pdv;
import controladores.controlador_operador;
import io.github.cdimascio.dotenv.Dotenv;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Verificar_estoque implements Callable<Integer> {
	private int quantidadeDisponivel = 0;
	 valida_login validarFilial = new valida_login();
	 controlador_operador op = new controlador_operador();
	
	
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./src")
            .filename(".env")
            .load();

    private static final String URL = "jdbc:mysql://" + dotenv.get("HOST_BD") + ":3306/" + dotenv.get("BACODEDADOS_BD");
    private static final String USUARIO = dotenv.get("USER_BD");
    private static final String SENHA = dotenv.get("PASSWORD_BD");

    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private final String produto;
    private final int quantidadeDesejada;

    public Verificar_estoque(String produto, int quantidadeDesejada) {
        this.produto = produto;
        this.quantidadeDesejada = quantidadeDesejada;
    }

    @Override
    public Integer call() {
        String sql = "SELECT quantidade AS total FROM controle_estoque WHERE produto = ? AND filial = ?";

        try (Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            int filial = op.getFilial();
            stmt.setString(1, produto);
            stmt.setInt(2, filial);
            System.out.println("Produto buscado: [" + produto + "]");
            
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    quantidadeDisponivel = rs.getInt("total");
                    System.out.println("Estoque do produto: " + produto + " (" + quantidadeDisponivel + ")");

                    return quantidadeDisponivel; // retorna o valor que tem no banco
                } else {
                    System.out.println("Produto não encontrado no estoque!");
                    return -1; // Produto não encontrado
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            discord_erro_pdv erroDiscord = new discord_erro_pdv();
            String tipoErro = e.getClass().getSimpleName();
            String mensagemErro = e.getMessage() != null ? e.getMessage() : "Sem mensagem específica.";
            erroDiscord.enviarEmbed("Erro desconhecido", mensagemErro, "Verificar_estoque()", "Alta", LocalDateTime.now().format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
            return -2; // Erro de conexão ou SQL
        }
    }
}
