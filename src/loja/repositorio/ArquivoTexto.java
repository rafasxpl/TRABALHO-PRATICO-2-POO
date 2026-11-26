package loja.repositorio;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import loja.excecao.PersistenciaException;

public final class ArquivoTexto {

    private ArquivoTexto() {
    }

    public static List<String> lerLinhas(String caminho) throws PersistenciaException {
        Path arquivo = Paths.get(caminho);
        if (!Files.exists(arquivo)) {
            return new ArrayList<>(); // primeira execucao: arquivo ainda nao existe
        }
        try {
            return Files.readAllLines(arquivo, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao ler o arquivo " + caminho, e);
        }
    }

    public static void gravarLinhas(String caminho, List<String> linhas) throws PersistenciaException {
        Path arquivo = Paths.get(caminho);
        try {
            if (arquivo.getParent() != null) {
                Files.createDirectories(arquivo.getParent());
            }
            Files.write(arquivo, linhas, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw new PersistenciaException("Erro ao gravar o arquivo " + caminho, e);
        }
    }
}
