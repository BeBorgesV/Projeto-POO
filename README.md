# Herdeiros do Khan - INF1636 - 2ª iteração

Trabalho de POO (prof. Ivan Mathias Filho), 2026.2.

Integrantes:
- Bernardo Borges Vieira - 2211838
- Ian Siqueira Matielo Moura - 2310643
- (completar)

Imagens originais do jogo (cartas, cartelas e cartões ainda não usados): https://drive.google.com/drive/folders/17Jtg_bu90KbPdT2hoqx60qx4Oskz6ZYV?usp=sharing

## Como rodar

Projeto do Eclipse. Basta importar com *File > Import > Existing Projects into Workspace*.
O JUnit 4 já está na pasta `lib/` e as imagens usadas estão na pasta `images/`.

- Jogo: botão direito em `src/controller/Principal.java` > *Run As > Java Application*.
- Testes: botão direito em `test/model/TodosOsTestes.java` > *Run As > JUnit Test*.

## Organização (MVC)

- `model`: regras do jogo. A única classe pública é `Jogo` (mais a `JogadaInvalidaException`).
- `view`: janela do tabuleiro. O tabuleiro e os peões são desenhados só com `drawImage()` do `Graphics2D`.
- `controller`: `Principal` (main) e os tratadores de eventos, um por classe, sem expressões lambda.

## O que tem nesta iteração

- O tabuleiro oficial no Model (`FabricaTabuleiro.criarTabuleiroOficial`): 34 paradas
  (R1-R11 na Rússia, P1-P11 na Pérsia, C1-C11 na China e Karakorum), 6 paradas duplas,
  as rotas, as 9 cidades e as 12 províncias, sendo 4 do Khan. Os testes continuam usando o mapa de exemplo.
- Janela de no máximo 1400 x 1050 com o tabuleiro, os peões e a cor do jogador da vez.
- Como o Khan não tem dados, o equivalente é a coluna ativada: o testador escolhe num `JComboBox`
  quantos movimentos (cavalos) a coluna dá e clica em *Ativar coluna*.
- Para mover, o jogador clica numa parada. Cada clique gasta 1 movimento. Se o movimento não for
  permitido pelas regras, nada acontece. As regras são as do Model: parada vizinha, pular yurts
  próprios, não terminar em parada ocupada (menos dupla e Karakorum).
- *Encerrar turno* passa a vez.
- A partida começa direto com 4 jogadores, porque as janelas iniciais não eram necessárias nesta iteração.

As imagens dos peões (`images/peao_*.png`) foram feitas por nós, nas cores das peças de ativação,
porque não estavam entre as imagens do jogo.

## Testes

São 65 testes em JUnit 4 (pasta `test/model`), separados por regra, e a suite `TodosOsTestes` roda todos.
Nesta iteração não foi pedido teste novo.

## Falta fazer

- janelas iniciais (nova partida / continuar e escolha dos herdeiros);
- tabuleiro do jogador com as colunas e peças de ativação (hoje os movimentos são escolhidos pelo testador);
- pegar tributo, Khan, atacar cidade e construir yurt pela interface (já existem no Model);
- exibição das cartas de conselheiro;
- salvar e carregar partida;
- melhorias, habilidades dos herdeiros e pontuação final.
