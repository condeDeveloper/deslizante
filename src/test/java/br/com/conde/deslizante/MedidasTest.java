package br.com.conde.deslizante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Prende os numeros que estao escritos no README. Se uma conta mudar, um destes
 * testes quebra e o texto tem que ser reescrito junto.
 */
@DisplayName("Os numeros do README")
class MedidasTest {

    @ParameterizedTest(name = "a {0} jogadas existem {1} arranjos")
    @CsvSource({
            " 0,      1",
            " 5,     20",
            "10,    286",
            "15,   2512",
            "20,  16993",
            "22,  23952",
            "24,  24047",
            "25,  15578",
            "30,    221",
            "31,      2",
    })
    @DisplayName("tabela DISTANCIAS")
    void tabelaDistancias(int jogadas, long arranjos) {
        assertThat(Varreduras.tresPorTres().distribuicao()[jogadas]).isEqualTo(arranjos);
    }

    @Test
    @DisplayName("o pico da distribuicao esta em 24 jogadas, nao no fim")
    void oPico() {
        long[] contagem = Varreduras.tresPorTres().distribuicao();
        int pico = 0;
        for (int jogadas = 1; jogadas < contagem.length; jogadas++) {
            if (contagem[jogadas] > contagem[pico]) {
                pico = jogadas;
            }
        }

        assertThat(pico).isEqualTo(24);
        assertThat(contagem[pico]).isEqualTo(24047);
    }

    @Test
    @DisplayName("tabela ALCANCAVEIS: o juiz nao discorda da varredura em grade nenhuma")
    void tabelaAlcancaveis() {
        for (int[] grade : new int[][]{{2, 2}, {2, 3}, {3, 2}, {2, 4}, {4, 2}, {3, 3}}) {
            Varredura varredura = grade[0] == 3 && grade[1] == 3
                    ? Varreduras.tresPorTres()
                    : new Varredura(grade[0], grade[1]);

            assertThat(varredura.discordanciasComAParidade())
                    .as("grade %dx%d", grade[0], grade[1])
                    .isZero();
        }
    }

    @ParameterizedTest(name = "{0}: maior {1}, folga media {2}")
    @CsvSource({
            "FORA_DO_LUGAR,       8, 14.86",
            "MANHATTAN,          22,  7.97",
            "CONFLITO_POR_PARES, 32,  6.81",
            "CONFLITO_LINEAR,    28,  6.85",
    })
    @DisplayName("tabela HEURISTICAS")
    void tabelaHeuristicas(Heuristica heuristica, int maior, double folgaMedia) {
        Varredura varredura = Varreduras.tresPorTres();
        Ranque ranque = new Ranque(9);
        int maiorVisto = 0;
        long somaFolga = 0;
        int quantos = 0;

        for (long i = 0; i < ranque.total(); i++) {
            Tabuleiro arranjo = ranque.tabuleiroDe(3, 3, i);
            if (!varredura.alcanca(arranjo)) {
                continue;
            }
            int chute = heuristica.estimativa(arranjo);
            maiorVisto = Math.max(maiorVisto, chute);
            somaFolga += varredura.jogadasAte(arranjo) - chute;
            quantos++;
        }

        assertThat(maiorVisto).isEqualTo(maior);
        assertThat((double) somaFolga / quantos).isEqualTo(folgaMedia, within(0.005));
    }

    @Test
    @DisplayName("o chute errado exagera em 7 arranjos e mesmo assim nunca devolve caminho longo")
    void oDefeitoQueNaoAparece() {
        Varredura varredura = Varreduras.tresPorTres();

        var errados = Medidor.exageros(varredura, Heuristica.CONFLITO_POR_PARES);

        assertThat(errados).hasSize(7);
        for (Tabuleiro errado : errados) {
            assertThat(new Busca(Heuristica.CONFLITO_POR_PARES).resolver(errado).jogadas())
                    .as("arranjo:%n%s", errado)
                    .isEqualTo(varredura.jogadasAte(errado));
        }
    }

    @Test
    @DisplayName("as tabelas rodam inteiras sem estourar")
    void asTabelasRodam() {
        Medidor.alcancaveis();
        Medidor.distancias();
    }
}
