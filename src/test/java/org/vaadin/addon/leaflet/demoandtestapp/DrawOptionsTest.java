package org.vaadin.addon.leaflet.demoandtestapp;

import com.vaadin.data.Property;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import org.vaadin.addon.leaflet.*;
import org.vaadin.addon.leaflet.draw.LDraw;
import org.vaadin.addon.leaflet.draw.shared.*;
import org.vaadin.addon.leaflet.shared.Point;
import org.vaadin.addonhelpers.AbstractTest;

import java.util.Arrays;

public class DrawOptionsTest extends AbstractTest {

    @Override
    public String getDescription() {
        return "Test leaflet draw";
    }

    private LMap leafletMap;
    private LDraw draw = new LDraw();
    private LFeatureGroup group;

    @Override
    public Component getTestComponent() {

        leafletMap = new LMap();
        leafletMap.setCenter(0, 0);
        leafletMap.setZoomLevel(7);
        leafletMap.addLayer(new LOpenStreetMapLayer());

        group = new LFeatureGroup();

        group.addComponent(new LPolyline(new Point(-60, -30),
                new Point(20, 10), new Point(40, 150)));

        leafletMap.addLayer(group);

        enableDrawing();

        return leafletMap;
    }

    private void enableDrawing() {
        draw = new LDraw();
        draw.setEditableFeatureGroup(group);

        DrawPolylineState polylineState = new DrawPolylineState();
        polylineState.metric = false;
        polylineState.allowIntersection = false;
        polylineState.repeatMode = true;
        DrawPolylineShapeState polylineShapeState = new DrawPolylineShapeState();
        polylineShapeState.color = "BLACK";
        polylineShapeState.weight = 8;
        polylineState.polylineShapeState = polylineShapeState;
        draw.setDrawPolylineState(polylineState);

        DrawPolygonState polygonState = new DrawPolygonState();
        polygonState.showArea = true;
        draw.setDrawPolygonState(polygonState);

        DrawRectangleState rectangleState = new DrawRectangleState();
        DrawShapeState rectangleShapeState = new DrawShapeState();
        rectangleShapeState.color = "GREEN";
        rectangleShapeState.stroke = true;
        rectangleState.shapeState = rectangleShapeState;
        rectangleState.repeatMode = true;
        draw.setDrawRectangleState(rectangleState);

        DrawCircleState circleState = new DrawCircleState();
        DrawShapeState circleShapeState = new DrawShapeState();
        circleShapeState.color = "GREEN";
        circleState.repeatMode = true;
        circleState.shapeState = circleShapeState;
        draw.setDrawCircleState(circleState);

        DrawMarkerState markerState = new DrawMarkerState();
        markerState.repeatMode = true;
        DrawIconState iconState = new DrawIconState();
        iconState.iconUrl = "http://leafletjs.com/examples/custom-icons/leaf-orange.png";
        markerState.iconState = iconState;
        draw.setDrawMarkerState(markerState);


        //TODO: should the style be applied here by applications or by v-leaflet-draw ?
        draw.addFeatureDrawnListener(new LDraw.FeatureDrawnListener() {

            @Override
            public void featureDrawn(LDraw.FeatureDrawnEvent event) {
                group.addComponent(event.getDrawnFeature());
                Notification.show("Drawed "
                        + event.getDrawnFeature().getClass().getSimpleName());
            }
        });

        draw.addFeatureModifiedListener(new LDraw.FeatureModifiedListener() {

            @Override
            public void featureModified(LDraw.FeatureModifiedEvent event) {
                Notification
                        .show("Modified "
                                + event.getModifiedFeature().getClass()
                                .getSimpleName());
                if (event.getModifiedFeature() instanceof LPolyline) {
                    LPolyline pl = (LPolyline) event.getModifiedFeature();
                    Point[] points = pl.getPoints();
                    System.out.println(Arrays.toString(points));
                }
            }
        });

        draw.addFeatureDeletedListener(new LDraw.FeatureDeletedListener() {

            @Override
            public void featureDeleted(LDraw.FeatureDeletedEvent event) {
                group.removeComponent(event.getDeletedFeature());
                Notification.show("Deleted "
                        + event.getDeletedFeature().getClass().getSimpleName());
            }
        });


        leafletMap.addControl(draw);
    }

    @Override
    protected void setup() {
        super.setup();

        final CheckBox checkBox = new CheckBox("Drawing mode");
        checkBox.setValue(true);
        checkBox.setImmediate(true);
        checkBox.addValueChangeListener(new Property.ValueChangeListener() {

            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                if (checkBox.getValue()) {
                    enableDrawing();
                } else {
                    leafletMap.removeControl(draw);
                }
            }
        });
        content.addComponent(checkBox);


    }
}