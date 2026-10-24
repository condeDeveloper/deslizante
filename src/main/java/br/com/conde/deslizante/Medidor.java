package br.com.conde.deslizante;

import java.util.List;
import java.util.Locale;

/**
 * As tabelas do README. Uso:
 * {@code mvn exec:java -Dexec.args="alcancaveis"} (ou {@code distancias},
 * {@code heuristicas}).
 */
public final class Medidor {

    private Medidor() {
    }

    public static void main(String[] args) {
        String tabela = args.length > 0 ? args[0] : "tudo";
        boolean tudo = tabela.equals("tudo");
        if (tudo || tabela.equals("alcancaveis")) {
            alcancaveis();
        }
        if (tudo || tabela.equals("distancias")) {
            distancias();
        }
        if (tudo || tabela.equals("heuristicas")) {
            heuristicas();
        }
    }

    /** Quanto do espaco de arranjos da para alcancar, e o que o juiz diz. */
    public static void alcancaveis() {
        System.out.println();
        System.out.println("ALCANCAVEIS  (varredura completa a partir do resolvido)");
        System.out.printf(Locale.ROOT, "%-10s %12s %14s %10s %10s %16s%n",
                "grade", "arranjos", "alcancaveis", "fracao", "diametro", "o juiz discorda");
        int[][] grades = {{2, 2}, {2, 3}, {3, 2}, {2, 4}, {4, 2}, {3, 3}};
        for (int[] grade : grades) {
            Varredura varredura = new Varredura(grade[0], grade[1]);
            System.out.printf(Locale.ROOT, "%-10s %12d %14d %10s %10d %16s%n",
                    grade[0] + "x" + grade[1], varredura.arranjos(), varredura.alcancaveis(),
                    fracao(varredura.alcancaveis(), varredura.arranjos()), varredura.diametro(),
                    varredura.discordanciasComAParidade() + " vezes");
        }
        System.out.println();
        System.out.println("a metade que sobra nao e um resto: ela e o mesmo problema.");
        Varredura doLado = new Varredura(3, 3);
        Varredura doOutroLado = new Varredura(doLado.umInalcancavel());
        System.out.printf(Locale.ROOT,
                "  3x3 a partir do resolvido:     %d arranjos, diametro %d%n",
                doLado.alcancaveis(), doLado.diametro());
        System.out.printf(Locale.ROOT,
                "  3x3 a partir de um inalcancavel: %d arranjos, diametro %d%n",
                doOutroLado.alcancaveis(), doOutroLado.diametro());
        System.out.println();
    }

    /** Quantos arranjos existem a cada distancia da solucao, na grade 3x3. */
    public static void distancias() {
        Varredura varredura = new Varredura(3, 3);
        long[] contagem = varredura.distribuicao();
        System.out.println();
        System.out.println("DISTANCIAS  (grade 3x3: quantos arranjos a cada numero de jogadas)");
        System.out.printf(Locale.ROOT, "%-10s %12s %12s%n", "jogadas", "arranjos", "acumulado");
        long acumulado = 0;
        for (int jogadas = 0; jogadas < contagem.length; jogadas++) {
            acumulado += contagem[jogadas];
            System.out.printf(Locale.ROOT, "%-10d %12d %12d%n", jogadas, contagem[jogadas],
                    acumulado);
        }
        System.out.println();
        System.out.printf(Locale.ROOT, "media de jogadas: %.2f%n", varredura.mediaDeJogadas());
        Tabuleiro deCabecaParaBaixo = Tabuleiro.de(3, 3, 8, 7, 6, 5, 4, 3, 2, 1, 0);
        System.out.printf(Locale.ROOT, "o arranjo invertido (8 7 6 / 5 4 3 / 2 1 .): %d jogadas%n",
                varredura.jogadasAte(deCabecaParaBaixo));
        List<Tabuleiro> piores = varredura.maisDistantes();
        System.out.printf(Locale.ROOT, "arranjos a %d jogadas: %d%n", varredura.diametro(),
                piores.size());
        for (Tabuleiro pior : piores) {
            System.out.println();
            System.out.print(pior);
        }
        System.out.println();
    }

    /** O preco de cada chute, conferido contra a distancia exata. */
    public static void heuristicas() {
        Varredura varredura = new Varredura(3, 3);
        Ranque ranque = new Ranque(9);
        System.out.println();
        System.out.println("HEURISTICAS  (grade 3x3, conferidas nos 181.440 arranjos com solucao)");
        System.out.printf(Locale.ROOT, "%-28s %10s %10s %12s %14s%n",
                "chute", "maior", "media", "folga media", "exagerou em");
        for (Heuristica heuristica : Heuristica.values()) {
            long soma = 0;
            long somaFolga = 0;
            int maior = 0;
            int exagerou = 0;
            int quantos = 0;
            for (long i = 0; i < ranque.total(); i++) {
                Tabuleiro arranjo = ranque.tabuleiroDe(3, 3, i);
                if (!varredura.alcanca(arranjo)) {
                    continue;
                }
                int chute = heuristica.estimativa(arranjo);
                int real = varredura.jogadasAte(arranjo);
                soma += chute;
                somaFolga += real - chute;
                maior = Math.max(maior, chute);
                if (chute > real) {
                    exagerou++;
                }
                quantos++;
            }
            System.out.printf(Locale.ROOT, "%-28s %10d %10.2f %12.2f %14s%n",
                    heuristica.rotulo(), maior, (double) soma / quantos,
                    (double) somaFolga / quantos, exagerou + " arranjos");
        }
        System.out.println();
        System.out.println("e o que cada chute cobra para achar o caminho no pior arranjo:");
        System.out.printf(Locale.ROOT, "%-28s %12s %12s %14s%n",
                "chute", "abertos", "gerados", "achou em");
        Tabuleiro pior = varredura.maisDistantes().get(0);
        for (Heuristica heuristica : Heuristica.values()) {
            Busca.Resultado r = new Busca(heuristica).resolver(pior);
            System.out.printf(Locale.ROOT, "%-28s %12d %12d %14s%n",
                    heuristica.rotulo(), r.abertos(), r.gerados(), r.jogadas() + " jogadas");
        }
        System.out.printf(Locale.ROOT, "%no minimo real desse arranjo e %d jogadas.%n",
                varredura.jogadasAte(pior));
        System.out.println();
        System.out.println("contar os conflitos por pares exagera em toda grade:");
        System.out.printf(Locale.ROOT, "%-10s %14s %14s %18s%n",
                "grade", "com solucao", "exagerou em", "deu caminho longo");
        for (int[] grade : new int[][]{{2, 3}, {2, 4}, {4, 2}, {3, 3}}) {
            Varredura outra = new Varredura(grade[0], grade[1]);
            List<Tabuleiro> errados = exageros(outra, Heuristica.CONFLITO_POR_PARES);
            int longos = 0;
            for (Tabuleiro errado : errados) {
                if (new Busca(Heuristica.CONFLITO_POR_PARES).resolver(errado).jogadas()
                        > outra.jogadasAte(errado)) {
                    longos++;
                }
            }
            System.out.printf(Locale.ROOT, "%-10s %14d %14d %18d%n",
                    grade[0] + "x" + grade[1], outra.alcancaveis(), errados.size(), longos);
        }
        System.out.println();
        System.out.println("os sete arranjos da grade 3x3 em que ele exagera:");
        for (Tabuleiro errado : exageros(varredura, Heuristica.CONFLITO_POR_PARES)) {
            Busca.Resultado r = new Busca(Heuristica.CONFLITO_POR_PARES).resolver(errado);
            System.out.println();
            System.out.print(errado);
            System.out.printf(Locale.ROOT,
                    "  chute %d, minimo real %d, e a busca guiada por ele devolve %d%n",
                    Heuristica.CONFLITO_POR_PARES.estimativa(errado),
                    varredura.jogadasAte(errado), r.jogadas());
        }
        System.out.println();
    }

    /** Os arranjos em que o chute passa do numero real de jogadas. */
    public static List<Tabuleiro> exageros(Varredura varredura, Heuristica heuristica) {
        Ranque ranque = new Ranque(varredura.linhas() * varredura.colunas());
        List<Tabuleiro> saida = new java.util.ArrayList<>();
        for (long i = 0; i < ranque.total(); i++) {
            Tabuleiro arranjo = ranque.tabuleiroDe(varredura.linhas(), varredura.colunas(), i);
            if (varredura.alcanca(arranjo)
                    && heuristica.estimativa(arranjo) > varredura.jogadasAte(arranjo)) {
                saida.add(arranjo);
            }
        }
        return saida;
    }

    private static String fracao(long parte, long total) {
        return String.format(Locale.ROOT, "%.4f", (double) parte / total);
    }
}
