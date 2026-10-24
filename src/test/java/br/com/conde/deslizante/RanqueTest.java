package br.com.conde.deslizante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("A numeracao dos arranjos")
class RanqueTest {

    @Test
    @DisplayName("sao n! arranjos")
    void total() {
        assertThat(new Ranque(4).total()).isEqualTo(24);
        assertThat(new Ranque(6).total()).isEqualTo(720);
        assertThat(new Ranque(9).total()).isEqualTo(362880);
    }

    @Test
    @DisplayName("o arranjo em ordem e o primeiro, o invertido e o ultimo")
    void pontasConhecidas() {
        Ranque ranque = new Ranque(4);

        assertThat(ranque.indiceDe(new int[]{0, 1, 2, 3})).isZero();
        assertThat(ranque.indiceDe(new int[]{3, 2, 1, 0})).isEqualTo(23);
    }

    @Test
    @DisplayName("ir e voltar devolve o mesmo arranjo, nos 720 de tamanho 6")
    void idaEVolta() {
        Ranque ranque = new Ranque(6);

        for (long i = 0; i < ranque.total(); i++) {
            assertThat(ranque.indiceDe(ranque.arranjoDe(i))).isEqualTo(i);
        }
    }

    @Test
    @DisplayName("e uma numeracao, nao um apelido: nenhum indice se repete nos 40.320")
    void numeracaoSemRepetir() {
        Ranque ranque = new Ranque(8);
        boolean[] visto = new boolean[(int) ranque.total()];

        for (long i = 0; i < ranque.total(); i++) {
            int indice = (int) ranque.indiceDe(ranque.arranjoDe(i));
            assertThat(visto[indice]).as("indice %d repetido", indice).isFalse();
            visto[indice] = true;
        }
    }

    @Test
    @DisplayName("o tabuleiro volta inteiro do indice")
    void tabuleiroDeIndice() {
        Ranque ranque = new Ranque(9);
        Tabuleiro resolvido = Tabuleiro.resolvido(3, 3);

        int indice = ranque.indiceDe(resolvido);

        assertThat(ranque.tabuleiroDe(3, 3, indice)).isEqualTo(resolvido);
    }

    @Test
    @DisplayName("indice fora da faixa nao existe")
    void indiceForaDaFaixa() {
        Ranque ranque = new Ranque(4);

        assertThatThrownBy(() -> ranque.arranjoDe(24))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("0 a 23");
        assertThatThrownBy(() -> ranque.arranjoDe(-1))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("tamanho que nao cabe em long nao e aceito")
    void tamanhoGrandeDemais() {
        assertThatThrownBy(() -> new Ranque(21)).isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> new Ranque(0)).isInstanceOf(IllegalArgumentException.class);
    }
}
