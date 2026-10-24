package br.com.conde.deslizante;

import java.util.ArrayList;
import java.util.List;

/**
 * A busca em largura a partir do estado resolvido, ate acabar.
 *
 * <p>Nada e amostrado: a varredura visita todos os arranjos que podem ser
 * alcancados e guarda, para cada um, a distancia exata ate a solucao. Dai saem
 * o tamanho do componente, o diametro (o famoso "numero de Deus" do
 * quebra-cabeca) e quantos arranjos existem a cada distancia.
 */
public final class Varredura {

    private static final byte NAO_VISITADO = -1;

    private final int linhas;
    private final int colunas;
    private final Ranque ranque;
    private final byte[] distancia;
    private final int alcancados;
    private final int diametro;
    private final boolean comecouNoResolvido;

    public Varredura(int linhas, int colunas) {
        this(Tabuleiro.resolvido(linhas, colunas));
    }

    /** Varre o componente de quem comeca, que nem sempre contem o resolvido. */
    public Varredura(Tabuleiro comeco) {
        this.linhas = comeco.linhas();
        this.colunas = comeco.colunas();
        this.ranque = new Ranque(comeco.casas());
        this.distancia = new byte[(int) ranque.total()];
        java.util.Arrays.fill(distancia, NAO_VISITADO);

        List<Tabuleiro> borda = new ArrayList<>();
        borda.add(comeco);
        distancia[ranque.indiceDe(comeco)] = 0;
        int vistos = 1;
        int passo = 0;

        while (!borda.isEmpty()) {
            List<Tabuleiro> proxima = new ArrayList<>();
            for (Tabuleiro atual : borda) {
                for (Tabuleiro vizinho : atual.vizinhos()) {
                    int indice = ranque.indiceDe(vizinho);
                    if (distancia[indice] == NAO_VISITADO) {
                        distancia[indice] = (byte) (passo + 1);
                        proxima.add(vizinho);
                        vistos++;
                    }
                }
            }
            if (!proxima.isEmpty()) {
                passo++;
            }
            borda = proxima;
        }
        this.alcancados = vistos;
        this.diametro = passo;
        this.comecouNoResolvido = comeco.estaResolvido();
    }

    public int linhas() {
        return linhas;
    }

    public int colunas() {
        return colunas;
    }

    /** Quantos arranjos existem ao todo, alcancaveis ou nao. */
    public int arranjos() {
        return distancia.length;
    }

    public int alcancaveis() {
        return alcancados;
    }

    /** A maior distancia ate a solucao, dentro do que da para alcancar. */
    public int diametro() {
        return diametro;
    }

    public boolean alcanca(Tabuleiro tabuleiro) {
        return distancia[ranque.indiceDe(tabuleiro)] != NAO_VISITADO;
    }

    /** Jogadas minimas ate a solucao, ou -1 se nao der para chegar la. */
    public int jogadasAte(Tabuleiro tabuleiro) {
        return distancia[ranque.indiceDe(tabuleiro)];
    }

    /** Quantos arranjos a cada distancia, do zero ao diametro. */
    public long[] distribuicao() {
        long[] contagem = new long[diametro + 1];
        for (byte d : distancia) {
            if (d != NAO_VISITADO) {
                contagem[d]++;
            }
        }
        return contagem;
    }

    /** Os arranjos que ficam o mais longe possivel da solucao. */
    public List<Tabuleiro> maisDistantes() {
        List<Tabuleiro> saida = new ArrayList<>();
        for (int i = 0; i < distancia.length; i++) {
            if (distancia[i] == diametro) {
                saida.add(ranque.tabuleiroDe(linhas, colunas, i));
            }
        }
        return saida;
    }

    /** A media de jogadas sobre todos os arranjos alcancaveis. */
    public double mediaDeJogadas() {
        long soma = 0;
        for (byte d : distancia) {
            if (d != NAO_VISITADO) {
                soma += d;
            }
        }
        return (double) soma / alcancados;
    }

    /**
     * Confronta a varredura com a {@link Paridade}, arranjo por arranjo, e
     * devolve quantas vezes os dois discordaram.
     */
    public int discordanciasComAParidade() {
        if (!comecouNoResolvido) {
            throw new IllegalStateException(
                    "a paridade fala de chegar ao resolvido: so da para confrontar a varredura"
                            + " que comecou nele");
        }
        int discordancias = 0;
        for (int i = 0; i < distancia.length; i++) {
            Tabuleiro arranjo = ranque.tabuleiroDe(linhas, colunas, i);
            boolean andando = distancia[i] != NAO_VISITADO;
            if (andando != Paridade.temSolucao(arranjo)) {
                discordancias++;
            }
        }
        return discordancias;
    }

    /** Um arranjo que a varredura nao alcancou, para varrer o outro lado. */
    public Tabuleiro umInalcancavel() {
        for (int i = 0; i < distancia.length; i++) {
            if (distancia[i] == NAO_VISITADO) {
                return ranque.tabuleiroDe(linhas, colunas, i);
            }
        }
        throw new IllegalStateException("esta grade nao tem arranjo inalcancavel");
    }
}
