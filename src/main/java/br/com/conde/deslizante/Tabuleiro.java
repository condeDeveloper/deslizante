package br.com.conde.deslizante;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Um estado do quebra-cabeca deslizante: pecas numeradas numa grade, com um
 * buraco (o zero) e a unica jogada possivel sendo empurrar para o buraco a peca
 * que esta ao lado dele.
 *
 * <p>O objetivo e sempre o mesmo: 1, 2, 3, ... em ordem de leitura e o buraco no
 * canto de baixo a direita.
 */
public final class Tabuleiro {

    private final int linhas;
    private final int colunas;
    private final int[] pecas;
    private final int buraco;

    private Tabuleiro(int linhas, int colunas, int[] pecas) {
        this.linhas = linhas;
        this.colunas = colunas;
        this.pecas = pecas;
        int onde = -1;
        for (int i = 0; i < pecas.length; i++) {
            if (pecas[i] == 0) {
                onde = i;
            }
        }
        this.buraco = onde;
    }

    public static Tabuleiro de(int linhas, int colunas, int... pecas) {
        if (linhas < 2 || colunas < 2) {
            throw new IllegalArgumentException("a grade precisa ter pelo menos 2x2");
        }
        if (pecas.length != linhas * colunas) {
            throw new IllegalArgumentException(
                    "a grade " + linhas + "x" + colunas + " tem " + (linhas * colunas) + " casas");
        }
        boolean[] visto = new boolean[pecas.length];
        for (int peca : pecas) {
            if (peca < 0 || peca >= pecas.length || visto[peca]) {
                throw new IllegalArgumentException(
                        "as pecas precisam ser 0 a " + (pecas.length - 1) + ", sem repetir");
            }
            visto[peca] = true;
        }
        return new Tabuleiro(linhas, colunas, pecas.clone());
    }

    /** O estado objetivo: 1, 2, 3, ... e o buraco no fim. */
    public static Tabuleiro resolvido(int linhas, int colunas) {
        int casas = linhas * colunas;
        int[] pecas = new int[casas];
        for (int i = 0; i < casas - 1; i++) {
            pecas[i] = i + 1;
        }
        return Tabuleiro.de(linhas, colunas, pecas);
    }

    public int linhas() {
        return linhas;
    }

    public int colunas() {
        return colunas;
    }

    public int casas() {
        return pecas.length;
    }

    public int peca(int casa) {
        return pecas[casa];
    }

    public int[] pecas() {
        return pecas.clone();
    }

    public int buraco() {
        return buraco;
    }

    public boolean estaResolvido() {
        for (int i = 0; i < pecas.length - 1; i++) {
            if (pecas[i] != i + 1) {
                return false;
            }
        }
        return true;
    }

    /** Os estados a uma jogada de distancia: de 2 a 4, conforme a borda. */
    public List<Tabuleiro> vizinhos() {
        List<Tabuleiro> saida = new ArrayList<>(4);
        int linha = buraco / colunas;
        int coluna = buraco % colunas;
        if (linha > 0) {
            saida.add(trocando(buraco - colunas));
        }
        if (linha < linhas - 1) {
            saida.add(trocando(buraco + colunas));
        }
        if (coluna > 0) {
            saida.add(trocando(buraco - 1));
        }
        if (coluna < colunas - 1) {
            saida.add(trocando(buraco + 1));
        }
        return saida;
    }

    private Tabuleiro trocando(int casa) {
        int[] novas = pecas.clone();
        novas[buraco] = novas[casa];
        novas[casa] = 0;
        return new Tabuleiro(linhas, colunas, novas);
    }

    /** Onde a peca deveria estar no estado resolvido. */
    public int casaCerta(int peca) {
        return peca == 0 ? pecas.length - 1 : peca - 1;
    }

    @Override
    public boolean equals(Object outro) {
        return outro instanceof Tabuleiro t
                && t.linhas == linhas && t.colunas == colunas && Arrays.equals(t.pecas, pecas);
    }

    @Override
    public int hashCode() {
        return 31 * (31 * linhas + colunas) + Arrays.hashCode(pecas);
    }

    @Override
    public String toString() {
        StringBuilder texto = new StringBuilder();
        for (int i = 0; i < pecas.length; i++) {
            texto.append(pecas[i] == 0 ? "." : String.valueOf(pecas[i]));
            texto.append((i + 1) % colunas == 0 ? "\n" : " ");
        }
        return texto.toString();
    }
}
