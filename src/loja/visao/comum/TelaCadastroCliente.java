package loja.visao.comum;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import loja.excecao.LojaException;
import loja.servico.AutenticacaoServico;

public class TelaCadastroCliente extends TelaBase {

    private final AutenticacaoServico autenticacaoServico;

    private JTextField campoNome;
    private JTextField campoCpf;
    private JTextField campoEmail;
    private JTextField campoEndereco;
    private JTextField campoLogin;
    private JPasswordField campoSenha;

    public TelaCadastroCliente(AutenticacaoServico autenticacaoServico) {
        super("Loja Virtual - Cadastro de Cliente", 420, 380);
        this.autenticacaoServico = autenticacaoServico;
        inicializar();
    }

    @Override
    protected void montarComponentes() {
        JPanel painel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(6, 8, 6, 8);
        c.fill = GridBagConstraints.HORIZONTAL;

        campoNome = new JTextField(20);
        campoCpf = new JTextField(20);
        campoEmail = new JTextField(20);
        campoEndereco = new JTextField(20);
        campoLogin = new JTextField(20);
        campoSenha = new JPasswordField(20);

        String[] rotulos = {"Nome:", "CPF:", "E-mail:", "Endereço:", "Login:", "Senha:"};
        JComponent[] campos = {campoNome, campoCpf, campoEmail, campoEndereco, campoLogin, campoSenha};
        for (int i = 0; i < rotulos.length; i++) {
            c.gridx = 0;
            c.gridy = i;
            c.weightx = 0;
            painel.add(new JLabel(rotulos[i]), c);
            c.gridx = 1;
            c.weightx = 1;
            painel.add(campos[i], c);
        }

        JButton botaoConfirmar = new JButton("Cadastrar");
        botaoConfirmar.addActionListener(e -> cadastrar());

        add(painel, BorderLayout.CENTER);
        add(botaoConfirmar, BorderLayout.SOUTH);
    }

    private void cadastrar() {
        try {
            autenticacaoServico.cadastrarCliente(
                    campoNome.getText().trim(),
                    campoCpf.getText().trim(),
                    campoEmail.getText().trim(),
                    campoEndereco.getText().trim(),
                    campoLogin.getText().trim(),
                    new String(campoSenha.getPassword()));
            mostrarAviso("Cadastro realizado com sucesso! Agora faça login.");
            dispose();
        } catch (LojaException e) {
            mostrarErro(e.getMessage());
        }
    }
}
