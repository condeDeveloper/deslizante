package br.com.conde.deslizante;

/**
 * O juiz que nao busca nada.
 *
 * <p>Toda jogada troca o buraco com uma peca vizinha. Uma troca inverte o sinal
 * da permutacao, e andar uma casa muda a distancia do buraco ate o canto dele em
 * exatamente um. Entao a soma das duas paridades nunca muda: ela e a mesma do
 * comeco ao fim de qualquer sequencia de jogadas.
 *
 * <p>No estado resolvido essa soma e par. Logo, um arranjo em que ela e impar
 * nao pode virar o resolvido por jogada nenhuma, e a conta disso e um laco
 * sobre as casas, sem explorar estado algum.
 *
 * <p>Este arquivo existe para ser confrontado com a {@link Varredura}: a
 * varredura descobre o que e alcancavel andando, e a paridade diz o que deveria
 * ser alcancavel sem andar. Os dois conjuntos sao comparados arranjo por
 * arranjo.
 */
public final class Paridade {

    private Paridade() {
    }

    public static boolean temSolucao(Tabuleiro tabuleiro) {
        return (inversoes(tabuleiro) + passosDoBuraco(tabuleiro)) % 2 == 0;
    }

    /**
     * Quantos pares de casas estao fora de ordem em relacao ao destino de cada
     * peca. O buraco conta como mais uma peca, cujo destino e o canto final.
     */
    public static int inversoes(Tabuleiro tabuleiro) {
        int casas = tabuleiro.casas();
        int[] destino = new int[casas];
        for (int casa = 0; casa < casas; casa++) {
            destino[casa] = tabuleiro.casaCerta(tabuleiro.peca(casa));
        }
        int inversoes = 0;
        for (int i = 0; i < casas; i++) {
            for (int j = i + 1; j < casas; j++) {
                if (destino[i] > destino[j]) {
                    inversoes++;
                }
            }
        }
        return inversoes;
    }

    /** Quantas casas o buraco esta do canto onde ele termina. */
    public static int passosDoBuraco(Tabuleiro tabuleiro) {
        int colunas = tabuleiro.colunas();
        int onde = tabuleiro.buraco();
        int destino = tabuleiro.casas() - 1;
        return Math.abs(onde / colunas - destino / colunas)
                + Math.abs(onde % colunas - destino % colunas);
    }
}
