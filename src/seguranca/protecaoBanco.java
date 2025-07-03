package seguranca;

public class protecaoBanco {
    private int contagemCliques = 0;
    private long tempoUltimoClique = 0;
    private boolean bloqueado = false;

    private static final int MAX_CLIQUES = 2;
    private static final int INTERVALO_MS = 500;
    private static final int TEMPO_BLOQUEIO_MS = 5000;

    public synchronized boolean isBloqueado() {
        return bloqueado;
    }

    public synchronized void clique(Runnable acao) {
        long agora = System.currentTimeMillis();

        if (bloqueado) {
            System.out.println("Aguardando desbloqueio...");
            return;
        }

        if (agora - tempoUltimoClique > INTERVALO_MS) {
            contagemCliques = 0;
        }

        contagemCliques++;
        tempoUltimoClique = agora;

        if (contagemCliques > MAX_CLIQUES) {
            System.out.println("Clique rápido demais! Aguardando 3 segundos...");
            bloqueado = true;

            new Thread(() -> {
                try {
                    Thread.sleep(TEMPO_BLOQUEIO_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                synchronized (protecaoBanco.this) {
                    bloqueado = false;
                    contagemCliques = 0;
                    System.out.println("Sistema desbloqueado.");
                }
            }).start();
            return;
        }

        if (acao != null) {
            acao.run();
        }
    }
}
