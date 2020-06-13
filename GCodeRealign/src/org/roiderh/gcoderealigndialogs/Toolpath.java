/*
 * Copyright (C) 2017 by Herbert Roider <herbert@roider.at>
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.roiderh.gcoderealigndialogs;

import javafx.scene.image.Image;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;
import java.util.Locale;
import javafx.event.EventHandler;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import math.geom2d.Point2D;
import math.geom2d.circulinear.CirculinearElement2D;
import math.geom2d.conic.CircleArc2D;
import math.geom2d.line.Line2D;
import math.geom2d.line.LineSegment2D;
import org.roiderh.gcodeviewer.contourelement;
import org.roiderh.gcodeviewer.point;

/**
 * widget contains the toolpath
 *
 * @author Herbert Roider <herbert@roider.at>
 */
public class Toolpath extends AnchorPane {

    private double pressedX, pressedY;

    private Text text = null;
    //private Circle endpoint = null;
    public double fact = 1.0;
    //public double prev_fact = 1.0;
    public double x_trans = 0.0;
    public double y_trans = 0.0;
    private double middle_x = 0.0;
    private double middle_y = 0.0;
    private boolean recalcTransScale = true;
    /**
     * The contour elements to display
     */
    public LinkedList<org.roiderh.gcodeviewer.contourelement> c_elements = null;
    /**
     * holds only the Path elements
     */
    public LinkedList<ToolpathElement> shapes = new LinkedList<>();

    public Toolpath() {

        //setStyle("-fx-border-color: blue;");
    }

    /**
     * calculate the translation and the scale to move and zoom the contour to
     * the center of the screen
     *
     * @param elements
     */
    public void calcTransScale(LinkedList<org.roiderh.gcodeviewer.contourelement> elements) {
        LinkedList<CirculinearElement2D> c_el = this.cleanup_contour(elements);
        math.geom2d.Box2D bb = this.getBoundingBox(c_el);

        for (int i = 0; i < c_el.size(); i++) {
            CirculinearElement2D el = c_el.get(i);
            math.geom2d.Box2D bb1 = el.boundingBox();
            if (i == 0) {
                bb = bb1;

            } else {
                bb = bb.union(bb1);
            }

        }
        if (bb == null) {
            return;
        }
        double max_x = bb.getMaxX();
        double min_x = bb.getMinX();
        double max_y = bb.getMaxY();
        double min_y = bb.getMinY();

        middle_x = (max_x + min_x) / 2.0;
        middle_y = (max_y + min_y) / 2.0;
        double width = bb.getWidth();
        double height = bb.getHeight();

        x_trans = (double) this.getWidth() / 2.0;
        y_trans = (double) this.getHeight() / 2.0;

        double x_fact = (double) this.getWidth() * 0.95 / width;
        double y_fact = (double) this.getHeight() * 0.95 / height;
        fact = Math.min(x_fact, y_fact);

    }

    public math.geom2d.Box2D getBoundingBox(LinkedList<CirculinearElement2D> c_el) {

        c_el.add(new Line2D(c_el.getLast().lastPoint(), c_el.getFirst().firstPoint()));
        math.geom2d.Box2D bb = null;

        for (int i = 0; i < c_el.size(); i++) {
            CirculinearElement2D el = c_el.get(i);
            math.geom2d.Box2D bb1 = el.boundingBox();
            if (bb1 == null) {
                return null;
            }
            if (i == 0) {
                bb = bb1;

            } else {
                bb = bb.union(bb1);
            }

        }
        return bb;

    }

    public Point2D translateScalePoint(Point2D p) {
        Point2D p2 = new Point2D(p.getX(), p.getY());
        p2 = p2.translate(-this.middle_x, -this.middle_y);
        p2 = p2.scale(fact, -fact);
        p2 = p2.translate(this.x_trans, this.y_trans);
        return p2;

    }

    /**
     * transform and cleanup the contour, remove empty and zero length elements
     *
     * @param contour
     * @return
     */
    private LinkedList<CirculinearElement2D> cleanup_contour(LinkedList<contourelement> contour) {
        LinkedList<CirculinearElement2D> elements = new LinkedList<>();
        for (contourelement current_ce : contour) {

            if (current_ce.curve == null) {
                continue;
            }
            if (current_ce.curve.length() == 0) {
                //continue;
            }
            elements.add(current_ce.curve);

            if (current_ce.transition_curve != null) {
                elements.add(current_ce.transition_curve);
            }
        }
        return elements;

    }

    /**
     * draws the contour
     */
    private void draw() {
        shapes.clear();
        this.getChildren().clear();
        for (contourelement current_ce : c_elements) {

            /*
             Add the current element to the contour as multiple lines.
             */
            ToolpathElement tpe = new ToolpathElement();
            tpe.path = new Path();

            if (current_ce.curve instanceof math.geom2d.conic.CircleArc2D) {
                CircleArc2D geo = (CircleArc2D) current_ce.curve;

                geo.supportingCircle().center().getX();

                MoveTo mt = new MoveTo();
                Point2D p1 = geo.asPolyline(1).firstPoint();
                Point2D p2 = geo.asPolyline(1).lastPoint();
                double radius = geo.supportingCircle().radius();

                p1 = this.translateScalePoint(p1);
                p2 = this.translateScalePoint(p2);

                radius *= fact;

                mt.setX(p1.getX());
                mt.setY(p1.getY());

                ArcTo at = new ArcTo();
                at.setLargeArcFlag(false);
                if (current_ce.ccw) {
                    at.setSweepFlag(false);
                } else {
                    at.setSweepFlag(true);
                }

                at.setRadiusX(radius);
                at.setRadiusY(radius);
                at.setX(p2.getX());
                at.setY(p2.getY());
                tpe.path.getElements().add(mt);
                tpe.path.getElements().add(at);

            } else {
                LineSegment2D geo = (LineSegment2D) current_ce.curve;
                MoveTo mt = new MoveTo();

                Point2D p1 = geo.firstPoint();
                Point2D p2 = geo.lastPoint();
                p1 = this.translateScalePoint(p1);
                p2 = this.translateScalePoint(p2);

                mt.setX(p1.getX());
                mt.setY(p1.getY());

                LineTo lt = new LineTo();
                lt.setX(p2.getX());
                lt.setY(p2.getY());
                tpe.path.getElements().add(mt);
                tpe.path.getElements().add(lt);

            }
            Point2D vertex = new Point2D(current_ce.points.getLast().x, current_ce.points.getLast().y);
            vertex = this.translateScalePoint(vertex);
            tpe.endpoint = new Circle(vertex.getX(), vertex.getY(), 4);

            /*
            draw icons and helplines for degree of freedom for nodes
             */
            if (current_ce.x_free && current_ce.y_free == false) {
                ImageView image = new ImageView(new Image(Toolpath.class.getResourceAsStream("fix_x.png")));
                image.relocate(vertex.getX() - 5, vertex.getY() - 5);
                image.setRotate(90);
                getChildren().add(image);
            } else if (current_ce.y_free && current_ce.x_free == false) {
                ImageView image = new ImageView(new Image(Toolpath.class.getResourceAsStream("fix_x.png")));
                image.relocate(vertex.getX() - 5, vertex.getY() - 5);
                getChildren().add(image);
            } else if (current_ce.y_free == false && current_ce.x_free == false) {
                ImageView image = new ImageView(new Image(Toolpath.class.getResourceAsStream("fix_xy.png")));
                image.relocate(vertex.getX() - 5, vertex.getY() - 5);
                getChildren().add(image);
            }
            if (current_ce.tangent) {
                //Line2D tangent = new Line2D(new Point2D(current_ce.points.getLast().x, current_ce.points.getLast().y), new Point2D(current_ce.points.getLast().x + 20.0, current_ce.points.getLast().y ));
                ImageView image = new ImageView(new Image(Toolpath.class.getResourceAsStream("tangent.png")));
                image.relocate(vertex.getX() - 40, vertex.getY() - 1);
                double angle = 0.0;
                if (current_ce.shape == contourelement.Shape.LINE) {
                    angle =  current_ce.angle * 180.0 / Math.PI;
                } else {
                    angle = current_ce.endangle * 180.0 / Math.PI;
                }
                image.setRotate(-angle);
                getChildren().add(image);
            }

            if (current_ce.shape == contourelement.Shape.LINE) {
                Point2D midpoint = new Point2D((current_ce.points.getLast().x + current_ce.points.getFirst().x) / 2.0, (current_ce.points.getLast().y + current_ce.points.getFirst().y) / 2.0);
                midpoint = this.translateScalePoint(midpoint);
                LineSegment2D geo = (LineSegment2D) current_ce.curve;
                double angle = geo.direction().angle();
                if (current_ce.angle_free == false) {
                    ImageView image = new ImageView(new Image(Toolpath.class.getResourceAsStream("fix_angle.png")));
                    image.relocate(midpoint.getX() - 5, midpoint.getY() - 5);
                    image.setRotate(45 - angle * 180.0 / Math.PI);
                    getChildren().add(image);
                }
            } else {
                // circle:
                // Draw the help lines:
                CircleArc2D geo = (CircleArc2D) current_ce.curve;
                Point2D midpoint = geo.supportingCircle().center();
                Point2D p1 = geo.firstPoint();
                Point2D p2 = geo.lastPoint();
                midpoint = this.translateScalePoint(midpoint);
                p1 = this.translateScalePoint(p1);
                p2 = this.translateScalePoint(p2);
                String style = "-fx-stroke-dash-array: 1 3 ; -fx-stroke: blue; ";
                if (current_ce.radius_free == false) {
                    style = "-fx-stroke: blue;";
                }
                Line2D l1 = new Line2D(p1, midpoint);
                Line2D l2 = new Line2D(midpoint, p2);
                Line line1 = new Line();
                Line line2 = new Line();
                line1 = this.getLineFromGeom(line1, l1, style);
                line2 = this.getLineFromGeom(line2, l2, style);
                getChildren().add(line1);
                getChildren().add(line2);
                // draw icons for degree of freedom
                if (current_ce.startangle_free == false) {
                    vertex = new Point2D((p1.getX() + midpoint.getX()) / 2.0, (p1.getY() + midpoint.getY()) / 2.0);
                    ImageView image = new ImageView(new Image(Toolpath.class.getResourceAsStream("fix_angle.png")));
                    image.relocate(vertex.getX() - 5, vertex.getY() - 5);
                    double angle = l1.direction().angle() * 180.0 / Math.PI;
                    image.setRotate(angle + 45.0);
                    getChildren().add(image);
                }
                if (current_ce.endangle_free == false) {
                    vertex = new Point2D((p2.getX() + midpoint.getX()) / 2.0, (p2.getY() + midpoint.getY()) / 2.0);
                    ImageView image = new ImageView(new Image(Toolpath.class.getResourceAsStream("fix_angle.png")));
                    image.relocate(vertex.getX() - 5, vertex.getY() - 5);
                    double angle = l2.direction().angle() * 180.0 / Math.PI;
                    image.setRotate(angle + 45.0);
                    getChildren().add(image);
                }
            }
            /*
            draw transition element like chamfer or round
             */
            if (current_ce.transition_curve != null) {
                if (current_ce.transition_curve instanceof math.geom2d.conic.CircleArc2D) {

                    ArcTo at = new ArcTo();
                    at.setLargeArcFlag(false);
                    CircleArc2D geo = (CircleArc2D) current_ce.transition_curve;
                    if (geo.getAngleExtent() > Math.PI) {
                        at.setLargeArcFlag(true);
                    } else {
                        at.setLargeArcFlag(false);
                    }
                    if (geo.isDirect()) {
                        at.setSweepFlag(false);
                    } else {
                        at.setSweepFlag(true);
                    }

                    Point2D p2 = geo.asPolyline(1).lastPoint();

                    p2 = this.translateScalePoint(p2);

                    at.setRadiusX(geo.supportingCircle().radius() * fact);
                    at.setRadiusY(geo.supportingCircle().radius() * fact);
                    at.setX(p2.getX());
                    at.setY(p2.getY());
                    tpe.path.getElements().add(at);

                } else {

                    Point2D p2 = current_ce.transition_curve.lastPoint();

                    p2 = this.translateScalePoint(p2);

                    LineTo lt = new LineTo();
                    lt.setX(p2.getX());
                    lt.setY(p2.getY());
                    tpe.path.getElements().add(lt);

                }
            }

            tpe.path.setOnMouseEntered(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {
                    NumberFormat nf = NumberFormat.getNumberInstance(Locale.US);
                    DecimalFormat df = (DecimalFormat) nf;
                    df.applyPattern("0.###");

                    contourelement current_ce = null;
                    for (ToolpathElement te : shapes) {
                        if (te.path.equals(event.getSource())) {
                            current_ce = te.element;

                        }
                    }
                    //contourelement current_ce = ((ToolpathElement) event.getSource()).element;

                    point startpoint = current_ce.points.getFirst();
                    point endpoint = current_ce.points.getLast();

                    if (current_ce.shape == contourelement.Shape.ARC) {
                        CircleArc2D geo = (CircleArc2D) current_ce.curve;
                        double startAngle;
                        double endAngle;

                        if (current_ce.ccw == true) {
                            startAngle = 90.0 + geo.getStartAngle() * 180.0 / Math.PI;
                            endAngle = 90.0 + (geo.getStartAngle() + geo.getAngleExtent()) * 180.0 / Math.PI;
                        } else {
                            startAngle = -90.0 + geo.getStartAngle() * 180.0 / Math.PI;
                            endAngle = -90.0 + (geo.getStartAngle() + geo.getAngleExtent()) * 180.0 / Math.PI;
                        }
                        if (startAngle > 360.0) {
                            startAngle -= 360.0;
                        }
                        if (startAngle < -360.0) {
                            startAngle += 360.0;
                        }
                        if (endAngle > 360.0) {
                            endAngle -= 360.0;
                        }
                        if (endAngle < -360.0) {
                            endAngle += 360.0;
                        }

                        text.setText(org.openide.util.NbBundle.getMessage(Toolpath.class, "Startpoint") + ": X=" + df.format(startpoint.y * 2.0) + ", Z=" + df.format(startpoint.x)
                                + "\n" + org.openide.util.NbBundle.getMessage(Toolpath.class, "Endpoint") + ": X=" + df.format(endpoint.y * 2.0) + ", Z=" + df.format(endpoint.x)
                                + "\n" + org.openide.util.NbBundle.getMessage(Toolpath.class, "Centerpoint") + ": X=" + df.format(geo.supportingCircle().center().getY() * 2.0) + ", Z=" + df.format(geo.supportingCircle().center().getX())
                                + "\n" + org.openide.util.NbBundle.getMessage(Toolpath.class, "Radius") + ": " + df.format(current_ce.radius)
                                + "\n" + org.openide.util.NbBundle.getMessage(Toolpath.class, "Startangle") + ": " + df.format(startAngle)
                                + "\n" + org.openide.util.NbBundle.getMessage(Toolpath.class, "Endangle") + ": " + df.format(endAngle)
                                + "\n" + "ΔX : " + df.format((endpoint.y - startpoint.y) * 2.0)
                                + "\n" + "ΔZ : " + df.format(endpoint.x - startpoint.x)
                        );
                    } else {
                        LineSegment2D geo = (LineSegment2D) current_ce.curve;
                        double angle = geo.direction().angle() * 180.0 / Math.PI;
                        text.setText(org.openide.util.NbBundle.getMessage(Toolpath.class, "Startpoint") + ": X=" + df.format(startpoint.y * 2.0) + ", Z=" + df.format(startpoint.x)
                                + "\n" + org.openide.util.NbBundle.getMessage(Toolpath.class, "Endpoint") + ": X=" + df.format(endpoint.y * 2.0) + ", Z=" + df.format(endpoint.x)
                                + "\n" + org.openide.util.NbBundle.getMessage(Toolpath.class, "Angle") + ": " + df.format(angle)
                                + "\n" + "ΔX : " + df.format((endpoint.y - startpoint.y) * 2.0)
                                + "\n" + "ΔZ : " + df.format(endpoint.x - startpoint.x)
                        );
                    }

                    ((Shape) event.getSource()).setStroke(Color.RED);
                    event.consume();

                }
            });

            tpe.path.setOnMouseExited(new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent event) {

                    text.setText("");
                    ((Shape) event.getSource()).setStroke(Color.BLACK);
                    event.consume();

                }
            });
            tpe.element = current_ce;
            tpe.path.setStrokeWidth(2);
            tpe.path.setStroke(Color.BLACK);
            if (current_ce.feed == contourelement.Feed.RAPID) {
                tpe.path.setStyle("-fx-stroke-dash-array: 1 3 ; ");
            }

            shapes.add(tpe);
            getChildren().add(tpe.path);
            tpe.endpoint.setStroke(Color.YELLOWGREEN);
            tpe.endpoint.setFill(Color.YELLOWGREEN);
            tpe.endpoint.setVisible(false);
            getChildren().add(tpe.endpoint);

        }

        setOnMousePressed(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                pressedX = event.getX();
                pressedY = event.getY();
                event.consume();
            }
        });
        setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                double x = event.getX();
                double y = event.getY();
                x_trans += (x - pressedX);
                y_trans += (y - pressedY);
                draw();
                pressedX = x;
                pressedY = y;
                event.consume();
            }
        });

        setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                double new_fact = fact;

                if (event.getDeltaY() < 0) {
                    new_fact *= 0.85;
                } else {
                    new_fact *= 1.15;
                }
                if (new_fact < 0.001) {
                    return;
                }
                if (new_fact > 1000.0) {
                    return;
                }
                fact = new_fact;

                draw();

                event.consume();
            }
        });

        text = new Text();
        double off = text.getBaselineOffset();
        text.setX(5);
        text.setY(off);
        //text.setCache(true);
        getChildren().add(0, text);

    }

    /**
     * draw the contour
     *
     * @param elements contour elements to draw
     */
    public void draw(LinkedList<contourelement> elements) {
        setMinSize(50, 50);
        this.setPrefSize(Double.MAX_VALUE, Double.MAX_VALUE);

        c_elements = elements;
        if (this.recalcTransScale) {
            this.calcTransScale(elements);
            this.recalcTransScale = false;
        }

        this.draw();

    }

    public void highlightElement(ToolpathElement te) {
        unhighlightElements();
        te.path.setStroke(Color.YELLOWGREEN);
        te.endpoint.setVisible(true);

    }

    public void highlightElement(contourelement ce) {
        for (ToolpathElement te : shapes) {
            if (te.element.equals(ce)) {
                highlightElement(te);

            }
        }
    }

    public void highlightElement(int index) {
        for (int i = 0; i < shapes.size(); i++) {
            if (i == index) {
                highlightElement(shapes.get(i));
                return;
            }
        }
    }

    public void unhighlightElements() {
        for (ToolpathElement te : shapes) {

            te.path.setStroke(Color.BLACK);
            te.endpoint.setVisible(false);

        }

    }

    private Line getLineFromGeom(Line line1, Line2D l, String style) {
        //Line line1 = new Line();       
        line1.setStartX(l.p1.getX());
        line1.setStartY(l.p1.getY());
        line1.setEndX(l.p2.getX());
        line1.setEndY(l.p2.getY());
        line1.setStyle(style);
        return line1;

    }

}
