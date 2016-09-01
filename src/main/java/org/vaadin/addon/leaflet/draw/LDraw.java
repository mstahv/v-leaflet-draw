package org.vaadin.addon.leaflet.draw;

import java.lang.reflect.Method;
import java.util.EventObject;
import java.util.Set;

import org.vaadin.addon.leaflet.LCircle;
import org.vaadin.addon.leaflet.LFeatureGroup;
import org.vaadin.addon.leaflet.LMarker;
import org.vaadin.addon.leaflet.LPolygon;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.LeafletLayer;
import org.vaadin.addon.leaflet.control.AbstractControl;
import org.vaadin.addon.leaflet.draw.client.LeafletDrawPolygonHandlerState;
import org.vaadin.addon.leaflet.draw.client.LeafletDrawServerRcp;
import org.vaadin.addon.leaflet.draw.client.LeafletDrawState;
import org.vaadin.addon.leaflet.draw.shared.LayerType;
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

    public static class DrawStartEvent extends EventObject {

        private LayerType layerType;

        public DrawStartEvent(LDraw lDraw, LayerType layerType) {
            super(lDraw);
            this.layerType = layerType;
        }

        public LayerType getLayerType() {
            return layerType;
        }
    }

    public static class DrawStopEvent extends EventObject {

        private LayerType layerType;

        public DrawStopEvent(LDraw lDraw, LayerType layerType) {
            super(lDraw);
            this.layerType = layerType;
        }

        public LayerType getLayerType() {
            return layerType;
        }
    }

    public static class EditStartEvent extends EventObject {

        public EditStartEvent(Object source) {
            super(source);
        }
    }

    public static class EditStopEvent extends EventObject {

        public EditStopEvent(Object source) {
            super(source);
        }
    }

    public static class DeleteStartEvent extends EventObject {

        public DeleteStartEvent(Object source) {
            super(source);
        }
    }

    public static class DeleteStopEvent extends EventObject {

        public DeleteStopEvent(Object source) {
            super(source);
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

    public interface DrawStartListener {

        public static final Method drawStartMethod = ReflectTools.findMethod(
                DrawStartListener.class, "drawStart",
                DrawStartEvent.class);

        public void drawStart(DrawStartEvent event);
    }

    public interface DrawStopListener {

        public static final Method drawStopMethod = ReflectTools.findMethod(
                DrawStopListener.class, "drawStop",
                DrawStopEvent.class);

        public void drawStop(DrawStopEvent event);
    }

    public interface EditStartListener {

        public static final Method editStartMethod = ReflectTools.findMethod(
                EditStartListener.class, "editStart",
                EditStartEvent.class);

        public void editStart(EditStartEvent event);
    }

    public interface EditStopListener {

        public static final Method editStopMethod = ReflectTools.findMethod(
                EditStopListener.class, "editStop",
                EditStopEvent.class);

        public void editStop(EditStopEvent event);
    }

    public interface DeleteStartListener {

        public static final Method deleteStartMethod = ReflectTools.findMethod(
                DeleteStartListener.class, "deleteStart",
                DeleteStartEvent.class);

        public void deleteStart(DeleteStartEvent event);
    }

    public interface DeleteStopListener {

        public static final Method deleteStopMethod = ReflectTools.findMethod(
                DeleteStopListener.class, "deleteStop",
                DeleteStopEvent.class);

        public void deleteStop(DeleteStopEvent event);
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

    public void addDrawStartListener(DrawStartListener listener) {
        addListener(DrawStartEvent.class, listener,
                DrawStartListener.drawStartMethod);
    }

    public void removeDrawStartListener(DrawStartListener listener) {
        removeListener(DrawStartEvent.class, listener);
    }

    public void addDrawStopListener(DrawStopListener listener) {
        addListener(DrawStopEvent.class, listener,
                DrawStopListener.drawStopMethod);
    }

    public void removeDrawStopListener(DrawStopListener listener) {
        removeListener(DrawStopEvent.class, listener);
    }

    public void addEditStartListener(EditStartListener listener) {
        addListener(EditStartEvent.class, listener,
                EditStartListener.editStartMethod);
    }

    public void removeEditStartListener(EditStartListener listener) {
        removeListener(EditStartEvent.class, listener);
    }

    public void addEditStopListener(EditStopListener listener) {
        addListener(EditStopEvent.class, listener,
                EditStopListener.editStopMethod);
    }

    public void removeEditStopListener(EditStopListener listener) {
        removeListener(EditStopEvent.class, listener);
    }

    public void addFeatureDeletedListener(FeatureDeletedListener listener) {
        addListener(FeatureDeletedEvent.class, listener, deletedMethod);
    }

    public void removeFeatureDeletedListener(FeatureDeletedListener listener) {
        removeListener(FeatureDeletedEvent.class, listener);
    }

    public void addDeleteStartListener(DeleteStartListener listener) {
        addListener(DeleteStartEvent.class, listener,
                DeleteStartListener.deleteStartMethod);
    }

    public void removeDeleteStartListener(DeleteStartListener listener) {
        removeListener(DeleteStartEvent.class, listener);
    }

    public void addDeleteStopListener(DeleteStopListener listener) {
        addListener(DeleteStopEvent.class, listener,
                DeleteStopListener.deleteStopMethod);
    }

    public void removeDeleteStopListener(DeleteStopListener listener) {
        removeListener(DeleteStopEvent.class, listener);
    }


    public LDraw() {
        registerRpc(new LeafletDrawServerRcp() {
            @Override
            public void markerDrawn(Point p) {
                fireEvent(new FeatureDrawnEvent(LDraw.this, new LMarker(p)));
            }

            @Override
            public void circleDrawn(Point point, double radius) {
                fireEvent(new FeatureDrawnEvent(LDraw.this, new LCircle(point,
                        radius)));
            }

            @Override
            public void rectangleDrawn(Bounds bounds) {
                fireEvent(new FeatureDrawnEvent(LDraw.this, new LRectangle(
                        bounds)));
            }

            @Override
            public void polygonDrawn(Point[] latLngs) {
                fireEvent(new FeatureDrawnEvent(LDraw.this, new LPolygon(
                        latLngs)));
            }

            @Override
            public void polylineDrawn(Point[] latLngs) {
                fireEvent(new FeatureDrawnEvent(LDraw.this, new LPolyline(
                        latLngs)));
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
            public void drawStart(LayerType layerType) {
                fireEvent(new DrawStartEvent(LDraw.this, layerType));
            }

            @Override
            public void drawStop(LayerType layerType) {
                fireEvent(new DrawStopEvent(LDraw.this, layerType));
            }

            @Override
            public void editStart() {
                fireEvent(new EditStartEvent(LDraw.this));
            }

            @Override
            public void editStop() {
                fireEvent(new EditStopEvent(LDraw.this));
            }

            @Override
            public void deleteStart() {
                fireEvent(new DeleteStartEvent(LDraw.this));
            }

            @Override
            public void deleteStop() {
                fireEvent(new DeleteStopEvent(LDraw.this));
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

    public void setEditableFeatureGroup(LFeatureGroup group) {
        getState().featureGroup = group;
    }

    public void setDrawVisibleButtons(Set<String> drawVisibleButtons) {
        getState().drawVisibleButtons = drawVisibleButtons;
    }

    public LeafletDrawPolygonHandlerState getPolygonHandlerState() {
        return getState().polygonHandlerState;
    }

    public void setPolygonHandlerState(LeafletDrawPolygonHandlerState polygonHandlerState) {
        getState().polygonHandlerState = polygonHandlerState;
    }

}
