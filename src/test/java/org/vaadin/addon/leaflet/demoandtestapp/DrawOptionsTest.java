package org.vaadin.addon.leaflet.demoandtestapp;

import com.vaadin.data.Property;
import com.vaadin.server.ClassResource;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Notification;
import org.vaadin.addon.leaflet.LFeatureGroup;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LOpenStreetMapLayer;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.draw.LDraw;
import org.vaadin.addon.leaflet.jsonmodels.VectorStyle;
import org.vaadin.addon.leaflet.shared.Point;
import org.vaadin.addonhelpers.AbstractTest;

import java.util.Arrays;

public class DrawOptionsTest extends AbstractTest {

    @Override
    public String getDescription() {
        return "Test leaflet draw with options";
    }

    private LMap leafletMap;
    private LDraw draw = new LDraw();
    private LFeatureGroup group;

    @Override
    public Component getTestComponent() {

        leafletMap = new LMap();
        leafletMap.setCenter(0, 0);
        leafletMap.setZoomLevel(6);
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

        //test polyline draw options
        VectorStyle polylineDrawStyle = new VectorStyle();
        polylineDrawStyle.setColor("GREEN");
        draw.setPolylineDrawStyle(polylineDrawStyle);
        draw.setPolylineDrawAllowIntersection(false);
        draw.setPolylineDrawGuidelineDistance(50);
        draw.setPolylineDrawHandlerVisible(true);

        //test polygon draw options
        VectorStyle polygonDrawStyle = new VectorStyle();
        polygonDrawStyle.setColor("RED");
        polygonDrawStyle.setStroke(true);
        polygonDrawStyle.setFillColor("YELLOW");
        draw.setPolygonDrawStyle(polygonDrawStyle);

        //test rectangle draw options
        draw.setRectangleDrawHandlerVisible(false);

        //test circle draw options
        VectorStyle circleDrawStyle = new VectorStyle();
        circleDrawStyle.setFillColor("GREY");
        circleDrawStyle.setColor("BROWN");
        draw.setCircleDrawStyle(circleDrawStyle);

        //test marker draw options
        draw.setMarkerDrawIcon(new ClassResource("testicon.png"));
        draw.setMarkerDrawIconAnchor(new Point(24,20));
        draw.setMarkerDrawIconSize(new Point(20,20));

        //test edit draw options
        draw.setEditHandlerVisible(false);  //hide the edit button
        draw.setDeleteHandlerVisible(false);  //hide the delete button

        leafletMap.addControl(draw);

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
                if(checkBox.getValue()) {
                    enableDrawing();
                } else {
                    leafletMap.removeControl(draw);
                }
            }
        });
        content.addComponent(checkBox);


    }
}
