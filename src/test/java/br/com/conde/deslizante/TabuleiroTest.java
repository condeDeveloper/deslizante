package br.com.conde.deslizante;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@DisplayName("O tabuleiro")
class TabuleiroTest {

    @Test
    @DisplayName("o resolvido e 1, 2, 3, ... com o buraco no fim")
    void oResolvido() {
        Tabuleiro t = Tabuleiro.resolvido(3, 3);

        assertThat(t.pecas()).containsExactly(1, 2, 3, 4, 5, 6, 7, 8, 0);
        assertThat(t.buraco()).isEqualTo(8);
        assertThat(t.estaResolvido()).isTrue();
    }

    @Test
    @DisplayName("quase resolvido nao e resolvido")
    void quaseResolvido() {
        assertThat(Tabuleiro.de(3, 3, 1, 2, 3, 4, 5, 6, 7, 0, 8).estaResolvido()).isFalse();
    }

    @Test
    @DisplayName("o buraco no canto tem duas jogadas, na borda tres, no meio quatro")
    void quantasJogadas() {
        assertThat(Tabuleiro.de(3, 3, 1, 2, 3, 4, 5, 6, 7, 8, 0).vizinhos()).hasSize(2);
        assertThat(Tabuleiro.de(3, 3, 1, 2, 3, 4, 5, 6, 7, 0, 8).vizinhos()).hasSize(3);
        assertThat(Tabuleiro.de(3, 3, 1, 2, 3, 4, 0, 6, 7, 8, 5).vizinhos()).hasSize(4);
    }

    @Test
    @DisplayName("toda jogada e reversivel: o estado de antes esta entre os vizinhos do depois")
    void jogadaReversivel() {
        Tabuleiro antes = Tabuleiro.resolvido(3, 3);

        for (Tabuleiro depois : antes.vizinhos()) {
            assertThat(depois.vizinhos()).contains(antes);
        }
    }

    @Test
    @DisplayName("a jogada move exatamente uma peca, e ela troca de lugar com o buraco")
    void aJogadaMoveUmaPeca() {
        Tabuleiro antes = Tabuleiro.de(3, 3, 1, 2, 3, 4, 0, 6, 7, 8, 5);

        for (Tabuleiro depois : antes.vizinhos()) {
            int diferencas = 0;
            for (int casa = 0; casa < antes.casas(); casa++) {
                if (antes.peca(casa) != depois.peca(casa)) {
                    diferencas++;
                }
            }
            assertThat(diferencas).isEqualTo(2);
            assertThat(antes.peca(depois.buraco())).isNotZero();
            assertThat(depois.peca(antes.buraco())).isNotZero();
        }
    }

    @Test
    @DisplayName("cada peca sabe onde deveria estar, e o buraco termina no canto")
    void casaCerta() {
        Tabuleiro t = Tabuleiro.resolvido(3, 3);

        assertThat(t.casaCerta(1)).isZero();
        assertThat(t.casaCerta(8)).isEqualTo(7);
        assertThat(t.casaCerta(0)).isEqualTo(8);
    }

    @Test
    @DisplayName("dois tabuleiros com as mesmas pecas sao iguais e cabem no mesmo conjunto")
    void igualdade() {
        Tabuleiro um = Tabuleiro.de(2, 2, 1, 2, 3, 0);
        Tabuleiro outro = Tabuleiro.de(2, 2, 1, 2, 3, 0);

        assertThat(um).isEqualTo(outro).hasSameHashCodeAs(outro);
        assertThat(java.util.Set.copyOf(java.util.List.of(um, outro))).hasSize(1);
    }

    @Test
    @DisplayName("mudar o vetor devolvido nao mexe no tabuleiro")
    void oVetorEhCopia() {
        Tabuleiro t = Tabuleiro.resolvido(2, 2);

        t.pecas()[0] = 99;

        assertThat(t.peca(0)).isEqualTo(1);
    }

    @Test
    @DisplayName("o desenho mostra o buraco como ponto")
    void desenho() {
        assertThat(Tabuleiro.de(2, 2, 1, 2, 3, 0)).hasToString("1 2\n3 .\n");
    }

    @Test
    @DisplayName("peca repetida, peca fora da faixa ou grade errada nao viram tabuleiro")
    void arranjoInvalido() {
        assertThatThrownBy(() -> Tabuleiro.de(2, 2, 1, 1, 2, 0))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("sem repetir");
        assertThatThrownBy(() -> Tabuleiro.de(2, 2, 1, 2, 3, 4))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> Tabuleiro.de(2, 2, 1, 2, 0))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("4 casas");
        assertThatThrownBy(() -> Tabuleiro.de(1, 3, 1, 2, 0))
                .isInstanceOf(IllegalArgumentException.class).hasMessageContaining("2x2");
    }
}
