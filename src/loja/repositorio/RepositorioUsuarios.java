package loja.repositorio;

import java.util.HashMap;
import java.util.Map;

import loja.excecao.PersistenciaException;
import loja.modelo.usuario.Administrador;
import loja.modelo.usuario.Cliente;
import loja.modelo.usuario.Usuario;

public class RepositorioUsuarios extends Repositorio<Usuario> {

    private final Map<String, Usuario> indicePorLogin = new HashMap<>();

    public RepositorioUsuarios(String caminhoArquivo) {
        super(caminhoArquivo);
    }

    @Override
    public void carregar() throws PersistenciaException {
        super.carregar();
        reconstruirIndice();
    }

    @Override
    public void adicionar(Usuario usuario) {
        super.adicionar(usuario);
        indicePorLogin.put(usuario.getLogin(), usuario);
    }

    // Devolve o usuario com esse login, ou null se nao existir
    public Usuario buscarPorLogin(String login) {
        return indicePorLogin.get(login);
    }

    private void reconstruirIndice() {
        indicePorLogin.clear();
        for (Usuario usuario : itens) {
            indicePorLogin.put(usuario.getLogin(), usuario);
        }
    }

    @Override
    protected Usuario deLinha(String linha) throws PersistenciaException {
        String[] campos = linha.split(";", -1);
        try {
            String tipo = campos[0];
            int id = Integer.parseInt(campos[1]);
            String nome = campos[2];
            String login = campos[3];
            String senha = campos[4];

            if ("ADMIN".equals(tipo)) {
                return new Administrador(id, nome, login, senha);
            } else if ("CLIENTE".equals(tipo)) {
                String cpf = campos[5];
                String email = campos[6];
                String endereco = campos[7];
                return new Cliente(id, nome, login, senha, cpf, email, endereco);
            }
            throw new PersistenciaException("Tipo de usuário desconhecido na linha: " + linha);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            throw new PersistenciaException("Linha de usuário corrompida: " + linha, e);
        }
    }

    @Override
    protected String paraLinha(Usuario usuario) {
        return usuario.paraLinha();
    }
}
