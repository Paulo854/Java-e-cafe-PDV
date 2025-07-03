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

public class verificar_estoque_btn { // Nome da classe ajustado para padrão CamelCase

    private final Dotenv dotenv;
    private final String url;
    private final String usuario;
    private final String senha;

    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    valida_login validarFilial = new valida_login();
    controlador_operador op = new controlador_operador();
    
    public verificar_estoque_btn() {
        this.dotenv = Dotenv.configure()
                      .directory("./src") 
                      .filename(".env")
                      .load();
        
        this.url = "jdbc:mysql://" + dotenv.get("HOST_BD") + ":3306/" + dotenv.get("BACODEDADOS_BD");
        this.usuario = dotenv.get("USER_BD");
        this.senha = dotenv.get("PASSWORD_BD");
    }

    public int getEstoque(String produto) {
        if (produto == null || produto.trim().isEmpty()) {
            JOptionPane.showMessageDialog(null, 
                "Nome do produto não pode ser vazio!", 
                "Erro de Validação", 
                JOptionPane.ERROR_MESSAGE);
            return 0;
        }

        String sql = "SELECT quantidade FROM controle_estoque WHERE LOWER(TRIM(produto)) = LOWER(TRIM(?)) and filial = ?";
        
        try (
            Connection conexao = DriverManager.getConnection(url, usuario, senha);
            PreparedStatement stmt = conexao.prepareStatement(sql)
        ) {
        	int filial = op.getFilial();
        	
            stmt.setString(1, produto.trim());
            stmt.setInt(2, filial);

            try (ResultSet resultado = stmt.executeQuery()) {
                if (resultado.next()) {
                    return resultado.getInt("quantidade");
                } else {
                    return 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Erro ao consultar estoque: " + e.getMessage());
            discord_erro_pdv erroDiscord = new discord_erro_pdv();
            String tipoErro = e.getClass().getSimpleName();
            String mensagemErro = e.getMessage() != null ? e.getMessage() : "Sem mensagem específica.";
            erroDiscord.enviarEmbed("Erro desconhecido", mensagemErro, "verificar_estoque_btn() -> getEstoque()", "Baixa", LocalDateTime.now().format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
            e.printStackTrace();
            return -1;
        }
    }
}
