package cliente_banco;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.github.cdimascio.dotenv.Dotenv;

public class cliente_pontos {
	public Dotenv dotenv = Dotenv.configure()
            .directory("./src") 
            .filename(".env")
            .load();
	

	
	public double getPontos(int cpf) {
        String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
        String usuario = dotenv.get("USER_BD");
        String senha = dotenv.get("PASSWORD_BD");

        String sql = "SELECT pontos FROM cliente WHERE cpf = ?";
        
        double pontos = 0;

        try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

        	 stmt.setInt(1, cpf);

            ResultSet resultado = stmt.executeQuery();

            if (resultado.next()) {
            	pontos = resultado.getInt("pontos");
                return pontos;
            } else {
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return pontos;
	}
	
}
