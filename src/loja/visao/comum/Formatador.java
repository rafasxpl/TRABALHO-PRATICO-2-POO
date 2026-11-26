package loja.visao.comum;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import loja.excecao.DadoInvalidoException;

public final class Formatador {

    private static final NumberFormat FORMATO_MOEDA = NumberFormat.getCurrencyInstance(new Locale("pt", "BR"));
    private static final DateTimeFormatter FORMATO_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private Formatador() {
    }

    public static String formatarMoeda(double valor) {
        return FORMATO_MOEDA.format(valor);
    }

    public static String formatarData(LocalDate data) {
        return data == null ? "-" : data.format(FORMATO_DATA);
    }

    public static double lerDoubleSeguro(String texto, String nomeCampo) throws DadoInvalidoException {
        try {
            return Double.parseDouble(texto.replace(",", ".").trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new DadoInvalidoException("\nO campo '" + nomeCampo + "' precisa ser um numero valido\n");
        }
    }
    public static int lerIntSeguro(String texto, String nomeCampo) throws DadoInvalidoException {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException | NullPointerException e) {
            throw new DadoInvalidoException("\nO campo '" + nomeCampo + "' precisa ser um numero inteiro valido\n");
        }
    }
}
