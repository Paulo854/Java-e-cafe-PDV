package ODS;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.JOptionPane;

import cliente_banco.verificar_cliente;
import conexao_controle.discord_erro_pdv;
import controladores.controle_cliente;
import controladores.controlador_operador;

import io.github.cdimascio.dotenv.Dotenv;

public class RegistrarDonate {
	 Dotenv dotenv = Dotenv.configure()
             .directory("./src")
             .filename(".env")
             .load();
	
	private final String URL;
    private final String USUARIO;
    private final String SENHA;
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    
    controle_cliente cliente = new controle_cliente();
    verificar_cliente pontos = new verificar_cliente();
    controlador_operador op = new controlador_operador();

    public RegistrarDonate() {
       
        this.URL = "jdbc:mysql://" + dotenv.get("HOST_BD") + ":3306/" + dotenv.get("BACODEDADOS_BD");
        this.USUARIO = dotenv.get("USER_BD");
        this.SENHA = dotenv.get("PASSWORD_BD");
    }

    
    public void registrarDonate(double valor, LocalDate dataDonate, String nome) {
        Runnable tarefa = () -> {
            String sql = "INSERT INTO donates (valor, data, cliente) VALUES (?, ?, ?)";	 

            try (Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

            	
                stmt.setDouble(1, valor);
                stmt.setDate(2, java.sql.Date.valueOf(dataDonate));
                stmt.setString(3, nome);
                
                
                int linhasAfetadas = stmt.executeUpdate();

                if (linhasAfetadas > 0) {
                    System.out.println("A doação do cliente: "+nome+" no valor de R$"+valor+" foi registrada e convertida em doação !)");
                    DonateDiscord donate = new DonateDiscord();
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    LocalDateTime agora = LocalDateTime.now();
                    String dataHora = agora.format(formatter);
                    donate.enviarEmbedDoacao(nome, valor, dataHora, "JavamosUnidos", urlDiscord(op.getFilial()));
                } 
            } catch (SQLException e) {
                System.err.println("Não conseguimos relizar a doação, o valor será convertido em pontos :(");
                int valorArredondado = (int) Math.floor(valor);
                pontos.addPontos(cliente.getNumberCPF(), valorArredondado*100);
                DonateDiscord donate = new DonateDiscord();
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime agora = LocalDateTime.now();
                String dataHora = agora.format(formatter);
                donate.enviarEmbedDoacaoFalha(nome, valor, dataHora, "JavamosUnidos", urlDiscord(op.getFilial()));
                discord_erro_pdv erroDiscord = new discord_erro_pdv();
                String tipoErro = e.getClass().getSimpleName();
                String mensagemErro = e.getMessage() != null ? e.getMessage() : "Sem mensagem específica.";
                erroDiscord.enviarEmbed("Erro desconhecido", mensagemErro, "RegistrarDonate() -> RegistrarDonate()", "Alta", LocalDateTime.now().format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
                e.printStackTrace();
            }
        };
        

        // Executa em segundo plano
        Thread thread = new Thread(tarefa);
        thread.setDaemon(true);
        thread.start();
    }
    
    private String urlDiscord(int filial) {
    	String urlWebhook = switch (filial) {
        case 1001 -> dotenv.get("WEBHOOK_PAULISTA");
        case 1002 -> dotenv.get("WEBHOOK_JDANGELA");
        case 1003 -> dotenv.get("WEBHOOK_LIBERDADE");
        default -> "";
    	};
    	return urlWebhook;
    }
}
