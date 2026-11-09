package model;

/**
 * Observado do padrão Observer, como no Cap. 17 (Relogio).
 *
 * {@link #get(int)} é o callback do slide: o observador recebe a notificação
 * e pergunta ao observado o que mudou.
 */
public interface ObservadoIF {
	void add(ObservadorIF o);

	void remove(ObservadorIF o);

	int get(int i);
}
