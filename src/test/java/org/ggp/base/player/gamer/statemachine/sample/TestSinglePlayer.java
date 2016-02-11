package org.ggp.base.player.gamer.statemachine.sample;

import org.ggp.base.player.gamer.statemachine.SinglePlayer;
import org.ggp.base.player.gamer.statemachine.random.RandomGamer;
import org.ggp.base.util.game.GameRepository;
import org.ggp.base.util.gdl.grammar.GdlPool;
import org.ggp.base.util.match.Match;
import org.junit.Assert;
import org.junit.Test;

/*
 * Við viljum hann keyri gefinn leik sem við þekkjum lausnirnar á.
 * Athuga hvort hann sé að gefa upp réttar lausnir.
 */
public class TestSinglePlayer extends Assert {
	@Test
	public void testCoins() {
		try {
			SinglePlayer s = new SinglePlayer();
			Match m = new Match("", -1, 1000, 1000, GameRepository
					.getDefaultRepository().getGame("coins"), "");

			s.setMatch(m);
			s.setRoleName(GdlPool.getConstant("you"));
			s.metaGame(1000);
			assertEquals("SinglePlayer", s.getName());

			assertTrue(s.selectMove(1000) != null);
			assertTrue(GdlPool.getConstant("you") == s.getRoleName());
			if(m.isCompleted())
			{
				assertTrue(s.bestValue == 100);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	public void testTicTacToe(){
		SinglePlayer s = new SinglePlayer();
		RandomGamer r = new RandomGamer();
		Match m = new Match("", 10, 120, 120, GameRepository.getDefaultRepository().getGame("ticTacToe"),"");
		s.setMatch(m);
		r.setMatch(m);

		s.setRoleName(GdlPool.getConstant("xplayer"));
		r.setRoleName(GdlPool.getConstant("oplayer"));

		assertTrue(s.getRoleName().toString() == "xplayer");
		assertTrue(r.getRoleName().toString() == "oplayer");

		if(m.isCompleted())
		{
			assertTrue(s.bestValue >= 50);


		}

	}

}
