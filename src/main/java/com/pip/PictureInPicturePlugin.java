package com.pip;

import com.google.inject.Provides;

import javax.imageio.ImageIO;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.events.ClientTick;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientUI;
import net.runelite.client.ui.DrawManager;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Consumer;
import javax.swing.*;


@Slf4j
@PluginDescriptor(
		name = "Picture In Picture",
		description = "Displays picture in picture mode when RuneLite is not in focus",
		tags = {"pip", "picture", "display", "afk"}
)
public class PictureInPicturePlugin extends Plugin
{

	private static boolean focused = true;
	private static boolean pipUp = false;
	private JFrame pipFrame = null;
	private JLabel lbl = null;
	private int clientTick = 0;
	private int pipWidth, pipHeight;
	private double pipScale;

	@Inject
	private ClientUI clientUi;

	@Inject
	private ScheduledExecutorService executor;

	@Inject
	private DrawManager drawManager;

	@Inject
	private PictureInPictureConfig config;

	@Override
	protected void startUp() throws Exception
	{
		log.debug("PIP started!");
	}

	@Override
	protected void shutDown() throws Exception
	{
		log.debug("PIP stopped!");
		destroyPip();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged gameStateChanged)
	{
		if (gameStateChanged.getGameState() == GameState.LOGIN_SCREEN)
			destroyPip();
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (!focused) {
			Window window = javax.swing.FocusManager.getCurrentManager().getActiveWindow();
			if (window == null) {
				if (pipFrame == null) {
					initializePip();
					log.debug("PIP initialized");
				}
			}
		}
	}

	@Subscribe
	public void onClientTick(ClientTick event)
	{
		if(clientTick % config.redrawRate().toInt()==0) {
			clientTick = 0;

			if (focused != clientUi.isFocused()) {
				focused = clientUi.isFocused();
				if (focused)
					destroyPip();
			}
			if (!focused) {
				if (pipFrame != null) {
					if (pipUp) {
						updatePip();
						//log.debug("PIP updated");
					}
				}
			}
		}
		clientTick++;
	}

	@Provides
	PictureInPictureConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(PictureInPictureConfig.class);
	}

	public void startPip(Image screenshot) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (config.limitedDimension().toString().equals("Width")) {
					pipWidth = config.targetSize().getWidth();
					pipScale = (double) pipWidth / (double) screenshot.getWidth(null) ;
					pipHeight = (int) (screenshot.getHeight(null) * pipScale);
				}
				else {
					pipHeight = config.targetSize().getHeight();
					pipScale = (double) pipHeight / (double) screenshot.getHeight(null);
					pipWidth = (int) (screenshot.getWidth(null) * pipScale);
				}

				Image img = screenshot.getScaledInstance(pipWidth, pipHeight, config.scaleQuality().toInt());
				ImageIcon icon=new ImageIcon(img);

				pipFrame=new JFrame();
				pipFrame.setFocusableWindowState(false);
				pipFrame.setType(Window.Type.UTILITY);
				pipFrame.setLayout(new FlowLayout());
				pipFrame.setSize(img.getWidth(null),img.getHeight(null));

				lbl=new JLabel();
				lbl.setIcon(icon);

				pipFrame.setUndecorated(true);
				pipFrame.add(lbl);
				pipFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				pipFrame.setAlwaysOnTop(true);

				pipFrame.addMouseListener(new MouseAdapter() {
					public void mousePressed(MouseEvent e) {
						pipClicked();
					}
				});

				//get screen info
				GraphicsConfiguration gc = clientUi.getGraphicsConfiguration();
				Rectangle bounds = gc.getBounds();
				Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
				Rectangle effectiveScreenArea = new Rectangle();
				effectiveScreenArea.x = bounds.x + screenInsets.left;
				effectiveScreenArea.y = bounds.y + screenInsets.top;
				effectiveScreenArea.height = bounds.height - screenInsets.top - screenInsets.bottom;
				effectiveScreenArea.width = bounds.width - screenInsets.left - screenInsets.right;

				//set location
				if (config.quadrantID().toInt() == 1)
					pipFrame.setLocation(effectiveScreenArea.width - pipWidth - config.paddingX(),config.paddingY());
				else if (config.quadrantID().toInt() == 2)
					pipFrame.setLocation(config.paddingX(),config.paddingY());
				else if (config.quadrantID().toInt() == 3)
					pipFrame.setLocation(config.paddingX(),effectiveScreenArea.height - pipHeight - config.paddingY());
				else
					pipFrame.setLocation(effectiveScreenArea.width - pipWidth - config.paddingX(),effectiveScreenArea.height - pipHeight - config.paddingY());

				// Display the window.
				pipFrame.pack();
				pipFrame.setVisible(true);
				pipUp = true;
			}
		});

	}

	public void destroyPip() {
		if (pipFrame != null) {
			pipFrame.setVisible(false);
			pipFrame.dispose();
			pipFrame = null;
			pipUp = false;
		}
	}

	public void pipClicked() {
		log.debug("PIP Clicked");
		if (config.clickAction().clickMode() == 0) {
			destroyPip();
			clientUi.requestFocus();
		}
	}

	//runs first to initialize pip
	public void initializePip() {
		Consumer<Image> imageCallback = (img) ->
		{
			executor.submit(() -> startPip(img));
		};
		drawManager.requestNextFrameListener(imageCallback);
	}

	//updates if pip is already up
	public void updatePip() {
		Consumer<Image> imageCallback = (img) ->
		{
			executor.submit(() -> updatePip(img));
		};
		drawManager.requestNextFrameListener(imageCallback);
	}

	//update image
	public void updatePip (Image image) {
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				Image img = image.getScaledInstance(pipWidth, pipHeight, config.scaleQuality().toInt());
				ImageIcon icon = new ImageIcon(img);
				icon.getImage().flush();
				lbl.setIcon(icon);
			}
		});
	}

	//test only (take screenshot)
	public void takeScreenshot(Image image) {
		BufferedImage screenshot = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
		Graphics graphics = screenshot.getGraphics();
		graphics.drawImage(image, 0, 0, null);

		File f = new File("clientImage.png");
		try {
			ImageIO.write(screenshot, "PNG", f);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
