# Herdeiros do Khan - INF1636 - 3ª iteração

Trabalho de POO (prof. Ivan Mathias Filho), 2026.2.

Integrantes:
- Bernardo Borges Vieira - 2211838
- Bruno Pestana - 2310763
- Ian Siqueira Matielo Moura - 2310643

Imagens originais do jogo: https://drive.google.com/drive/folders/17Jtg_bu90KbPdT2hoqx60qx4Oskz6ZYV?usp=sharing
As cópias usadas pelo programa, reduzidas e com o nome do id do Model, estão em `images/`.

## Como rodar

Projeto do Eclipse. Basta importar com *File > Import > Existing Projects into Workspace*.
O JUnit 4 já está na pasta `lib/`.

- Jogo: botão direito em `src/controller/Principal.java` > *Run As > Java Application*.
- Testes: botão direito em `test/model/TodosOsTestes.java` > *Run As > JUnit Test*.

## Organização (MVC)

- `model`: regras do jogo. Públicos só a fachada `Jogo`, a `JogadaInvalidaException` e as interfaces
  `ObservadorIF` / `ObservadoIF`.
- `view`: janelas. Tabuleiro, peões, cartela, melhorias, cartas e peças são desenhados só com `drawImage()`.
- `controller`: `ControladorDoJogo` organiza a sequência da partida; os tratadores de eventos são classes
  próprias, sem expressões lambda.

## Padrões da 3ª iteração (Cap. 17)

- **Singleton:** `Jogo.getJogo()` e `ControladorDoJogo.getControlador()`, com construtor privado, como o
  `CtrlVenda` do slide.
- **Façade:** `Jogo` é a única porta de entrada das regras e só recebe e devolve `String`, `int`, `boolean`
  e `List<String>`.
- **Observer:** `Jogo` implementa `ObservadoIF` (`add`, `remove`, `get`) e avisa a cada mudança de estado. Os
  painéis e janelas implementam `ObservadorIF` (`notify`) e se registram pelo Controller, como o `pnRelogio`
  do slide. É assim que a tela acompanha o movimento dos peões e que a cartela e a janela de cartas abrem
  e fecham (`Jogo.exibirCartela` / `exibirCartas`).
- **Strategy:** cada carta de conselheiro (aberto e secreto) traz a própria regra de contagem
  (`ConselheiroAberto`, `ConselheiroSecreto`), o que deixa entrar carta nova sem mexer na apuração.

## Como se joga na interface

1. Janela inicial > *Novo jogo* (*Continuar* fica para a 4ª iteração).
2. Marque de 2 a 5 herdeiros e clique em *Jogar*. A ordem de jogo é a das caixas.
3. Na 1ª vez de cada jogador, a janela de cartas abre para escolher o conselheiro secreto.
4. A cartela abre sozinha: clique numa coluna livre para ativá-la.
5. Para mover, clique numa das paradas contornadas de branco no mapa (são as que o peão alcança com
   1 movimento; não há botão de mover, como pede a 2ª iteração). Para o Khan, para pegar tributo e para
   construir yurt, clique no botão e depois num dos lugares marcados em amarelo (o mesmo botão cancela).
   As ações extras ficam nos botões à direita, agrupadas por seção.
6. *Encerrar turno* passa a vez. Ação inválida dos botões aparece no rodapé, sem caixa de diálogo; clique
   inválido no mapa para mover não faz nada.

## Escolhas que o manual não decide

- As imagens mostram uma melhoria de cada tipo e cor, mas não quantas há de cada cor. Os totais por tipo
  seguem o manual; a divisão por cor está em `Melhoria.todas()`.
- Conselheiro de "3 tesouros quaisquer" (C9): o programa entrega os tesouros de que o jogador tem mais.
- Bônus "3 tesouros": o jogador fica com o 1º sorteado.
- Jochi: das 2 melhorias iniciais, uma vai para a coluna branca e a outra para a coluna da cor dela.

## Testes

São 111 testes em JUnit 4 (pasta `test/model`), separados por regra, e a suite `TodosOsTestes` roda todos.

## Falta fazer (4ª iteração)

- salvar e carregar partida.
