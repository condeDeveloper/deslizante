package br.com.conde.deslizante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Os chutes do quanto falta")
class HeuristicaTest {

    @ParameterizedTest
    @EnumSource(Heuristica.class)
    @DisplayName("no resolvido todo chute e zero")
    void zeroNoResolvido(Heuristica heuristica) {
        assertThat(heuristica.estimativa(Tabuleiro.resolvido(3, 3))).isZero();
    }

    @ParameterizedTest
    @EnumSource(value = Heuristica.class,
            names = {"FORA_DO_LUGAR", "MANHATTAN", "CONFLITO_LINEAR"})
    @DisplayName("nunca passa do numero real de jogadas, nos 181.440 arranjos")
    void nuncaExagera(Heuristica heuristica) {
        assertThat(Medidor.exageros(Varreduras.tresPorTres(), heuristica)).isEmpty();
    }

    @Test
    @DisplayName("contar o conflito por pares exagera, e em exatamente 7 arranjos")
    void contarPorParesExagera() {
        List<Tabuleiro> errados = Medidor.exageros(Varreduras.tresPorTres(),
                Heuristica.CONFLITO_POR_PARES);

        assertThat(errados).hasSize(7);
    }

    @Test
    @DisplayName("o pior exagero: tres pecas invertidas na linha viram tres pares, e bastava uma")
    void oPiorExagero() {
        Tabuleiro arranjo = Tabuleiro.de(3, 3, 0, 8, 7, 6, 5, 4, 3, 2, 1);

        assertThat(Varreduras.tresPorTres().jogadasAte(arranjo)).isEqualTo(28);
        assertThat(Heuristica.CONFLITO_POR_PARES.estimativa(arranjo)).isEqualTo(32);
        assertThat(Heuristica.CONFLITO_LINEAR.estimativa(arranjo)).isEqualTo(28);
    }

    @Test
    @DisplayName("o conflito linear nunca diz menos que a distancia de quarteirao")
    void oConflitoSoAcrescenta() {
        Ranque ranque = new Ranque(9);

        for (long i = 0; i < ranque.total(); i += 7) {
            Tabuleiro arranjo = ranque.tabuleiroDe(3, 3, i);
            assertThat(Heuristica.CONFLITO_LINEAR.estimativa(arranjo))
                    .isGreaterThanOrEqualTo(Heuristica.MANHATTAN.estimativa(arranjo));
        }
    }

    @Test
    @DisplayName("pecas fora do lugar nunca diz mais que 8, e a distancia de quarteirao chega a 22")
    void oTeto() {
        Varredura varredura = Varreduras.tresPorTres();
        Ranque ranque = new Ranque(9);
        int maiorFora = 0;
        int maiorQuarteirao = 0;

        for (long i = 0; i < ranque.total(); i++) {
            Tabuleiro arranjo = ranque.tabuleiroDe(3, 3, i);
            if (!varredura.alcanca(arranjo)) {
                continue;
            }
            maiorFora = Math.max(maiorFora, Heuristica.FORA_DO_LUGAR.estimativa(arranjo));
            maiorQuarteirao = Math.max(maiorQuarteirao, Heuristica.MANHATTAN.estimativa(arranjo));
        }

        assertThat(maiorFora).isEqualTo(8);
        assertThat(maiorQuarteirao).isEqualTo(22);
    }

    @Test
    @DisplayName("o chute cai no maximo um por jogada: e por isso que ele serve de guia")
    void variaDeUmEmUm() {
        Ranque ranque = new Ranque(8);
        Varredura varredura = new Varredura(2, 4);

        for (long i = 0; i < ranque.total(); i += 3) {
            Tabuleiro arranjo = ranque.tabuleiroDe(2, 4, i);
            if (!varredura.alcanca(arranjo)) {
                continue;
            }
            int aqui = Heuristica.MANHATTAN.estimativa(arranjo);
            for (Tabuleiro vizinho : arranjo.vizinhos()) {
                assertThat(Math.abs(Heuristica.MANHATTAN.estimativa(vizinho) - aqui))
                        .isEqualTo(1);
            }
        }
    }

    @ParameterizedTest
    @EnumSource(Heuristica.class)
    @DisplayName("cada chute tem um rotulo para a tabela")
    void rotulo(Heuristica heuristica) {
        assertThat(heuristica.rotulo()).isNotBlank();
    }
}
