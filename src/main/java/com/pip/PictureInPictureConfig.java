package com.pip;

import java.awt.Image;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("pip")
public interface PictureInPictureConfig extends Config
{
	enum quadrant
	{
		BOTTOM_RIGHT("Bottom Right", 4),
		BOTTOM_LEFT("Bottom Left", 3),
		TOP_LEFT("Top Left", 2),
		TOP_RIGHT("Top Right", 1)
		;

		private String value;
		private int id;

		quadrant(String value, int id)
		{
			this.value = value;
			this.id = id;
		}

		@Override
		public String toString()
		{
			return this.value;
		}

		public int toInt()
		{
			return this.id;
		}
	}

	enum redrawRate
	{
		FASTEST("Fastest", 2),
		STANDARD("Standard", 4),
		SLOWER("Slower", 8),
		SLOWEST("Slowest", 16)
		;

		private String value;
		private int id;

		redrawRate(String value, int id)
		{
			this.value = value;
			this.id = id;
		}

		@Override
		public String toString()
		{
			return this.value;
		}

		public int toInt()
		{
			return this.id;
		}
	}

	enum targetSize
	{
		SMALL("320 x 180", 320, 180),
		MEDIUM("480 x 270", 480, 270),
		LARGE("640 x 360", 640, 360),
		XLARGE("800 x 450", 800, 450)
		;

		private String value;
		private int width;
		private int height;

		targetSize(String value, int width, int height)
		{
			this.value = value;
			this.width = width;
			this.height = height;
		}

		@Override
		public String toString()
		{
			return this.value;
		}
		public int getHeight()
		{
			return this.height;
		}
		public int getWidth()
		{
			return this.width;
		}
	}

	enum limitedDimension
	{
		HEIGHT("Height"),
		WIDTH("Width")
		;

		private String value;

		limitedDimension(String value)
		{
			this.value = value;
		}

		@Override
		public String toString()
		{
			return this.value;
		}

	}

	enum clickAction
	{
		FOCUS("Focus RuneLite", 0),
		NOTHING("Do Nothing", 1)
		;

		private String value;
		private int action;

		clickAction(String value, int action)
		{
			this.action = action;
			this.value = value;
		}

		@Override
		public String toString()
		{
			return this.value;
		}
		public int clickMode()
		{
			return this.action;
		}
	}

	enum scaleQuality
	{
		FAST("Fast", Image.SCALE_FAST),
		SMOOTH("Smooth", Image.SCALE_SMOOTH)
		;

		private String value;
		private int quality;

		scaleQuality(String value, int quality)
		{
			this.value = value;
			this.quality = quality;
		}

		@Override
		public String toString()
		{
			return this.value;
		}
		public int toInt()
		{
			return this.quality;
		}
	}

	@ConfigItem(
			keyName = "quadrantID",
			name = "Position",
			description = "Configures the position of the Picture in Picture",
			position = 0
	)
	default quadrant quadrantID() { return quadrant.BOTTOM_RIGHT; }

	@ConfigItem(
			keyName = "redrawRate",
			name = "Redraw Rate",
			description = "Configures the redraw rate of the Picture in Picture",
			position = 1
	)
	default redrawRate redrawRate() { return redrawRate.STANDARD; }

	@ConfigItem(
			keyName = "paddingX",
			name = "Horizontal Padding",
			description = "The horizontal padding (in pixels) from the left/right edge of the screen",
			position = 2
	)
	default int paddingX() { return 40; }

	@ConfigItem(
			keyName = "paddingY",
			name = "Vertical Padding",
			description = "The vertical padding (in pixels) from the top/bottom edge of the screen",
			position = 3
	)
	default int paddingY() { return 25; }

	@ConfigItem(
			keyName = "targetSize",
			name = "Target Size",
			description = "Specifies the target size of the Picture in Picture",
			position = 4
	)
	default targetSize targetSize() { return targetSize.MEDIUM; }

	@ConfigItem(
			keyName = "limitedDimension",
			name = "Limited Dimension",
			description = "Configures which dimension is limited when not 16:9",
			position = 5
	)
	default limitedDimension limitedDimension() { return limitedDimension.HEIGHT; }

	@ConfigItem(
			keyName = "clickAction",
			name = "Click Action",
			description = "Action to perform when the Picture in Picture is clicked",
			position = 6
	)
	default clickAction clickAction() { return clickAction.FOCUS; }

	@ConfigItem(
			keyName = "scaleQuality",
			name = "Scale Quality",
			description = "What quality Picture in Picture is scaled with",
			position = 7
	)
	default scaleQuality scaleQuality() { return scaleQuality.FAST; }
}
