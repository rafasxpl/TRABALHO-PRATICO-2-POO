package loja.visao.comum;

import java.awt.BorderLayout;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

public abstract class TelaBase extends JFrame {

    protected TelaBase(String titulo, int largura, int altura) {
        super(titulo);
        setSize(largura, altura);
        setLocationRelativeTo(null); // centraliza a janela
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
    }

    protected abstract void montarComponentes();

    protected void inicializar() {
        montarComponentes();
        revalidate();
        repaint();
    }
    protected void mostrarErro(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Erro", JOptionPane.ERROR_MESSAGE);
    }

    protected void mostrarAviso(String mensagem) {
        JOptionPane.showMessageDialog(this, mensagem, "Aviso", JOptionPane.INFORMATION_MESSAGE);
    }
}
