# deslizante

O quebra-cabeça deslizante do zero em Java 21: peças numeradas numa grade, um
buraco, e a única jogada possível é empurrar para o buraco a peça que está ao
lado dele.

Nada aqui é amostrado. A grade 3x3 tem 362.880 arranjos, e o programa **visita
todos**, guardando para cada um a distância exata até a solução. É isso que
transforma frases como "esse chute é seguro" e "esse é o arranjo mais difícil"
em contas que ou fecham ou não fecham.

```
mvn -B -ntp verify
mvn -B -ntp exec:java -Dexec.args="alcancaveis"   # (ou distancias, heuristicas)
```

## Metade dos embaralhamentos não tem solução

| grade | arranjos | com solução | fração | diâmetro | o juiz discorda |
|---|---:|---:|---:|---:|---:|
| 2x2 | 24 | 12 | 0,5000 | 6 | 0 vezes |
| 2x3 | 720 | 360 | 0,5000 | 21 | 0 vezes |
| 3x2 | 720 | 360 | 0,5000 | 21 | 0 vezes |
| 2x4 | 40.320 | 20.160 | 0,5000 | 36 | 0 vezes |
| 4x2 | 40.320 | 20.160 | 0,5000 | 36 | 0 vezes |
| **3x3** | **362.880** | **181.440** | **0,5000** | **31** | **0 vezes** |

Embaralhar as peças na mão e montar de volta não é garantido: **metade exata dos
arranjos é impossível**, e nenhuma sequência de jogadas chega neles.

A última coluna é o ponto do repositório. Existe uma regra que diz se um arranjo
tem solução sem andar um passo: toda jogada troca o buraco com uma vizinha, o que
inverte o sinal da permutação, e ao mesmo tempo muda em um a distância do buraco
até o canto dele. A soma das duas paridades, portanto, nunca muda. A regra está
em `Paridade` e não conhece a `Varredura`; a varredura descobre o que é
alcançável andando. Os dois são comparados **arranjo por arranjo**, nas seis
grades, e discordam zero vezes em 444.984 comparações.

A metade sem solução também não é um resto:

```
3x3 a partir do resolvido:       181.440 arranjos, diâmetro 31
3x3 a partir de um inalcançável: 181.440 arranjos, diâmetro 31
```

É o mesmo problema com outro objetivo. Quem embaralhou e caiu no lado errado não
está com um quebra-cabeça quebrado: está com um quebra-cabeça cuja solução é
outra figura.

## Quantas jogadas cada arranjo custa

Grade 3x3, distância exata de cada um dos 181.440 arranjos até a solução:

| jogadas | arranjos | acumulado |
|---:|---:|---:|
| 0 | 1 | 1 |
| 1 | 2 | 3 |
| 2 | 4 | 7 |
| 3 | 8 | 15 |
| 4 | 16 | 31 |
| 5 | 20 | 51 |
| 6 | 39 | 90 |
| 7 | 62 | 152 |
| 8 | 116 | 268 |
| 9 | 152 | 420 |
| 10 | 286 | 706 |
| 11 | 396 | 1.102 |
| 12 | 748 | 1.850 |
| 13 | 1.024 | 2.874 |
| 14 | 1.893 | 4.767 |
| 15 | 2.512 | 7.279 |
| 16 | 4.485 | 11.764 |
| 17 | 5.638 | 17.402 |
| 18 | 9.529 | 26.931 |
| 19 | 10.878 | 37.809 |
| 20 | 16.993 | 54.802 |
| 21 | 17.110 | 71.912 |
| 22 | 23.952 | 95.864 |
| 23 | 20.224 | 116.088 |
| **24** | **24.047** | 140.135 |
| 25 | 15.578 | 155.713 |
| 26 | 14.560 | 170.273 |
| 27 | 6.274 | 176.547 |
| 28 | 3.910 | 180.457 |
| 29 | 760 | 181.217 |
| 30 | 221 | 181.438 |
| **31** | **2** | **181.440** |

A média é de **21,97 jogadas** e o pico está em 24: quase todo arranjo está longe.
Os dois extremos são raros dos dois lados, e os arranjos realmente difíceis são
**dois**, em 181.440:

```
6 4 7        8 6 7
8 5 .        2 5 4
3 2 1        3 . 1
```

O arranjo de cabeça para baixo (`8 7 6 / 5 4 3 / 2 1 .`), que é o que todo mundo
aponta como o pior, custa **30 jogadas** e divide essa posição com outros 220.

## O preço de cada chute

Uma busca guiada precisa de um palpite do quanto falta, e o palpite só serve se
nunca passar do número real de jogadas: um palpite que exagera faz a busca
desistir do caminho curto. Como a varredura tem a distância exata de todos os
arranjos, aqui o palpite não é defendido com argumento, é **conferido nos 181.440**:

| chute | maior | média | folga média | exagerou em |
|---|---:|---:|---:|---:|
| peças fora do lugar | 8 | 7,11 | 14,86 | 0 arranjos |
| distância de quarteirão | 22 | 14,00 | 7,97 | 0 arranjos |
| conflito contado por pares | 32 | 15,17 | 6,81 | **7 arranjos** |
| quarteirão + conflito linear | 28 | 15,12 | 6,85 | 0 arranjos |

E o que cada um cobra para achar o caminho no pior arranjo:

| chute | estados abertos | gerados | achou em |
|---|---:|---:|---:|
| peças fora do lugar | 121.527 | 146.153 | 31 jogadas |
| distância de quarteirão | 6.790 | 10.468 | 31 jogadas |
| conflito contado por pares | 3.825 | 5.985 | 31 jogadas |
| **quarteirão + conflito linear** | **3.821** | **5.975** | **31 jogadas** |

Contar quantas peças estão fora do lugar é um palpite legítimo e inútil: ele
nunca passa de 8 num problema que chega a 31 jogadas, e por isso abre **18 vezes
mais estados** que a distância de quarteirão para chegar na mesma resposta.

## O defeito que não aparece

O conflito linear é a ideia de que duas peças já na linha certa, mas trocadas
entre si, obrigam uma a sair da linha e voltar: duas jogadas que a soma de
quarteirão não enxerga. Escrevi isso contando os **pares trocados**, que é o que
parece óbvio.

Está errado. Três peças invertidas na mesma linha formam três pares, mas tirar
**uma** delas já desfaz o problema das três. O jeito certo é contar as peças da
linha menos a maior sequência que já está em ordem.

A varredura mostra o tamanho do erro:

| grade | com solução | exagerou em | devolveu caminho mais longo |
|---|---:|---:|---:|
| 2x3 | 360 | 0 | 0 |
| 2x4 | 20.160 | 4 | 0 |
| 4x2 | 20.160 | 4 | 0 |
| 3x3 | 181.440 | 7 | 0 |

Um dos sete:

```
. 8 7      chute 32, mínimo real 28
6 5 4      e a busca guiada por ele devolve 28
3 2 1
```

São 7 arranjos em 181.440, **0,0039%**. E a última coluna é a parte incômoda: em
nenhum deles, em nenhuma grade, a busca guiada pelo chute errado devolveu um
caminho mais longo. O defeito existe, está provado, e **não aparece no
resultado**. Nenhum teste por amostragem o encontraria, e nenhum teste de
"resolveu certo?" também não.

## O que as medidas me corrigiram

1. **"O arranjo mais difícil é o de cabeça para baixo."** Ele custa 30 jogadas e
   está empatado com outros 220. Os dois arranjos de 31 jogadas não têm cara de
   nada.
2. **"Conflito linear é contar os pares de peças trocadas."** Esse jeito exagera
   em 7 dos 181.440 arranjos, porque três peças invertidas formam três pares e
   custam só duas jogadas a mais, não seis.
3. **"Um chute que exagera dá resposta errada."** Nos 7 arranjos em que ele
   exagera, e nos 4 da grade 2x4, a busca guiada por ele devolveu o caminho
   mínimo assim mesmo. Testar o resultado não teria achado o defeito.
4. **"Peças fora do lugar é um chute razoável."** Abre 121.527 estados onde a
   distância de quarteirão abre 6.790, porque o teto dele é 8 e o problema chega
   a 31.
5. **"Embaralhar as peças dá um quebra-cabeça."** Dá um quebra-cabeça em metade
   das vezes. Na outra metade dá uma figura que não existe, e nenhuma
   quantidade de jogadas chega nela.

## Como está organizado

- `Tabuleiro`: um arranjo e as jogadas que saem dele. Imutável.
- `Ranque`: numera os arranjos pelo código de Lehmer, para a varredura caber num
  vetor de bytes em vez de num conjunto de objetos.
- `Paridade`: o juiz que decide sem buscar, num laço sobre as casas.
- `Varredura`: a busca em largura completa, de onde saem as distâncias, o
  diâmetro e o confronto com o juiz.
- `Heuristica`: os quatro chutes, incluindo de propósito o que está errado.
- `Busca`: A\*, que existe para ser medida, não para descobrir a resposta.
- `Medidor`: as três tabelas acima, rodando no CI nos três sistemas.

79 testes.
