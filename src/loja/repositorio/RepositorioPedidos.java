package loja.repositorio;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import loja.excecao.PersistenciaException;
import loja.excecao.ProdutoNaoEncontradoException;
import loja.modelo.pagamento.FormaPagamento;
import loja.modelo.pagamento.PagamentoBoleto;
import loja.modelo.pagamento.PagamentoCartao;
import loja.modelo.pagamento.PagamentoPix;
import loja.modelo.pedido.ItemPedido;
import loja.modelo.pedido.Pedido;
import loja.modelo.pedido.StatusPedido;
import loja.modelo.produto.Produto;
import loja.modelo.usuario.Cliente;
import loja.modelo.usuario.Usuario;


public class RepositorioPedidos extends Repositorio<Pedido> {

    private final RepositorioProdutos repositorioProdutos;
    private final RepositorioUsuarios repositorioUsuarios;

    public RepositorioPedidos(String caminhoArquivo, RepositorioProdutos repositorioProdutos,
                               RepositorioUsuarios repositorioUsuarios) {
        super(caminhoArquivo);
        this.repositorioProdutos = repositorioProdutos;
        this.repositorioUsuarios = repositorioUsuarios;
    }

    // Proximo numero de pedido disponivel (maior numero ja usado + 1)
    public int proximoNumero() {
        int maior = 0;
        for (Pedido pedido : itens) {
            if (pedido.getNumero() > maior) {
                maior = pedido.getNumero();
            }
        }
        return maior + 1;
    }

    public List<Pedido> listarPorCliente(Cliente cliente) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido pedido : itens) {
            if (pedido.getCliente().getLogin().equals(cliente.getLogin())) {
                resultado.add(pedido);
            }
        }
        return resultado;
    }

    public List<Pedido> listarPorStatus(StatusPedido status) {
        List<Pedido> resultado = new ArrayList<>();
        for (Pedido pedido : itens) {
            if (pedido.getStatus() == status) {
                resultado.add(pedido);
            }
        }
        return resultado;
    }

    @Override
    protected Pedido deLinha(String linha) throws PersistenciaException {
        String[] campos = linha.split(";", -1);
        try {
            int numero = Integer.parseInt(campos[0]);
            String loginCliente = campos[1];
            StatusPedido status = StatusPedido.valueOf(campos[2]);
            LocalDate data = LocalDate.parse(campos[3]);
            double subtotal = Double.parseDouble(campos[4]);
            double frete = Double.parseDouble(campos[5]);
            double total = Double.parseDouble(campos[6]);
            FormaPagamento formaPagamento = formaPagamentoDeToken(campos[7]);

            Usuario usuario = repositorioUsuarios.buscarPorLogin(loginCliente);
            if (!(usuario instanceof Cliente)) {
                throw new PersistenciaException(
                        "Cliente '" + loginCliente + "' do pedido " + numero + " não encontrado");
            }
            Cliente cliente = (Cliente) usuario; // DOWNCASTING: só sabemos que é Cliente depois do instanceof

            List<ItemPedido> itensPedido = new ArrayList<>();
            if (!campos[8].isBlank()) {
                for (String tokenItem : campos[8].split("\\|")) {
                    String[] partes = tokenItem.split(":");
                    int codigoProduto = Integer.parseInt(partes[0]);
                    int quantidade = Integer.parseInt(partes[1]);
                    double precoUnitario = Double.parseDouble(partes[2]);
                    Produto produto = repositorioProdutos.buscarPorCodigo(codigoProduto);
                    itensPedido.add(new ItemPedido(produto, quantidade, precoUnitario));
                }
            }

            return new Pedido(numero, cliente, itensPedido, subtotal, frete, total,
                    formaPagamento, status, data);
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException | ProdutoNaoEncontradoException e) {
            throw new PersistenciaException("Linha de pedido corrompida: " + linha, e);
        }
    }

    @Override
    protected String paraLinha(Pedido pedido) {
        StringBuilder itensToken = new StringBuilder();
        List<ItemPedido> itensPedido = pedido.getItens();
        for (int i = 0; i < itensPedido.size(); i++) {
            ItemPedido item = itensPedido.get(i);
            itensToken.append(item.getProduto().getCodigo()).append(':')
                    .append(item.getQuantidade()).append(':')
                    .append(item.getPrecoUnitario());
            if (i < itensPedido.size() - 1) {
                itensToken.append('|');
            }
        }
        return pedido.getNumero() + ";"
                + pedido.getCliente().getLogin() + ";"
                + pedido.getStatus() + ";"
                + pedido.getData() + ";"
                + pedido.getSubtotal() + ";"
                + pedido.getFrete() + ";"
                + pedido.getTotal() + ";"
                + pedido.getFormaPagamento().paraLinha() + ";"
                + itensToken;
    }

    private FormaPagamento formaPagamentoDeToken(String token) throws PersistenciaException {
        String[] partes = token.split(",");
        switch (partes[0]) {
            case "PIX":
                return new PagamentoPix();
            case "BOLETO":
                return new PagamentoBoleto();
            case "CARTAO":
                try {
                    return new PagamentoCartao(Integer.parseInt(partes[1]));
                } catch (loja.excecao.DadoInvalidoException e) {
                    throw new PersistenciaException("Forma de pagamento inválida: " + token, e);
                }
            default:
                throw new PersistenciaException("Forma de pagamento desconhecida: " + token);
        }
    }
}
