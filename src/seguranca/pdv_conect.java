package seguranca;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import io.github.cdimascio.dotenv.Dotenv;


//connect tem 2 n
public class pdv_conect {

    private final String URL;
    private final String USUARIO;
    private final String SENHA;
    private final ExecutorService executor;

    public pdv_conect() {
        Dotenv dotenv = Dotenv.configure()
                .directory("./src")
                .filename(".env")
                .load();

        this.URL = "jdbc:mysql://" + dotenv.get("HOST_BD") + ":3306/" + dotenv.get("BACODEDADOS_BD");
        this.USUARIO = dotenv.get("USER_BD");
        this.SENHA = dotenv.get("PASSWORD_BD");

        // Um executor para rodar tarefas em segundo plano
        this.executor = Executors.newSingleThreadExecutor();
    }

    public Future<Integer> verificarIp(String ip) {
        Callable<Integer> tarefa = () -> {
            String sql = "SELECT ip FROM caixas_abertos WHERE ip = ?";
            try (Connection conn = DriverManager.getConnection(URL, USUARIO, SENHA);
                 PreparedStatement stmt = conn.prepareStatement(sql)) {

                stmt.setString(1, ip);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return 1; // Encontrou o IP
                    } else {
                        return 0; // Não encontrou o IP
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return -1; // Erro ao consultar
            }
        };

        // Submete a tarefa e devolve o Future
        return executor.submit(tarefa);
    }

    // Para fechar o executor quando não precisar mais
    public void shutdown() {
        executor.shutdown();
    }
}
