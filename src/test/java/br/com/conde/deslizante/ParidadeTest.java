package br.com.conde.deslizante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("A paridade, que decide sem buscar")
class ParidadeTest {

    @Test
    @DisplayName("o resolvido tem solucao, obviamente")
    void oResolvido() {
        assertThat(Paridade.temSolucao(Tabuleiro.resolvido(3, 3))).isTrue();
        assertThat(Paridade.inversoes(Tabuleiro.resolvido(3, 3))).isZero();
        assertThat(Paridade.passosDoBuraco(Tabuleiro.resolvido(3, 3))).isZero();
    }

    @Test
    @DisplayName("trocar duas pecas de lugar tira a solucao")
    void duasPecasTrocadas() {
        Tabuleiro trocado = Tabuleiro.de(3, 3, 2, 1, 3, 4, 5, 6, 7, 8, 0);

        assertThat(Paridade.temSolucao(trocado)).isFalse();
        assertThat(Paridade.inversoes(trocado)).isEqualTo(1);
    }

    @Test
    @DisplayName("jogada nenhuma muda a resposta: ela e invariante")
    void aJogadaNaoMudaAResposta() {
        percorrer(Tabuleiro.resolvido(3, 3), 8, true);
        percorrer(Tabuleiro.de(3, 3, 2, 1, 3, 4, 5, 6, 7, 8, 0), 8, false);
    }

    private void percorrer(Tabuleiro atual, int passos, boolean esperado) {
        assertThat(Paridade.temSolucao(atual)).isEqualTo(esperado);
        if (passos == 0) {
            return;
        }
        for (Tabuleiro vizinho : atual.vizinhos()) {
            percorrer(vizinho, passos - 1, esperado);
        }
    }

    @Test
    @DisplayName("ela acerta os 24 arranjos da grade 2x2, um por um")
    void todosOsArranjosDaGradeMinima() {
        Varredura varredura = new Varredura(2, 2);
        Ranque ranque = new Ranque(4);
        int comSolucao = 0;

        for (long i = 0; i < ranque.total(); i++) {
            Tabuleiro arranjo = ranque.tabuleiroDe(2, 2, i);
            assertThat(Paridade.temSolucao(arranjo))
                    .as("arranjo %d:%n%s", i, arranjo)
                    .isEqualTo(varredura.alcanca(arranjo));
            if (Paridade.temSolucao(arranjo)) {
                comSolucao++;
            }
        }
        assertThat(comSolucao).isEqualTo(12);
    }

    @Test
    @DisplayName("e acerta os 362.880 da grade 3x3 tambem, sem errar uma vez")
    void todosOsArranjosDaGradeCheia() {
        assertThat(new Varredura(3, 3).discordanciasComAParidade()).isZero();
    }

    @Test
    @DisplayName("so da para confrontar com a varredura que comecou no resolvido")
    void varreduraDoOutroLado() {
        Varredura doOutroLado = new Varredura(new Varredura(2, 2).umInalcancavel());

        assertThat(doOutroLado.alcancaveis()).isEqualTo(12);
        org.assertj.core.api.Assertions
                .assertThatThrownBy(doOutroLado::discordanciasComAParidade)
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    @DisplayName("os passos do buraco sao contados ate o canto onde ele termina")
    void passosDoBuraco() {
        assertThat(Paridade.passosDoBuraco(Tabuleiro.de(3, 3, 0, 1, 2, 3, 4, 5, 6, 7, 8)))
                .isEqualTo(4);
        assertThat(Paridade.passosDoBuraco(Tabuleiro.de(3, 3, 1, 2, 3, 4, 5, 6, 7, 0, 8)))
                .isEqualTo(1);
    }
}
