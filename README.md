# Herdeiros do Khan - INF1636 - 1ª iteração

Trabalho de POO (prof. Ivan Mathias Filho), 2026.2.

Integrantes:
- Bernardo Borges Vieira - 2211838
- (completar)
- (completar)

## Como rodar

Projeto do Eclipse. Basta importar com *File > Import > Existing Projects into Workspace*.
O JUnit 4 já está na pasta `lib/`.

Para rodar os testes: botão direito em `test/model/TodosOsTestes.java` > *Run As > JUnit Test*.

## O que tem nesta iteração

Nesta iteração fizemos só o componente **Model** (pacote `model`), como pedido nas orientações.

- A única classe pública é `Jogo`, que é a API usada pela View e pelo Controller. Ela só recebe e devolve
  `String`, `int` e `boolean`. A `JogadaInvalidaException` também é pública porque sai pela API.
- As outras classes (tabuleiro, paradas, províncias, cidades, jogador etc.) não são públicas.
- As coleções usam o framework do Cap. 15 (`HashMap`, `HashSet`, `ArrayList`).
- Singleton e Façade ficam para a 3ª iteração.

Regras implementadas:
- preparação da partida (2 a 5 jogadores, moedas iniciais, peões em Karakorum, 3 cidades reveladas);
- mover o peão (vizinhança, pular yurts próprios, parada ocupada, parada dupla);
- pegar tributo de província vizinha;
- usar o Khan (só quando a coluna tem o ícone, uma vez por turno);
- atacar cidade com espadas e conquistar;
- construir yurt;
- votos no Kurultai e fim de partida.

## Testes

São 65 testes em JUnit 4 (pasta `test/model`), separados por regra, e a suite `TodosOsTestes` roda todos.
Seguimos o resumo do Cap. 18: nome descritivo, um assert por teste com mensagem, timeout, `@Before`
e teste de exceção com `expected`.

## Falta fazer

- passar o tabuleiro real para a `FabricaTabuleiro` (hoje usamos um mapa de exemplo pequeno);
- nomes dos tesouros 3, 4 e 5;
- colunas do tabuleiro do jogador e peças de ativação (hoje `iniciarTurno` recebe os números direto);
- melhorias, conselheiros, habilidades dos herdeiros e pontuação final.
