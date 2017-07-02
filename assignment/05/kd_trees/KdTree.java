import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdDraw;

import java.util.LinkedList;

public class KdTree {
    private class Node {
        private int depth;

        private Node left, right;

        private Point2D point;
        
        public Node(Point2D p, int depth) {
            this.depth = depth;
            point = p;
            left = null;
            right = null;
        }
        
        public boolean isVertical() {
            return depth % 2 == 0;
        }
        
        public boolean isHorizontal() {
            return !isVertical();
        }
        
        public void draw(double xMin, double yMin, double xMax, double yMax) {
            StdDraw.setPenColor(StdDraw.BLACK);
            StdDraw.setPenRadius(0.02);
            point.draw();
            StdDraw.setPenRadius();

            if (isVertical()) {
                StdDraw.setPenColor(StdDraw.RED);
                new Point2D(point.x(), yMin).drawTo(new Point2D(point.x(), yMax));
            }
            else {
                StdDraw.setPenColor(StdDraw.BLUE);
                new Point2D(xMin, point.y()).drawTo(new Point2D(xMax, point.y()));
            }
        }
    }
    
    private Node root = null;
    
    private int size = 0;
    
    /**
     * construct an empty set of points
     */
    public KdTree() {
        
    }
    
    /**
     * is the set empty?
     * @return
     */
    public boolean isEmpty() {
        return size == 0;
    }
    
    /**
     * number of points in the set
     * @return
     */
    public int size() {
        return size;
    }
    
    /**
     * add the point to the set (if it is not already in the set)
     * @param p
     */
    public void insert(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        
        int depth = 0;
        
        if (isEmpty()) {
            root = new Node(p, depth);
            ++size;
            return;
        }
        
        Node n = root;

        while (!n.point.equals(p)) {
            if (((n.isVertical()) && (p.x() < n.point.x())) || ((n.isHorizontal()) && (p.y() < n.point.y()))) {
                if (n.left == null) {
                    n.left = new Node(p, depth + 1);
                    ++size;
                }

                n = n.left;
            }
            else {
                if (n.right == null) {
                    n.right = new Node(p, depth + 1);
                    ++size;
                }
                
                n = n.right;
            }
            
            ++depth;
        }
        
        /* don't insert a duplicate point */
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
        
        Node n = root;
        
        while (n != null && !n.point.equals(p)) {
            if (((n.isVertical()) && (p.x() < n.point.x())) || ((n.isHorizontal()) && (p.y() < n.point.y()))) {
                n = n.left;
            }
            else {
                n = n.right;
            }
        }
        
        return n != null;
    }
    
    private void drawImpl(Node n, double xMin, double yMin, double xMax, double yMax) {
        if (n == null) {
            return;
        }
        
        n.draw(xMin, yMin, xMax, yMax);
        
        if (n.isVertical()) {
            drawImpl(n.left, xMin, yMin, n.point.x(), yMax);
            drawImpl(n.right, n.point.x(), yMin, xMax, yMax);
        }
        else {
            drawImpl(n.left, xMin, yMin, xMax, n.point.y());
            drawImpl(n.right, xMin, n.point.y(), xMax, yMax);
        }
    }
    
    /**
     * draw all points to standard draw
     */
    public void draw() {
        drawImpl(root, 0, 0, 1, 1);
    }
    
    private void rangeImpl(RectHV rect, Node n, RectHV nRect, LinkedList<Point2D> list) {
        if (n == null) {
            return;
        }
        
        if (rect.contains(n.point)) {
            list.add(n.point);
        }
        
        if (!rect.intersects(nRect)) {
            return;
        }
        
        if (n.isVertical()) {
            rangeImpl(rect, n.left, new RectHV(nRect.xmin(), nRect.ymin(), n.point.x(), nRect.ymax()), list);
            rangeImpl(rect, n.right, new RectHV(n.point.x(), nRect.ymin(), nRect.xmax(), nRect.ymax()), list);
        }
        else {
            rangeImpl(rect, n.left, new RectHV(nRect.xmin(), nRect.ymin(), nRect.xmax(), n.point.y()), list);
            rangeImpl(rect, n.right, new RectHV(nRect.xmin(), n.point.y(), nRect.xmax(), nRect.ymax()), list);
        }
    }
    
    /**
     * all points that are inside the rectangle
     * @param rect
     * @return
     */
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new java.lang.IllegalArgumentException();
        }
        
        LinkedList<Point2D> list = new LinkedList<Point2D>();
        
        rangeImpl(rect, root, new RectHV(0, 0, 1, 1), list);
        
        return list;
    }
    
    private Node nearestImpl(Node n, RectHV rect, Node nearestPoint, Point2D p) {
        double minDistance = nearestPoint.point.distanceTo(p);
        
        if (n == null || n.point.distanceTo(p) > minDistance) {
            return nearestPoint;
        }
        
        nearestPoint = n;
        
        if (n.isVertical()) {
            RectHV left = new RectHV(rect.xmin(), rect.ymin(), n.point.x(), rect.ymax());
            RectHV right = new RectHV(n.point.x(), rect.ymin(), rect.xmax(), rect.ymax());
            if (p.x() < n.point.x()) {
                nearestPoint = nearestImpl(n.left, left, nearestPoint, p);
                minDistance = nearestPoint.point.distanceTo(p);
                if (right.distanceTo(p) < minDistance) {
                    nearestPoint = nearestImpl(n.right, right, nearestPoint, p);
                }
            }
            else {
                nearestPoint = nearestImpl(n.right, right, nearestPoint, p);
                minDistance = nearestPoint.point.distanceTo(p);
                if (right.distanceTo(p) < minDistance) {
                    nearestPoint = nearestImpl(n.left, left, nearestPoint, p);
                }
            }
        }
        else {
            RectHV up = new RectHV(rect.xmin(), rect.ymin(), rect.xmax(), n.point.y());
            RectHV down = new RectHV(rect.xmin(), n.point.y(), rect.xmax(), rect.ymax());
            if (p.y() < n.point.y()) {
                nearestPoint = nearestImpl(n.right, down, nearestPoint, p);
                minDistance = nearestPoint.point.distanceTo(p);
                if (down.distanceTo(p) < minDistance) {
                    nearestPoint = nearestImpl(n.left, up, nearestPoint, p);
                }
            }
            else {
                nearestPoint = nearestImpl(n.left, up, nearestPoint, p);
                minDistance = nearestPoint.point.distanceTo(p);
                if (down.distanceTo(p) < minDistance) {
                    nearestPoint = nearestImpl(n.right, down, nearestPoint, p);
                }
            }
        }
        
        return nearestPoint;
    }
    
    /**
     * a nearest neighbor in the set to point p; null if the set is empty
     * @param p
     * @return
     */
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new java.lang.IllegalArgumentException();
        }
        
        if (root == null) {
            return null;
        }
        
        return nearestImpl(root, new RectHV(0, 0, 1, 1), root, p).point;
    }

    /**
     * unit testing of the methods (optional)
     * @param args
     */
    public static void main(String[] args) {
        KdTree kd = new KdTree();
        
        kd.insert(new Point2D(0.7, 0.2));
        kd.insert(new Point2D(0.5, 0.4));
        kd.insert(new Point2D(0.2, 0.3));
        kd.insert(new Point2D(0.4, 0.7));
        kd.insert(new Point2D(0.9, 0.6));
        
        System.out.println(kd.contains(new Point2D(0.5, 0.4)));
        System.out.println(kd.contains(new Point2D(0.4, 0.4)));
        System.out.println(kd.contains(new Point2D(0.4, 0.7)));
        System.out.println(kd.contains(new Point2D(0.4, 0.8)));
        
        kd.draw();
        
        System.out.println("range test 1");
        for (Point2D point : kd.range(new RectHV(0, 0, 1, 1))) {
            System.out.println(point.toString());
        }
        
        System.out.println("range test 2");
        for (Point2D point : kd.range(new RectHV(0.1, 0.2, 0.6, 0.5))) {
            System.out.println(point.toString());
        }
        
        Point2D p = new Point2D(0.3, 0.55);
        StdDraw.setPenRadius(0.02);
        p.draw();
        StdDraw.setPenRadius();
        System.out.printf("Nearest point to %s is %s\n", p.toString(), kd.nearest(p).toString());
    }
}