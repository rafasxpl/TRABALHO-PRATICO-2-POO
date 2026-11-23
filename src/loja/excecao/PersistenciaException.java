package loja.excecao;

// Erro ao ler ou gravar arquivo .txt, ou linha corrompida.
public class PersistenciaException extends LojaException {
    public PersistenciaException(String mensagem) {
        super(mensagem);
    }

    // Usado para encapsular uma IOException
    public PersistenciaException(String mensagem, Throwable causa) {
        super(mensagem, causa);
    }
}
