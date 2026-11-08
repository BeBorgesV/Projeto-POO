package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Clique no tabuleiro: o Controller tenta mover o peão da vez para a parada clicada. */
class TratadorDoTabuleiro extends MouseAdapter {
	private final ControladorDoJogo controlador;

	TratadorDoTabuleiro(ControladorDoJogo controlador) {
		this.controlador = controlador;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		controlador.cliqueNoTabuleiro(e.getX(), e.getY());
	}
}
