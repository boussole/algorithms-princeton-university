import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdDraw;
import edu.princeton.cs.algs4.StdOut;
import java.util.Arrays;

public class FastCollinearPoints {
    private int numberOfSegments = 0;

    private LineSegment[] lineSegments = new LineSegment[1];
    
    /**
     * finds all line segments containing 4 or more points
     * @param points
     */
    public FastCollinearPoints(Point[] points) {
        if (points == null) {
            throw new java.lang.NullPointerException();
        }
        
        for (int i = 0; i < points.length; ++i) {
            if (points[i] == null) {
                throw new java.lang.NullPointerException();
            }
        }
        
        Point[] sortedPoints = points.clone();
        Arrays.sort(sortedPoints);
        
        checkDuplicate(sortedPoints);

        for (int i = 0; i < sortedPoints.length; ++i) {
            Point[] pointsBySlope = sortedPoints.clone();
            
            Arrays.sort(pointsBySlope, points[i].slopeOrder());
            
            int current = 1;
            while (current < pointsBySlope.length) {
                Point min = pointsBySlope[current];
                Point max = pointsBySlope[current];
                double currentSlope = pointsBySlope[0].slopeTo(pointsBySlope[current]);
                int numberIdenticalSlopes = 1;

                while (++current < pointsBySlope.length) {
                    double newSlope = pointsBySlope[0].slopeTo(pointsBySlope[current]);
                    if (newSlope != currentSlope) {
                        break;
                    }
                    
                    ++numberIdenticalSlopes;

                    if (pointsBySlope[current].compareTo(min) < 0) {
                        min = pointsBySlope[current];
                    }
                    else if (pointsBySlope[current].compareTo(max) > 0) {
                        max = pointsBySlope[current];
                    }
                }

                if (numberIdenticalSlopes < 3) {
                    continue;
                }
                
                if (lineSegments.length == numberOfSegments) {
                    resizeSegmentsArray();
                }
                
                if (pointsBySlope[0].compareTo(min) < 0) {
                    min = pointsBySlope[0];
                }
                else if (pointsBySlope[0].compareTo(max) > 0) {
                    max = pointsBySlope[0];
                }
                
                if (isLineSegmentExists(min, max)) {
                    continue;
                }
                
                lineSegments[numberOfSegments++] = new LineSegment(min, max);
            }
        }
        
        lineSegments = Arrays.copyOfRange(lineSegments, 0, numberOfSegments);
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
    
    private void checkDuplicate(Point[] points) {
        for (int i = 0; i < points.length - 1; i++) {
            if (points[i].compareTo(points[i + 1]) == 0) {
                throw new IllegalArgumentException("Duplicate(s) found.");
            }
        }
    }
    
    /**
     * the number of line segments
     * @return
     */
    public int numberOfSegments() {
        return numberOfSegments;
    }
    
    /**
     * the line segments
     * @return
     */
    public LineSegment[] segments() {
        LineSegment[] copy = new LineSegment[numberOfSegments];
        System.arraycopy(lineSegments, 0, copy, 0, numberOfSegments);
        return copy;
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
        FastCollinearPoints collinear = new FastCollinearPoints(points);
        for (LineSegment segment : collinear.segments()) {
            StdOut.println(segment);
            segment.draw();
        }
        StdDraw.show();

    }

}
