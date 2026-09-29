package model;

/**
 * Tudo no mapa que fica ao lado de paradas: províncias e cidades.
 *
 * As regras de pegar tributo e de atacar cidade usam o mesmo teste
 * ("esta área é vizinha de alguma parada visitada no turno?"), então o
 * Jogo trabalha com a interface e não precisa saber qual das duas é.
 */
interface AreaDoMapa {
	boolean ehVizinhaDe(Parada p);
}
