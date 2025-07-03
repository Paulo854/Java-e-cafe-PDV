package inteface;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.imageio.ImageIO;
import javax.swing.*;

import io.github.cdimascio.dotenv.Dotenv;
import conect_banco.TesteConexaoMySQL;
import conect_banco.valida_login;
import conexao_controle.conect_internet;
import conexao_controle.discord_entrada_caixa;
import conexao_controle.discord_erro_pdv;
import conexao_controle.discord_pedidos;
import controladores.controlador_login_system;
import controladores.controlador_operador;
import controladores.interface_controller;

public class Login extends JFrame implements ActionListener {
    public JLabel date, logoIcon, lblLogin, lblSenha;
    public JTextField txtLogin;
    public JPasswordField txtSenha;
    public Timer timer;
    public JButton btn_logar;
    public static int matriculaGerencia, senhaGerencia;
    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public discord_entrada_caixa discord_entrada = new discord_entrada_caixa();
    public JFrame parentFrame;
    public Dotenv dotenv = Dotenv.configure()
            .directory("./src")
            .filename(".env")
            .load();

    public Login() {
        setUndecorated(true);
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        setTitle("Java&Café");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("icon_bar.png")));

        JPanel panelBTN = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton closeButton = new JButton("X");
        closeButton.setBackground(Color.RED);
        closeButton.setForeground(Color.WHITE);
        closeButton.setFocusPainted(false);
        closeButton.setBorderPainted(false);
        closeButton.addActionListener(e -> solicitarSenhaParaFechar());
        panelBTN.add(closeButton);
        add(panelBTN, BorderLayout.NORTH);

        JPanel panelDate = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        date = new JLabel();
        panelDate.add(date);
        add(panelDate, BorderLayout.SOUTH);

        JPanel centro = new JPanel(new GridBagLayout());
        centro.setOpaque(false);

        JPanel iconPanel = new JPanel();
        iconPanel.setLayout(new BoxLayout(iconPanel, BoxLayout.X_AXIS));
        iconPanel.setOpaque(false);

        logoIcon = new JLabel();
        ImageIcon icon = new ImageIcon(getClass().getResource("logo1.png"));
        Image img = icon.getImage().getScaledInstance(150, 150, Image.SCALE_SMOOTH);
        logoIcon.setIcon(new ImageIcon(img));
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        GridBagConstraints info = new GridBagConstraints();
        info.insets = new Insets(0, 50, 0, 0);

        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setOpaque(false);

        JPanel loginPanel = new JPanel();
        loginPanel.setLayout(new BoxLayout(loginPanel, BoxLayout.X_AXIS));
        loginPanel.setOpaque(false);

        lblLogin = new JLabel("LOGIN:  ");
        lblLogin.setFont(new Font("Arial", Font.BOLD, 20));
        txtLogin = new JTextField(20);
        txtLogin.setMaximumSize(new Dimension(200, 30));
        txtLogin.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel senhaPanel = new JPanel();
        senhaPanel.setLayout(new BoxLayout(senhaPanel, BoxLayout.X_AXIS));
        senhaPanel.setOpaque(false);

        lblSenha = new JLabel("SENHA: ");
        lblSenha.setFont(new Font("Arial", Font.BOLD, 20));
        txtSenha = new JPasswordField();
        txtSenha.setMaximumSize(new Dimension(200, 30));
        txtSenha.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel botaoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botaoPanel.setOpaque(false);
        btn_logar = new JButton("Entrar");
        btn_logar.setPreferredSize(new Dimension(200, 30));
        btn_logar.addActionListener(e -> validarLogin(txtLogin.getText(), new String(txtSenha.getPassword())));
        botaoPanel.add(btn_logar);

        iconPanel.add(logoIcon);
        loginPanel.add(lblLogin);
        loginPanel.add(txtLogin);
        senhaPanel.add(lblSenha);
        senhaPanel.add(txtSenha);
        infoPanel.add(Box.createVerticalStrut(40));
        infoPanel.add(loginPanel);
        infoPanel.add(senhaPanel);
        infoPanel.add(Box.createVerticalStrut(10));
        infoPanel.add(botaoPanel);

        centro.add(iconPanel, new GridBagConstraints());
        centro.add(infoPanel, info);
        infoPanel.add(Box.createVerticalStrut(20));

        add(centro, BorderLayout.CENTER);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.out.println("Alt + F4 bloqueado!");
            }
        });

        timer = new Timer(1000, e -> {
            LocalDateTime agora = LocalDateTime.now();
            date.setText(agora.format(formatter));
        });

        timer.start();
    }

    private void solicitarSenhaParaFechar() {;
    valida_login validaBanco = new valida_login();
    controlador_operador operador = new controlador_operador();
    while (true) {
    	
        String input = JOptionPane.showInputDialog("Para executar essa ação, informe a senha:");
        if (input == null) { 
            return;
        }

        try {
            int senha = Integer.parseInt(input);
            int resultado = validaBanco.fecharPDV(senha);
            if (resultado == 1) {
            	 LocalDateTime agora = LocalDateTime.now();
            	discord_entrada_caixa.enviarEmbed("Sistema fechado", "Uma filial fechou o PDV", "**"+validaBanco.getNomeGerencia(senha)+"**", agora.format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ACOES"));
            	if(operador.getNomeOperador() != null) {
            		validaBanco.deletarPDV(operador.getNumberOperador());
            	}
                System.exit(0);
            } else {
            	 LocalDateTime agora = LocalDateTime.now();
            	discord_entrada_caixa.enviarEmbed("Tentativa de fechar aplicação", "indetificamos que uma filial tentou fechar o PDV", "foi identificado uma senha: **"+senha+"**", agora.format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ACOES"));
                JOptionPane.showMessageDialog(null, "Senha incorreta!", "Erro", JOptionPane.ERROR_MESSAGE);
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Digite um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
        }            
    }
}

    private void validarLogin(String mat, String senha) {
        try {
            matriculaGerencia = Integer.parseInt(mat);
            senhaGerencia = Integer.parseInt(senha);

            
            valida_login validaBanco = new valida_login();
            controlador_operador operador = new controlador_operador();

            btn_logar.setEnabled(false);
            

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
            LocalDateTime agora = LocalDateTime.now();
            String dataHora = agora.format(formatter);
            LocalDate dataAtual = LocalDate.now();

            int resultado = validaBanco.verificaLogin(matriculaGerencia, senhaGerencia);

            if (resultado == 1 && operador.getNumberOperador() == 0) {
                JOptionPane.showMessageDialog(null, "Os dados de liberação foram gravados");
                int matricula = Integer.parseInt(JOptionPane.showInputDialog("Qual a matrícula do operador?"));
                double money = Double.parseDouble(JOptionPane.showInputDialog("Qual o valor de lastro do caixa?"));

                if(matricula == JOptionPane.CANCEL_OPTION || matricula == JOptionPane.CLOSED_OPTION || money == JOptionPane.CANCEL_OPTION || money == JOptionPane.CLOSED_OPTION) {
                	btn_logar.setEnabled(true);
                }
                while (true) {
                    try {
                        String nomeOperador = validaBanco.getNomeFuncionario(matricula);
                        int filialGerente = validaBanco.getFilial(matriculaGerencia);
                        String dataBanco = validaBanco.getDataAbertudaPDV(matricula);
                        int filialOperador = validaBanco.getFilial(matricula);

                        if (nomeOperador == null || filialOperador == -1) {
                            JOptionPane.showMessageDialog(null, "Matrícula inválida ou operador não encontrado.", "Erro", JOptionPane.ERROR_MESSAGE);
                            String novaMatricula = JOptionPane.showInputDialog("Digite uma nova matrícula:");
                            if (novaMatricula == null || novaMatricula.trim().isEmpty()) break;
                            matricula = Integer.parseInt(novaMatricula.trim());
                            continue;
                        }

                        if (filialOperador != filialGerente) {
                            JOptionPane.showMessageDialog(null, "Filiais diferentes.", "Erro", JOptionPane.ERROR_MESSAGE);
                            String novaMatricula = JOptionPane.showInputDialog("Digite uma nova matrícula:");
                            if (novaMatricula == null || novaMatricula.trim().isEmpty()) break;
                            matricula = Integer.parseInt(novaMatricula.trim());
                            continue;
                        }

                        int pdvAberto = validaBanco.verificaPDV(matricula);
                        if (pdvAberto == 1) {
                            double moneyAnterior = validaBanco.getmoneyPDV(matricula);
                            money += moneyAnterior;
                            operador.pagamentoDinheiro(money);
                            JOptionPane.showMessageDialog(null, "Valor anterior: R$" + moneyAnterior + "\nNovo total: R$" + money);
                            validaBanco.deletarPDVData(matricula, dataBanco);
                            continue;
                        }

                        operador.setNomeOperador(nomeOperador);
                        operador.setNumberOperador(matricula);
                        validaBanco.setPDV(nomeOperador, filialOperador, money, matricula, matriculaGerencia, dataAtual);

                        String urlWebhook = switch (filialOperador) {
                            case 1001 -> dotenv.get("WEBHOOK_PAULISTA");
                            case 1002 -> dotenv.get("WEBHOOK_JDANGELA");
                            case 1003 -> dotenv.get("WEBHOOK_LIBERDADE");
                            default -> "";
                        };

                        String nomeGerente = validaBanco.getNomeGerenciaMatricula(validaBanco.getNumberGerenciaPDV(matricula));
                        String filialNome = switch (filialOperador) {
                            case 1001 -> "Paulista";
                            case 1002 -> "Jd Ângela";
                            case 1003 -> "Liberdade";
                            default -> "Desconhecida";
                        };

                        discord_entrada.enviarEmbed("Abertura caixa", "Caixa da filial " + filialNome + " aberto valor **R$" + String.format("%.2f", money) + "**",
                                nomeGerente, dataHora, "Op: " + nomeOperador, urlWebhook);

                        operador.setFilial(filialOperador);
                        new interface_controller().setVenda(true);
                        Vendas vender = new Vendas();
                        vender.setVisible(true);
                        this.setVisible(false);
                        break;

                    } catch (NumberFormatException ex) {
                        JOptionPane.showMessageDialog(null, "Digite valores válidos!", "Erro", JOptionPane.ERROR_MESSAGE);
                        btn_logar.setEnabled(true);
                    }
                }

            } else if (resultado == 0) {
                JOptionPane.showMessageDialog(null, "Login ou senha incorretos!");
                btn_logar.setEnabled(true);
            } else if(operador.getNumberOperador() != 0) {
            }else{
                JOptionPane.showMessageDialog(null, "Erro ao conectar ao banco de dados.");
                discord_erro_pdv erroDiscord = new discord_erro_pdv();
                erroDiscord.enviarEmbed("Erro desconhecido", "Problema para estabelecer conexão com o banco de dados", "login() -> linha 276", "Média", LocalDateTime.now().format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
                btn_logar.setEnabled(true);
            }

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(null, "Digite um número válido!", "Erro", JOptionPane.ERROR_MESSAGE);
            btn_logar.setEnabled(true);
        } catch (Exception e) {
            discord_erro_pdv erroDiscord = new discord_erro_pdv();
            String tipoErro = e.getClass().getSimpleName();
            String mensagemErro = e.getMessage() != null ? e.getMessage() : "Sem mensagem específica.";
            erroDiscord.enviarEmbed("Erro desconhecido", mensagemErro, "login() -> validarLogin()", "Alta", LocalDateTime.now().format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
            JOptionPane.showMessageDialog(null, "Ocorreu um erro: " + mensagemErro, "Erro", JOptionPane.ERROR_MESSAGE);
            btn_logar.setEnabled(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        // Pode ser usado para outros eventos no futuro
    }
}
