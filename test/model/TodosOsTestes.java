package model;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/** Roda todos os testes do Model de uma vez (Cap. 18, "Suite de testes"). */
@RunWith(Suite.class)
@Suite.SuiteClasses({
	PreparacaoTest.class,
	TurnoTest.class,
	MovimentoTest.class,
	TributoTest.class,
	AtaqueCidadeTest.class,
	YurtTest.class,
	KhanTest.class,
	KurultaiTest.class,
	ColunaTest.class,
	MelhoriaTest.class,
	ConselheiroTest.class,
	FimDeJogoTest.class,
	HerdeiroTest.class,
	PadroesTest.class
})
public class TodosOsTestes {
}
