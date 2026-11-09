package model;

/**
 * Ícones de ação das colunas do tabuleiro do jogador e das melhorias.
 *
 * MOEDA, ESPADA, YURT e TESOURO são os recursos "virtuais" das melhorias:
 * valem só no turno em que a coluna é ativada.
 */
enum Acao {
	CAVALO, PUNHO, KHAN, RECUPERAR, MOEDA, ESPADA, YURT, TESOURO
}
