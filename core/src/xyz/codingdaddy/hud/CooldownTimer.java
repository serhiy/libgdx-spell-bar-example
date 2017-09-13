package xyz.codingdaddy.hud;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Allows to create circular cooldowns.
 * 
 * @author serhiy
 */
public class CooldownTimer extends Table {
	
	private static final float START_ANGLE = 90;
	private static final int SCALE_FACTOR = 4;
	
	private final boolean clockwise;
	
	private Table cooldownDisplay;
	private TextureRegionDrawable cooldownTexture;
	
	private float alpha = 0.5f;
	
	/**
	 * @param clockwise determines the rotation side of the cooldown timer.
	 */
	public CooldownTimer(boolean clockwise) {
		this.clockwise = clockwise;

		cooldownDisplay = new Table();
		cooldownDisplay.setPosition(0, 0);
		addActor(cooldownDisplay);
	}
	
	/**
	 * @param remainingPercentage to be rendered by cooldown timer.
	 */
	public void update(float remainingPercentage) {
		cooldownDisplay.clear();
		
		Image cooldownTimer = new Image(cooldownTimer(remainingPercentage));
		cooldownTimer.setColor(1.0f, 1.0f, 1.0f, alpha);
		cooldownDisplay.addActor(cooldownTimer);
	}
	
	public float getAlpha() {
		return alpha;
	}

	/**
	 * @param alpha to be applied to the final cooldown indicator.
	 */
	public void setAlpha(float alpha) {
		this.alpha = alpha;
	}
	
	private Drawable cooldownTimer(float remainingPercentage) {
		if (remainingPercentage > 1.0f || remainingPercentage < 0.0f) {
			return null;
		}
		
		float radius = Math.min(getWidth(), getHeight()) * SCALE_FACTOR / 2;
		float angle = calculateAngle(remainingPercentage);
		int segments = calculateSegments(angle);
		
		Pixmap display = new Pixmap((int) (getWidth() * SCALE_FACTOR), (int) (getHeight() * SCALE_FACTOR), Format.RGBA8888);
		Pixmap finalDisplay = new Pixmap((int) getWidth(), (int) getHeight(), Format.RGBA8888);
		
		Blending blending = Pixmap.getBlending();
		
		try {
			float theta = (2 * MathUtils.PI * (angle / 360.0f)) / segments;
			float cos = MathUtils.cos(theta);
			float sin = MathUtils.sin(theta);
			float cx = radius * MathUtils.cos(START_ANGLE * MathUtils.degreesToRadians);
			float cy = radius * MathUtils.sin((-1 * START_ANGLE) * MathUtils.degreesToRadians);

			display.setColor(getColor());
			
			for (int count = 0; count < segments; count++) {
				float pcx = cx;
				float pcy = cy;
				float temp = cx;
				cx = cos * cx - sin * cy;
				cy = sin * temp + cos * cy;
				display.fillTriangle((int) (getWidth() * SCALE_FACTOR / 2), (int) (getHeight() * SCALE_FACTOR / 2), (int) ((int) (getWidth() * SCALE_FACTOR / 2) + pcx), (int) (getHeight() * SCALE_FACTOR / 2 + pcy), (int) ((int) (getWidth() * SCALE_FACTOR / 2) + cx), (int) (getHeight() * SCALE_FACTOR / 2 + cy));
			}
			
			finalDisplay.drawPixmap(display, 0, 0, (int)((getWidth() * SCALE_FACTOR / 2) - (getWidth() / 2)), (int)((getHeight() * SCALE_FACTOR / 2) - (getHeight() / 2)), (int) (getWidth()), (int) (getHeight()));

			Pixmap.setBlending(Blending.None);

			if (cooldownTexture == null) {
				cooldownTexture = new TextureRegionDrawable(new TextureRegion(new Texture(finalDisplay)));
			} else {
				cooldownTexture.getRegion().getTexture().draw(finalDisplay, 0, 0);
			}

			return cooldownTexture;
		} finally {
			Pixmap.setBlending(blending);
			
			display.dispose();
			finalDisplay.dispose();
		}
	}
	
	private float calculateAngle(float remainingPercentage) {
		if (clockwise) {
			return 360 * remainingPercentage - 360;
		} else {
			return 360 - 360 * remainingPercentage;
		}
	}
	
	private int calculateSegments(float angle) {
		return Math.max(1, (int) (6 * (float) Math.cbrt(Math.abs(angle)) * (Math.abs(angle) / 360.0f)));
	}
}
