package br.com.conde.deslizante;

/**
 * A varredura da grade 3x3 custa uma busca em largura sobre 181.440 estados.
 * Os testes que precisam dela compartilham a mesma, senao o CI passa o tempo
 * refazendo o mesmo trabalho.
 */
final class Varreduras {

    private static Varredura doTresPorTres;

    private Varreduras() {
    }

    static synchronized Varredura tresPorTres() {
        if (doTresPorTres == null) {
            doTresPorTres = new Varredura(3, 3);
        }
        return doTresPorTres;
    }
}
