package inteface;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.swing.*;                
import javax.swing.text.DefaultFormatter;

import ODS.CalcularValor;
import ODS.RegistrarDonate;

import java.text.NumberFormat;     
import java.text.ParseException;     
import java.util.Locale;             


import cliente_banco.verificar_cliente;
import conect_banco.valida_login;
import conexao_controle.discord_entrada_caixa;
import controladores.FechamentoCaixa;
import controladores.controlador_operador;
import controladores.controle_cliente;
import controle_estoque.Atualizar_estoque;
import io.github.cdimascio.dotenv.Dotenv;
import produto.Produtos;
import seguranca.ControleDiscordEstoque;
import status_pagamento.EnvioDiscordPagamento;
import status_pagamento.PedidosDiscord;
import status_pagamento.status;

public class FormaPagto extends JFrame {
    private boolean usarPontos = false;
    private Vendas pnVenda;
    private List<Object[][]> dados;
    private double total;
    private double pontos;
    private double desconto;
    private double recebido;
    private double money;
    private double troco;
    private double caridade;
    private double dinheiro = 0;
    private double credito = 0;
    private double debito = 0;
    private double pix = 0;
    private int bonusPoint = 300;
    public JLabel lblRecebido;
    public JLabel lblTotal;
    boolean pagamentoConfirma = false;
    private Dotenv dotenv = Dotenv.configure()
            .directory("./src") 
            .filename(".env")
            .load();
    private controlador_operador op = new controlador_operador();
    private controle_cliente cliente = new controle_cliente();
    private EnvioDiscordPagamento auditoria = new EnvioDiscordPagamento();
    private verificar_cliente bancoCl = new verificar_cliente();
    private valida_login bancoFun = new valida_login();
    public status addPontos = new status();
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public LocalDateTime agora = LocalDateTime.now();
    public String data = agora.format(formatter);
    private Vendas vender = new Vendas();


	
    public FormaPagto(double valor, double pontos, double desconto, List<Object[][]> dados) {
        this.dados = dados;
        this.total = valor;
        this.pontos = pontos;
        this.desconto = desconto;
        
        
        
        vender.setVisible(false);
        vender.dispose();
        
        // Estilo moderno
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 13));

        // Janela principal
        setTitle("Java&Café");
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        getContentPane().setBackground(new Color(255, 255, 255));
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon_bar.png")));
        setLayout(new BorderLayout());

        // -------- PAINEL CENTRAL --------
        JPanel painelCentro = new JPanel();
        painelCentro.setLayout(new BoxLayout(painelCentro, BoxLayout.X_AXIS));
        painelCentro.setBackground(new Color(215, 215, 215));
        painelCentro.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Selecione uma Forma de Pagamento",
            0, 0,
            new Font("SansSerif", Font.BOLD, 20),
            Color.BLACK
        ));

        // -------- PAINEL DE FORMAS DE PAGTO --------
        JPanel painelFormas = new JPanel();
        painelFormas.setLayout(new BoxLayout(painelFormas, BoxLayout.Y_AXIS));
        painelFormas.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelFormas.setBackground(new Color(215, 215, 215));
        painelFormas.setPreferredSize(new Dimension(350, 700));
        painelFormas.setMaximumSize(new Dimension(350, 700));
        painelFormas.setBorder(new EmptyBorder(20, 20, 20, 20));

        JButton btnCredito = new Estilizar().estilizarBotao("Crédito", 300, 160, 40);
        JButton btnDebito = new Estilizar().estilizarBotao("Débito", 300, 160, 40);
        JButton btnPix = new Estilizar().estilizarBotao("Pix", 300, 160, 40);
        JButton btnDinheiro = new Estilizar().estilizarBotao("Dinheiro", 300, 160, 40);

        painelFormas.add(btnCredito);
        painelFormas.add(Box.createVerticalStrut(30));
        painelFormas.add(btnDebito);
        painelFormas.add(Box.createVerticalStrut(30));
        painelFormas.add(btnPix);
        painelFormas.add(Box.createVerticalStrut(30));
        painelFormas.add(btnDinheiro);

        // -------- PAINEL DIREITO --------
        JPanel painelDireito = new JPanel();
        painelDireito.setLayout(new BoxLayout(painelDireito, BoxLayout.Y_AXIS));
        painelDireito.setAlignmentX(Component.RIGHT_ALIGNMENT);
        painelDireito.setBackground(new Color(215, 215, 215));
        painelDireito.setPreferredSize(new Dimension(1000, 700));
        painelDireito.setMaximumSize(new Dimension(1000, 700));
        painelDireito.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(Color.GRAY),
            "Resumo do Pedido",
            0, 0,
            new Font("SansSerif", Font.BOLD, 20),
            Color.BLACK
        ));

        // Painel de valores do pedido
        JPanel painelValores = new JPanel();
        painelValores.setLayout(new BoxLayout(painelValores, BoxLayout.Y_AXIS));
        painelValores.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelValores.setBackground(new Color(215, 215, 215));
        painelValores.setPreferredSize(new Dimension(1000, 500));
        painelValores.setMaximumSize(new Dimension(1000, 500));
        painelValores.setBorder(new EmptyBorder(20, 20, 20, 20));

        ArrayList<Object[][]> dadosImportados = importarDados(this.dados);
        JTable tabela = criarTabela(dadosImportados);
        tabela.getColumnModel().getColumn(0).setPreferredWidth(150);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(615);
        painelValores.add(new JScrollPane(tabela), BorderLayout.CENTER);

        // Painel de cálculo do total
        JPanel painelTotal = new JPanel();
        painelTotal.setLayout(new BoxLayout(painelTotal, BoxLayout.Y_AXIS));
        painelTotal.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelTotal.setBackground(new Color(235, 235, 235));
        painelTotal.setPreferredSize(new Dimension(950, 150));
        painelTotal.setMaximumSize(new Dimension(950, 150));
        painelTotal.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        painelTotal.add(Box.createHorizontalStrut(20));
        painelTotal.add(Box.createVerticalStrut(20));
        JLabel lblValor = new JLabel("Valor: R$ " + String.format("%.2f", valor));
        JLabel lblDesconto = new JLabel("Desconto: R$ -" + String.format("%.2f", desconto));
        JLabel lblPontos;
        JLabel lblLinha = new JLabel("________________________");
        lblTotal = new JLabel("Total: R$ " + String.format("%.2f", (total - desconto - (pontos > 0 ? pontos : 0))));
        lblRecebido = new JLabel("Valor recebido: R$ " + String.format("%.2f", (recebido)));
        JLabel lblTroco = new JLabel("Troco: R$ " + String.format("%.2f", (troco)));
        lblTotal.setFont(new Font("SansSerif", Font.BOLD, 20));
        painelTotal.add(lblValor);
        painelTotal.add(lblDesconto);
        if (pontos > 0){
            if (pontos > valor){
                lblPontos = new JLabel("Pontos: R$ -" + String.format("%.2f", (valor - desconto)) + " (" + pontos + ")");
            }else{
                lblPontos = new JLabel("Pontos: R$ -" + String.format("%.2f", pontos));
            }
            painelTotal.add(lblPontos);
        }
        painelTotal.add(lblLinha);
        painelTotal.add(lblTotal);
        painelTotal.add(lblRecebido);
        painelTotal.add(lblTroco);
        painelTotal.add(Box.createVerticalStrut(20));

        // Painel de botões
        JPanel painelBotoes = new JPanel();
        painelBotoes.setLayout(new BoxLayout(painelBotoes, BoxLayout.X_AXIS));
        painelBotoes.setAlignmentX(Component.CENTER_ALIGNMENT);
        painelBotoes.setBackground(new Color(215, 215, 215));
        painelBotoes.setBorder(new EmptyBorder(20, 20, 20, 20));
        JButton btnConfirmar = new Estilizar().estilizarBotao("Confirmar", 150, 50, 20);
        JButton btnCancelar = new Estilizar().estilizarBotao("Cancelar", 150, 50, 20);
        painelBotoes.add(btnConfirmar);
        painelBotoes.add(Box.createHorizontalStrut(20));
        painelBotoes.add(btnCancelar);

        painelDireito.add(painelValores);
        painelDireito.add(painelTotal);
        painelDireito.add(painelBotoes);

        // Adicionando os painéis à tela principal
        painelCentro.add(painelFormas);
        painelCentro.add(painelDireito);
        getContentPane().add(painelCentro, BorderLayout.CENTER);
        
        
        btnCredito.addActionListener(e -> processarPagamento(Double.MAX_VALUE, "credito"));
        btnDebito.addActionListener(e -> processarPagamento(Double.MAX_VALUE, "debito"));
        btnPix.addActionListener(e -> processarPagamento(Double.MAX_VALUE, "pix"));
        
        btnDinheiro.addActionListener(e -> {
            NumberFormat format = NumberFormat.getNumberInstance(new Locale("pt", "BR"));
            format.setMinimumFractionDigits(2);
            format.setMaximumFractionDigits(2);

            JFormattedTextField campoDinheiro = new JFormattedTextField(format);
            campoDinheiro.setColumns(10);
            ((DefaultFormatter) campoDinheiro.getFormatter()).setAllowsInvalid(false);
            campoDinheiro.setFocusLostBehavior(JFormattedTextField.COMMIT_OR_REVERT);
            
            if(pagamentoConfirma != true) {
            int option = JOptionPane.showConfirmDialog(null, campoDinheiro, "Qual foi o valor recebido?", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    campoDinheiro.commitEdit();
                    Number valorNumber = (Number) campoDinheiro.getValue();
                    if (valorNumber == null) throw new ParseException("Valor nulo", 0);

                    money = valorNumber.doubleValue();
                    double totalRestante = getTotalRestante();

                    if (money <= 0) {
                        JOptionPane.showMessageDialog(null, "Valor inválido!", "Erro", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    if (money >= totalRestante) {
                        troco = money - totalRestante;
                        recebido += totalRestante;
                        op.pagamentoDinheiro(money);
                        dinheiro = money;
                        pagamentoConfirma = true;
                        lblTotal.setText("Troco: R$ " + String.format("%.2f", troco));
                        lblTroco.setVisible(false);
                    } else {
                        recebido += money;
                        op.pagamentoDinheiro(money);
                        dinheiro = money;
                        lblTotal.setText("Restante: R$ " + String.format("%.2f", getTotalRestante()));
                    }
                

                    lblRecebido.setText("Valor recebido: R$ " + String.format("%.2f", recebido));
                

                } catch (ParseException ex) {
                    JOptionPane.showMessageDialog(null, "Digite um valor válido.", "Erro", JOptionPane.ERROR_MESSAGE);
                	}
            	}
           }
        });
        

        //Função botão confirmar
        btnConfirmar.addActionListener(e -> {
        	if(pagamentoConfirma == true) {
        		int metodos = 0;
        		if(credito > 0) metodos++;
        		if(debito > 0) metodos++;
        		if(dinheiro > 0) metodos++;
        		if(pix > 0) metodos++;

        		String operadorInfo = "Operador: " + op.getNomeOperador() + " Matrícula: " + op.getNumberOperador();
        		String canal = urlDiscord(op.getFilial());

        		if (metodos == 1) {
        		    if (credito > 0)
        		        auditoria.enviarEmbed(cliente.getNomeCliente(), credito, "Crédito", data, operadorInfo, canal);
        		    else if (debito > 0)
        		        auditoria.enviarEmbed(cliente.getNomeCliente(), debito, "Débito", data, operadorInfo, canal);
        		    else if (dinheiro > 0)
        		        auditoria.enviarEmbed(cliente.getNomeCliente(), dinheiro, "Dinheiro", data, operadorInfo, canal);
        		    else if (pix > 0)
        		        auditoria.enviarEmbed(cliente.getNomeCliente(), pix, "PIX", data, operadorInfo, canal);
        		} else {
        		    // Montar o texto da forma de pagamento diretamente
        		    StringBuilder formas = new StringBuilder("Pagamento múltiplo:\n");
        		    if (dinheiro > 0)
        		        formas.append("• Dinheiro: R$ ").append(String.format("%.2f", dinheiro)).append("\n");
        		    if (credito > 0)
        		        formas.append("• Crédito: R$ ").append(String.format("%.2f", credito)).append("\n");
        		    if (debito > 0)
        		        formas.append("• Débito: R$ ").append(String.format("%.2f", debito)).append("\n");
        		    if (pix > 0)
        		        formas.append("• PIX: R$ ").append(String.format("%.2f", pix)).append("\n");

        		    double totalPago = dinheiro + credito + debito + pix;

        		    auditoria.enviarEmbed(cliente.getNomeCliente(), totalPago, formas.toString(), data, operadorInfo, canal);
        		}
        		if (troco > 0 && troco % 1 != 0) {
        		    int arredondar = JOptionPane.showConfirmDialog(null, "Gostaria de doar o troco?", "Troco", JOptionPane.YES_NO_OPTION);
        		    if (arredondar == JOptionPane.YES_OPTION) {
        		        BigDecimal trocoBD = BigDecimal.valueOf(troco).setScale(2, RoundingMode.HALF_UP);
        		        BigDecimal inteiroInferior = trocoBD.setScale(0, RoundingMode.DOWN);
        		        BigDecimal caridadeBD = trocoBD.subtract(inteiroInferior);

        		        RegistrarDonate donate = new RegistrarDonate();
        		        LocalDate dataAtual = LocalDate.now();
        		        donate.registrarDonate(caridadeBD.doubleValue(), dataAtual, cliente.getNomeCliente());

        		        // Formatação do valor como moedaa
        		        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
        		        String trocoFormatado = formatoMoeda.format(inteiroInferior);
        		        
        		        JOptionPane.showMessageDialog(null, "O troco do cliente foi atualizado. Devolva:" + trocoFormatado);
        		        lblTroco.setText("Troco: R$ " + String.format("%.2f", inteiroInferior.doubleValue()));
        		    }
        		}
        		
        		if(cliente.getClienteS() == true) {
        			addPontos.pagamentoOkay(cliente.getNumberCPF(), bonusPoint);
        		}else if (cliente.getMember() == true) {
        			addPontos.pagamentoOkay(cliente.getNumberCPF(), bonusPoint*2);
        		}
            	PedidosDiscord pedido = new PedidosDiscord();
            	pedido.enviarPedido(cliente.getNomeCliente(), dados, dotenv.get("WEBHOOK_PEDIDOS"));
            	controle_cliente cliente = new controle_cliente();
            	cliente.setNumberCpf(0);
            	cliente.setClienteS(false);
            	cliente.setMember(false);
            	Vendas pgVenda = new Vendas();
            	pgVenda.setVisible(true);
            	dispose();
            }
        });
        
        //Função botão cancelar
        btnCancelar.addActionListener(e -> {
            	for (Object[][] item : dados) {
            		Produtos produtoTabela = Produtos.fromArray(item);
            	    String produto = produtoTabela.getNome();
            	    int qtd = produtoTabela.getQuantidade();
            	    Atualizar_estoque atualizaEstoque = new Atualizar_estoque();
                	atualizaEstoque.addEstoque(produto, qtd);
            }
            	controle_cliente cliente = new controle_cliente();
            	cliente.setNumberCpf(0);
            	cliente.setClienteS(false);
            	cliente.setMember(false);
            	Vendas pgVenda = new Vendas();
            	pgVenda.setVisible(true);
            	dispose();
        });   
        
       
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	solicitarSenhaParaFechar();
            }
        });
    }
    

    
    private void solicitarSenhaParaFechar() {
        valida_login validaBanco = new valida_login();
        controlador_operador operador = new controlador_operador();
        String venda = null;
        while (true) {
            String input = JOptionPane.showInputDialog("Para executar essa ação, informe a senha");
            if (input == null) { 
                return;
            }
                        
            try {
                int senha = Integer.parseInt(input);
                int resultado = validaBanco.fecharPDV(senha);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime agora = LocalDateTime.now();
                if (resultado == 1) {
                	JOptionPane.showMessageDialog(null, "O caixa será fechado automaticamente e auditado, caso necessário.", "Fechamento de caixa", JOptionPane.WARNING_MESSAGE);
                	FechamentoCaixa fechamentoCaixa = new FechamentoCaixa();
            		String discord = urlDiscord(op.getFilial());
            		CalcularValor donate = new CalcularValor();
                	ControleDiscordEstoque estoqueControle = new ControleDiscordEstoque();
                	fechamentoCaixa.enviarFechamentoCaixa(op.getNomeOperador(), op.fechamentoPIX(), op.fechamentoCredit(), op.fechamentoDebit(), op.fechamentoDinheiro(), data, "Segurança PDVs", discord);
                	JOptionPane.showMessageDialog(null,"Caixa fechado! Deixe apenas R$100,00 no caixa para o troco.","Confirmação fechamento",JOptionPane.INFORMATION_MESSAGE);
                	estoqueControle.enviarRelatorio(op.getFilial());
                	donate.calcularEDisparar(urlDiscord(op.getFilial()));
                    discord_entrada_caixa.enviarEmbed("Sistema fechado", "Uma filial fechou o PDV (Venda Cancelada)", "**"+validaBanco.getNomeGerencia(senha)+"**", agora.format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ACOES"));
                    if(operador.getNomeOperador() != null) {
                        validaBanco.deletarPDV(operador.getNumberOperador());
                        for (Object[][] item : dados) {
                    		Produtos produtoTabela = Produtos.fromArray(item);
                    	    String produto = produtoTabela.getNome();
                    	    int qtd = produtoTabela.getQuantidade();
                    	    Atualizar_estoque atualizaEstoque = new Atualizar_estoque();
                        	atualizaEstoque.addEstoque(produto, qtd);
                        }
                    }
                    System.exit(0);
                } else {
                    discord_entrada_caixa.enviarEmbed("Tentativa de fechar aplicação", "indetificamos que uma filial tentou fechar o PDV", "foi identificado uma senha: **"+senha+"**", agora.format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ACOES"));
                    JOptionPane.showMessageDialog(null, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(null, "Digite um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            }            
        }
    }
    

    private void processarPagamento(double valorMetodo, String metodo) {
        if (pagamentoConfirma) return;

        double restante = getTotalRestante();
        if (restante <= 0) {
            JOptionPane.showMessageDialog(null, "O valor já foi totalmente pago!", "Aviso", JOptionPane.WARNING_MESSAGE);
            return;
        }

        double valorUsado = Math.min(restante, valorMetodo);
        recebido += valorUsado;

        switch (metodo) {
            case "credito": op.pagamentoCredit(valorUsado); credito = valorUsado; break;
            case "debito": op.pagamentoDebito(valorUsado); debito = valorUsado; break;
            case "pix": op.pagamentoPIX(valorUsado); pix = valorUsado; break;
        }

        lblRecebido.setText("Valor recebido: R$ " + String.format("%.2f", recebido));
        lblTotal.setText(restante > valorUsado ?
            "Restante: R$ " + String.format("%.2f", restante - valorUsado) :
            "Pagamento concluído!");

        if (recebido >= calcularTotalComDescontos()) {
            pagamentoConfirma = true;
        }
    }

    
    private String urlDiscord(int filial) {
    	String urlWebhook = switch (filial) {
        case 1001 -> dotenv.get("WEBHOOK_PAULISTA");
        case 1002 -> dotenv.get("WEBHOOK_JDANGELA");
        case 1003 -> dotenv.get("WEBHOOK_LIBERDADE");
        default -> "";
    	};
    	return urlWebhook;
    }
    
    private double getTotalRestante() {
        return Math.max(calcularTotalComDescontos() - recebido, 0);
    }

    
    private double calcularTotalComDescontos() {
        double totalProu = total;

        if (cliente.getUserPoins()) {
        	totalProu -= pontos;
        }

        totalProu -= desconto;

        return Math.max(totalProu, 0);
    }

    
    private void descontarPontos(double pontos) {
    	controle_cliente cliente = new controle_cliente();
        if(cliente.getUserPoins() == true) {
        	pontos = pontos*100;
        	JOptionPane.showMessageDialog(null, "Vc usou"+pontos);
        }
    }
    
    private ArrayList<Object[][]> importarDados(List<Object[][]> tabelaPedidos) {
        ArrayList<Object[][]> dados = new ArrayList<>();

        for (Object[][] item : tabelaPedidos) {
            Produtos produtoTabela = Produtos.fromArray(item);
            String produto = produtoTabela.getNome();
            int qtd = produtoTabela.getQuantidade();
            double preco = produtoTabela.getPreco();
            String detalhes = produtoTabela.getDetalhes();

            Object[][] tabelas = {{produto, qtd, preco, detalhes}};
            dados.add(tabelas);
        }

        return dados;
    }

    private JTable criarTabela(ArrayList<Object[][]> dados) {
        String[] colunas = {"Item", "Qtd", "Preço (R$)", "Detalhes"};
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        for (Object[][] item : dados) {
            modelo.addRow(item[0]);
        }
        JTable tabela = new JTable(modelo) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        return tabela;
    }
}
