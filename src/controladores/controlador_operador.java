package controladores;

import javax.swing.JOptionPane;

public class controlador_operador {

	private static int opN = 0;
	private static int filial = 0;
	private static String nome = null;
	private static double money;
	private static double credit;
	private static double debit;
	private static double pix;
	
	public controlador_operador() {
		
	}
	public void setNumberOperador(int number) {
		this.opN = number;
	}
	public int getNumberOperador() {
		return this.opN;
	}
	public void setFilial(int filial) {
		this.filial = filial;
	}
	public int getFilial() {
		return this.filial;
	}
	public void setNomeOperador(String nome) {
		this.nome = nome;
	}
	public String getNomeOperador() {
		return this.nome;
	}
	public void pagamentoDebito(double d) {
		this.debit = this.debit + d;
	}
	public void pagamentoPIX(double p) {
		this.pix = this.pix + p;
	}
	public void pagamentoCredit(double c) {
		this.credit = this.credit + c;
	}
	public void pagamentoDinheiro(double dinheiro) {
		this.money = this.money + dinheiro;
	}
	public double fechamentoDinheiro() {
		return this.money;
	}
	public double fechamentoPIX() {
		return this.pix;
	}
	public double fechamentoCredit() {
		return this.credit;
	}
	public double fechamentoDebit() {
		return this.debit;
	}	
}
