package cliente_banco;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

import javax.swing.JOptionPane;

import io.github.cdimascio.dotenv.Dotenv;

public class verificar_cliente {
	
	public Dotenv dotenv = Dotenv.configure()
            .directory("./src") 
            .filename(".env")
            .load();

	// Pede cpf do cliente
	public int verificaClienteCPFSimples(int cpf) {
        String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
        String usuario = dotenv.get("USER_BD");
        String senha = dotenv.get("PASSWORD_BD");

	    String sql = "SELECT cpf FROM cliente WHERE cpf = ? AND mb_fidelidade = 0";

	    try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
	         PreparedStatement stmt = conexao.prepareStatement(sql)) {

	        stmt.setInt(1, cpf);
	        
	        ResultSet resultado = stmt.executeQuery();

		// Verifica se o cliente está cadastrado no banco de dados
	        if (resultado.next()) {
	            int cpfBanco = resultado.getInt("cpf");
	            

	            if (cpfBanco == cpf) {
	                return 1;
	            } else {
	                return 0;
	            }
	        } else {
	            return -2;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return -1;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    }
	}
	public int verificaClienteCPFMember(int cpf) {
        String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
        String usuario = dotenv.get("USER_BD");
        String senha = dotenv.get("PASSWORD_BD");

        
	    String sql = "SELECT cpf FROM cliente WHERE cpf = ? AND mb_fidelidade = 1";

	    try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
	         PreparedStatement stmt = conexao.prepareStatement(sql)) {

	        stmt.setInt(1, cpf);
	        
	        ResultSet resultado = stmt.executeQuery();

	        if (resultado.next()) {
	            int cpfBanco = resultado.getInt("cpf");
	            

	            if (cpfBanco == cpf) {
	                return 1;
	            } else {
	                return 0;
	            }
	        } else {
	            return -2;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return -1;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    }
	}

	public String nomeCliente(int cpf) {
		String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
        String usuario = dotenv.get("USER_BD");
        String senha = dotenv.get("PASSWORD_BD");

        String sql = "SELECT nome FROM cliente WHERE cpf = ?";
        
        String nome = null;

        try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
             PreparedStatement stmt = conexao.prepareStatement(sql)) {

        	 stmt.setInt(1, cpf);

            ResultSet resultado = stmt.executeQuery();

            if (resultado.next()) {
                nome = resultado.getString("nome");
                return nome;
            } else {
            	System.out.println("Cliente não encontrado");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return nome;
	}
	
	public int verificaClientePontos(int cpf) {
        String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
        String usuario = dotenv.get("USER_BD");
        String senha = dotenv.get("PASSWORD_BD");

	    String sql = "SELECT pontos FROM cliente WHERE cpf = ?";

	    try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
	         PreparedStatement stmt = conexao.prepareStatement(sql)) {

	        stmt.setInt(1, cpf);
	        
	        ResultSet resultado = stmt.executeQuery();

	        if (resultado.next()) {
	            int pontosBanco = resultado.getInt("pontos");
	            

	            if (pontosBanco > 0) {
	                return pontosBanco;
	            } else {
	                return 0;
	            }
	        } else {
	            return -2;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return -1;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    }
	}
	public int usarPontos(int cpf, int pontosParaUsar) {
        String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
        String usuario = dotenv.get("USER_BD");
        String senha = dotenv.get("PASSWORD_BD");

        String selectSQL = "SELECT pontos FROM cliente WHERE cpf = ?";
        String updateSQL = "UPDATE cliente SET pontos = pontos - ? WHERE cpf = ?";

        try (Connection conexao = DriverManager.getConnection(url, usuario, senha)) {

            // 1. Verifica os pontos atuais
            try (PreparedStatement selectStmt = conexao.prepareStatement(selectSQL)) {
                selectStmt.setInt(1, cpf);
                ResultSet resultado = selectStmt.executeQuery();

                if (resultado.next()) {
                    int pontosAtuais = resultado.getInt("pontos");

                    if (pontosAtuais < pontosParaUsar) {
                        return 0; // Não tem pontos suficientes
                    }

                    // 2. Atualiza os pontos
                    try (PreparedStatement updateStmt = conexao.prepareStatement(updateSQL)) {
                        updateStmt.setInt(1, pontosParaUsar);
                        updateStmt.setInt(2, cpf);
                        int linhasAfetadas = updateStmt.executeUpdate();

                        if (linhasAfetadas > 0) {
                            return pontosAtuais - pontosParaUsar;
                        } else {
                            return -3; // Erro ao atualizar
                        }
                    }

                } else {
                    return -2; // Cliente não encontrado
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Erro de banco
        }
    }
	public int addPontos(int cpf, int pontosAdd) {
        String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
        String usuario = dotenv.get("USER_BD");
        String senha = dotenv.get("PASSWORD_BD");

        String selectSQL = "SELECT pontos FROM cliente WHERE cpf = ?";
        String updateSQL = "UPDATE cliente SET pontos = pontos + ? WHERE cpf = ?";

        try (Connection conexao = DriverManager.getConnection(url, usuario, senha)) {

            // 1. Verifica os pontos atuais
            try (PreparedStatement selectStmt = conexao.prepareStatement(selectSQL)) {
                selectStmt.setInt(1, cpf);
                ResultSet resultado = selectStmt.executeQuery();

                if (resultado.next()) {
                    int pontosAtuais = resultado.getInt("pontos");

                    if (pontosAtuais < pontosAdd) {
                        return 0; // Não tem pontos suficientes
                    }

                    // 2. Atualiza os pontos
                    try (PreparedStatement updateStmt = conexao.prepareStatement(updateSQL)) {
                        updateStmt.setInt(1, pontosAdd);
                        updateStmt.setInt(2, cpf);
                        int linhasAfetadas = updateStmt.executeUpdate();

                        if (linhasAfetadas > 0) {
                            return pontosAtuais - pontosAdd;
                        } else {
                            return -3; // Erro ao atualizar
                        }
                    }

                } else {
                    return -2; // Cliente não encontrado
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1; // Erro de banco
        }
    }
}
