package controladores;

public class controle_cliente {
	
	private static int cpfCliente = 0;
	private static String nomeCliente = "Não identificado";
	private static boolean isPontos;
	private static boolean memberFidelidade = false, clienteSimples = false;
	
	public controle_cliente() {
		
	}
	public void setNumberCpf(int number) {
		this.cpfCliente = number;
	}
	public int getNumberCPF() {
		return this.cpfCliente;
	}
	public void setNomeCliente(String nome) {
		this.nomeCliente = nome;
	}
	public String getNomeCliente() {
		return this.nomeCliente;
	}
	public void setUserpoints(boolean status) {
		this.isPontos = status;
	}
	public boolean getUserPoins() {
		return this.isPontos;
	}
	public void setClienteS(boolean status) {
		this.clienteSimples = status;
	}
	public boolean getClienteS() {
		return this.clienteSimples;
	}
	public void setMember(boolean status) {
		this.memberFidelidade = status;
	}
	public boolean getMember() {
		return this.memberFidelidade;
	}
}
