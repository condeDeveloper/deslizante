package br.com.conde.deslizante;

import java.util.PriorityQueue;

/**
 * A busca guiada por um chute (A*), que existe aqui para ser MEDIDA.
 *
 * <p>O resultado dela ja e conhecido: a {@link Varredura} tem a distancia exata
 * de todo arranjo. O que nao e conhecido e o preco: quantos estados cada chute
 * obriga a abrir ate achar o mesmo caminho. E esse numero que separa um chute
 * bom de um ruim.
 */
public final class Busca {

    private final Heuristica heuristica;

    public Busca(Heuristica heuristica) {
        this.heuristica = heuristica;
    }

    /** O caminho achado e o que custou achar. */
    public record Resultado(int jogadas, int abertos, int gerados) {
    }

    public Resultado resolver(Tabuleiro comeco) {
        Ranque ranque = new Ranque(comeco.casas());
        int[] melhor = new int[(int) ranque.total()];
        java.util.Arrays.fill(melhor, Integer.MAX_VALUE);

        record No(Tabuleiro tabuleiro, int jogadas, int previsao) {
        }
        PriorityQueue<No> fila = new PriorityQueue<>((a, b) -> {
            int ordem = Integer.compare(a.previsao(), b.previsao());
            return ordem != 0 ? ordem : Integer.compare(b.jogadas(), a.jogadas());
        });

        melhor[ranque.indiceDe(comeco)] = 0;
        fila.add(new No(comeco, 0, heuristica.estimativa(comeco)));
        int abertos = 0;
        int gerados = 1;

        while (!fila.isEmpty()) {
            No atual = fila.poll();
            if (atual.jogadas() > melhor[ranque.indiceDe(atual.tabuleiro())]) {
                continue;                       // ja chegamos ali por um caminho mais curto
            }
            if (atual.tabuleiro().estaResolvido()) {
                return new Resultado(atual.jogadas(), abertos, gerados);
            }
            abertos++;
            for (Tabuleiro vizinho : atual.tabuleiro().vizinhos()) {
                int indice = ranque.indiceDe(vizinho);
                int jogadas = atual.jogadas() + 1;
                if (jogadas < melhor[indice]) {
                    melhor[indice] = jogadas;
                    fila.add(new No(vizinho, jogadas, jogadas + heuristica.estimativa(vizinho)));
                    gerados++;
                }
            }
        }
        throw new IllegalArgumentException("este arranjo nao tem solucao");
    }
}
