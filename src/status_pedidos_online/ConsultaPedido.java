package status_pedidos_online;

import io.github.cdimascio.dotenv.Dotenv;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import io.github.cdimascio.dotenv.Dotenv;

public class ConsultaPedido {
	public Dotenv dotenv = Dotenv.configure()
            .directory("./src") 
            .filename(".env")
            .load();
	
	public ConsultaPedido(){
		
	}
	
	public int consultaPedido() {
	    String url = "jdbc:mysql://" + dotenv.get("HOST_BD") + ":3306/" + dotenv.get("BACODEDADOS_BD");
	    String usuario = dotenv.get("USER_BD");
	    String senha = dotenv.get("PASSWORD_BD");

	    String sql = "SELECT EXISTS(SELECT 1 FROM pedido_online) AS tem_dados";
	    try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
	         PreparedStatement stmt = conexao.prepareStatement(sql)) {

	        ResultSet resultado = stmt.executeQuery();

	        if (resultado.next()) {
	            return resultado.getInt("tem_dados");
	            
	        }

	        return 0;

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return -1;
	    }
	}

}
