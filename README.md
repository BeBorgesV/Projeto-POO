# Herdeiros do Khan – INF1636 (1ª iteração)

Começo do componente **Model** do trabalho, seguindo as "Orientações para a 1ª Iteração" e o Cap. 18 (JUnit) do prof. Ivan.

## Como abrir

**VS Code**
1. Instale a extensão **Extension Pack for Java** (Microsoft) e um JDK 21 ou mais novo.
2. *File → Open Folder…* e escolha a pasta `HerdeirosDoKhan`.
3. Abra o painel **Testing** (ícone de tubo de ensaio) e rode `TodosOsTestes`.

**Eclipse** (é assim que o professor vai abrir)
*File → Import → General → Existing Projects into Workspace* e aponte para a pasta. O JUnit já está em `lib/`, sem configurar nada.

## Estrutura

```
src/model/     componente Model (um pacote Java, como pedido)
test/model/    testes JUnit 4 (mesmo pacote, para enxergar as classes não públicas)
lib/           junit-4.13.2.jar e hamcrest-core-1.3.jar
```

| Classe | Papel | Visibilidade |
|---|---|---|
| `Jogo` | API do Model: a única porta para View e Controller | **pública** |
| `JogadaInvalidaException` | erro de regra, que sai pela API | **pública** |
| `Tabuleiro`, `Parada`, `Provincia`, `Cidade` | o mapa | pacote |
| `Jogador`, `Khan`, `TrilhaKurultai`, `Turno` | estado do jogo | pacote |
| `AreaDoMapa` | interface comum de província e cidade (Cap. 7) | pacote |
| `TipoTributo`, `TipoTesouro`, `Regiao` | tipos | pacote |
| `FabricaTabuleiro` | monta o mapa (hoje, um mapa de exemplo) | pacote |

## O que o professor pediu e onde está

| Orientação | Onde |
|---|---|
| Model é um pacote Java | `src/model` |
| Elementos do jogo não públicos | só `Jogo` e a exceção são `public` |
| API pública para View e Controller | `Jogo` (só recebe e devolve texto e números) |
| Singleton e Façade | **só na 3ª iteração**: hoje `Jogo` é uma classe comum |
| Princípio aberto/fechado | regras isoladas por classe; o mapa entra pela `FabricaTabuleiro`; a interface `AreaDoMapa` aceita novos tipos de área sem mudar o `Jogo` |
| Coleções do Cap. 15, HashMap sempre que possível | `HashMap`/`HashSet`/`ArrayList`, sem arrays; coleções internas só saem como leitura |
| ≥ 8 testes JUnit 4 não triviais (Cap. 18) | **63 testes**, com timeout, mensagem, `@Before` e `expected` |

## Regras já implementadas

- **Preparação:** 2 a 5 jogadores. Peões em Karakorum, 12 yurts por jogador, moedas iniciais (1 para os dois primeiros, 2 para os demais), 3 cidades reveladas com 4 tesouros e 1 tributo por província.
- **Mover:** só para parada vizinha. Pode pular paradas com yurt próprio, e a parada pulada não conta como visitada. Pode passar por parada ocupada, mas não terminar nela. Parada dupla comporta 2 peões e Karakorum, todos.
- **Pegar tributo:** só de província vizinha a uma parada visitada no turno.
- **Atacar cidade:** o 1º tesouro custa 1 espada e os seguintes custam 2. Quem tira o último tesouro conquista a cidade: coloca um yurt nela e a próxima cidade é revelada.
- **Construir yurt:** gasta 1 tributo de yurt. Não vale em Karakorum nem onde já há yurt; a parada dupla aceita 2.
- **Khan:** vai para uma província do Khan, que recebe tributo junto com as 2 províncias apontadas pelas setas (máx. 3 por província). Não pode ficar onde está e é obrigatório quando a coluna tem o ícone.
- **Kurultai:** votos do jogador e ficha neutra. O fim dispara em 10/14/16/17 votos (2/3/4/5 jogadores).

## O que falta (para o grupo)

1. **Transcrever o tabuleiro real** em `FabricaTabuleiro.criarTabuleiroOficial`. Hoje o jogo roda num mapa de exemplo pequeno, desenhado no comentário da classe.
2. **Conferir os nomes dos tesouros.** O manual só cita "pele" e "ferro"; os outros 3 estão como `TESOURO_3..5`.
3. **Colunas e peças de ativação.** Hoje `iniciarTurno(movimentos, acoesDeTributo, colunaTemKhan)` recebe esses números diretamente.
4. **Melhorias**, incluindo o efeito do Khan na área de melhorias.
5. **Conselheiros:** entregar tesouros, cartas de conselheiro aberto e secreto, peças de bônus e votos no Kurultai.
6. **Habilidades dos herdeiros** e **pontuação final.**

## Entrega

ZIP do projeto (sem a pasta `bin/`) no EAD até **11/10, 23h59**. Todos do grupo precisam saber explicar o código na arguição.
