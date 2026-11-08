package controller;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/** Clique na cartela: o Controller ativa a coluna clicada. */
class TratadorDaCartela extends MouseAdapter {
	private final ControladorDoJogo controlador;

	TratadorDaCartela(ControladorDoJogo controlador) {
		this.controlador = controlador;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		controlador.cliqueNaCartela(e.getX(), e.getY());
	}
}
