package org.vaadin.addon.leaflet.draw;

import java.lang.reflect.Method;
import java.util.EventObject;

import com.vaadin.server.Resource;
import org.vaadin.addon.leaflet.LCircle;
import org.vaadin.addon.leaflet.LFeatureGroup;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolygon;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LeafletLayer;
import org.vaadin.addon.leaflet.control.AbstractControl;
import org.vaadin.addon.leaflet.draw.client.LeafletDrawServerRcp;
import org.vaadin.addon.leaflet.draw.shared.LeafletDrawState;
import org.vaadin.addon.leaflet.jsonmodels.VectorStyle;
import org.vaadin.addon.leaflet.shared.Point;

import com.vaadin.server.AbstractClientConnector;
import com.vaadin.shared.Connector;
import com.vaadin.util.ReflectTools;
import org.vaadin.addon.leaflet.LRectangle;
import org.vaadin.addon.leaflet.shared.Bounds;

/**
 * Draw "toolbar" that is added to the map. This allows users to draw and edit
 * various feature types.
 */
public class LDraw extends AbstractControl {

    public static class FeatureDrawnEvent extends EventObject {

        private LeafletLayer drawnLayer;

        public FeatureDrawnEvent(Connector lDraw, LeafletLayer drawnLayer) {
            super(lDraw);
            this.drawnLayer = drawnLayer;
        }

        public LeafletLayer getDrawnFeature() {
            return drawnLayer;
        }
    }

    public static class FeatureModifiedEvent extends EventObject {

        private LeafletLayer modifiedLayer;

        public FeatureModifiedEvent(AbstractClientConnector connector,
                LeafletLayer modifiedLayer) {
            super(connector);
            this.modifiedLayer = modifiedLayer;
        }

        public LeafletLayer getModifiedFeature() {
            return modifiedLayer;
        }
    }

    public static class FeatureDeletedEvent extends EventObject {

        private LeafletLayer deleted;

        public FeatureDeletedEvent(LDraw lDraw, LeafletLayer deletedLayer) {
            super(lDraw);
            this.deleted = deletedLayer;
        }

        public LeafletLayer getDeletedFeature() {
            return deleted;
        }
    }

    public interface FeatureDrawnListener {

        public static final Method drawnMethod = ReflectTools
                .findMethod(FeatureDrawnListener.class, "featureDrawn",
                        FeatureDrawnEvent.class);

        public void featureDrawn(FeatureDrawnEvent event);
    }

    public interface FeatureModifiedListener {

        public static final Method modifiedMethod = ReflectTools.findMethod(
                FeatureModifiedListener.class, "featureModified",
                FeatureModifiedEvent.class);

        public void featureModified(FeatureModifiedEvent event);
    }

    public interface FeatureDeletedListener {

        public void featureDeleted(FeatureDeletedEvent event);
    }

    private static final Method deletedMethod = ReflectTools.findMethod(
            FeatureDeletedListener.class, "featureDeleted",
            FeatureDeletedEvent.class);

    public void addFeatureDrawnListener(FeatureDrawnListener listener) {
        addListener(FeatureDrawnEvent.class, listener,
                FeatureDrawnListener.drawnMethod);
    }

    public void removeFeatureDrawnListener(FeatureDrawnListener listener) {
        removeListener(FeatureDrawnEvent.class, listener);
    }

    public void addFeatureModifiedListener(FeatureModifiedListener listener) {
        addListener(FeatureModifiedEvent.class, listener,
                FeatureModifiedListener.modifiedMethod);
    }

    public void removeFeatureModifiedListener(FeatureModifiedListener listener) {
        removeListener(FeatureModifiedEvent.class, listener);
    }

    public void addFeatureDeletedListener(FeatureDeletedListener listener) {
        addListener(FeatureDeletedEvent.class, listener, deletedMethod);
    }

    public void removeFeatureDeletedListener(FeatureDeletedListener listener) {
        removeListener(FeatureDeletedEvent.class, listener);
    }

    public LDraw() {
        registerRpc(new LeafletDrawServerRcp() {
            @Override
            public void markerDrawn(Point p) {
                LMarker marker = new LMarker(p);
                marker.setIcon(getResource("markerDrawIcon"));
                marker.setIconSize(getState().drawMarkerState.iconSize);
                marker.setIconAnchor(getState().drawMarkerState.iconAnchor);
                fireEvent(new FeatureDrawnEvent(LDraw.this, marker));
            }

            @Override
            public void circleDrawn(Point point, double radius) {
                LCircle circle = new LCircle(point, radius);
                circle.setStyle(circleDrawStyle);
                fireEvent(new FeatureDrawnEvent(LDraw.this, circle));
            }

            @Override
            public void rectangleDrawn(Bounds bounds) {
                LRectangle rectangle = new LRectangle(bounds);
                rectangle.setStyle(rectangleDrawStyle);
                fireEvent(new FeatureDrawnEvent(LDraw.this, rectangle));
            }

            @Override
            public void polygonDrawn(Point[] latLngs) {
                LPolygon polygon = new LPolygon(latLngs);
                polygon.setStyle(polygonDrawStyle);
                fireEvent(new FeatureDrawnEvent(LDraw.this, polygon));
            }

            @Override
            public void polylineDrawn(Point[] latLngs) {
                LPolyline polyline = new LPolyline(latLngs);
                polyline.setStyle(polylineDrawStyle);
                fireEvent(new FeatureDrawnEvent(LDraw.this, polyline));
            }

            @Override
            public void markerModified(Connector mc, Point newPoint) {
                LMarker m = (LMarker) mc;
                m.setPoint(newPoint);
                fireEvent(new FeatureModifiedEvent(LDraw.this, m));
            }

            @Override
            public void circleModified(Connector cc, Point latLng, double radius) {
                LCircle c = (LCircle) cc;
                c.setRadius(radius);
                c.setPoint(latLng);
                fireEvent(new FeatureModifiedEvent(LDraw.this, c));
            }

            @Override
            public void polylineModified(Connector plc, Point[] pointArray) {
                LPolyline pl = (LPolyline) plc;
                pl.setPoints(pointArray);
                fireEvent(new FeatureModifiedEvent(LDraw.this, pl));
            }

            @Override
            public void rectangleModified(Connector rc, Bounds bounds) {
                LRectangle r = (LRectangle) rc;
                r.setBounds(bounds);
                fireEvent(new FeatureModifiedEvent(LDraw.this, r));
            }

            @Override
            public void layerDeleted(Connector c) {
                fireEvent(new FeatureDeletedEvent(LDraw.this, (LeafletLayer) c));
            }

            @Override
            public void polygonModified(Connector plc, Point[] pointArray) {
                LPolygon pl = (LPolygon) plc;
                pl.setPoints(pointArray);
                fireEvent(new FeatureModifiedEvent(LDraw.this, pl));
            }
            
        });
    }

    @Override
    protected LeafletDrawState getState() {
        return (LeafletDrawState) super.getState();
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        getState().drawPolylineState.vectorStyleJson = polylineDrawStyle != null ? polylineDrawStyle.asJson() : "{}";
        getState().drawPolygonState.vectorStyleJson = polygonDrawStyle != null ? polygonDrawStyle.asJson() : "{}";
        getState().drawRectangleState.vectorStyleJson = rectangleDrawStyle != null ? rectangleDrawStyle.asJson() : "{}";
        getState().drawCircleState.vectorStyleJson = circleDrawStyle != null ? circleDrawStyle.asJson() : "{}";
        super.beforeClientResponse(initial);
    }

    public void setEditableFeatureGroup(LFeatureGroup group) {
        getState().featureGroup = group;
    }

    private VectorStyle polylineDrawStyle;
    private VectorStyle polygonDrawStyle;
    private VectorStyle rectangleDrawStyle;
    private VectorStyle circleDrawStyle;


    public void setPolylineDrawHandlerVisible(Boolean visible) {
        getState().drawPolylineState.visible = visible;
    }

    public void setPolylineDrawAllowIntersection(Boolean allowIntersection) {
        getState().drawPolylineState.allowIntersection = allowIntersection;
    }

    public void setPolylineDrawGuidelineDistance(Integer guidelineDistance) {
        getState().drawPolylineState.guidelineDistance = guidelineDistance;
    }

    public void setPolylineDrawStyle(VectorStyle style) {
        polylineDrawStyle = style;
    }

    public void setPolylineDrawMetric(Boolean metric) {
        getState().drawPolylineState.metric = metric;
    }

    public void setPolylineDrawZIndexOffset(Integer zIndexOffset) {
        getState().drawPolylineState.zIndexOffset = zIndexOffset;
    }

    public void setPolylineDrawRepeatMode(Boolean repeatMode) {
        getState().drawPolylineState.repeatMode = repeatMode;
    }


    public void setPolygonDrawHandlerVisible(boolean visible) {
        getState().drawPolygonState.visible = visible;
    }

    public void setPolygonDrawShowArea(Boolean showArea) {
        getState().drawPolygonState.showArea = showArea;
    }

    public void setPolygonDrawAllowIntersection(Boolean allowIntersection) {
        getState().drawPolygonState.allowIntersection = allowIntersection;
    }

    public void setPolygonDrawGuidelineDistance(Integer guidelineDistance) {
        getState().drawPolygonState.guidelineDistance = guidelineDistance;
    }

    public void setPolygonDrawStyle(VectorStyle style) {
        this.polygonDrawStyle = style;
    }

    public void setPolygonDrawMetric(Boolean metric) {
        getState().drawPolygonState.metric = metric;
    }

    public void setPolygonDrawZIndexOffset(Integer zIndexOffset) {
        getState().drawPolygonState.zIndexOffset = zIndexOffset;
    }

    public void setPolygonDrawRepeatMode(Boolean repeatMode) {
        getState().drawPolygonState.repeatMode = repeatMode;
    }


    public void setRectangleDrawHandlerVisible(boolean visible) {
        getState().drawRectangleState.visible = visible;
    }

    public void setRectangleDrawStyle(VectorStyle style) {
        this.rectangleDrawStyle = style;
    }

    public void setRectangleDrawRepeatMode(Boolean repeatMode) {
        getState().drawRectangleState.repeatMode = repeatMode;
    }


    public void setCircleDrawHandlerVisible(boolean visible) {
        getState().drawCircleState.visible = visible;
    }

    public void setCircleDrawStyle(VectorStyle style) {
        this.circleDrawStyle = style;
    }

    public void setCircleDrawRepeatMode(Boolean repeatMode) {
        getState().drawCircleState.repeatMode = repeatMode;
    }


    public void setMarkerDrawHandlerVisible(boolean visible) {
        getState().drawMarkerState.visible = visible;
    }

    public void setMarkerDrawIcon(Resource icon) {
        setResource("markerDrawIcon", icon);
    }

    public void setMarkerDrawIconAnchor(Point anchor) {
        getState().drawMarkerState.iconAnchor = anchor;
    }

    public void setMarkerDrawIconSize(Point size) {
        getState().drawMarkerState.iconSize = size;
    }
}
