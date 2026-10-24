package br.com.conde.deslizante;

/**
 * Chutes do quanto falta, usados para guiar a busca.
 *
 * <p>Um chute serve se ele nunca passa do numero real de jogadas: um chute que
 * exagera leva a busca a abandonar o caminho curto. Como a {@link Varredura}
 * conhece a distancia exata de todos os arranjos, aqui nao se argumenta que o
 * chute e seguro: confere-se, um por um.
 */
public enum Heuristica {

    /** Quantas pecas nao estao no lugar. Cada uma precisa de pelo menos uma jogada. */
    FORA_DO_LUGAR("pecas fora do lugar") {
        @Override
        public int estimativa(Tabuleiro tabuleiro) {
            int fora = 0;
            for (int casa = 0; casa < tabuleiro.casas(); casa++) {
                int peca = tabuleiro.peca(casa);
                if (peca != 0 && casa != tabuleiro.casaCerta(peca)) {
                    fora++;
                }
            }
            return fora;
        }
    },

    /** A soma dos passos que cada peca precisa dar ate a casa dela. */
    MANHATTAN("distancia de quarteirao") {
        @Override
        public int estimativa(Tabuleiro tabuleiro) {
            int colunas = tabuleiro.colunas();
            int soma = 0;
            for (int casa = 0; casa < tabuleiro.casas(); casa++) {
                int peca = tabuleiro.peca(casa);
                if (peca == 0) {
                    continue;
                }
                int certa = tabuleiro.casaCerta(peca);
                soma += Math.abs(casa / colunas - certa / colunas)
                        + Math.abs(casa % colunas - certa % colunas);
            }
            return soma;
        }
    },

    /**
     * A anterior mais duas jogadas por conflito linear, contando os conflitos
     * do jeito que parece obvio: cada par de pecas trocado dentro da linha (ou
     * coluna) de destino custa duas jogadas a mais.
     *
     * <p>Este jeito esta aqui porque e o primeiro que eu escrevi, e porque a
     * varredura tem como dizer se ele e seguro ou nao.
     */
    CONFLITO_POR_PARES("conflito contado por pares") {
        @Override
        public int estimativa(Tabuleiro tabuleiro) {
            return MANHATTAN.estimativa(tabuleiro) + 2 * conflitos(tabuleiro, true);
        }
    },

    /**
     * O conflito linear como ele de fato se conta: numa linha, o que custa duas
     * jogadas a mais e cada peca que PRECISA sair da linha e voltar, e esse
     * numero e o de pecas menos a maior sequencia que ja esta em ordem. Tres
     * pecas invertidas formam tres pares, mas tirar UMA delas resolve.
     */
    CONFLITO_LINEAR("quarteirao + conflito linear") {
        @Override
        public int estimativa(Tabuleiro tabuleiro) {
            return MANHATTAN.estimativa(tabuleiro) + 2 * conflitos(tabuleiro, false);
        }
    };

    private final String rotulo;

    Heuristica(String rotulo) {
        this.rotulo = rotulo;
    }

    public String rotulo() {
        return rotulo;
    }

    public abstract int estimativa(Tabuleiro tabuleiro);

    private static int conflitos(Tabuleiro tabuleiro, boolean contandoPares) {
        int linhas = tabuleiro.linhas();
        int colunas = tabuleiro.colunas();
        int total = 0;
        for (int linha = 0; linha < linhas; linha++) {
            total += conflitosNaFaixa(tabuleiro, linha, colunas, true, contandoPares);
        }
        for (int coluna = 0; coluna < colunas; coluna++) {
            total += conflitosNaFaixa(tabuleiro, coluna, linhas, false, contandoPares);
        }
        return total;
    }

    /** As pecas da faixa que ja estao na faixa de destino delas, na ordem em que aparecem. */
    private static int[] alvosNaFaixa(Tabuleiro tabuleiro, int faixa, int quantas,
                                      boolean porLinha) {
        int colunas = tabuleiro.colunas();
        int[] alvos = new int[quantas];
        int quantos = 0;
        for (int i = 0; i < quantas; i++) {
            int casa = porLinha ? faixa * colunas + i : i * colunas + faixa;
            int peca = tabuleiro.peca(casa);
            if (peca == 0) {
                continue;
            }
            int certa = tabuleiro.casaCerta(peca);
            int faixaCerta = porLinha ? certa / colunas : certa % colunas;
            if (faixaCerta == faixa) {
                alvos[quantos++] = porLinha ? certa % colunas : certa / colunas;
            }
        }
        return java.util.Arrays.copyOf(alvos, quantos);
    }

    private static int conflitosNaFaixa(Tabuleiro tabuleiro, int faixa, int quantas,
                                        boolean porLinha, boolean contandoPares) {
        int[] alvos = alvosNaFaixa(tabuleiro, faixa, quantas, porLinha);
        if (contandoPares) {
            int pares = 0;
            for (int i = 0; i < alvos.length; i++) {
                for (int j = i + 1; j < alvos.length; j++) {
                    if (alvos[i] > alvos[j]) {
                        pares++;
                    }
                }
            }
            return pares;
        }
        return alvos.length - maiorSequenciaEmOrdem(alvos);
    }

    /** O tamanho da maior subsequencia crescente: o que da para deixar parado. */
    private static int maiorSequenciaEmOrdem(int[] alvos) {
        int melhor = 0;
        int[] ate = new int[alvos.length];
        for (int i = 0; i < alvos.length; i++) {
            ate[i] = 1;
            for (int j = 0; j < i; j++) {
                if (alvos[j] < alvos[i] && ate[j] + 1 > ate[i]) {
                    ate[i] = ate[j] + 1;
                }
            }
            melhor = Math.max(melhor, ate[i]);
        }
        return melhor;
    }
}
