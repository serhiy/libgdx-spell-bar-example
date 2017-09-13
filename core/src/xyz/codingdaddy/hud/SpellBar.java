package xyz.codingdaddy.hud;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import xyz.codingdaddy.GameTime;
import xyz.codingdaddy.domain.Spell;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Touchable;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.ChangeListener;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.Align;

public class SpellBar extends Table {
	
	private static final float EPSILON = 0.00001f;
	private static final int MAX_SLOTS = 10;
	
	private final Map<Spell, SpellBarImageButton> spellBarButtons = new HashMap<Spell, SpellBarImageButton>();

	public SpellBar(List<Spell> spells) {
		super();
		
		TextureAtlas textureAtlas = new TextureAtlas(Gdx.files.internal("hud.pack"));
		
		align(Align.left);
		
		setBackground(new TextureRegionDrawable(textureAtlas.findRegion("spellbar_background")));
		
		for (int count = 0; count < Math.min(spells.size(), MAX_SLOTS); count ++) {
			final Spell spell = spells.get(count);
			
			String spellName = spell.getName().toLowerCase();
			SpellBarImageButton imageButton = new SpellBarImageButton(new TextureRegionDrawable(textureAtlas.findRegion(spellName + "_up")), new TextureRegionDrawable(textureAtlas.findRegion(spellName + "_down")), spell.getCooldown(), count);
			imageButton.getStyle().imageDisabled = new TextureRegionDrawable(textureAtlas.findRegion(spellName + "_down"));
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

		Image frame = new Image(textureAtlas.findRegion("spellbar_frame"));
		frame.setPosition(0, 0);
		frame.setTouchable(Touchable.disabled);
		addActor(frame);
	}
	
	public void update() {
		for (Entry<Spell, SpellBarImageButton> combatUnitBaseRecruitmentButton : spellBarButtons.entrySet()) {
			combatUnitBaseRecruitmentButton.getValue().update();
		}
	}
	
	class SpellBarImageButton extends ImageButton {

		private final CooldownTimer cooldownTimer;
		
		private final float cooldown;
		private final int index;

		private float cooldownTriggerTime = -Float.MAX_VALUE;
		
		public SpellBarImageButton(Drawable imageUp, Drawable imageDown, float cooldown, int index) {
			super(imageUp, imageDown);
			this.cooldown = cooldown;
			this.index = index;

			this.cooldownTimer = new CooldownTimer(true);
			cooldownTimer.setSize(50, 54);
			cooldownTimer.setPosition(0, 0);
			cooldownTimer.setColor(Color.WHITE);
			
			addActor(this.cooldownTimer);
		}

		public boolean isOnCooldown() {
			return getRemainingCooldownTime() - EPSILON > 0;
		}

		public float getCooldownTriggerTime() {
			return cooldownTriggerTime;
		}

		public void setCooldownTriggerTime(float cooldownTriggerTime) {
			this.cooldownTriggerTime = cooldownTriggerTime;
		}
		
		public int getIndex() {
			return index;
		}
		
		public float getRemainingCooldownTime() {
			return Math.max(0, cooldown - (GameTime.getCurrentTime() - cooldownTriggerTime));
		}
		
		public float getRemainingCooldownPercentage() {
			if (GameTime.getCurrentTime() - cooldownTriggerTime > cooldown) {
				return 1.0f;
			}
			return (GameTime.getCurrentTime() - cooldownTriggerTime) / cooldown;
		}
		
		public void update() {
			if (getRemainingCooldownPercentage() - EPSILON <= 1.0f) {
				cooldownTimer.setVisible(true);
				cooldownTimer.update(getRemainingCooldownPercentage());
			} else {
				cooldownTimer.setVisible(false);
			}
		}
	}
}
