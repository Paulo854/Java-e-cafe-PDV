package conect_banco;

import javax.swing.*;

import conexao_controle.discord_erro_pdv;

import io.github.cdimascio.dotenv.Dotenv;
import java.awt.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class valida_login {	
	public String ipPC;
	public Dotenv dotenv = Dotenv.configure()
            .directory("./src") 
            .filename(".env")
            .load();
	
	
	public int verificaLogin(int matricula, int senhaLogin) {
        String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
        String usuario = dotenv.get("USER_BD");
        String senha = dotenv.get("PASSWORD_BD");

        String sql = "select * from funcionario where matricula = ? and senha = ? and gerencia = 1";
        try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                PreparedStatement stmt = conexao.prepareStatement(sql)) {
        	 
        	stmt.setInt(1, matricula);
            stmt.setInt(2, senhaLogin);
            
            ResultSet resultado = stmt.executeQuery();
            
            if (resultado.next()) {
            	conexao.close();
                 return 1;
            } else {
            	conexao.close();
                return 0;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }
	}	
	public int verificaPDV(int matricula) {
        String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
        String usuario = dotenv.get("USER_BD");
        String senha = dotenv.get("PASSWORD_BD");

	    String sql = "SELECT data_abertura FROM caixas_abertos WHERE matricula = ?";

	    try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
	         PreparedStatement stmt = conexao.prepareStatement(sql)) {

	        stmt.setInt(1, matricula);
	        ResultSet resultado = stmt.executeQuery();

	        if (resultado.next()) {
	            Date dataAberturaBanco = resultado.getDate("data_abertura");
	            LocalDate dataAbertura = dataAberturaBanco.toLocalDate();
	            LocalDate dataHoje = LocalDate.now();

	            if (dataAbertura.equals(dataHoje)) {
	                return 0;
	            } else {
	                return 1;
	            }
	        } else {
	            // Matrícula não encontrada
	            return 0;
	        }

	    } catch (SQLException e) {
	        e.printStackTrace();
	        return -1;
	    } catch (Exception e) {
	        e.printStackTrace();
	        return -1;
	    }
	}
        public int fecharPDV(int senhaLogin) {
            String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
            String usuario = dotenv.get("USER_BD");
            String senha = dotenv.get("PASSWORD_BD");

            String sql = "select * from funcionario where senha = ? and gerencia = 1";
            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                    PreparedStatement stmt = conexao.prepareStatement(sql)) {
            	 
                stmt.setInt(1, senhaLogin);
                
                ResultSet resultado = stmt.executeQuery();
                
                if (resultado.next()) {
                	conexao.close();
                     return 1;
                } else {
                	conexao.close();
                    return 0;
                }

            } catch (SQLException e) {
                e.printStackTrace();
                return -1;
            }
    }
        public String getNomeGerencia(int senhaLogin) {
            String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
            String usuario = dotenv.get("USER_BD");
            String senha = dotenv.get("PASSWORD_BD");

            String sql = "SELECT nome FROM funcionario WHERE senha = ? AND gerencia = 1";
            
            String nome = null;

            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

            	 stmt.setInt(1, senhaLogin);

                ResultSet resultado = stmt.executeQuery();

                if (resultado.next()) {
                    nome = resultado.getString("nome");
                    return nome;
                } else {
                	System.err.print("Funcionário não encontrado");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return nome;
    }
        public String getNomeGerenciaMatricula(int matricula) {
            String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
            String usuario = dotenv.get("USER_BD");
            String senha = dotenv.get("PASSWORD_BD");

            String sql = "SELECT nome FROM funcionario WHERE matricula = ? AND gerencia = 1";
            
            String nome = null;

            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

            	 stmt.setInt(1, matricula);

                ResultSet resultado = stmt.executeQuery();

                if (resultado.next()) {
                    nome = resultado.getString("nome");
                    return nome;
                } else {
                	JOptionPane.showMessageDialog(null, "Funcionário não encontrado", "error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return nome;
    }

        public int getQtdPDV(int matricula) {
            String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
            String usuario = dotenv.get("USER_BD");
            String senha = dotenv.get("PASSWORD_BD");

            String sql = "SELECT COUNT(*) FROM caixas_abertos WHERE matricula = ?";
            
            int qtdPDV = 0;

            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

            	 stmt.setInt(1, matricula);

                ResultSet resultado = stmt.executeQuery();

                if (resultado.next()) {
                	qtdPDV = resultado.getInt(1);
                    return qtdPDV;
                } else {
                	JOptionPane.showMessageDialog(null, "Funcionário não encontrado", "error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return qtdPDV;
    }
        public String getNomeFuncionario(int matricula) {
            String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
            String usuario = dotenv.get("USER_BD");
            String senha = dotenv.get("PASSWORD_BD");

            String sql = "SELECT nome FROM funcionario WHERE matricula = ?";
            
            String nome = null;

            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

            	 stmt.setInt(1, matricula);

                ResultSet resultado = stmt.executeQuery();

                if (resultado.next()) {
                    nome = resultado.getString("nome");
                    return nome;
                } else {
                	System.err.print("Funcionário não encontrado");
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return nome;
    }
        
        public int getFilial(int matricula) {
            String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
            String usuario = dotenv.get("USER_BD");
            String senha = dotenv.get("PASSWORD_BD");
            
            String sql = "SELECT filial FROM funcionario WHERE matricula = ?";
            
            int filial = 0;

            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

            	 stmt.setInt(1, matricula);

                ResultSet resultado = stmt.executeQuery();

                if (resultado.next()) {
                	filial = resultado.getInt("filial");
                    return filial;
                } else {
                	System.err.println("Filial não localizada");
                	return -1;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return filial;
    }
        public int getNumberGerenciaPDV(int matricula) {
            String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
            String usuario = dotenv.get("USER_BD");
            String senha = dotenv.get("PASSWORD_BD");

            String sql = "SELECT matriculaGerencia FROM caixas_abertos WHERE matricula = ?";
            
            int matriculaGerencia = 0;

            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

            	 stmt.setInt(1, matricula);

                ResultSet resultado = stmt.executeQuery();

                if (resultado.next()) {
                	matriculaGerencia = resultado.getInt("matriculaGerencia");
                    return matriculaGerencia;
                } else {
                	JOptionPane.showMessageDialog(null, "Membro gerencial não localizado", "error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return matriculaGerencia;
    }
        public String getDataAbertudaPDV(int matricula) {
            String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
            String usuario = dotenv.get("USER_BD");
            String senha = dotenv.get("PASSWORD_BD");

            String sql = "SELECT data_abertura FROM caixas_abertos WHERE matricula = ?";
            
            String dataAbertura = null;

            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

            	 stmt.setInt(1, matricula);

                ResultSet resultado = stmt.executeQuery();

                if (resultado.next()) {
                	dataAbertura = resultado.getString("data_abertura");
                    return dataAbertura;
                } else {
                	//JOptionPane.showMessageDialog(null, "Data do PDV não localizado", "error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return dataAbertura;
    }  
        public double getmoneyPDV(int matricula) {
            String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
            String usuario = dotenv.get("USER_BD");
            String senha = dotenv.get("PASSWORD_BD");

            String sql = "SELECT qtd_dinheiro FROM caixas_abertos WHERE matricula = ?";
            
            double money = 0;

            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

            	 stmt.setInt(1, matricula);

                ResultSet resultado = stmt.executeQuery();

                if (resultado.next()) {
                	money = resultado.getDouble("qtd_dinheiro");
                    return money;
                } else {
                	//JOptionPane.showMessageDialog(null, "Data do PDV não localizado", "error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
            
            return money;
    }  
        public void setPDV(String nomeFuncionario, int filial, double money, int matricula, int matriculaGerencia, LocalDate dataAbertura) {
            String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
            String usuario = dotenv.get("USER_BD");
            String senha = dotenv.get("PASSWORD_BD");

            
            try (Connection conexao = DriverManager.getConnection(url, usuario, senha)) {

                int novoPDV = 1;

                String consultaHoje = "SELECT MAX(pdv) AS max_pdv FROM caixas_abertos WHERE DATE(data_abertura) = CURDATE()";

                try (PreparedStatement stmt = conexao.prepareStatement(consultaHoje);
                     ResultSet resultado = stmt.executeQuery()) {

                    if (resultado.next()) {
                        int maxPDV = resultado.getInt("max_pdv");
                        if (!resultado.wasNull()) {
                            novoPDV = maxPDV + 1;
                        }
                    }
                }

               
                String sql = "INSERT INTO caixas_abertos (funcionario, filial, qtd_dinheiro, pdv, matricula, ip, matriculaGerencia, data_abertura) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

                
                try {
                    InetAddress ip = InetAddress.getLocalHost();
                    ipPC = ip.getHostAddress();
                } catch (UnknownHostException e) {
                    System.err.println("Não foi possível obter o endereço IP.");
                    e.printStackTrace();
                }
                try (PreparedStatement stmtInsercao = conexao.prepareStatement(sql)) {
                	
                	
                	
                    stmtInsercao.setString(1, nomeFuncionario);
                    stmtInsercao.setInt(2, filial);
                    stmtInsercao.setDouble(3, money);
                    stmtInsercao.setInt(4, novoPDV);
                    stmtInsercao.setInt(5, matricula);
                    stmtInsercao.setString(6,  ipPC);
                    stmtInsercao.setInt(7, matriculaGerencia);
                    stmtInsercao.setDate(8, java.sql.Date.valueOf(dataAbertura));

                    int linhasAfetadas = stmtInsercao.executeUpdate();

                    if (linhasAfetadas > 0) {
                        System.out.print("Este PDV está registrado no Java&Café");
                    } else {
                    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    	LocalDateTime agora = LocalDateTime.now();
                        String data = agora.format(formatter);
                    	discord_erro_pdv discordErro = new discord_erro_pdv();
                    	discordErro.enviarEmbed("Conexão do PDV","o PDV não está sendo registrado no banco de dados", "valida_login() -> setPDV()", "Alta", data, "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
                        System.out.println("Falha ao inserir o registro.");
                    }
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
      }
        public boolean deletarPDV(int matricula) {
            String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
            String usuario = dotenv.get("USER_BD");
            String senha = dotenv.get("PASSWORD_BD");

            String sql = "DELETE FROM caixas_abertos WHERE matricula = ?";

            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

                stmt.setInt(1, matricula);

                int linhasAfetadas = stmt.executeUpdate();

                return linhasAfetadas > 0;

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }
        public boolean deletarPDVData(int matricula, String data) {
            String url = "jdbc:mysql://"+dotenv.get("HOST_BD")+":3306/"+dotenv.get("BACODEDADOS_BD");
            String usuario = dotenv.get("USER_BD");
            String senha = dotenv.get("PASSWORD_BD");

            String sql = "DELETE FROM caixas_abertos WHERE matricula = ? and data_abertura = ?";

            try (Connection conexao = DriverManager.getConnection(url, usuario, senha);
                 PreparedStatement stmt = conexao.prepareStatement(sql)) {

                stmt.setInt(1, matricula);
                stmt.setString(2, data);

                int linhasAfetadas = stmt.executeUpdate();

                return linhasAfetadas > 0;

            } catch (SQLException e) {
                e.printStackTrace();
            }

            return false;
        }
}
