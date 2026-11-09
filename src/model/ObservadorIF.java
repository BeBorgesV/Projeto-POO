package model;

/**
 * Observador do padrão Observer, como no Cap. 17 (pnRelogio).
 *
 * Quem implementa (os painéis e janelas da View) se registra no observado
 * e, ao ser notificado, pede de volta os dados de que precisa (callback).
 */
public interface ObservadorIF {
	void notify(ObservadoIF o);
}
