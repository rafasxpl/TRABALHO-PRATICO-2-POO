package loja.repositorio;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import loja.excecao.PersistenciaException;


public abstract class Repositorio<T> {

    protected List<T> itens = new ArrayList<>();
    private final String caminhoArquivo;

    protected Repositorio(String caminhoArquivo) {
        this.caminhoArquivo = caminhoArquivo;
    }

    // Converte uma linha do arquivo de texto no objeto correspondente
    protected abstract T deLinha(String linha) throws PersistenciaException;

    // Converte um objeto em uma linha de texto para gravar no arquivo
    protected abstract String paraLinha(T item);

    // o arquivo de texto e recarrega a lista em memoria (lista fica vazia se o arquivo nao existir)
    public void carregar() throws PersistenciaException {
        itens.clear();
        List<String> linhas = ArquivoTexto.lerLinhas(caminhoArquivo);
        for (String linha : linhas) {
            if (linha == null || linha.isBlank()) {
                continue;
            }
            itens.add(deLinha(linha));
        }
    }

    // Grava a lista em memoria no arquivo de texto, sobrescrevendo o conteudo anterior
    public void salvar() throws PersistenciaException {
        List<String> linhas = new ArrayList<>();
        for (T item : itens) {
            linhas.add(paraLinha(item));
        }
        ArquivoTexto.gravarLinhas(caminhoArquivo, linhas);
    }

    public void adicionar(T item) {
        itens.add(item);
    }

    public void remover(T item) {
        itens.remove(item);
    }

    // Lista nao modificavel dos itens em memoria
    public List<T> listar() {
        return Collections.unmodifiableList(itens);
    }
}
