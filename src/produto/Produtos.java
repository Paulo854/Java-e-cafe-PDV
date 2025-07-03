package produto;

public class Produtos {
    private final String nome;
    private final int quantidade;
    private final double preco;
    private final String detalhes;

    public Produtos(String nome, int quantidade, double preco, String detalhes) {
        this.nome = nome;
        this.quantidade = quantidade;
        this.preco = preco;
        this.detalhes = detalhes;
    }

    // Getters
    public String getNome() { return nome; }
    public int getQuantidade() { return quantidade; }
    public double getPreco() { return preco; } 
    public String getDetalhes() { return detalhes; }

    // Para converter de Object[][]
    public static Produtos fromArray(Object[][] array) {
        return new Produtos(
            String.valueOf(array[0][0]),
            ((Number) array[0][1]).intValue(),
            ((Number) array[0][2]).doubleValue(),
            String.valueOf(array[0][3])
        );
    }

    public Object[] getDadosProduto() {
        return new Object[]{nome, quantidade, preco, detalhes};
    }
    
    // Para converter para Object[][]
    public Object[][] toArray() {
        return new Object[][]{{nome, quantidade, preco, detalhes}};
    }
}