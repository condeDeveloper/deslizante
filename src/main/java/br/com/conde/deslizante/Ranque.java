package br.com.conde.deslizante;

/**
 * Numera os arranjos. Cada disposicao das pecas vira um indice entre 0 e n!-1,
 * e cada indice volta a ser a disposicao.
 *
 * <p>E o que torna a varredura completa possivel: em vez de um conjunto de
 * tabuleiros, a busca usa um vetor de bytes com uma casa por arranjo. Para a
 * grade 3x3 sao 362.880 bytes.
 */
public final class Ranque {

    private final int tamanho;
    private final long[] fatorial;

    public Ranque(int tamanho) {
        if (tamanho < 1 || tamanho > 20) {
            throw new IllegalArgumentException("tamanho fora do que cabe em long: " + tamanho);
        }
        this.tamanho = tamanho;
        this.fatorial = new long[tamanho + 1];
        fatorial[0] = 1;
        for (int i = 1; i <= tamanho; i++) {
            fatorial[i] = fatorial[i - 1] * i;
        }
    }

    /** Quantos arranjos existem: n!. */
    public long total() {
        return fatorial[tamanho];
    }

    public int indiceDe(Tabuleiro tabuleiro) {
        return (int) indiceDe(tabuleiro.pecas());
    }

    /** O codigo de Lehmer do arranjo, em base fatorial. */
    public long indiceDe(int[] arranjo) {
        long indice = 0;
        for (int i = 0; i < tamanho; i++) {
            int menores = 0;
            for (int j = i + 1; j < tamanho; j++) {
                if (arranjo[j] < arranjo[i]) {
                    menores++;
                }
            }
            indice += menores * fatorial[tamanho - 1 - i];
        }
        return indice;
    }

    public int[] arranjoDe(long indice) {
        if (indice < 0 || indice >= total()) {
            throw new IllegalArgumentException("indice fora de 0 a " + (total() - 1));
        }
        int[] disponiveis = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            disponiveis[i] = i;
        }
        int[] arranjo = new int[tamanho];
        int quantos = tamanho;
        for (int i = 0; i < tamanho; i++) {
            long passo = fatorial[tamanho - 1 - i];
            int escolhido = (int) (indice / passo);
            indice %= passo;
            arranjo[i] = disponiveis[escolhido];
            System.arraycopy(disponiveis, escolhido + 1, disponiveis, escolhido,
                    quantos - escolhido - 1);
            quantos--;
        }
        return arranjo;
    }

    public Tabuleiro tabuleiroDe(int linhas, int colunas, long indice) {
        return Tabuleiro.de(linhas, colunas, arranjoDe(indice));
    }
}
