package br.com.conde.deslizante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("A varredura completa")
class VarreduraTest {

    @ParameterizedTest(name = "grade {0}x{1}: {2} arranjos, {3} com solucao, diametro {4}")
    @CsvSource({
            "2, 2,     24,     12,  6",
            "2, 3,    720,    360, 21",
            "3, 2,    720,    360, 21",
            "2, 4,  40320,  20160, 36",
            "4, 2,  40320,  20160, 36",
    })
    @DisplayName("em toda grade pequena, exatamente metade dos arranjos tem solucao")
    void metadeTemSolucao(int linhas, int colunas, int arranjos, int alcancaveis, int diametro) {
        Varredura varredura = new Varredura(linhas, colunas);

        assertThat(varredura.arranjos()).isEqualTo(arranjos);
        assertThat(varredura.alcancaveis()).isEqualTo(alcancaveis);
        assertThat(varredura.alcancaveis() * 2).isEqualTo(varredura.arranjos());
        assertThat(varredura.diametro()).isEqualTo(diametro);
    }

    @Test
    @DisplayName("a grade 3x3 tem 181.440 arranjos com solucao e diametro 31")
    void aGradeCheia() {
        Varredura varredura = Varreduras.tresPorTres();

        assertThat(varredura.arranjos()).isEqualTo(362880);
        assertThat(varredura.alcancaveis()).isEqualTo(181440);
        assertThat(varredura.diametro()).isEqualTo(31);
    }

    @Test
    @DisplayName("a distribuicao soma os alcancaveis e cresce de um em um no comeco")
    void distribuicao() {
        Varredura varredura = Varreduras.tresPorTres();
        long[] contagem = varredura.distribuicao();

        assertThat(contagem).hasSize(32);
        assertThat(java.util.Arrays.stream(contagem).sum()).isEqualTo(181440);
        assertThat(contagem[0]).isEqualTo(1);
        assertThat(contagem[1]).isEqualTo(2);
        assertThat(contagem[31]).isEqualTo(2);
    }

    @Test
    @DisplayName("so dois arranjos ficam a 31 jogadas, e nenhum deles e o invertido")
    void osDoisPiores() {
        Varredura varredura = Varreduras.tresPorTres();
        Tabuleiro invertido = Tabuleiro.de(3, 3, 8, 7, 6, 5, 4, 3, 2, 1, 0);

        assertThat(varredura.maisDistantes()).hasSize(2).doesNotContain(invertido);
        assertThat(varredura.jogadasAte(invertido)).isEqualTo(30);
    }

    @Test
    @DisplayName("a metade sem solucao e o mesmo problema: mesmo tamanho e mesmo diametro")
    void oOutroLado() {
        Varredura deste = Varreduras.tresPorTres();
        Varredura doOutro = new Varredura(deste.umInalcancavel());

        assertThat(doOutro.alcancaveis()).isEqualTo(deste.alcancaveis());
        assertThat(doOutro.diametro()).isEqualTo(deste.diametro());
    }

    @Test
    @DisplayName("a distancia anda de um em um: todo vizinho fica a uma jogada de diferenca")
    void distanciaDeVizinho() {
        Varredura varredura = new Varredura(2, 4);
        Ranque ranque = new Ranque(8);

        for (long i = 0; i < ranque.total(); i++) {
            Tabuleiro arranjo = ranque.tabuleiroDe(2, 4, i);
            if (!varredura.alcanca(arranjo)) {
                continue;
            }
            for (Tabuleiro vizinho : arranjo.vizinhos()) {
                assertThat(Math.abs(varredura.jogadasAte(vizinho) - varredura.jogadasAte(arranjo)))
                        .as("entre %s e %s", arranjo, vizinho)
                        .isEqualTo(1);
            }
        }
    }

    @Test
    @DisplayName("quem nao tem solucao devolve -1 jogadas")
    void semSolucao() {
        Varredura varredura = new Varredura(2, 2);
        Tabuleiro sem = varredura.umInalcancavel();

        assertThat(varredura.alcanca(sem)).isFalse();
        assertThat(varredura.jogadasAte(sem)).isEqualTo(-1);
    }

    @Test
    @DisplayName("a media de jogadas da grade 3x3 e 21,97")
    void mediaDeJogadas() {
        assertThat(Varreduras.tresPorTres().mediaDeJogadas())
                .isEqualTo(21.97, org.assertj.core.api.Assertions.within(0.005));
    }

}
