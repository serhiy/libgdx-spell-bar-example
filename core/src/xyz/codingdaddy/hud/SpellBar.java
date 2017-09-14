package xyz.codingdaddy.hud;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import xyz.codingdaddy.Constants;
import xyz.codingdaddy.GameTime;
import xyz.codingdaddy.domain.Spell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton.ImageButtonStyle;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Represents the spell bar HUD component.
 * 
 * @author serhiy
 */
public class SpellBar extends Table {
	
	private static final int MAX_SLOTS = 10;
	
	private final Map<Spell, SpellBarImageButton> spellBarButtons = new HashMap<Spell, SpellBarImageButton>();

	/**
	 * @param spells to be added to the spell bar.
	 */
	public SpellBar(List<Spell> spells) {
		super();
		
		TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal(Constants.TEXTURE_ATLAS_HUD));
		
		setBackground(new TextureRegionDrawable(textureAtlas.findRegion(Constants.TEXTURE_ATLAS_HUD_SPELL_BAR_BACKGROUND)));
		
		for (int count = 0; count < Math.min(spells.size(), MAX_SLOTS); count ++) {
			final Spell spell = spells.get(count);
			
			String spellName = spell.getName().toLowerCase();
			
			ImageButtonStyle imageButtonStyle = new ImageButtonStyle();
			imageButtonStyle.imageUp = new TextureRegionDrawable(textureAtlas.findRegion(spellName + Constants.TEXTURE_ATLAS_HUD_BUTTON_UP_SUFFIX));
			imageButtonStyle.imageDown = new TextureRegionDrawable(textureAtlas.findRegion(spellName + Constants.TEXTURE_ATLAS_HUD_BUTTON_DOWN_SUFFIX));
			imageButtonStyle.imageDisabled = new TextureRegionDrawable(textureAtlas.findRegion(spellName + Constants.TEXTURE_ATLAS_HUD_BUTTON_DOWN_SUFFIX));
			
			SpellBarImageButton imageButton = new SpellBarImageButton(imageButtonStyle, spell.getCooldown());
			imageButton.setPosition(7 + count * 57, 8);
			addActor(imageButton);

			imageButton.addListener(new ChangeListener() {
				public void changed (ChangeEvent event, Actor actor) {
					if (!((SpellBarImageButton)actor).isOnCooldown()) {
						spell.getAction().execute(spell.getName());
						((SpellBarImageButton)actor).setCooldownTriggerTime(GameTime.getCurrentTime());
					}
			    }
			});
				
			spellBarButtons.put(spell, imageButton);
		}

		Image frame = new Image(textureAtlas.findRegion(Constants.TEXTURE_ATLAS_HUD_SPELL_BAR_FRAME));
		frame.setPosition(0, 0);
		frame.setTouchable(Touchable.disabled);
		addActor(frame);
	}
	
	/**
	 * Update the state of the spell bar.
	 */
    public void update() {
		for (SpellBarImageButton spellBarButton : spellBarButtons.values()) {
			spellBarButton.update();
		}
	}
	
	/**
	 * Represents a button of the spell bar component.
	 * 
	 * @author serhiy
	 */
	class SpellBarImageButton extends ImageButton {

		private final CooldownTimer cooldownTimer;
		private final float cooldown;
		
		private float cooldownTriggerTime = -Float.MAX_VALUE;
		
		public SpellBarImageButton(ImageButtonStyle imageButtonStyle, float cooldown) {
			super(imageButtonStyle);
			this.cooldown = cooldown;

			this.cooldownTimer = new CooldownTimer(true, 50, 54);
			cooldownTimer.setPosition(0, 0);
			cooldownTimer.setColor(Color.WHITE);
			
			addActor(this.cooldownTimer);
		}

		public boolean isOnCooldown() {
			return getRemainingCooldownTime() - Constants.EPSILON > 0;
		}

		public float getCooldownTriggerTime() {
			return cooldownTriggerTime;
		}

		public void setCooldownTriggerTime(float cooldownTriggerTime) {
			this.cooldownTriggerTime = cooldownTriggerTime;
		}

		public float getRemainingCooldownTime() {
			return Math.max(0, cooldown - (GameTime.getCurrentTime() - cooldownTriggerTime));
		}
		
		public float getRemainingCooldownPercentage() {
			if ((cooldown - (GameTime.getCurrentTime() - cooldownTriggerTime)) <= 0) {
				return 0.0f;
			}
			return (cooldown - (GameTime.getCurrentTime() - cooldownTriggerTime)) / cooldown;
		}
		
		public void update() {
			if (getRemainingCooldownPercentage() - Constants.EPSILON >= 0.0f) {
				cooldownTimer.setVisible(true);
				cooldownTimer.update(getRemainingCooldownPercentage());
			} else {
				cooldownTimer.setVisible(false);
			}
		}
	}
}
