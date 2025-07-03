 package controladores;

public class interface_controller {
	private static boolean carregamento = true;
	private static boolean login = false;
	private static boolean venda = false;
	
	
	public interface_controller() {
		
	}
	public void setCarregamento(boolean status) {
		this.carregamento = status;
	}
	public boolean getStatusCarregamento() {
		return this.carregamento;
	}
	public void setLogin(boolean status) {
		this.login = status;
	}
	public boolean getStatusLogin() {
		return this.login;
	}
	
	public void setVenda(boolean status) {
		this.venda = status;
	}
	public boolean getStatusVenda() {
		return this.venda;
	}
}
