package csun.c182L;

import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 * 2D software rasterization routines
 * with a hardcoded bitmap font table.
 * 
 * @author Milaud Miremadi
 *
 */
public class OSGVideo {

	public static final int VID_MODE_320x240 = (320 << 10) | 240;
	public static final int VID_MODE_480x272 = (480 << 10) | 272;
	public static final int VID_MODE_512x384 = (512 << 10) | 384;
	public static final int VID_MODE_640x400 = (640 << 10) | 400;
	public static final int VID_MODE_640x480 = (640 << 10) | 480;
	public static final int VID_MODE_800x600 = (800 << 10) | 600;
	public static final int VID_MODE_960x640 = (960 << 10) | 640;
	public static final int VID_MODE_960x720 = (960 << 10) | 720;

	static int width;
	static int height;
	static int size;

	static BufferedImage image;
	static int[] video;

	static long[] system_font;

	private OSGVideo() {

	}

	static void vid_init_system_font() {
		system_font = new long[0x80];
		system_font[vid_c2memidx('A')] = 0x182424243c242424l;
		system_font[vid_c2memidx('B')] = 0x382424383824243cl;
		system_font[vid_c2memidx('C')] = 0x3c2020202020203cl;
		system_font[vid_c2memidx('D')] = 0x3824242424242438l;
		system_font[vid_c2memidx('E')] = 0x3c20203c2020203cl;
		system_font[vid_c2memidx('F')] = 0x3c20203c20202020l;
		system_font[vid_c2memidx('G')] = 0x3c20203c2424243cl;
		system_font[vid_c2memidx('H')] = 0x2424243c24242424l;
		system_font[vid_c2memidx('I')] = 0x3c1818181818183cl;
		system_font[vid_c2memidx('J')] = 0x3c08080828282838l;
		system_font[vid_c2memidx('K')] = 0x2428283030282824l;
		system_font[vid_c2memidx('L')] = 0x202020202020203cl;
		system_font[vid_c2memidx('M')] = 0x4266665a5a5a5a5al;
		system_font[vid_c2memidx('N')] = 0x2434343c3c2c2c24l;
		system_font[vid_c2memidx('O')] = 0x3c2424242424243cl;
		system_font[vid_c2memidx('P')] = 0x3c24243c20202020l;
		system_font[vid_c2memidx('Q')] = 0x3c24242424302834l;
		system_font[vid_c2memidx('R')] = 0x3c24243c30282424l;
		system_font[vid_c2memidx('S')] = 0x3c20203c0404043cl;
		system_font[vid_c2memidx('T')] = 0x3c18181818181818l;
		system_font[vid_c2memidx('U')] = 0x242424242424243cl;
		system_font[vid_c2memidx('V')] = 0x2424242418181818l;
		system_font[vid_c2memidx('W')] = 0x4242425a5a5a6624l;
		system_font[vid_c2memidx('X')] = 0x4224241818242442l;
		system_font[vid_c2memidx('Y')] = 0x2424243c18181818l;
		system_font[vid_c2memidx('Z')] = 0x3c0408081010203cl;

		system_font[vid_c2memidx('0')] = 0x3c2c2c2c3434343cl;
		system_font[vid_c2memidx('1')] = 0x183818181818183cl;
		system_font[vid_c2memidx('2')] = 0x380404182020203cl;
		system_font[vid_c2memidx('3')] = 0x3804041804040438l;
		system_font[vid_c2memidx('4')] = 0x081828283c080808l;
		system_font[vid_c2memidx('5')] = 0x3c20203804040438l;
		system_font[vid_c2memidx('6')] = 0x1c20203824242418l;
		system_font[vid_c2memidx('7')] = 0x3c04040c08080808l;
		system_font[vid_c2memidx('8')] = 0x3c2424181824243cl;
		system_font[vid_c2memidx('9')] = 0x1c24241c04040404l;
		system_font[vid_c2memidx('/')] = 0x0404080810102020l;
		system_font[vid_c2memidx('%')] = 0x2424080810102424l;
		system_font[vid_c2memidx('.')] = 0x0000000000001818l;
		system_font[vid_c2memidx('-')] = 0x0000003c3c000000l;
		system_font[vid_c2memidx(':')] = 0x0018180000181800l;
		system_font[vid_c2memidx('+')] = 0x0018183c3c181800l;
		system_font[vid_c2memidx('!')] = 0x1818181818180018l;
		system_font[vid_c2memidx('?')] = 0x3804041818180018l;
		system_font[vid_c2memidx(',')] = 0x0000000000181808l;
		system_font[vid_c2memidx('[')] = 0x1810101010101018l;
		system_font[vid_c2memidx(']')] = 0x1808080808080818l;
		system_font[vid_c2memidx('@')] = 0x18243c34343c203cl;
		system_font[vid_c2memidx('_')] = 0x000000000000003cl;
		system_font[vid_c2memidx('(')] = 0x0810101010101008l;
		system_font[vid_c2memidx(')')] = 0x1008080808080810l;
		system_font[vid_c2memidx('=')] = 0x00003c00003c0000l;
		system_font[vid_c2memidx('<')] = 0x0408102020100804l;
		system_font[vid_c2memidx('>')] = 0x2010080404081020l;
	}

	static void vid_init(int mode) {
		OSGVideo.width = (mode >> 10) & 0x3ff;
		OSGVideo.height = mode & 0x3ff;
		OSGVideo.size = width * height;

		GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
		GraphicsDevice device = env.getDefaultScreenDevice();
		GraphicsConfiguration config = device.getDefaultConfiguration();

		image = config.createCompatibleImage(width, height);
		video = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	}

	static int vid_c2memidx(char c) {
		if (IS_ALPHA(c)) {
			return ((c & 0x5f) & 0x3f) - 1;
		}
		return c;
	}

	public static void vid_draw_char(char c, int x, int y, int color, int size) {
		if (x >= width || y >= height) {
			return;
		}
		int idx = vid_c2memidx(c);
		if (idx < 0) {
			return;
		}
		long b = system_font[idx];
		y *= width;
		int start = x;
		for (int i = 63; i >= 0; i--) {
			if (((b >> (i - 1)) & 1) != 0) {
				video[(x * size) + (y * size)] = color;
			}
			x++;
			if ((i & 7) == 0) {
				y += width;
				x = start;
			}
		}
	}

	public static void vid_draw_text(String s, int x, int y, int color, int size) {
		for (int i = s.length() - 1; i >= 0; i--) {
			vid_draw_char(s.charAt(i), x + (i << 3), y, color, size);
		}
	}

	public static void vid_reset() {
		for (int i = 0; i < size; i++) {
			video[i] = 0x000000;
		}
	}

	public static void vid_reset(int color) {
		for (int i = 0; i < size; i++) {
			video[i] = color;
		}
	}

	public static void vid_draw_rect(int x, int y, int w, int h, int color) {
		vid_draw_horizontal_line(x, y, w, color);
		vid_draw_horizontal_line(x, y + h - 1, w, color);
		vid_draw_vertical_line(x, y, h, color);
		vid_draw_vertical_line(x + w - 1, y, h, color);
	}

	public static void vid_fill_rect(int x, int y, int w, int h, int color) {
		if (x < 0) {
			w += x;
			x = 0;
		}

		if (y < 0) {
			h += y;
			y = 0;
		}

		if (x + w > width) {
			w = width - x;
		}

		if (y + h > height) {
			h = height - y;
		}

		int step = width - w;
		int pos = x + y * width;

		for (int i = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				video[pos++] = color;
			}
			pos += step;
		}
	}

	public static void vid_draw_horizontal_line(int x, int y, int w, int color) {
		if (y >= 0 && y < height) {
			if (x < 0) {
				w += x;
				x = 0;
			}

			if (x + w > width) {
				w = width - x;
			}

			int pos = x + y * width;

			for (int i = 0; i < w; i++) {
				video[pos++] = color;
			}
		}
	}

	public static void vid_draw_vertical_line(int x, int y, int h, int color) {
		if (x < 0 || x >= width) {
			return;
		}

		if (y < 0) {
			h += y;
			y = 0;
		}

		if (y + h > height) {
			h = height - y;
		}

		int pos = x + y * width;

		for (int i = 0; i < h; i++) {
			video[pos] = color;
			pos += width;
		}
	}

	public static void vid_draw_line(int x1, int y1, int x2, int y2, int color) {
		int dx = Math.abs(x2 - x1);
		int dy = Math.abs(y2 - y1);
		int sx = (x1 < x2) ? 1 : -1;
		int sy = (y1 < y2) ? 1 : -1;
		int err = dx - dy;

		if (x1 > 0 && x1 < width && y1 > 0 && y1 < height) {
			video[x1 + y1 * width] = color;
		}
		while ((x1 != x2) || (y1 != y2)) {
			int err2 = err << 1;
			if (err2 > -dy) {
				err -= dy;
				x1 += sx;
			}
			if (err2 < dx) {
				err += dx;
				y1 += sy;
			}
			if (x1 > 0 && x1 < width && y1 > 0 && y1 < height) {
				video[x1 + y1 * width] = color;
			}
		}
	}

	public static boolean IS_ALPHA(int c) {
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
	}

	public static boolean IS_ALPHA_CAPS(int c) {
		return c >= 'A' && c <= 'Z';
	}

	public static boolean IS_NUMERIC(int c) {
		return c >= '0' && c <= '9';
	}

	public static boolean IS_ALPHANUMERIC(int c) {
		return IS_ALPHA(c) || IS_NUMERIC(c);
	}

	public static final int PACK_RGB24(int r, int g, int b) {
		return (r << 16) | (g << 8) | b;
	}

}