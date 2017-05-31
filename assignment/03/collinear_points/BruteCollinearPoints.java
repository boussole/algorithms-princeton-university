import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;

public class BruteCollinearPoints {
    private int numberOfSegments = 0;

    private LineSegment[] lineSegments = new LineSegment[1];

    /**
     * finds all line segments containing 4 points
     * @param points
     */
    public BruteCollinearPoints(Point[] points) {
        checkNullError(points);
        
        for (int p = 0; p < points.length; ++p) {
            for (int q = p + 1; q < points.length; ++q) {
                if (points[q].compareTo(points[p]) == 0) {
                    throw new java.lang.IllegalArgumentException();
                }
                
                double pToQ = points[p].slopeTo(points[q]);
                
                for (int r = q + 1; r < points.length; ++r) {
                    if ((points[r].compareTo(points[p]) == 0) ||
                        (points[r].compareTo(points[q]) == 0)) {
                        throw new java.lang.IllegalArgumentException();
                    }
                    
                    double qToR = points[q].slopeTo(points[r]);
                    if (pToQ != qToR) {
                        continue;
                    }
                    
                    for (int s = r + 1; s < points.length; ++s) {
                        if ((points[s].compareTo(points[p]) == 0) ||
                            (points[s].compareTo(points[q]) == 0) ||
                            (points[s].compareTo(points[r]) == 0)) {
                            throw new java.lang.IllegalArgumentException();
                        }
                        
                        double rToS = points[r].slopeTo(points[s]);
                        if (qToR != rToS) {
                            continue;
                        }
                        
                        Point[] coll = { points[q], points[r], points[s] };
                        Point min = points[p], max = points[p];
                        for (int i = 0; i < coll.length; ++i) {
                            if (coll[i].compareTo(min) < 0) {
                                min = coll[i];
                            }
                            else if (coll[i].compareTo(max) > 0) {
                                max = coll[i];
                            }
                        }
                        
                        if (isLineSegmentExists(min, max)) {
                            continue;
                        }
                        
                        if (lineSegments.length == numberOfSegments) {
                            resizeSegmentsArray();
                        }
                        
                        lineSegments[numberOfSegments++] = new LineSegment(min, max);
                    }
                }
            }
        }
        
        lineSegments = Arrays.copyOfRange(lineSegments, 0, numberOfSegments);
    }
    
    private void checkNullError(Point[] points) {
        if (points == null) {
            throw new java.lang.NullPointerException();
        }
        
        for (int i = 0; i < points.length; ++i) {
            if (points[i] == null) {
                throw new java.lang.NullPointerException();
            }
        }
    }
    
    /**
     * increase length
     */
    private void resizeSegmentsArray() {
        int newSize = lineSegments.length * 2;
        LineSegment[] newArray = new LineSegment[newSize];
        
        for (int i = 0; i < lineSegments.length; ++i) {
            newArray[i] = lineSegments[i];
        }
        
        lineSegments = newArray;
    }
    
    /**
     * This is slowly, and use a hack with a string
     * @param min
     * @param max
     * @return
     */
    private boolean isLineSegmentExists(Point min, Point max) {
        String key = min.toString() + " -> " + max.toString();
        for (int i = 0; i < numberOfSegments; ++i) {
            if (lineSegments[i].toString().equals(key)) {
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * the number of line segments
     * @return number of line segments
     */
    public int numberOfSegments() {
        return numberOfSegments;
    }
    
    /**
     * the line segments
     * @return segments
     */
    public LineSegment[] segments() {
        LineSegment[] copy = new LineSegment[numberOfSegments];
        System.arraycopy(lineSegments, 0, copy, 0, numberOfSegments);
        return copy;
    }
    
    /**
     * @param args
     */
    public static void main(String[] args) {
        // read the n points from a file
        In in = new In(args[0]);
        int n = in.readInt();
        Point[] points = new Point[n];
        for (int i = 0; i < n; i++) {
            int x = in.readInt();
            int y = in.readInt();
            points[i] = new Point(x, y);
        }

        // draw the points
        StdDraw.enableDoubleBuffering();
        StdDraw.setXscale(0, 32768);
        StdDraw.setYscale(0, 32768);
        for (Point p : points) {
            p.draw();
        }
        StdDraw.show();

        // print and draw the line segments
        BruteCollinearPoints collinear = new BruteCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();
    }

}
