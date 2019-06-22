import java.awt.Color;
import java.util.Scanner;

import edu.princeton.cs.algs4.Picture;
import edu.princeton.cs.algs4.PictureDump;
import edu.princeton.cs.algs4.StdOut;
import edu.princeton.cs.algs4.Stopwatch;

public class SeamCarver {
	private Picture picture;
	private double[][] energy;

	public SeamCarver(Picture picture) // create a seam carver object based on the given picture
	{
		if (picture == null)
			throw new IllegalArgumentException();
		this.picture = new Picture(picture);
		energy = new double[picture.width()][picture.height()];
		for (int col = 0; col < width(); col++)
			for (int row = 0; row < height(); row++)
				energy[col][row] = energy(col, row);
	}

	public Picture picture() // current picture
	{
		return picture;
	}

	public int width() // width of current picture
	{
		return energy.length;
	}

	public int height() // height of current picture
	{
		return energy[0].length;

	}

	public double energy(int x, int y) // energy of pixel at column x and row y
	{
		if (x < 0 || y < 0 || x >= width() || y >= height())
			throw new IllegalArgumentException("Out of Range");
		return findGradient(x, y);

	}

	private double findGradient(int x, int y) {
		if (x == 0 || y == 0 || y == height() - 1 || x == width() - 1) {
			return 1000;
		} else {
			return Math.sqrt(findGradientX(x, y) + findGradientY(x, y));
			// energy[x][y] = findGradientX(x, y) + findGradientY(x, y);
		}
	}

	private double findGradientX(int x, int y) {
		Color colorprevx = picture.get(x - 1, y);
		Color colorpostx = picture.get(x + 1, y);
		double dR = colorpostx.getRed() - colorprevx.getRed();
		double dG = colorpostx.getGreen() - colorprevx.getGreen();
		double dB = colorpostx.getBlue() - colorprevx.getBlue();
		// System.out.println("X: "+dR+"\t"+dG+ "\t" +dB+"\n");
		return Math.pow(dR, 2) + Math.pow(dG, 2) + Math.pow(dB, 2);
	}

	private double findGradientY(int x, int y) {
		Color colorprevy = picture.get(x, y - 1);
		Color colorposty = picture.get(x, y + 1);
		double dR = colorposty.getRed() - colorprevy.getRed();
		double dG = colorposty.getGreen() - colorprevy.getGreen();
		double dB = colorposty.getBlue() - colorprevy.getBlue();
		// System.out.println("Y: "+3dR+"\t"+dG+ "\t" +dB+"\n");
		return Math.pow(dR, 2) + Math.pow(dG, 2) + Math.pow(dB, 2);
	}

	public int[] findVerticalSeam() // sequence of indices for horizontal seam
	{
		int[] seam = new int[height()];
		int[][] edgeTo = new int[width()][height()];
		double[][] distTo = new double[width()][height()];
		/*
		 * if (energy[x][y] == 0) energy[x][y] = findGradient(x, y); return
		 * energy[x][y];
		 */
		initDistToEdgeTo(distTo, edgeTo);

		for (int y = 0; y < height() - 1; y++) {
			for (int x = 0; x < width(); x++) {
				// System.out.println(energy[x][y]);
				// if (energy[x][y] == 0) energy[x][y] = findGradient(x, y);

				if (x - 1 >= 0) {
					relaxEdge(x, y, x - 1, y + 1, distTo, edgeTo);
				}
				relaxEdge(x, y, x, y + 1, distTo, edgeTo);
				if (x + 1 < width()) {
					relaxEdge(x, y, x + 1, y + 1, distTo, edgeTo);
				}
			}
		}

		/*
		 * for (int y = 0; y < height(); y++) { for (int x = 0; x < width(); x++) { //
		 * System.out.println(distTo[x][y]+"\t"); System.out.println(edgeTo[x][y] +
		 * "\t"); } System.out.println(); }
		 * 
		 * /* for (int y = 0; y < height(); y++) { for (int x = 0; x < width(); x++) {
		 * System.out.println(energy[x][y]+"\t");} System.out.println();}
		 */

		int seamend = -1;
		double minDist = Double.POSITIVE_INFINITY;
		for (int x = 0; x < width(); x++) {
			if (distTo[x][height() - 1] < minDist) {
				minDist = distTo[x][height() - 1];
				seamend = x;
			}
		}
//		System.out.println(seamend);
		// System.out.println("\t" + seamend);
		seam = restoreSeam(edgeTo, seamend);
		return seam;
	}

	private void relaxEdge(int x0, int y0, int x1, int y1, double[][] distTo, int[][] edgeTo) {
		/*if (energy[x1][y1] == 0) {
			energy[x1][y1] = findGradient(x1, y1);
		}*/
		if (distTo[x1][y1] > distTo[x0][y0] + energy[x1][y1]) {
			distTo[x1][y1] = distTo[x0][y0] + energy[x1][y1];
			edgeTo[x1][y1] = x0;
			// System.out.println(x1+":"+edgeTo[x1]);
		}

	}

	private int[] restoreSeam(int[][] edgeTo, int seamend) {
		int[] seam = new int[height()];
		int y = height() - 1;
		for (int i = height() - 1; i >= 0; i--) {
			seam[i] = seamend;
			seamend = edgeTo[seamend][y];
			y--;
		}
		return seam;
	}

	private void initDistToEdgeTo(double[][] distTo, int[][] edgeTo) {
		for (int h = 0; h < height(); h++) {
			for (int w = 0; w < width(); w++) {
				if (h == 0)
					distTo[w][h] = 1000;
				else {
					distTo[w][h] = Double.POSITIVE_INFINITY;
				}
				edgeTo[w][h] = -1;
			}
		}

	}

	public int[] findHorizontalSeam() // sequence of indices for vertical seam
	{
		double[][] backupEnergy = energy;
		energy = transposeEnergy(energy);
		int[] seam = findVerticalSeam();
		energy = backupEnergy;
		;
		return seam;

	}

	private double[][] transposeEnergy(double[][] energy2) {
		double[][] newEnergy = new double[height()][width()];
		for (int x = 0; x < width(); x++) {
			for (int y = 0; y < height(); y++) {
				newEnergy[y][x] = energy2[x][y];
			}
		}
		return newEnergy;
	}

	private double[][] transposeEnergy() {
		return transposeEnergy(energy);
	}

	public void removeHorizontalSeam(int[] seam) { // remove horizontal seam from current picture
		if(seam==null)
			throw new IllegalArgumentException();
		if (height() <= 1) {
			throw new java.lang.IllegalArgumentException("height <= 1");
		}

		if (seam.length != width()) {
			throw new java.lang.IllegalArgumentException();
		}

		Picture updatedPicture = new Picture(width(), height() - 1);
		double[][] updatedPicEnergy = new double[width()][height() - 1];
		for (int col = 0; col < width(); col++) {
			// copy the upper part of the picture and picEnergy
			for (int row = 0; row < seam[col]; row++) {
				updatedPicture.set(col, row, picture.get(col, row));
				updatedPicEnergy[col][row] = energy[col][row];
			}
			// System.arraycopy(picEnergy[col], 0, updatedPicEnergy[col], 0, a[col]);

			// copy the bottom part of the picEnergy
			for (int row = seam[col] + 1; row < height(); row++) {
				updatedPicture.set(col, row - 1, picture.get(col, row));
				updatedPicEnergy[col][row - 1] = energy[col][row];
			}
			// System.arraycopy(picEnergy, a[col] + 1, updatedPicEnergy, a[col],
			// picEnergy[col].length - a[col]);
		}
		picture = updatedPicture;
		energy = updatedPicEnergy;
	}

	public void removeVerticalSeam(int[] seam) // remove vertical seam from current picture
	{if(seam==null)
		throw new IllegalArgumentException();

		if (width() <= 1) {
			throw new java.lang.IllegalArgumentException("width <= 1");
		}

		if (seam.length != height()) {
			throw new java.lang.IllegalArgumentException();
		}

		Picture updatedPicture = new Picture(width() - 1, height());
		double[][] updatedPicEnergy = new double[width() - 1][height()];
		for (int row = 0; row < height(); row++) {
			// copy the upper part of the picture and picEnergy
			for (int col = 0; col < seam[row]; col++) {
				updatedPicture.set(col, row, picture.get(col, row));
				updatedPicEnergy[col][row] = energy[col][row];
			}
			// copy the bottom part of the picEnergy
			for (int col = seam[row] + 1; col < width(); col++) {
				updatedPicture.set(col - 1, row, picture.get(col, row));
				updatedPicEnergy[col - 1][row] = energy[col][row];
			}
		}

		picture = updatedPicture;
		energy = updatedPicEnergy;
	}

	public static void main(String[] args) {

		Scanner scan = new Scanner(System.in);
		String pic = scan.nextLine();
		Picture picture = new Picture(pic);
		Picture inputImg = new Picture(pic);
		int removeColumns = scan.nextInt();
		int removeRows = scan.nextInt();

		StdOut.printf("image is %d columns by %d rows\n", inputImg.width(), inputImg.height());
		SeamCarver sc = new SeamCarver(inputImg);

		Stopwatch sw = new Stopwatch();

		for (int i = 0; i < removeRows; i++) {
			int[] horizontalSeam = sc.findHorizontalSeam();
			sc.removeHorizontalSeam(horizontalSeam);
		}

		for (int i = 0; i < removeColumns; i++) {
			int[] verticalSeam = sc.findVerticalSeam();
			sc.removeVerticalSeam(verticalSeam);
		}
		Picture outputImg = sc.picture();

		StdOut.printf("new image size is %d columns by %d rows\n", sc.width(), sc.height());

		StdOut.println("Resizing time: " + sw.elapsedTime() + " seconds.");
		inputImg.show();
		outputImg.show();

	}
}
