package csun.c182L;

import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

/**
 * Simple pathfinding through a maze.
 * 
 * @author Milaud Miremadi
 *
 */
public class Pathfinder {

	private static final int SIZE_X = 41;
	private static final int SIZE_Y = 41;

	private static final boolean ALLOW_DIAGONAL_MOVEMENT = false;

	private static final int VID_MODE = ((SIZE_X * 16) << 10) | (SIZE_Y * 16);
	private static final int VID_W = (VID_MODE >> 10) & 0x3ff;
	private static final int VID_H = VID_MODE & 0x3ff;

	private static final int SCALE_X = VID_W / SIZE_X;
	private static final int SCALE_Y = VID_H / SIZE_Y;

	private static boolean[][] solid_tiles = new boolean[SIZE_X][SIZE_Y];
	private static int[][] tile_distances = new int[SIZE_X][SIZE_Y];

	private static Vec2 start = new Vec2();
	private static Vec2 end = new Vec2();

	private static Frame frame;

	private static Vec2 curr;

	public static void main(String[] args) {
		frame = new Frame();
		frame.setTitle("Maze");
		frame.setSize(VID_W + 200, VID_H + 22);
		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				frame.dispose();
				System.exit(0);
			}
		});
		frame.setVisible(true);
		OSGVideo.vid_init_system_font();
		OSGVideo.vid_init((VID_W + 200) << 10 | VID_H);
		run();
	}

	private static void run() {
		int lineNum = 0;
		try {
			FileOutputStream out = new FileOutputStream("read_data.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(Pathfinder.class.getResourceAsStream("/maze_input.csv")));
			String line;
			while ((line = in.readLine()) != null) {
				String[] data = line.split(",");
				if (lineNum != 0) {
					int i = 0;
					for (String s : data) {
						if (i != 0) {
							if (Integer.parseInt(s) == 1) {
								solid_tiles[i - 1][lineNum - 1] = false;
							} else {
								solid_tiles[i - 1][lineNum - 1] = true;
							}
						}
						i++;
					}
				}
				String outStr = line + " <-- i read this line!\n";
				out.write(outStr.getBytes());
				lineNum++;
			}
			in.close();
			out.close();
		} catch (Exception e) {
			System.err.println("At line: " + lineNum);
			e.printStackTrace();
		}

		// we will conduct two maze scans, one vertically and one
		// horizontally. We move along the edges of the maze looking for a
		// gap. The location of this gap will be the start point, if the
		// start point has not been found yet, else it will be the end
		// point.

		Vec2 possible = new Vec2();
		boolean start_found = false;

		// vertical scan
		for (int x = 0; x < SIZE_X; x += (SIZE_X - 1)) {
			int num_solid = 0;
			for (int y = 0; y < SIZE_Y; y++) {
				if (solid_tiles[x][y]) {
					num_solid++;
				} else {
					possible.set(x, y);
				}
			}
			if (num_solid == (SIZE_X - 1)) {
				if (!start_found) {
					start.set(possible);
					start_found = true;
				} else {
					end.set(possible);
				}
			}
		}

		// horizontal scan
		for (int y = 0; y < SIZE_Y; y += (SIZE_Y - 1)) {
			int num_solid = 0;
			for (int x = 0; x < SIZE_X; x++) {
				if (solid_tiles[x][y]) {
					num_solid++;
				} else {
					possible.set(x, y);
				}
			}
			if (num_solid == (SIZE_Y - 1)) {
				if (!start_found) {
					start.set(possible);
					start_found = true;
				} else {
					end.set(possible);
				}
			}
		}

		System.out.println("Start position determined to be: " + start);
		System.out.println("End position determined to be: " + end);

		curr = new Vec2(start);
		path_find(curr.x, curr.y);
	}

	private static void path_find(int good_x, int good_y) {
		String cur = "current pos: [" + curr.x + ", " + curr.y + "]";
		// base case
		if (end.eq(good_x, good_y)) {
			return;
		}
		double closestDist = 0xffffff;
		Vec2 potential = new Vec2();
		for (int x = -1; x <= 1; x++) {
			for (int y = -1; y <= 1; y++) {
				potential.set(curr.x + x, curr.y + y);
				if (potential.inBounds(0, 0, SIZE_X, SIZE_Y) && !is_bad_move(x, y)) {
					if (!solid_tiles[potential.x][potential.y]) {
						Vec2 dist = potential.sub(end);
						double tileDist = dist.mag();
						tileDist += tile_distances[potential.x][potential.y];
						if (tileDist < closestDist) {
							good_x = potential.x;
							good_y = potential.y;
							closestDist = tileDist;
						}
					}
				}
			}
		}

		curr.set(good_x, good_y);

		tile_distances[good_x][good_y] += (SIZE_X + SIZE_Y) >> 1;

		sleep(100);
		render(cur);

		path_find(curr.x, curr.y);
	}

	private static boolean is_bad_move(int x, int y) {
		if (ALLOW_DIAGONAL_MOVEMENT) {
			return false;
		}
		return Math.abs(x) == 1 && Math.abs(y) == 1;
	}

	private static void render(String cur) {
		OSGVideo.vid_reset();

		for (int x = 0; x < SIZE_X; x++) {
			for (int y = 0; y < SIZE_Y; y++) {
				if (solid_tiles[x][y]) {
					OSGVideo.vid_fill_rect(x * SCALE_X, y * SCALE_Y, SCALE_X, SCALE_Y, 0xbbbbbb);
				} else {
					if (tile_distances[x][y] == (SIZE_X + SIZE_Y) >> 1) {
						OSGVideo.vid_fill_rect(x * SCALE_X, y * SCALE_Y, SCALE_X, SCALE_Y, 0x007f00);
						OSGVideo.vid_draw_text(tile_distances[x][y] + "", x * SCALE_X, y * SCALE_Y + 4, 0xffffff, 1);
					} else {
						OSGVideo.vid_fill_rect(x * SCALE_X, y * SCALE_Y, SCALE_X, SCALE_Y, 0x777777);
						OSGVideo.vid_draw_text(tile_distances[x][y] + "", x * SCALE_X, y * SCALE_Y + 4, 0xffffff, 1);
					}

				}
			}
		}

		for (int i = 0; i <= SIZE_X; i++) {
			OSGVideo.vid_draw_vertical_line(i * SCALE_X, 0, SIZE_X * SCALE_Y, 0x999999);
		}

		for (int i = 0; i <= SIZE_Y; i++) {
			OSGVideo.vid_draw_horizontal_line(0, i * SCALE_Y, SIZE_Y * SCALE_X, 0x999999);
		}

		OSGVideo.vid_fill_rect(start.x * SCALE_X, start.y * SCALE_Y, SCALE_X, SCALE_Y, 0x00ff00);
		OSGVideo.vid_draw_rect(start.x * SCALE_X, start.y * SCALE_Y, SCALE_X, SCALE_Y, 0);

		OSGVideo.vid_fill_rect(end.x * SCALE_X, end.y * SCALE_Y, SCALE_X, SCALE_Y, 0xff0000);
		OSGVideo.vid_draw_rect(end.x * SCALE_X, end.y * SCALE_Y, SCALE_X, SCALE_Y, 0);

		OSGVideo.vid_fill_rect(curr.x * SCALE_X, curr.y * SCALE_Y, SCALE_X, SCALE_Y, 0xffff00);
		OSGVideo.vid_draw_rect(curr.x * SCALE_X, curr.y * SCALE_Y, SCALE_X, SCALE_Y, 0);

		OSGVideo.vid_draw_text(cur, VID_W + 16, 22, 0xffffff, 1);
		Graphics g = frame.getGraphics();
		if (g != null) {
			g.drawImage(OSGVideo.image, 0, 22, null);
		}
	}

	private static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}