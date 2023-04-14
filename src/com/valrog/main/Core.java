package com.valrog.main;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;

public class Core extends Canvas implements Runnable {

	private static final long serialVersionUID = 1L;
	
	public static int width = 300;
	public static int height = width / 12 * 9;
	public static int scale = 3;
	public static int test = '';s
	
	public static String title = "Valrog";
	
	private boolean running = false;
	private Thread thread;
	private JFrame frame;
	private Screen screen;
	
	private BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	
	public Core() {
		Dimension size = new Dimension(width * scale, height * scale);
		setPreferredSize(size);
		
		screen = new Screen(width, height);
		frame = new JFrame();
	}
	
	public synchronized void start() {
		running = true;
		
		thread = new Thread(this, "Display");
		thread.start();
	}
	
	public synchronized void stop() {
		running = false;
		
		try {
			thread.join();
		} catch(InterruptedException e) {
			e.printStackTrace();
		}
	}

	public void run() {
		double ns = 1000000000.0 / 60.0;
		double delta = 0;
		
		int frames = 0;
		int updates = 0;
		
		long lastTime = System.nanoTime();
		long timer = System.currentTimeMillis();
		
		while(running) {
			long now = System.nanoTime();
			
			delta += (now - lastTime) / ns;
			lastTime = now;
			
			while(delta >= 1) {
				update();
				updates++;
			}
			
			render();
			frames++;
			
			if(System.currentTimeMillis() - timer >= 1000) {
				timer += 1000;
				frame.setTitle(title + " | " + updates + " UPS, " + frames + " FPS");
				frames = 0;
				updates = 0;
			}
		}
		stop();
	}
	
	public void update() {
		
	}
	
	public void render() {
		BufferStrategy bs = getBufferStrategy();
		if(bs == null) {
			createBufferStrategy(3);
			return;
		}
		
		screen.clear();
		screen.render(0, 0);
		
		for(int i = 0; i < pixels.length; i++) {
			pixels[i] = screen.pixels[i];
		}
		
		Graphics g = bs.getDrawGraphics();
		
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}
	
	public static void main(String[] args) {
		Core game = new Core();
		
		game.frame.setResizable(false);
		game.frame.setTitle(Core.title);
		game.frame.add(game);
		game.frame.pack();
		game.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.frame.setLocationRelativeTo(null);
		game.frame.setVisible(true);
		
		game.start();
	}
	
}
