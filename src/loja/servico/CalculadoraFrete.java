package loja.servico;

import java.util.List;
import loja.modelo.pedido.ItemCarrinho;

/*
Regra do frete: 
- Grátis se o subtotal for >= R$300,00 ou se o carrinho só tiver produtos digitais
- Se não: R$5,00 (uma vez por pedido) + R$2,00 por kg do peso total
*/
public class CalculadoraFrete {
    public static final double LIMITE_FRETE_GRATIS = 300.00;
    public static final double TAXA_FIXA = 5.00;
    public static final double VALOR_POR_KG = 2.00;

    public double calcular(List<ItemCarrinho> itens, double subtotal) {
        if (subtotal >= LIMITE_FRETE_GRATIS) {
            return 0;
        }

        double pesoTotal = 0;

        for (ItemCarrinho item : itens) {
            pesoTotal += item.getProduto().getPesoParaFrete() * item.getQuantidade();
        }
        if (pesoTotal <= 0) {
            return 0; // só produtos digitais
        }
        
        return Math.round((TAXA_FIXA + VALOR_POR_KG * pesoTotal) * 100.0) / 100.0;
    }
}
