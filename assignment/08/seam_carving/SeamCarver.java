import edu.princeton.cs.algs4.Picture;
import java.awt.Color;

/**
 * @todo Don't save picture directly, extract it to Color array and work with them
 * @todo Use one table for V & H ways
 * @todo Fix if (last > -1) checking
 * @todo Try to update way table online (during remove(HV)Seam), try to archive O(H) and O(V) for find(HV)Seam respectevely
 * @todo Add invalid param checking
 */

public class SeamCarver {
    private Picture picture;
    
    private class PointOfDistance {
        private int from = -1;
        private double distance = 0;
        
        public PointOfDistance() {
            
        }
        
        public PointOfDistance(int from, double distance) {
            init(from, distance);
        }
        
        public void init(int fromNew, double distanceNew) {
            this.from = fromNew;
            this.distance = distanceNew;
        }
        
        public int from() {
            return from;
        }
        
        public double distance() {
            return distance;
        }
    }

    /**
     * create a seam carver object based on the given picture
     * @param picture
     */
    public SeamCarver(Picture picture) {
        if (picture == null) {
            throw new java.lang.IllegalArgumentException();
        }
        
        this.picture = new Picture(picture);
    }

    /**
     * current picture
     * @return
     */
    public Picture picture() {
        return picture;
    }

    /**
     * width of current picture
     * @return
     */
    public int width() {
        return picture.width();
    }

    /**
     * height of current picture
     * @return
     */
    public int height() {
        return picture.height();
    }

    /**
     * energy of pixel at column x and row y
     * @param x
     * @param y
     * @return
     */
    public double energy(int x, int y) {
        if (x < 0 || x >= picture.width() || y < 0 || y >= picture.height()) {
            throw new java.lang.IllegalArgumentException(Integer.toString(x) + " " + Integer.toString(y));
        }
        
        if (x == 0 || y == 0 || x == picture.width() - 1 || y == picture.height() - 1) {
            return 1000;
        }
        
        double answer = 0;
        Color colorPre = picture.get(x - 1, y);
        Color colorPost = picture.get(x + 1, y);
        int red = colorPre.getRed() - colorPost.getRed();
        int green = colorPre.getGreen() - colorPost.getGreen();
        int blue = colorPre.getBlue() - colorPost.getBlue();
        answer = red * red + green * green + blue * blue;
        
        colorPre = picture.get(x, y - 1);
        colorPost = picture.get(x, y + 1);
        red = colorPre.getRed() - colorPost.getRed();
        green = colorPre.getGreen() - colorPost.getGreen();
        blue = colorPre.getBlue() - colorPost.getBlue();
        answer += red * red + green * green + blue * blue;
        
        return Math.sqrt(answer);
    }

    /**
     * sequence of indices for horizontal seam
     * @return
     */
    public int[] findHorizontalSeam() {
        int widthMax = picture.width() - 1;
        int heightMax = picture.height() - 1;

        PointOfDistance [][] points = new PointOfDistance[picture.width()][picture.height()];
        for (int h = 0; h < picture.height(); ++h) {
            points[0][h] = new PointOfDistance();
            points[0][h].init(-1, 1000);
        }
        
        for (int w = 1; w < widthMax; ++w) {
            points[w][0] = new PointOfDistance(-1, Double.MAX_VALUE);
            points[w][heightMax] = new PointOfDistance(-1, Double.MAX_VALUE);
        }
        
        for (int w = 1; w < widthMax; ++w) {
            for (int h = 1; h < heightMax; ++h) {
                points[w][h] = new PointOfDistance();
                int min = h - 1;
                if (points[w - 1][h].distance() < points[w - 1][min].distance()) {
                    min = h;
                }
                
                if (points[w - 1][h + 1].distance() < points[w - 1][min].distance()) {
                    min = h + 1;
                }

                points[w][h].init(min, energy(w, h) + points[w - 1][min].distance());
            }
        }

        int[] horizontalSeam = new int[picture.width()];
        
        // find a winner :-)
        int last = widthMax - 1;
        int winner = 0;
        if (last != -1) {
            double minimalDistance = points[last][winner].distance();
            for (int i = 1; i < heightMax; ++i) {
                if (points[last][i].distance() < minimalDistance) {
                    minimalDistance = points[last][i].distance();
                    winner = i;
                }
            }
        }

        horizontalSeam[last + 1] = winner;
        while (winner > -1 && last > -1) {
            horizontalSeam[last] = winner;
            winner = points[last--][winner].from();
        }

        return horizontalSeam;
    }

    /**
     * sequence of indices for vertical seam
     * @return
     */
    public int[] findVerticalSeam() {
        int widthMax = picture.width() - 1;
        int heightMax = picture.height() - 1;

        PointOfDistance [][] points = new PointOfDistance[picture.width()][picture.height()];
        for (int w = 0; w < picture.width(); ++w) {
            points[w][0] = new PointOfDistance();
            points[w][0].init(-1, 1000);
        }
        
        for (int h = 1; h < heightMax; ++h) {
            points[0][h] = new PointOfDistance(-1, Double.MAX_VALUE);
            points[widthMax][h] = new PointOfDistance(-1, Double.MAX_VALUE);
        }
        
        for (int h = 1; h < heightMax; ++h) {
            for (int w = 1; w < widthMax; ++w) {
                points[w][h] = new PointOfDistance();
                int min = w - 1;
                if (points[w][h - 1].distance() < points[min][h - 1].distance()) {
                    min = w;
                }
                
                if (points[w + 1][h - 1].distance() < points[min][h - 1].distance()) {
                    min = w + 1;
                }

                points[w][h].init(min, energy(w, h) + points[min][h - 1].distance());
            }
        }
        
        int[] verticalSeam = new int[picture.height()];
        
        // find a winner :-)
        int last = heightMax - 1;
        int winner = 0;
        if (last != -1) {
            double minimalDistance = points[winner][last].distance();
            for (int i = 1; i < widthMax; ++i) {
                if (points[i][last].distance() < minimalDistance) {
                    minimalDistance = points[i][last].distance();
                    winner = i;
                }
            }
        }
        
        verticalSeam[last + 1] = winner;
        while (winner > -1 && last > -1) {
            verticalSeam[last] = winner;
            winner = points[winner][last--].from();
        }

        return verticalSeam;
    }

    /**
     * remove horizontal seam from current picture
     * @param seam
     */
    public void removeHorizontalSeam(int[] seam) {
        if (seam == null || picture.height() <= 1) {
            throw new java.lang.IllegalArgumentException();
        }

        Picture newPicture = new Picture(picture.width(), picture.height() - 1);
        for (int w = 0; w < picture.width(); ++w) {
            for (int h = 0, hNew = 0; h < picture.height(); ++h) {
                if (h != seam[w]) {
                    newPicture.set(w, hNew++, picture.get(w, h));
                }
            }
        }
        
        picture = newPicture;
    }
    
    /**
     * remove vertical seam from current picture
     * @param seam
     */
    public void removeVerticalSeam(int[] seam) {
        if (seam == null || picture.width() <= 1) {
            throw new java.lang.IllegalArgumentException();
        }
        
        Picture newPicture = new Picture(picture.width() - 1, picture.height());
        for (int h = 0; h < picture.height(); ++h) {
            for (int w = 0, wNew = 0; w < picture.width(); ++w) {
                if (w != seam[h]) {
                    newPicture.set(wNew++, h, picture.get(w, h));
                }
            }
        }
        
        picture = newPicture;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        Picture picture = new Picture(args[0]);
        System.out.printf("image is %d pixels wide by %d pixels high.\n", picture.width(), picture.height());
        
        SeamCarver sc = new SeamCarver(picture);
        
        // energy
        for (int row = 0; row < sc.height(); row++) {
            for (int col = 0; col < sc.width(); col++)
                System.out.printf("%9.0f ", sc.energy(col, row));
            System.out.println();
        }
        
        int[] vSeam = sc.findVerticalSeam();
        System.out.print("Vertical seam: ");
        for (int i = 0; i < vSeam.length; ++i) {
            System.out.printf("%d (%f) ", vSeam[i], sc.energy(vSeam[i], i));
        }
        System.out.println("");
        
        int[] hSeam = sc.findHorizontalSeam();
        System.out.print("Hosizontal seam: ");
        for (int i = 0; i < hSeam.length; ++i) {
            System.out.printf("%d (%f) ", hSeam[i], sc.energy(i, hSeam[i]));
        }
        System.out.println("");

	sc.removeVerticalSeam(vSeam);
	sc.removeHorizontalSeam(hSeam);
    }
}
