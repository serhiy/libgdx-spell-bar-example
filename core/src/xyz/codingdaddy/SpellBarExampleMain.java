package xyz.codingdaddy;

import java.util.Arrays;
import java.util.List;

import xyz.codingdaddy.domain.Action;
import xyz.codingdaddy.domain.Spell;
import xyz.codingdaddy.hud.SpellBar;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;

/**
 * Application renderer.
 * 
 * @author serhiy
 */
public class SpellBarExampleMain extends ApplicationAdapter {

	private Stage stage;
	private SpellBar spellBar;
	private List<Spell> spells = Arrays.asList(new Spell("attack", 2, new Action()),
											   new Spell("heal", 10, new Action()));

	@Override
	public void create () {
		stage = new Stage();
		
		spellBar = new SpellBar(spells);
		spellBar.setPosition(30, 0);

		stage.addActor(spellBar);
		
		Gdx.input.setInputProcessor(stage);
	}

	@Override
	public void render () {
		Gdx.gl.glClearColor(0, 0, 0, 1);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
		GameTime.updateCurrentTime(Gdx.graphics.getDeltaTime());
		
		spellBar.update();
		
		stage.act();
		stage.draw();
	}
	
	@Override
	public void dispose () {
		stage.dispose();
	}
}
