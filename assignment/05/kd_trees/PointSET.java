import edu.princeton.cs.algs4.SET;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import java.util.LinkedList;

public class PointSET {
    private SET<Point2D> points;

    /**
     * construct an empty set of points
     */
    public PointSET() {
        points = new SET<Point2D>();
    }
    
    /**
     * @return is the set empty?
     */
    public boolean isEmpty() {
        return points.isEmpty();
    }
    
    /**
     * @return number of points in the set
     */
    public int size() {
        return points.size();
    }
    
    /**
     * add the point to the set (if it is not already in the set)
     * @param p
     */
    public void insert(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        
        if (!points.contains(p)) {
            points.add(p);
        }
    }
    
    
    /**
     * does the set contain point p?
     * @param p
     * @return
     */
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        
        return points.contains(p);
    }
    
    /**
     * draw all points to standard draw
     */
    public void draw() {
        for (Point2D p : points) {
            p.draw();
        }
    }
    
    /**
     * all points that are inside the rectangle
     * speed O(N)
     * 
     * @param rect
     * @return
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new java.lang.IllegalArgumentException();
        }
        
        LinkedList<Point2D> list = new LinkedList<Point2D>();

        for (Point2D p : points) {
            if (rect.contains(p)) {
                list.add(p);
            }
        }
        
        return list;
    }
    
    /**
     * a nearest neighbor in the set to point p; null if the set is empty
     * speed O(N)
     * 
     * @param p
     * @return
     */
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        
        double minDistance = Double.MAX_VALUE;
        Point2D nearestPoint = null;
        
        for (Point2D point : points) {
            double newDistance = point.distanceTo(p);
            if (newDistance < minDistance) {
                nearestPoint = point;
                minDistance = newDistance;
            }
        }
        
        return nearestPoint;
    }

    /**
     * unit testing of the methods (optional)
     * @param args
     */
    public static void main(String[] args) {
       
    }
}