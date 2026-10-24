package br.com.conde.deslizante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A busca guiada")
class BuscaTest {

    @ParameterizedTest
    @EnumSource(Heuristica.class)
    @DisplayName("todo chute acha o mesmo caminho que a varredura mediu")
    void achaOMinimo(Heuristica heuristica) {
        Varredura varredura = new Varredura(2, 3);
        Ranque ranque = new Ranque(6);
        Busca busca = new Busca(heuristica);

        for (long i = 0; i < ranque.total(); i++) {
            Tabuleiro arranjo = ranque.tabuleiroDe(2, 3, i);
            if (!varredura.alcanca(arranjo)) {
                continue;
            }
            assertThat(busca.resolver(arranjo).jogadas())
                    .as("arranjo %d:%n%s", i, arranjo)
                    .isEqualTo(varredura.jogadasAte(arranjo));
        }
    }

    @Test
    @DisplayName("no pior arranjo da grade 3x3, o caminho tem 31 jogadas")
    void oPiorArranjo() {
        Tabuleiro pior = Varreduras.tresPorTres().maisDistantes().get(0);

        assertThat(new Busca(Heuristica.CONFLITO_LINEAR).resolver(pior).jogadas()).isEqualTo(31);
    }

    @Test
    @DisplayName("o chute melhor abre muito menos: 3.821 estados contra 121.527")
    void oPrecoDoChute() {
        Tabuleiro pior = Varreduras.tresPorTres().maisDistantes().get(0);

        Busca.Resultado fraco = new Busca(Heuristica.FORA_DO_LUGAR).resolver(pior);
        Busca.Resultado quarteirao = new Busca(Heuristica.MANHATTAN).resolver(pior);
        Busca.Resultado bom = new Busca(Heuristica.CONFLITO_LINEAR).resolver(pior);

        assertThat(fraco.abertos()).isEqualTo(121527);
        assertThat(quarteirao.abertos()).isEqualTo(6790);
        assertThat(bom.abertos()).isEqualTo(3821);
        assertThat(fraco.jogadas()).isEqualTo(bom.jogadas());
    }

    @Test
    @DisplayName("abrir um estado gera os vizinhos dele: gerados nunca e menor que abertos")
    void geradosEAbertos() {
        Busca.Resultado r = new Busca(Heuristica.MANHATTAN)
                .resolver(Tabuleiro.de(3, 3, 1, 2, 3, 4, 5, 6, 0, 7, 8));

        assertThat(r.jogadas()).isEqualTo(2);
        assertThat(r.gerados()).isGreaterThanOrEqualTo(r.abertos());
    }

    @Test
    @DisplayName("no resolvido a busca nao abre nada")
    void jaResolvido() {
        Busca.Resultado r = new Busca(Heuristica.MANHATTAN).resolver(Tabuleiro.resolvido(3, 3));

        assertThat(r.jogadas()).isZero();
        assertThat(r.abertos()).isZero();
    }

    @Test
    @DisplayName("arranjo sem solucao nao devolve caminho: ele acaba a fila e avisa")
    void semSolucao() {
        Tabuleiro sem = Tabuleiro.de(2, 2, 2, 1, 3, 0);

        assertThatThrownBy(() -> new Busca(Heuristica.MANHATTAN).resolver(sem))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nao tem solucao");
    }
}
