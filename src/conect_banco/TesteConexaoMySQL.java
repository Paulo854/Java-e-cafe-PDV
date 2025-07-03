package conect_banco;

import io.github.cdimascio.dotenv.Dotenv;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class TesteConexaoMySQL {
	public Dotenv dotenv = Dotenv.configure()
            .directory("./src") 
            .filename(".env")
            .load();
	
		//connect tem 2 n
			public boolean conect() {
		        String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
		        String usuario = dotenv.get("USER_BD");
		        String senha = dotenv.get("PASSWORD_BD");

		        try {
		            Connection conexao = DriverManager.getConnection(url, usuario, senha);
		            System.out.println("Conectado aos serviços Java&Café!");
		            conexao.close();
		            return true;
		        } catch (SQLException e) {
		            System.out.println("Erro ao conectar ao banco de dados:");
		            e.printStackTrace();
		            return false;
		        }
		  }
}
