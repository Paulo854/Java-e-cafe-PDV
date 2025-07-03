package inteface;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;

public class Estilizar {
    // Estilo moderno
    public void aplicarEstilo() {
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("Label.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("TextField.font", new Font("SansSerif", Font.PLAIN, 13));
        UIManager.put("Table.font", new Font("SansSerif", Font.PLAIN, 13));
    }

    // Painel customizado
    public JPanel estilizarPainel() {
        JPanel painel = new JPanel();
        painel.setLayout(new BoxLayout(painel, BoxLayout.Y_AXIS));
        painel.setBorder(new EmptyBorder(20, 20, 20, 20));
        painel.setBackground(new Color(245, 245, 245));
        return painel;
    }

    // Botão customizado
    public JButton estilizarBotao(String texto) {
        JButton botao = new JButton(texto);
        botao.setBackground(new Color(100, 149, 237));  // Azul
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(130, 30));

        // Adicionando a ação baseada no texto do botão
        botao.addActionListener((ActionEvent e) -> {
            System.out.println(texto);
        });
        
        return botao;
    }
    // Botão customizado
    public JButton estilizarBotao(String texto, int x, int y, int tamanho) {
        JButton botao = new JButton(texto);
        botao.setFont(new Font("SansSerif", Font.BOLD, tamanho));
        botao.setBackground(new Color(100, 149, 237));
        botao.setForeground(Color.WHITE);
        botao.setFocusPainted(false);
        botao.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        botao.setPreferredSize(new Dimension(x, y));
        botao.setMaximumSize(new Dimension(x, y));

        // Adicionando a ação baseada no texto do botão
        botao.addActionListener((ActionEvent e) -> {
            System.out.println(texto);
        });
        
        return botao;
    }

    // Tabela customizada
    public void estilizarTabela(JTable tabela) {
        tabela.setRowHeight(30);
        tabela.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        // Ajusta os tamanhos das colunas de Item, Qtd, Preço, Detalhes e dos botões Editar e Remover
        tabela.getColumnModel().getColumn(0).setPreferredWidth(120);
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
}
