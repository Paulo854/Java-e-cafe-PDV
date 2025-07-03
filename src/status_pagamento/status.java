package status_pagamento;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.sql.DataSource;
import javax.swing.JOptionPane;

import org.apache.commons.dbcp2.BasicDataSource;

import conexao_controle.discord_erro_pdv;
import io.github.cdimascio.dotenv.Dotenv;

public class status {
    private final DataSource dataSource;
    private volatile ThreadPoolExecutor executor;
    private static final int MAX_RETRIES = 5;
    private static final int INITIAL_DELAY_MS = 1000;
    private static final int MAX_DELAY_MS = 30000;
    private static final int MAX_CONNECTIONS = 5;
    private static final int QUEUE_CAPACITY = 100;
    public DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    public Dotenv dotenv = Dotenv.configure()
            .directory("./src")
            .filename(".env")
            .load();

    public status() {
        Dotenv dotenv = Dotenv.configure()
            .directory("./src")
            .filename(".env")
            .load();

        BasicDataSource ds = new BasicDataSource();
        ds.setUrl("jdbc:mysql://" + dotenv.get("HOST_BD") + ":3306/" + dotenv.get("BACODEDADOS_BD"));
        ds.setUsername(dotenv.get("USER_BD"));
        ds.setPassword(dotenv.get("PASSWORD_BD"));
        ds.setMaxTotal(MAX_CONNECTIONS);
        this.dataSource = ds;

        initializeThreadPool();
    }

    private synchronized void initializeThreadPool() {
        if (executor == null || executor.isShutdown()) {
            BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(QUEUE_CAPACITY);
            executor = new ThreadPoolExecutor(
                MAX_CONNECTIONS,
                MAX_CONNECTIONS,
                30L, TimeUnit.SECONDS,
                workQueue,
                new ThreadPoolExecutor.CallerRunsPolicy()
            );
        }
    }

    public void pagamentoOkay(int cpf, int quantidade) {
        if (executor == null || executor.isShutdown()) {
            initializeThreadPool();
        }

        executor.execute(() -> {
            int retryCount = 0;
            boolean success = false;

            while (!success && retryCount < MAX_RETRIES) {
                try (Connection conn = dataSource.getConnection()) {
                    conn.setAutoCommit(false);

                    String sql = "UPDATE cliente SET pontos = pontos + ? WHERE cpf = ?";

                    try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                        stmt.setInt(1, quantidade);
                        stmt.setInt(2, cpf);


                        int linhasAfetadas = stmt.executeUpdate();

                        if (linhasAfetadas > 0) {
                            conn.commit();
                            System.out.println("Pagamento confirmado. Pontos adicionados.");
                            JOptionPane.showMessageDialog(null, "Compra concluída com sucesso!");
                        } else {
                            conn.rollback();
                            if (produtoExiste(conn, cpf)) {
                            } else {
                                System.out.println("Cliente não encontrado");
                            }
                        }

                        success = true;
                    }
                } catch (SQLException e) {
                    retryCount++;
                    System.err.println("Erro (" + retryCount + "/" + MAX_RETRIES + "): " + cpf);
                    discord_erro_pdv erroDiscord = new discord_erro_pdv();
                    String tipoErro = e.getClass().getSimpleName();
                    String mensagemErro = e.getMessage() != null ? e.getMessage() : "Sem mensagem específica.";
                    erroDiscord.enviarEmbed("Erro desconhecido", mensagemErro, "status() -> pagamentoOkay() -> linha 98", "Alta", LocalDateTime.now().format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
                    e.printStackTrace();

                    if (retryCount < MAX_RETRIES) {
                        try {
                            TimeUnit.MILLISECONDS.sleep(calcularDelay(retryCount));
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            discord_erro_pdv erroDiscord1 = new discord_erro_pdv();
                            String tipoErro1 = e.getClass().getSimpleName();
                            String mensagemErro1 = e.getMessage() != null ? e.getMessage() : "Sem mensagem específica.";
                            erroDiscord1.enviarEmbed("Erro desconhecido", mensagemErro1, "status() -> pagamentoOkay() -> linha 98", "Alta", LocalDateTime.now().format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
                            break;
                        }
                    }
                }
            }

            if (!success) {
                System.err.println("Falha após " + MAX_RETRIES + " tentativas: " + cpf);
            }
        });
    }


    private boolean produtoExiste(Connection conn, int cpf) throws SQLException {
        String sql = "SELECT 1 FROM cliente WHERE cpf = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cpf);
            return stmt.executeQuery().next();
        }
    }

    private int calcularDelay(int tentativa) {
        return Math.min(INITIAL_DELAY_MS * (int) Math.pow(2, tentativa - 1), MAX_DELAY_MS);
    }

    public synchronized void shutdown() {
        if (executor != null && !executor.isShutdown()) {
            executor.shutdown();
            try {
                if (!executor.awaitTermination(60, TimeUnit.SECONDS)) {
                    executor.shutdownNow();
                }
            } catch (InterruptedException e) {
            	discord_erro_pdv erroDiscord = new discord_erro_pdv();
                String tipoErro = e.getClass().getSimpleName();
                String mensagemErro = e.getMessage() != null ? e.getMessage() : "Sem mensagem específica.";
                erroDiscord.enviarEmbed("Erro desconhecido", mensagemErro, "status() -> shutdown() -> linha 140", "Alta", LocalDateTime.now().format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
                executor.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }

        if (dataSource instanceof BasicDataSource) {
            try {
                ((BasicDataSource) dataSource).close();
            } catch (SQLException e) {
                System.err.println("Erro ao fechar pool de conexões");
                discord_erro_pdv erroDiscord = new discord_erro_pdv();
                String tipoErro = e.getClass().getSimpleName();
                String mensagemErro = e.getMessage() != null ? e.getMessage() : "Sem mensagem específica.";
                erroDiscord.enviarEmbed("Erro desconhecido", mensagemErro, "status() -> shutdown() -> linha 160", "Alta", LocalDateTime.now().format(formatter), "Segurança PDVs", dotenv.get("WEBHOOK_ERROS"));
                e.printStackTrace();
            }
        }
    }

    public synchronized void restartThreadPool() {
        shutdown();
        initializeThreadPool();
    }
}
