package controller;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

/** "X" da cartela ou da janela de cartas: o Controller esconde a janela pelo Model. */
class TratadorDasJanelas extends WindowAdapter {
	private final ControladorDoJogo controlador;

	TratadorDasJanelas(ControladorDoJogo controlador) {
		this.controlador = controlador;
	}

	@Override
	public void windowClosing(WindowEvent e) {
		controlador.fecharJanela(e.getWindow());
	}
}
