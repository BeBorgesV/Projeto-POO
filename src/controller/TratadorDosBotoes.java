package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Tratador dos botões de todas as janelas (Cap. 12: um listener pode tratar
 * vários eventos). Cada botão tem um comando (setActionCommand) e o
 * Controller decide o que fazer com ele.
 */
class TratadorDosBotoes implements ActionListener {
	private final ControladorDoJogo controlador;

	TratadorDosBotoes(ControladorDoJogo controlador) {
		this.controlador = controlador;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		controlador.executar(e.getActionCommand());
	}
}
