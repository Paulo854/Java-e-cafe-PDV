package inteface;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import cliente_banco.verificar_cliente;
import controladores.controle_cliente;
import io.github.cdimascio.dotenv.Dotenv;
import produto.Produtos;
import status_pagamento.PedidosDiscord;

import java.util.List;
import java.util.ArrayList;

public class Pagto extends JDialog {
    private boolean usarPontos = false;
    private Vendas pnVenda;
    private List<Object[][]> dados;
    private double pontosVerifica;
    public  Dotenv dotenv = Dotenv.configure()
            .directory("./src") 
            .filename(".env")
            .load();

    public Pagto(Window parent, double valor, int cpf, double pontos, double desconto, Vendas pnVenda,  List<Object[][]> dados) {
        super(parent);
        this.pnVenda = pnVenda;
        this.dados = dados;

        // Configurações básicas da janela
        setSize(400, 280);
        setLocationRelativeTo(parent);
        setModal(true);
        setAlwaysOnTop(true);
        setUndecorated(true);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        getContentPane().setLayout(new BorderLayout()); // ESSENCIAL para funcionar com BorderLayout
        getContentPane().setBackground(new Color(215, 215, 215));
        getRootPane().setBorder(BorderFactory.createEmptyBorder()); // Remove borda branca

        // Painel principal
        JPanel painelCentro = new JPanel();
        painelCentro.setLayout(new BoxLayout(painelCentro, BoxLayout.Y_AXIS));
        painelCentro.setBorder(new EmptyBorder(20, 20, 20, 20));
        painelCentro.setBackground(new Color(215, 215, 215)); // Mesma cor do fundo da janela

        // Painel com título "Usar Pontos?"
        JPanel painelPontos = new JPanel();
        painelPontos.setLayout(new BoxLayout(painelPontos, BoxLayout.Y_AXIS));
        painelPontos.setBackground(new Color(215, 215, 215));
        painelPontos.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Usar Pontos?",
            0, 0,
            new Font("SansSerif", Font.BOLD, 20),
            Color.BLACK
        ));

        // Painel de valores
        JPanel painelValores = new JPanel();
        painelValores.setLayout(new BoxLayout(painelValores, BoxLayout.Y_AXIS));
        painelValores.setBackground(new Color(245, 245, 245));
        painelValores.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Soma dos valores",
            0, 0,
            new Font("SansSerif", Font.PLAIN, 10),
            Color.GRAY
        ));

        JLabel lblValor = new JLabel("Valor: R$ " + String.format("%.2f", valor));
        JLabel lblDesconto = new JLabel("Desconto: R$ -" + String.format("%.2f", desconto));
        JLabel lblPontos;
        if (pontos > valor){
            lblPontos = new JLabel("Pontos: R$ -" + String.format("%.2f", verificaTotal(valor, pontos, desconto)) + " (" + pontos + ")");
        }else{
            lblPontos = new JLabel("Pontos: R$ -" + String.format("%.2f", pontos));
        }
        JLabel lblLinha = new JLabel("________________________");
        JLabel lblTotal = new JLabel("Total: R$ " + String.format("%.2f", (valor - desconto - verificaTotal(valor, pontos, desconto))));
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 20));

        painelValores.add(lblValor);
        painelValores.add(lblDesconto);
        painelValores.add(lblPontos);
        painelValores.add(lblLinha);
        painelValores.add(lblTotal);

        // Painel com botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new FlowLayout(FlowLayout.CENTER));
        painelBotoes.setBackground(new Color(215, 215, 215));

        JButton btnSim = new Estilizar().estilizarBotao("Sim");
        JButton btnNao = new Estilizar().estilizarBotao("Não");

        btnSim.setFocusable(false);
        btnNao.setFocusable(false);

        btnSim.addActionListener(e -> {
            usarPontos = true;
            controle_cliente cliente = new controle_cliente();
    	    Vendas pgVendas = new Vendas();
            pgVendas.setVisible(true);
            cliente.setUserpoints(usarPontos);
            if(verificaTotal(valor, pontos, desconto) == valor - desconto) {
            	for (Object[][] item : dados) {
            		Produtos produtoTabela = Produtos.fromArray(item);
            	    String produto = produtoTabela.getNome();
            	    int qtd = produtoTabela.getQuantidade();
            	}
            	if (pnVenda != null) pnVenda.dispose();
        	    pnVenda.setVisible(false);
        	    cliente.setNumberCpf(0);
        	    cliente.setClienteS(false);
        	    cliente.setMember(false);
        	    verificar_cliente bancoCl = new verificar_cliente();
        	    PedidosDiscord pedido = new PedidosDiscord();
        	    pedido.enviarPedido(bancoCl.nomeCliente(cpf), dados, dotenv.get("WEBHOOK_PEDIDOS"));
        	    int pontoBaixo = (int) Math.floor(verificaTotal(valor, pontos, desconto)*100);
        	    bancoCl.usarPontos(cpf, pontoBaixo);
        	    JOptionPane.showMessageDialog(null, "Compra concluída! Muito obrigado!");
                dispose();
            } else {
            	//double total = valor - verificaTotal(valor, pontos, desconto);
            	novaTela(valor, verificaTotal(valor, pontos, desconto), desconto, dados);
            	verificar_cliente bancoCl = new verificar_cliente();
        	    int pontoBaixo = (int) Math.floor(verificaTotal(valor, pontos, desconto)*100);
        	    bancoCl.usarPontos(cpf, pontoBaixo);
            	if (pnVenda != null) pnVenda.dispose();
        	    pnVenda.setVisible(false);
                dispose();
            }
            
        });

        btnNao.addActionListener(e -> {
            usarPontos = false;
            novaTela(valor, 0, desconto, dados);
            controle_cliente cliente = new controle_cliente();
            cliente.setUserpoints(usarPontos);
            if (pnVenda != null) pnVenda.dispose();
            if(pontos > 90.0) {pontosVerifica = 90.0;}
            dispose();
        });

        painelBotoes.add(btnSim);
        painelBotoes.add(btnNao);

        // Montagem final dos painéis
        painelPontos.add(painelValores);
        painelPontos.add(Box.createVerticalStrut(10));
        painelPontos.add(painelBotoes);

        painelCentro.add(painelPontos);
        getContentPane().add(painelCentro, BorderLayout.CENTER); // AQUI é onde o painel realmente aparece
        
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                JOptionPane.showMessageDialog(Pagto.this,"Há um pedido em andamento...");
            }
        });
    }
    
    private double verificaTotal(double total, double pontos, double desconto) {
    	double totalDesconto = total - desconto;
    	if(totalDesconto < pontos) {
    		pontosVerifica = totalDesconto;
    		return pontosVerifica;
    	}else if(pontos > 90.0){
    		pontosVerifica = 90.0;
    		return pontosVerifica;
    	}else {
    		return pontos;
    	}
    }

    public boolean isUsarPontos() {
        return usarPontos;
    }
    

    public void novaTela(double valor, double pontos, double desconto, List<Object[][]> dados) {
        FormaPagto Pagto2 = new FormaPagto(valor, pontos, desconto, dados);
        Pagto2.setVisible(true);
    }
}
