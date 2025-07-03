package inteface;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.*;
import java.nio.channels.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import io.github.cdimascio.dotenv.Dotenv;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import conect_banco.TesteConexaoMySQL;

import javax.swing.JWindow;

import conexao_controle.conect_internet;
import conexao_controle.discord_erro_pdv;
import controladores.interface_controller;



public class TelaCarregamento extends JWindow { // Extende JWindow ou JFrame

    private JProgressBar barraProgresso;
    private JLabel statusLabel;
    public conect_internet conect = new conect_internet();
    public TesteConexaoMySQL banco = new TesteConexaoMySQL();
    private static FileLock lock;
    private static FileChannel channel;		
    File envFile = new File("./src/.env");
    public  Dotenv dotenv = Dotenv.configure()
            .directory("./src") 
            .filename(".env")
            .load();

    public TelaCarregamento() {
        JPanel painel = new JPanel(new BorderLayout());
        setAlwaysOnTop(true); 
        painel.setBackground(new Color(40, 40, 40)); // fundo escuro

        // Logo no centro
        JLabel logo = new JLabel();
        logo.setHorizontalAlignment(SwingConstants.CENTER);
        try {
            BufferedImage imagem = ImageIO.read(getClass().getResource("logo1.png")); 
            Image redimensionada = imagem.getScaledInstance(80, 80, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(redimensionada));
        } catch (IOException e) {
            logo.setText("LOGO");
            logo.setForeground(Color.WHITE);
        }

        // Barra de progresso
        barraProgresso = new JProgressBar(0, 100);
        barraProgresso.setForeground(Color.ORANGE);
        barraProgresso.setBackground(Color.DARK_GRAY);
        barraProgresso.setStringPainted(true);
        barraProgresso.setBorderPainted(false);

        // Texto de status
        statusLabel = new JLabel("Iniciando...", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
        statusLabel.setForeground(Color.WHITE);
        statusLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Layout final
        painel.add(logo, BorderLayout.CENTER);
        painel.add(barraProgresso, BorderLayout.SOUTH);
        painel.add(statusLabel, BorderLayout.NORTH);

        // Ajuste da tela
        int largura = 400;
        int altura = 250;
        Dimension tela = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds((tela.width - largura) / 2, (tela.height - altura) / 2, largura, altura);

        setContentPane(painel);
        setVisible(true);

        // Começar carregamento
        iniciarCarregamento();
    }

    private void iniciarCarregamento() {
        SwingWorker<Void, String> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                // Verificação se o programa já está rodando
            	System.out.println("Verificando lock...");
            	File file = new File(System.getProperty("java.io.tmpdir") + "/program.lock");
            	FileChannel channel = new RandomAccessFile(file, "rw").getChannel();
            	FileLock lock = channel.tryLock();
                System.out.println("Lock OK");
                
               /*interface_controller status = new interface_controller();
                if(status.getStatusCarregamento() == true) {
                	status.setLogin(true);
                	status.setCarregamento(false);
                	System.out.println("Sistema login liberado");
                }*/

                if (lock == null) {
                    publish("<html><font face='Segoe UI Emoji'>⚠️ O programa já está em execução.</font></html>");
                    Thread.sleep(10000); // Mostra a mensagem por 3 segundos
                    System.exit(1);
                }

                // Mensagens a serem exibidas com emojis (usando HTML)
                String[] mensagens = {
                    "<html><font face='Segoe UI Emoji'>🚀 Verificando conexão com o banco de dados...</font></html>",
                    "<html><font face='Segoe UI Emoji'>⚙️ Carregando configurações...</font></html>",
                    "<html><font face='Segoe UI Emoji'>💥 Validando permissões...</font></html>",
                    "<html><font face='Segoe UI Emoji'>⚖️ Inicializando módulos...</font></html>",
                    "<html><font face='Segoe UI Emoji'>✅ Tudo pronto!</font></html>"
                };
                
                System.out.println("Verificando conexões...");
                // verifica cada etapa
                for (int i = 0; i < mensagens.length; i++) {
                	//verifica as conexões
                	if(!conect.temConexao()) {
                    	publish("<html><font face='Segoe UI Emoji'>❌ Você está sem conexão com a internet</font></html>");
                    	 Thread.sleep(10000);
                         System.out.println("Mensagem " + i);
                         System.exit(1);
                    }
                	if(i == 0 && !envFile.exists() && !envFile.isFile()) {
                		publish("<html><font face='Segoe UI Emoji'>❌ Identificamos a falta de um arquivo de configuração</font></html>");
                		Thread.sleep(10000);
                        System.out.println("Mensagem " + i);
                        System.exit(1);
                	}
                	if (i == 0 && (!banco.conect())) {
                    	discord_erro_pdv erroDiscord = new discord_erro_pdv();
                    	DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
                    	LocalDateTime agora = LocalDateTime.now();
                        String data = agora.format(formatter);
                        publish("<html><font face='Segoe UI Emoji'>❌ Erro ao conectar ao banco de dados.</font></html>");
                        erroDiscord.enviarEmbed("Iniciação do PDV","O PDV está sofrendo dificulade para se conectar ao serviços do Java&Café", "TelaCarregamento()", "Média", data, "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
                        Thread.sleep(10000);
                        System.out.println("Mensagem " + i);
                        System.exit(1);
                        
                        break; // Interrompe o loop se não houver conexão
                    }

                    publish(mensagens[i]);
                    Thread.sleep(1000); // Simulando o tempo de cada etapa
                    barraProgresso.setValue((i + 1) * 100 / mensagens.length);
                }
                System.out.println("Conexão OK");

                
                return null;
            }

            @Override
            protected void process(java.util.List<String> chunks) {
                String mensagemAtual = chunks.get(chunks.size() - 1);
                statusLabel.setText(mensagemAtual);
            }

            @Override
            protected void done() {
                fechar();

                // Abrir tela principal
                SwingUtilities.invokeLater(() -> {
                    Login lg = new Login();
                    lg.setVisible(true);
                });
            }
        };

        worker.execute();
      
    }
        
    public void fechar() {
        setVisible(false);
        dispose();
    }

    public static void main(String[] args) {
        new TelaCarregamento();
    }
}
