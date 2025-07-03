package controladores;

public class controlador_login_system {

	private int matricula;
	private int senha;

	//funcao vazia?
	public controlador_login_system() {	

	}
	public void setNumberMatricula(int matricula) {
		this.matricula = matricula;
	}
	public int getMatricula() {
		return this.matricula;
	}
	public void setSenha(int senha) {
		this.senha = senha;
	}
	public int getSenha() {
		return this.senha;
	}
}
