package loja.modelo.pedido;

// Situação de um pedido
public enum StatusPedido {
    AGUARDANDO_PAGAMENTO("Aguardando pagamento"),
    PAGO("Pago"),
    CANCELADO("Cancelado");

    private final String descricao;

    StatusPedido(String descricao) {
        this.descricao = descricao;
    }

    // Texto para exibir nas telas
    public String getDescricao() {
        return descricao;
    }
}
