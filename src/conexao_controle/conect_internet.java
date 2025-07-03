package conexao_controle;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class conect_internet {
	private static boolean verificado = false;
	public boolean temConexao() {
		try {
	        URL url = new URL("https://discord.com");
	        HttpURLConnection conexao = (HttpURLConnection) url.openConnection();
	        conexao.setRequestMethod("HEAD");
	        conexao.setConnectTimeout(3000); 
	        conexao.setReadTimeout(3000);
	        int codigoResposta = conexao.getResponseCode();
	        if(verificado == false) {
	        System.out.println("Sistema conectado a internet");
	        verificado = true;
	        }
	        return (codigoResposta >= 200 && codigoResposta < 400);
	    } catch (IOException e) {
	    	System.out.println("Sistema sem conexão com a internet");
	        return false; 
	    	}
		}
}
