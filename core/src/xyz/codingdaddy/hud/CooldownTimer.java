package xyz.codingdaddy.hud;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Blending;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Representation of the circular cooldown optimized for spell bar component.
 * 
 * @author serhiy
 */
public class CooldownTimer extends Table {
	
	private static final float START_ANGLE = 90;
	private static final float ALPHA = 0.5f;
	private static final int SCALE_FACTOR = 4;
	
	private final boolean clockwise;
	
	private Image cooldownDisplay;
	private TextureRegionDrawable cooldownTexture;
	
	/**
	 * @param clockwise determines the rotation side of the cooldown timer.
	 * @param width determines the width of the cooldown representation.
	 * @param height determines the height of the cooldown representation.
	 */
	public CooldownTimer(boolean clockwise, float width, float height) {
		super();
		
		this.setSize(width, height);

		this.clockwise = clockwise;

		this.cooldownDisplay = new Image(cooldownTimer(0.0f));
		this.cooldownDisplay.setPosition(0, 0);
		this.cooldownDisplay.setSize(width, height);
		this.cooldownDisplay.setColor(1.0f, 1.0f, 1.0f, ALPHA);
		addActor(this.cooldownDisplay);
	}
	
	/**
	 * @param remainingPercentage to be rendered by cooldown timer.
	 */
	public void update(float remainingPercentage) {
		((TextureRegionDrawable)cooldownDisplay.getDrawable()).setRegion(cooldownTimer(remainingPercentage).getRegion());
	}
	
	/**
	 * Creates a {@link TextureRegionDrawable} which represents the remaining time for cooldown to be completed. Two
	 * {@link Pixmap}s are required, since the first one is drawing the large circular cooldown indicator, while the 
	 * second one is used to clip the area which fits respective spell bar button.
	 * 
	 * @param remainingPercentage for calculating and creating a representation of remaining cooldown.
	 * @return {@link TextureRegionDrawable} which represents the remaining time for cooldown.
	 */
	private TextureRegionDrawable cooldownTimer(float remainingPercentage) {
		if (remainingPercentage > 1.0f || remainingPercentage < 0.0f) {
			return null;
		}
		
		float radius = Math.min(getWidth(), getHeight()) * SCALE_FACTOR / 2;
		float angle = calculateAngle(remainingPercentage);
		int segments = calculateSegments(angle);
		
		int width = (int) (getWidth());
		int height = (int) (getHeight());
		
		Pixmap display = new Pixmap(width * SCALE_FACTOR, height * SCALE_FACTOR, Format.RGBA8888);
		Pixmap finalDisplay = new Pixmap(width, height, Format.RGBA8888);
		
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
				display.fillTriangle(width * SCALE_FACTOR/2, height * SCALE_FACTOR/2, (int) (width * SCALE_FACTOR/2 + pcx), (int)(height * SCALE_FACTOR/2 + pcy), (int) (width * SCALE_FACTOR/2 + cx), (int) (height * SCALE_FACTOR/2 + cy));
			} 
			
			finalDisplay.drawPixmap(display, 0, 0, width * SCALE_FACTOR/2 - width/2, height * SCALE_FACTOR/2 - height/2, width, height);

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
	
	/**
	 * @param remainingPercentage to calculate angle.
	 * @return the angle of the arc which needs to be displayed according to the provided parameters.
	 */
	private float calculateAngle(float remainingPercentage) {
		if (clockwise) {
			return 360 - 360 * remainingPercentage;
		} else {
			return 360 * remainingPercentage - 360;
		}
	}
	
	/**
	 * @param angle to determine the number of segments required for displaying an arc.
	 * @return the number of the segments according to the provided arguments.
	 */
	private int calculateSegments(float angle) {
		return Math.max(1, (int) (6 * (float) Math.cbrt(Math.abs(angle)) * (Math.abs(angle) / 360.0f)));
	}
}
