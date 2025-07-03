package inteface;

import conect_banco.valida_login;
import conexao_controle.discord_entrada_caixa;
import conexao_controle.discord_erro_pdv;
import controladores.FechamentoCaixa;
import controladores.controlador_operador;
import controladores.controle_cliente;
import controladores.interface_controller;
import controle_estoque.Atualizar_estoque;
import controle_estoque.Verificar_estoque;
import controle_estoque.verificar_estoque_btn;
import io.github.cdimascio.dotenv.Dotenv;
import produto.Produtos;
import seguranca.ControleDiscordEstoque;
import seguranca.pdv_conect;
import seguranca.protecaoBanco;

import java.util.concurrent.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.MaskFormatter;

import ODS.CalcularValor;

import java.text.ParseException;

import cliente_banco.cliente_pontos;
import cliente_banco.verificar_cliente;

public class Vendas extends JFrame {

    // Tabela de pedidos
	private static Timer timerConect = null;
    public JLabel detLabel;
	public int resultConect;
	public JButton botaoIdCliente;
    public JTable tabela;
	public static boolean identifica = false;
	String produto;
    String detalhesAnt;
    double preco;
    static double desconto;
    double total = 0;
    int qtdAnt;
    int qtdBanco;
    int cpf;
    public JTextField qtdField;
    private double pontos;
    boolean editandoPdt = false;
    private boolean escolhaCliente = false;
    public boolean telaPontos = false;
    public static int btnEdit = 0;
    String[] colunas = {"Item", "Qtd", "Preço", "Detalhes", "E.", "R.", "L."};
    ArrayList<Object[][]> dados = new ArrayList<>();
    public Dotenv dotenv = Dotenv.configure()
            .directory("./src") 
            .filename(".env")
            .load();
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public LocalDateTime agora = LocalDateTime.now();
    public String data = agora.format(formatter);
    private controlador_operador op = new controlador_operador();
    public valida_login validaBanco = new valida_login();
    public protecaoBanco controle = new protecaoBanco();
    public controle_cliente cliente = new controle_cliente();
    
    public Vendas() {
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
        getContentPane().setBackground(new Color(245, 245, 245)); // Fundo cinza-claro
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon_bar.png"))); // chama imagem para aparecer na barra de tarefas

        setLayout(new BorderLayout());

        // -------- PAINEL CENTRAL --------
        JPanel painelCentro = new JPanel(new GridLayout(1, 2));
        painelCentro.setBorder(new EmptyBorder(10, 10, 10, 10));
        painelCentro.setBackground(new Color(245, 245, 245));

        // -------- PAINEL DIREITO (Pedidos + Total) --------
        JPanel painelDireito = new JPanel(new BorderLayout(10, 10));
        painelDireito.setBackground(new Color(245, 245, 245));

        // Tabela de pedidos
        Object[][] modelo = dados.toArray(Object[][][]::new);
        estilizarTabela(new JTable(modelo, colunas));
        painelDireito.add(new JScrollPane(new JTable(modelo, colunas)), BorderLayout.CENTER);

        // Total
        JPanel painelTotal = new JPanel(new BorderLayout());
        painelTotal.setBorder(BorderFactory.createTitledBorder("Resumo"));
        painelTotal.setBackground(new Color(245, 245, 245));

        JLabel totalLabel = new JLabel("Total: R$ 0,00");
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        JButton removerBtn = estilizarBotao("Limpar Tudo");
        removerBtn.setBackground(new Color(255, 69, 0)); // Vermelho

        painelTotal.add(totalLabel, BorderLayout.WEST);
        painelTotal.add(removerBtn, BorderLayout.EAST);

        painelDireito.add(painelTotal, BorderLayout.SOUTH);

        // -------- PAINEL ESQUERDO (Cardápio) --------
        JPanel painelEsquerdo = new JPanel(new BorderLayout(10, 10));
        painelEsquerdo.setBackground(new Color(245, 245, 245));

     // tabela de produtos
        String[][] pdtDados = {
            {"Café Preto", "2.95"},
            {"Café com Leite", "3.85"},
            {"Cappuccino Simples", "7.90"},
            {"Chocolate Quente", "10.99"},
            {"Chá Quente", "5.69"},
            {"Pão de Queijo", "10.00"},
            {"Bolo do Dia", "15.00"},
            {"Torrada com Recheio", "25.00"},
            {"Sanduíche Natural", "26.50"},
            {"Biscoito Caseiro", "4.50"}
        };

        // Cardápio
        JPanel painelCardapio = new JPanel(new GridLayout(10, 1, 5, 5));
        painelCardapio.setBorder(BorderFactory.createTitledBorder("Cardápio"));
        painelCardapio.setBackground(new Color(245, 245, 245));
        // Criar os botões de cada item do cardápio
        List<JButton> botoesProdutos = new ArrayList<>();

        for (int i = 1; i <= pdtDados.length; i++) {
            int index = i - 1;
            JButton botao = new JButton(pdtDados[index][0] + " - " + pdtDados[index][1]);
            botao.setFont(new Font("SansSerif", Font.PLAIN, 14));
            botao.setBackground(new Color(190, 230, 240));
            botao.setFocusPainted(false);
            botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            botao.addActionListener(e -> {
                if (botao.isEnabled()) {
                    produto = pdtDados[index][0];
                    preco = Double.parseDouble(pdtDados[index][1]);
                    editandoPdt = false;
                    detLabel.setText("Produto selecionado: " + produto + "        ");
                }
            });
            painelCardapio.add(botao);
            botoesProdutos.add(botao);
        }

        // Subpainel de detalhes do produto
        JPanel painelPdtQtd = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelPdtQtd.setLayout(new BoxLayout(painelPdtQtd, BoxLayout.X_AXIS));
        painelPdtQtd.setAlignmentX(Component.LEFT_ALIGNMENT);
        painelPdtQtd.setBackground(new Color(245, 245, 245));
        detLabel = new JLabel("Produto selecionado: " + (produto==null?"nada":produto) + "        ");
        qtdField = new JTextField("1", 1);
        painelPdtQtd.add(detLabel);
        painelPdtQtd.add(new JLabel("qtd.: "));
        qtdField.setPreferredSize(new Dimension(30, 30));
        qtdField.setMaximumSize(new Dimension(30, 30));
        painelPdtQtd.add(qtdField);

        // Detalhes do Produto
        JPanel painelDetalhes = new JPanel(new FlowLayout(FlowLayout.LEFT));
        painelDetalhes.setLayout(new BoxLayout(painelDetalhes, BoxLayout.Y_AXIS));
        painelDetalhes.setBackground(new Color(245, 245, 245));
        JTextField detField = new JTextField(3);
        JButton botao = estilizarBotao("Adicionar");
        painelDetalhes.setBorder(BorderFactory.createTitledBorder("Detalhes do Pedido"));
        painelDetalhes.add(painelPdtQtd);
        painelDetalhes.add(new JLabel("Informações adicionais:"));
        detField.setPreferredSize(new Dimension(750, 30));
        detField.setMaximumSize(new Dimension(750, 30));
        painelDetalhes.add(detField);
        painelDetalhes.add(botao);
        botao.addActionListener(e -> {
        	controle.clique(() -> {
                try {
                    int qtd = Integer.parseInt(qtdField.getText());
                    interface_controller status = new interface_controller();

                    if (qtd <= 0) {
                        JOptionPane.showMessageDialog(null, "Quantidade inválida!", "Erro", JOptionPane.ERROR_MESSAGE);
                        qtdField.setText("1");
                    } else {
                        String detalhes = detField.getText();
                        adicionarPedido(produto, qtd, preco, detalhes);
                        
                        if (editandoPdt) {
                        	SwingUtilities.invokeLater(() -> {
                            	 for (int i = 0; i < dados.size(); i++) {
                                     Object[][] item = dados.get(i);
                                     String nomeProdutoItem = (String) item[0][0];
                                     
                                     if(nomeProdutoItem == produto) {
                                    	 dados.remove(i);
                                     }
                                   break;
                                 }
                            });
                            editandoPdt = false;
                        }
                        detField.setText("");
                        qtdField.setText("1");
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(null, "Digite um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
                    qtdField.setText("1");
                }
            });
        });

        // Organiza lado esquerdo
        painelEsquerdo.add(painelCardapio, BorderLayout.CENTER);

        JPanel painelInferiorEsquerdo = new JPanel(new BorderLayout(10, 10));
        painelInferiorEsquerdo.setBackground(new Color(245, 245, 245));
        painelInferiorEsquerdo.add(painelDetalhes, BorderLayout.CENTER);
        painelEsquerdo.add(painelInferiorEsquerdo, BorderLayout.SOUTH);

        painelCentro.add(painelEsquerdo);
        painelCentro.add(painelDireito);

        // -------- RODAPÉ --------
        JPanel rodape = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton botaoConfirmar = estilizarBotao("Confirmar");
        rodape.setBackground(new Color(230, 230, 230));
        rodape.setBorder(new EmptyBorder(10, 10, 10, 10));
        rodape.add(botaoConfirmar);
        botaoConfirmar.addActionListener(e -> {
            // Verifica se o total não é 0 e identifica o cliente caso não o tenha sido
            // Caso haja um CPF registrado, abre a opção de usar ou não pontos para o pgto
        		
                if (total == 0) {
                    JOptionPane.showMessageDialog(null, "Nenhum item adicionado ao pedido!", "Erro", JOptionPane.ERROR_MESSAGE);
                } else {
                    if (cliente.getNumberCPF() == 0) {
                        identificarCliente();
                    } else {
                        JOptionPane.showMessageDialog(null, "Cliente já identificado", "Identificação cliente", JOptionPane.INFORMATION_MESSAGE);
                    }
                    cliente_pontos pontosCliente = new cliente_pontos();
                    for (int i = 0; i < dados.size(); i++) {
            	        Object[][] item = dados.get(i);
            	        String produtoTabela = (String) item[0][0];
                        int qtd = (int) item[0][1];
                        String detalhes = (String) item[0][3];
            	        Produtos produto = new Produtos(produtoTabela, qtd, total, detalhes);
            	       break;
                    }
                    if(!escolhaCliente) {
                    	if(pontosCliente.getPontos(cpf) >= 10) {
                            // Usa o top-level ancestor como referência para o frame
                            java.awt.Component parent = javax.swing.SwingUtilities.getWindowAncestor(botaoConfirmar);
                            Pagto Pagto = new Pagto(this, total, cpf, pontosCliente.getPontos(cpf)/100, desconto, this, dados);
                            telaPontos = true;
                            timerConect.stop();
                            Pagto.setVisible(true);
                    	}else{
                    		FormaPagto Pagto2 = new FormaPagto(total, pontosCliente.getPontos(cpf)/100, desconto, dados);
                            Pagto2.setVisible(true);
                            timerConect.stop();
                            dispose();
                    	}
                    } else {
                    	FormaPagto Pagto2 = new FormaPagto(total, 0, desconto, dados);
                        Pagto2.setVisible(true);
                        timerConect.stop();
                        dispose();
                    }
                }
        });
        botaoIdCliente = estilizarBotao("Id. cliente");
        rodape.add(botaoIdCliente);
        botaoIdCliente.addActionListener(e -> {
            if (identifica == false) {
                identificarCliente();
            } else {
                JOptionPane.showMessageDialog(null, "Cliente já identificado", "Identificação cliente", JOptionPane.INFORMATION_MESSAGE);
            }
        });
        botaoIdCliente.setVisible(true);

        // -------- ADD NA JANELA --------
        add(painelCentro, BorderLayout.CENTER);
        add(rodape, BorderLayout.SOUTH);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
            	if(telaPontos == false) {
            		solicitarSenhaParaFechar();
            	}
            }
        });
    
        pdv_conect pdv = new pdv_conect();
        
        
        
        if (timerConect == null && op.getNumberOperador() != 0) {
            timerConect = new Timer(60000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    new Thread(() -> {
                        String ipPC = null;
                        try {
                            InetAddress ip = InetAddress.getLocalHost();
                            ipPC = ip.getHostAddress();
                        } catch (UnknownHostException ex) {
                            ex.printStackTrace();
                            return;
                        }

                        Future<Integer> resultadoFuture = pdv.verificarIp(ipPC);
                        try {
                            int resultConect = resultadoFuture.get();
                            System.out.println("Resultado: " + resultConect);
                            if (resultConect == 0) {
                                JOptionPane.showMessageDialog(null, "Este PDV não está em nosso sistema", "error", JOptionPane.ERROR_MESSAGE);
                                System.exit(1);
                            }
                        } catch (Exception exz) {
                        	 discord_erro_pdv erroDiscord = new discord_erro_pdv();
                             String tipoErro = exz.getClass().getSimpleName();
                             String mensagemErro = exz.getMessage() != null ? exz.getMessage() : "Sem mensagem específica.";
                             erroDiscord.enviarEmbed("Erro desconhecido", mensagemErro, "Vendas() -> timerConect() -> linha 321", "Alta", LocalDateTime.now().format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
                            exz.printStackTrace();
                        }
                    }).start();
                }
            });

            timerConect.setRepeats(true); // repete a cada 60s
            timerConect.start();
            System.out.println("Timer criado em: " + System.currentTimeMillis());
        } else if (!timerConect.isRunning()) {
            timerConect.start();
            System.out.println("Timer reiniciado em: " + System.currentTimeMillis());
        }

        System.out.println("Timer criado em: " + System.currentTimeMillis());
    }

    // Botão customizado
    private JButton estilizarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(new Color(100, 149, 237));  // Azul
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(130, 30));

        // Adicionando a ação baseada no texto do botão
        botao.addActionListener((ActionEvent e) -> {
            switch (texto) {
                case "Confirmar" -> {
                    // Ação para o botão Confirmar
                    if(identifica == true) {
                        identifica = false;
                    }
                }
                case "Cancelar" -> // Ação para o botão Cancelar
                    System.out.println("Ação Cancelar executada.");
                case "Id. cliente" -> // Ação para o botão identificar cliente
                    identifica = false;
                default -> {
                }
            }
        });
        
        return botao;
    }

    // Tabela customizada
    private void estilizarTabela(JTable tabela) {
        tabela.setRowHeight(30);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // Ajusta os tamanhos das colunas de Item, Qtd, Preço, Detalhes e dos botões Editar e Remover
        tabela.getColumnModel().getColumn(0).setPreferredWidth(120);
        tabela.getColumnModel().getColumn(1).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(2).setPreferredWidth(60);
        tabela.getColumnModel().getColumn(3).setPreferredWidth(615);
        tabela.getColumnModel().getColumn(4).setPreferredWidth(30);
        tabela.getColumnModel().getColumn(5).setPreferredWidth(30);
        tabela.getColumnModel().getColumn(6).setPreferredWidth(30);
        tabela.setGridColor(new Color(200, 200, 200));
        tabela.setShowGrid(true);

        JTableHeader header = tabela.getTableHeader();
        header.setFont(new Font("SansSerif", Font.BOLD, 13));
        header.setBackground(new Color(200, 220, 240));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < tabela.getColumnCount(); i++) {
            tabela.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }
    }

    private void adicionarPedido(String pedido, int qtd, double preco, String detalhes) {

        dados.add(new Object[][]{{pedido, carregarItem(pedido, qtd, detalhes), preco, (detalhes.equals("") ? "--" : detalhes), "[+]", "[-]", "[x]"}}); // se detalhes for vazio, usa-se "--"
        verificar_estoque_btn estoque = new verificar_estoque_btn();
        SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                return null;
            }

            @Override
            protected void done() {
                atualizarTabela(dados); 
            }
        };
        worker.execute();
        atualizarTabela(dados);
    }
       	
    private void removerPedido(String pedido, int qtd, double preco, String detalhes) {
        // Remove um item selecionado da ArrayList, e passa a nova lista ao método atualizarTabela
        for (int i = 0; i < dados.size(); i++) {
            Object[][] item = dados.get(i);
            if (item[0][0].equals(pedido) && (item[0][3].equals(detalhes) || (item[0][3].equals("--") && detalhes.equals("")))) {
                if ((int) item[0][1] < 1 || (int) item[0][1] == qtd) {
                	Atualizar_estoque atualizaEstoque = new Atualizar_estoque();
                	atualizaEstoque.addEstoque(pedido, qtd);
                    dados.remove(i);
                } else {                
                    // Diminui a qtd do item usando a especificada, ou remove-o se só tiver um ou menos dele
                    int novaQtd = (int) item[0][1] - qtd;
                    Atualizar_estoque atualizaEstoque = new Atualizar_estoque();
                	atualizaEstoque.addEstoque(pedido, novaQtd);
                    dados.set(i, new Object[][]{{pedido, novaQtd, preco, (detalhes.equals("") ? "--" : detalhes), "[+]", "[-]", "[x]"}});
                }
                break;
            }
        }
        atualizarTabela(dados);
    }
    
    //Sistema executa uma verificação dentro do vetor verificar a qtd do produto indicado 
    private int verificarQtd(String produto) {
        for (int i = 0; i < dados.size(); i++) {
            Object[][] item = dados.get(i);
            if (item[0][0] != null && item[0][0].equals(produto)) {
                int novaQtd = (int) item[0][1];
                return novaQtd; 
            }
        }
        return -1; 
    }

    private int carregarItem(String produto, int qtd, String detalhes) {
        // Wrapper mutável para permitir acesso na thread
        int[] quantidadeFinal = new int[]{qtd};
        qtdBanco = qtd;
        
        // Atualiza a lista de dados localmente (otimista)
        for (int i = 0; i < dados.size(); i++) {
            Object[][] item = dados.get(i);
            String nomeProduto = (String) item[0][0];
            int qtdAtual = (int) item[0][1];
            String detalhesItem = (String) item[0][3];

            boolean mesmoProduto = nomeProduto != null && nomeProduto.equals(produto);
            boolean mesmosDetalhes = detalhesItem.equals(detalhes) || (detalhesItem.equals("--") && detalhes.equals(""));

            if (mesmoProduto && mesmosDetalhes) {
                quantidadeFinal[0] = qtdAtual + qtd;
                dados.remove(i);
                break;
            }
        }

        // Verificação de estoque em segundo plano
        ExecutorService executor = Executors.newSingleThreadExecutor();
        executor.execute(() -> {
            Verificar_estoque verificar = new Verificar_estoque(produto, quantidadeFinal[0]);
            int resultado = verificar.call();

            if (resultado == -1) {
                SwingUtilities.invokeLater(() -> {
                	JOptionPane.showMessageDialog(null, 
                            "O produto \"" + produto + "\" não existe no estoque da filial.", 
                            "Produto não encontrado", 
                            JOptionPane.ERROR_MESSAGE);
                	 for (int i = 0; i < dados.size(); i++) {
                         Object[][] item = dados.get(i);
                         String nomeProdutoItem = (String) item[0][0];
                         int quantidadeTabela = (int) item[0][1];
                         String detalhesItem = (String) item[0][3];

                         boolean mesmoProduto = nomeProdutoItem != null && nomeProdutoItem.equals(produto);
                         boolean mesmosDetalhes = detalhesItem.equals(detalhes) || (detalhesItem.equals("--") && detalhes.equals(""));

                         if (mesmoProduto && mesmosDetalhes) {
                              // Substitui a linha com a quantidade máxima disponível 
                             dados.set(i, new Object[][]{
                                 {produto, quantidadeTabela-qtd, preco, (detalhes.equals("") ? "--" : detalhes), "[+]", "[-]", "[x]"}
                             });
                             atualizarTabela(dados);
                             break;
                         }
                     }
                	
                });
            } else if (resultado == -2) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(null, 
                        "Erro ao verificar o estoque do produto " + produto + ".", 
                        "Erro no banco de dados", 
                        JOptionPane.ERROR_MESSAGE);
                });
            }else if (resultado == 0) {
                SwingUtilities.invokeLater(() -> {
                    for (int i = 0; i < dados.size(); i++) {
                        Object[][] item = dados.get(i);
                        String nomeProdutoItem = (String) item[0][0];
                        int quantidadeTabela = (int) item[0][1]; // já adicionada na tabela
                        String detalhesItem = (String) item[0][3];

                        boolean mesmoProduto = nomeProdutoItem != null && nomeProdutoItem.equals(produto);
                        boolean mesmosDetalhes = detalhesItem.equals(detalhes) || (detalhesItem.equals("--") && detalhes.equals(""));

                        if (mesmoProduto && mesmosDetalhes) {
                            // Já foi adicionado com o estoque que havia, não precisa alterar
                            if (quantidadeTabela > 0) {
                                JOptionPane.showMessageDialog(null, 
                                    "Você já adicionou todas as unidades disponíveis do produto " + produto + ".", 
                                    "Estoque esgotado", 
                                    JOptionPane.INFORMATION_MESSAGE);
                                dados.set(i, new Object[][]{
                                    {produto, quantidadeTabela-qtd, preco, (detalhes.equals("") ? "--" : detalhes), "[+]", "[-]", "[x]"}
                                });
                                atualizarTabela(dados);
                                // Nenhuma alteração na tabela
                                break;
                            } else {
                                // Está na tabela, mas com quantidade 0, então realmente não tem
                                JOptionPane.showMessageDialog(null, 
                                    "O estoque do produto " + produto + " acabou", 
                                    "Controle de estoque", 
                                    JOptionPane.WARNING_MESSAGE);

                                dados.set(i, new Object[][]{
                                    {produto, 0, 0.0, "SEM ESTOQUE", "[+]", "[-]", "[x]"}
                                });
                                atualizarTabela(dados);
                                break;
                            }
                        }
                    }
                });
            }else if (quantidadeFinal[0] > resultado) {
                SwingUtilities.invokeLater(() -> {
                	qtdBanco = resultado;
                    for (int i = 0; i < dados.size(); i++) {
                        Object[][] item = dados.get(i);
                        String nomeProdutoItem = (String) item[0][0];
                        String detalhesItem = (String) item[0][3];

                        boolean mesmoProduto = nomeProdutoItem != null && nomeProdutoItem.equals(produto);
                        boolean mesmosDetalhes = detalhesItem.equals(detalhes) || (detalhesItem.equals("--") && detalhes.equals(""));

                        if (mesmoProduto && mesmosDetalhes) {
                            JOptionPane.showMessageDialog(null,
                                "Estoque insuficiente para o produto \"" + produto + "\".\n" +
                                "Você pediu " + quantidadeFinal[0] + ", mas só há " + resultado + " disponível.",
                                "Aviso de Estoque",
                                JOptionPane.WARNING_MESSAGE
                            );

                            // Substitui a linha com a quantidade máxima disponível 
                            dados.set(i, new Object[][] {
                                {produto, resultado, preco, (detalhes.equals("") ? "--" : detalhes), "[+]", "[-]", "[x]"}
                            });
                            Atualizar_estoque atualizaEstoque = new Atualizar_estoque();
                            atualizaEstoque.retirarEstoque(produto, qtdBanco);

                            atualizarTabela(dados);
                            break;
                        }
                    }
                });
            }else if (quantidadeFinal[0] <= resultado) {
                SwingUtilities.invokeLater(() -> {
                	qtdBanco = resultado;
                    for (int i = 0; i < dados.size(); i++) {
                        Object[][] item = dados.get(i);
                        String nomeProdutoItem = (String) item[0][0];
                        String detalhesItem = (String) item[0][3];

                        boolean mesmoProduto = nomeProdutoItem != null && nomeProdutoItem.equals(produto);
                        boolean mesmosDetalhes = detalhesItem.equals(detalhes) || (detalhesItem.equals("--") && detalhes.equals(""));

                        if (mesmoProduto && mesmosDetalhes) {
                            Atualizar_estoque atualizaEstoque = new Atualizar_estoque();
                            atualizaEstoque.retirarEstoque(produto, quantidadeFinal[0]);
                            break;
                        }
                    }
                });
            }
        });
        executor.shutdown();

        return quantidadeFinal[0];
    }

    
    private void limparPedidos() {
    	cancelarBancotudo();
    	dados.clear(); // Limpa a lista de pedidos
        recriarLadoDD(new JTable(), 0); // Atualiza a interface com uma tabela vazia
    }
    
    private void atualizarTabela(ArrayList<Object[][]> dados) {
        // Simplifica a ArrayList em colunas para a tabela
        DefaultTableModel modelo = new DefaultTableModel(colunas, 0);
        for (Object[][] item : dados) {
            modelo.addRow(item[0]);
        }

        // Cria uma JTable com os dados atualizados
        tabela = new JTable(modelo) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Bloqueia a edição da tabela
                return false;
            }
        };
        estilizarTabela(tabela);
        tabela.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = tabela.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / tabela.getRowHeight();
                // Detecta cliques dentro da tabela
                if (row < tabela.getRowCount() && row >= 0 && column < tabela.getColumnCount() && column >= 0) {
                    if (column == 4) {
                        // Botão de editar pedidos
                        produto = tabela.getValueAt(row, 0).toString();
                        qtdAnt = (int) tabela.getValueAt(row, 1);
                        preco = Double.parseDouble(tabela.getValueAt(row, 2).toString());
                        detalhesAnt = tabela.getValueAt(row, 3).toString();
                        btnEdit = qtdAnt;
                        Atualizar_estoque atualizaEstoque = new Atualizar_estoque();
                        atualizaEstoque.addEstoque(produto, qtdAnt);
                        detLabel.setText("Produto selecionado: " + produto + "        ");
                        qtdField.setText(String.valueOf(qtdAnt));
                        editandoPdt = true;

                        //recriarLadoEE(produto, qtd, detalhes);
                    }
                    if (column == 5) {
                        // Botão de remover pedidos
                        String produto = tabela.getValueAt(row, 0).toString();
                        double preco = (double) tabela.getValueAt(row, 2);
                        String detalhes = tabela.getValueAt(row, 3).toString();
                        removerPedido(produto, 1, preco, detalhes);
                    }
                    if (column == 6) {
                        // Botão de limpar pedidos
                        String produto = tabela.getValueAt(row, 0).toString();
                        int qtd = (int) tabela.getValueAt(row, 1);
                        double preco = (double) tabela.getValueAt(row, 2);
                        String detalhes = tabela.getValueAt(row, 3).toString();
                        removerPedido(produto, qtd, preco, detalhes);
                    }
                }
            }
        });

     // Recalcula o total
        total = 0;
        for (Object[][] item : dados) {
            int quantidade = (int) item[0][1];
            double preco;

            try {
                preco = (item[0][2] instanceof Double)
                    ? (double) item[0][2]
                    : Double.parseDouble(item[0][2].toString());
            } catch (Exception e) {
                preco = 0.0; // Em caso de erro, assume 0 para evitar travar o sistema
            }

            total += quantidade * preco;
        }
        recriarLadoDD(tabela, total);
    }

    
    private void cancelarBancotudo() {
        if (dados.isEmpty()) {
            System.out.println("Nenhum item na tabela.");
            return;
        }

        // Agrupa os produtos por nome e soma suas quantidades
        Map<String, Integer> produtosAgrupados = new HashMap<>();
        for (Object[][] item : dados) {
            String produto = (String) item[0][0];
            int qtd = (int) item[0][1];
            produtosAgrupados.put(produto, produtosAgrupados.getOrDefault(produto, 0) + qtd);
        }

        ExecutorService executor = Executors.newFixedThreadPool(produtosAgrupados.size());
        try {
            for (Map.Entry<String, Integer> entry : produtosAgrupados.entrySet()) {
                String produto = entry.getKey();
                int qtdTotalPedida = entry.getValue();

                Verificar_estoque verificar = new Verificar_estoque(produto, qtdTotalPedida);
                Future<Integer> future = executor.submit(verificar);
                int estoqueDisponivel = future.get();
                
                Atualizar_estoque atualizaEstoque = new Atualizar_estoque();
            	atualizaEstoque.addEstoque(produto, qtdTotalPedida);
                
            }
        } catch (InterruptedException | ExecutionException e) {
        	 discord_erro_pdv erroDiscord = new discord_erro_pdv();
             String tipoErro = e.getClass().getSimpleName();
             String mensagemErro = e.getMessage() != null ? e.getMessage() : "Sem mensagem específica.";
             erroDiscord.enviarEmbed("Erro desconhecido", mensagemErro, "Vendas() -> cancelarBancotudoo() -> linha 706", "Alta", LocalDateTime.now().format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
             e.printStackTrace();
        } finally {
            executor.shutdown();
        }
    }

    public static double arredondar(double valor, int casas) {
        if (casas < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(valor);
        bd = bd.setScale(casas, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
    
    private void recriarLadoDD(JTable tabela, double total) {
        // Cria um novo painelTotal com o valor atualizado
        JPanel painelTotal = new JPanel(new BorderLayout());
        painelTotal.setBorder(BorderFactory.createTitledBorder("Resumo"));
        painelTotal.setBackground(new Color(245, 245, 245));
        
        if (total != 0){
            desconto = 0;
            if ( cliente.getClienteS() ) {
                desconto = total * 0.05; // 5% de desconto
            } else if ( cliente.getMember() ) {
                desconto = total * 0.15; // 15% de desconto
            }
            desconto = arredondar(desconto, 2);
        }

        DecimalFormat df = new DecimalFormat("#.00");
        JLabel totalLabel = new JLabel("Total: R$ " + (total==0?"0,00":df.format(total - desconto)));
        totalLabel.setFont(new Font("SansSerif", Font.BOLD, 15));

        JButton removerBtn = estilizarBotao("Limpar Tudo");
        removerBtn.setBackground(new Color(255, 69, 0)); // Vermelho
        removerBtn.addActionListener(e -> limparPedidos()); // Limpa os pedidos

        painelTotal.add(totalLabel, BorderLayout.WEST);
        painelTotal.add(removerBtn, BorderLayout.EAST);

        // Recria o painelDireito
        JPanel painelDireito = new JPanel(new BorderLayout(10, 10));
        painelDireito.setBackground(new Color(245, 245, 245));
        painelDireito.add(new JScrollPane(tabela), BorderLayout.CENTER);
        painelDireito.add(painelTotal, BorderLayout.SOUTH);

        // Substitui o painelDireito na interface
        JPanel painelCentro = (JPanel) getContentPane().getComponent(0);
        painelCentro.remove(1); // Remove o painelDireito antigo
        painelCentro.add(painelDireito, 1); // Adiciona o novo painelDireito

        // Atualiza a interface
        painelCentro.revalidate();
        painelCentro.repaint();
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
            for (Object[][] item : dados) {
                venda = (String) item[0][0];
            }
            if(venda != null) {
                JOptionPane.showMessageDialog(null, "Para fechar o sistema, está venda precisar ser finalizada", "Informação", JOptionPane.INFORMATION_MESSAGE);
                return;
            }
            
            try {
                int senha = Integer.parseInt(input);
                int resultado = validaBanco.fecharPDV(senha);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                LocalDateTime agora = LocalDateTime.now();
            	JOptionPane.showMessageDialog(null, "O caixa será fechado automaticamente e auditado, caso necessário.", "Fechamento de caixa", JOptionPane.WARNING_MESSAGE);
            	FechamentoCaixa fechamentoCaixa = new FechamentoCaixa();
            	valida_login bancoFun = new valida_login();
            	ControleDiscordEstoque estoqueControle = new ControleDiscordEstoque();
            	CalcularValor donate = new CalcularValor();
            	String discord = urlDiscord(op.getFilial());
            	fechamentoCaixa.enviarFechamentoCaixa(op.getNomeOperador(), op.fechamentoPIX(), op.fechamentoCredit(), op.fechamentoDebit(), op.fechamentoDinheiro(), data, "Segurança PDVs", discord);
            	estoqueControle.enviarRelatorio(op.getFilial());
            	donate.calcularEDisparar(discord);
            	JOptionPane.showMessageDialog(null,"Caixa fechado! Deixe apenas R$100,00 no caixa para o troco.","Confirmação fechamento",JOptionPane.INFORMATION_MESSAGE);
                if (resultado == 1) {
                    discord_entrada_caixa.enviarEmbed("Sistema fechado", "Uma filial fechou o PDV", "**"+validaBanco.getNomeGerencia(senha)+"**", agora.format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ACOES"));
                    if(operador.getNomeOperador() != null) {
                        validaBanco.deletarPDV(operador.getNumberOperador());
                    }
                    cancelarBancotudo();
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
    
    private String urlDiscord(int filial) {
    	String urlWebhook = switch (filial) {
        case 1001 -> dotenv.get("WEBHOOK_PAULISTA");
        case 1002 -> dotenv.get("WEBHOOK_JDANGELA");
        case 1003 -> dotenv.get("WEBHOOK_LIBERDADE");
        default -> "";
    	};
    	return urlWebhook;
    }

    private void identificarCliente() {
        controle_cliente controleCliente = new controle_cliente();
        
        if (!identifica || controleCliente.getNumberCPF() == 0) {
            int resultCliente = JOptionPane.showConfirmDialog(null, "Cliente Java+ ?", "Identificação cliente", JOptionPane.YES_NO_OPTION);
            
            if (resultCliente == JOptionPane.YES_OPTION) {
                identifica = true;
                boolean clienteValido = false;

                while (!clienteValido) {
                    try {
                        MaskFormatter cpfMask = new MaskFormatter("###.###.###-##");
                        cpfMask.setPlaceholderCharacter('_');
                        JFormattedTextField cpfField = new JFormattedTextField(cpfMask);
                        
                        int cpfResult = JOptionPane.showConfirmDialog(null, cpfField, "Digite o CPF", JOptionPane.OK_CANCEL_OPTION);
                        
                        if (cpfResult == JOptionPane.OK_OPTION) {
                            String cpfFormatado = cpfField.getText();
                            String cpfNumerico = cpfFormatado.replaceAll("[^0-9]", "");

                            if (cpfNumerico.length() != 11) {
                                JOptionPane.showMessageDialog(null, "CPF incompleto ou inválido. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
                                continue;
                            }

                            cpf = Integer.parseInt(cpfNumerico);
                            verificar_cliente clienteBanco = new verificar_cliente();

                            int simples = clienteBanco.verificaClienteCPFSimples(cpf);
                            int member = clienteBanco.verificaClienteCPFMember(cpf);
                            String nome = clienteBanco.nomeCliente(cpf);
                            
                            
                            if (simples == 1) {
                                cliente.setClienteS(true);
                                JOptionPane.showMessageDialog(null, "Cliente identificado", "Identificação cliente", JOptionPane.INFORMATION_MESSAGE);
                                controleCliente.setNumberCpf(cpf);
	                             controleCliente.setNomeCliente(nome);
                                clienteValido = true;
                                botaoIdCliente.setVisible(false);
                            } else if (member == 1) {
                                cliente.setMember(true);
                                JOptionPane.showMessageDialog(null, "Cliente fidelidade localizado", "Identificação cliente", JOptionPane.INFORMATION_MESSAGE);
                                controleCliente.setNumberCpf(cpf);
                                controleCliente.setNomeCliente(nome);
                                clienteValido = true;
                                botaoIdCliente.setVisible(false);
                            } else {
                                JOptionPane.showMessageDialog(null, "Cliente não cadastrado. Tente novamente.", "Erro", JOptionPane.ERROR_MESSAGE);
                            }

                        } else {
                            // Cancelado: permite sair da identificação
                            JOptionPane.showMessageDialog(null, "Identificação cancelada", "Cancelado", JOptionPane.INFORMATION_MESSAGE);
                            botaoIdCliente.setVisible(true);
                            escolhaCliente = true;
                            identifica = true;
                            clienteValido = true; // Sai do loop sem definir CPF
                        }

                    } catch (ParseException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(null, "Erro ao formatar o CPF", "Erro", JOptionPane.ERROR_MESSAGE);
                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Informe um CPF válido", "Erro", JOptionPane.ERROR_MESSAGE);
                    }
                }

                recriarLadoDD(tabela, total);

            } else if (resultCliente == JOptionPane.NO_OPTION) {
                botaoIdCliente.setVisible(true);
                escolhaCliente = true;
                identifica = true;
            }
        }
    }
    /*
   public static void main(String[] args) {
   	
        Vendas vender = new Vendas();
        vender.setVisible(true);
      
    }
    */
}