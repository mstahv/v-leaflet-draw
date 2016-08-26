package org.vaadin.addon.leaflet.draw.client;

import org.peimari.gleaflet.client.Circle;
import org.peimari.gleaflet.client.EditableMap;
import org.peimari.gleaflet.client.FeatureGroup;
import org.peimari.gleaflet.client.Layer;
import org.peimari.gleaflet.client.Marker;
import org.peimari.gleaflet.client.Polygon;
import org.peimari.gleaflet.client.Polyline;
import org.peimari.gleaflet.client.Rectangle;
import org.peimari.gleaflet.client.draw.*;
import org.peimari.gleaflet.client.resources.LeafletDrawResourceInjector;
import org.vaadin.addon.leaflet.client.AbstractControlConnector;
import org.vaadin.addon.leaflet.client.AbstractLeafletLayerConnector;
import org.vaadin.addon.leaflet.client.LeafletCircleConnector;
import org.vaadin.addon.leaflet.client.LeafletFeatureGroupConnector;
import org.vaadin.addon.leaflet.client.LeafletMarkerConnector;
import org.vaadin.addon.leaflet.client.LeafletPolylineConnector;
import org.vaadin.addon.leaflet.client.U;
import org.vaadin.addon.leaflet.draw.LDraw;

import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;
import org.vaadin.addon.leaflet.client.LeafletPolygonConnector;
import org.vaadin.addon.leaflet.client.LeafletRectangleConnector;

@Connect(LDraw.class)
public class LeafletDrawConnector extends AbstractControlConnector<Draw> {

    static {
        LeafletDrawResourceInjector.ensureInjected();
    }

    private LeafletDrawServerRcp rpc = RpcProxy.create(
            LeafletDrawServerRcp.class, this);

    @Override
    protected Draw createControl() {
        DrawControlOptions options = DrawControlOptions.create();
        final LeafletFeatureGroupConnector fgc = (LeafletFeatureGroupConnector) getState().featureGroup;
        FeatureGroup layerGroup = (FeatureGroup) fgc.getLayer();
        options.setEditableFeatureGroup(layerGroup);

        DrawControlButtonOptions buttonOptions = DrawControlButtonOptions.
                create();
        if (getState().drawVisibleButtons != null) {
            buttonOptions.setVisibleButtons(getState().drawVisibleButtons);
        }

        if (getState().polygonHandlerState != null) {
            LeafletDrawPolygonHandlerState polygonState = getState().polygonHandlerState;
            DrawPolygonHandlerOptions polygonOptions = DrawPolygonHandlerOptions.create();
            ShapeOptions shapeOptions = ShapeOptions.create();

            if (polygonState.showArea != null) {
                polygonOptions.setShowArea(polygonState.showArea);
            }
            if (polygonState.allowIntersection != null) {
                polygonOptions.setAllowIntersection(polygonState.allowIntersection);
            }
            if (polygonState.guidelineDistance != null) {
                polygonOptions.setGuidelineDistance(polygonState.guidelineDistance);
            }
            if (polygonState.metric != null) {
                polygonOptions.setMetric(polygonState.metric);
            }
            if (polygonState.zIndexOffset != null) {
                polygonOptions.setZIndexOffset(polygonState.zIndexOffset);
            }
            if (polygonState.repeatMode != null) {
                polygonOptions.setRepeatMode(polygonState.repeatMode);
            }
            if (polygonState.stroke != null) {
                shapeOptions.setStroke(polygonState.stroke);
            }
            if (polygonState.color != null) {
                shapeOptions.setColor(polygonState.color);
            }
            if (polygonState.weight != null) {
                shapeOptions.setWeight(polygonState.weight);
            }
            if (polygonState.opacity != null) {
                shapeOptions.setOpacity(polygonState.opacity);
            }
            if (polygonState.fill != null) {
                shapeOptions.setFill(polygonState.fill);
            }
            if (polygonState.fillColor != null) {
                shapeOptions.setFillColor(polygonState.fillColor);
            }
            if (polygonState.fillOpacity != null) {
                shapeOptions.setFillOpacity(polygonState.fillOpacity);
            }
            if (polygonState.dashArray != null) {
                shapeOptions.setDashArray(polygonState.dashArray);
            }
            polygonOptions.setShapeOptions(shapeOptions);
            buttonOptions.setPolygonHandlerOptions(polygonOptions);
        }

        options.setDraw(buttonOptions);

        Draw l = Draw.create(options);

        getMap().addLayerCreatedListener(new LayerCreatedListener() {

            @Override
            public void onCreate(LayerCreatedEvent event) {
                LayerType type = event.getLayerType();
                /* type specific actions... */
                switch (type) {
                    case marker:
                        Marker m = (Marker) event.getLayer();
                        rpc.markerDrawn(U.toPoint(m.getLatLng()));
                        return;
                    case circle:
                        Circle c = (Circle) event.getLayer();
                        rpc.circleDrawn(U.toPoint(c.getLatLng()), c.getRadius());
                        break;
                    case rectangle:
                        Rectangle r = (Rectangle) event.getLayer();
                        rpc.rectangleDrawn(U.toBounds(r.getBounds()));
                        break;
                    case polygon:
                        Polygon p = (Polygon) event.getLayer();
                        rpc.polygonDrawn(U.toPointArray(p.getExteriorRing()));
                        break;
                    case polyline:
                        Polyline pl = (Polyline) event.getLayer();
                        rpc.polylineDrawn(U.toPointArray(pl.getLatLngs()));
                        break;
                    default:
                        break;
                }
            }
        });

        getMap().addLayersEditedListener(new LayersEditedListener() {

            @Override
            public void onEdit(LayersEditedEvent event) {
                Layer[] layers = event.getLayers().getLayers();
                for (Layer iLayer : layers) {
                    AbstractLeafletLayerConnector<?> c = fgc
                            .getConnectorFor(iLayer);
                    if (c != null) {
                        if (c instanceof LeafletMarkerConnector) {
                            LeafletMarkerConnector mc = (LeafletMarkerConnector) c;
                            rpc.markerModified(mc,
                                    U.toPoint(((Marker) iLayer).getLatLng()));
                        } else if (c instanceof LeafletCircleConnector) {
                            LeafletCircleConnector cc = (LeafletCircleConnector) c;
                            Circle circle = (Circle) cc.getLayer();
                            rpc.circleModified(cc,
                                    U.toPoint(circle.getLatLng()),
                                    circle.getRadius());
                        } else if (c instanceof LeafletRectangleConnector) {
                            LeafletRectangleConnector rc = (LeafletRectangleConnector) c;
                            Rectangle polyline = (Rectangle) rc.getLayer();
                            rpc.rectangleModified(rc,
                                    U.toBounds(polyline.getBounds()));
                        } else if (c instanceof LeafletPolygonConnector) {
                            // polygon also gets here
                            LeafletPolygonConnector plc = (LeafletPolygonConnector) c;
                            Polygon polyline = (Polygon) plc.getLayer();
                            rpc.polygonModified(plc, U.toPointArray(polyline.
                                    getExteriorRing()));
                        } else if (c instanceof LeafletPolylineConnector) {
                            LeafletPolylineConnector plc = (LeafletPolylineConnector) c;
                            Polyline polyline = (Polyline) plc.getLayer();
                            rpc.polylineModified(plc,
                                    U.toPointArray(polyline.getLatLngs()));
                        }
                    }
                }
            }
        });

        getMap().addLayersDeletedListener(new LayersDeletedListener() {

            @Override
            public void onDelete(LayersDeletedEvent event) {
                Layer[] layers = event.getLayers().getLayers();
                for (Layer iLayer : layers) {
                    AbstractLeafletLayerConnector<?> c = fgc
                            .getConnectorFor(iLayer);
                    rpc.layerDeleted(c);
                }
            }
        });

        getMap().addEditStartListener(new EditStartListener() {
            @Override
            public void onEditStart(EditStartEvent event) {
                rpc.editStart();
            }
        });

        getMap().addEditStopListener(new EditStopListener() {
            @Override
            public void onEditStop(EditStopEvent event) {
                rpc.editStop();
            }
        });

        getMap().addDeleteStartListener(new DeleteStartListener() {
            @Override
            public void onDeleteStart(DeleteStartEvent event) {
                rpc.deleteStart();
            }
        });

        getMap().addDeleteStopListener(new DeleteStopListener() {
            @Override
            public void onDeleteStop(DeleteStopEvent event) {
                rpc.deleteStop();
            }
        });

        return l;
    }

    protected void doStateChange(StateChangeEvent stateChangeEvent) {

    }

    @Override
    protected EditableMap getMap() {
        return super.getMap().cast();
    }

    @Override
    public LeafletDrawState getState() {
        return (LeafletDrawState) super.getState();
    }

}
