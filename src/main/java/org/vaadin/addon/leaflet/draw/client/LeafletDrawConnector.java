package org.vaadin.addon.leaflet.draw.client;

import com.google.gwt.core.client.JsonUtils;
import com.vaadin.shared.communication.URLReference;
import org.peimari.gleaflet.client.*;
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
import org.vaadin.addon.leaflet.draw.shared.*;

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

        DrawControlButtonOptions buttonOptions = DrawControlButtonOptions.create();

        if (getState().drawPolylineState.visible) {
            DrawPolylineOptions polylineOptions = drawPolylineOptionsFor(getState().drawPolylineState);
            buttonOptions.setPolyline(polylineOptions);
        } else {
            buttonOptions.setPolylineVisibility(false);
        }

        if (getState().drawPolygonState.visible) {
            DrawPolygonOptions polygonOptions = drawPolygonOptionsFor(getState().drawPolygonState);
            buttonOptions.setPolygon(polygonOptions);
        } else {
            buttonOptions.setPolygonVisibility(false);
        }

        if (getState().drawRectangleState.visible) {
            DrawRectangleOptions rectangleOptions = drawRectangleOptionsFor(getState().drawRectangleState);
            buttonOptions.setRectangle(rectangleOptions);
        } else {
            buttonOptions.setRectangleVisibility(false);
        }

        if (getState().drawCircleState.visible) {
            DrawCircleOptions circleOptions = drawCircleOptionsFor(getState().drawCircleState);
            buttonOptions.setCircle(circleOptions);
        } else {
            buttonOptions.setCircleVisibility(false);
        }

        if (getState().drawMarkerState.visible) {
            DrawMarkerOptions markerOptions = drawMarkerOptionsFor(getState().drawMarkerState, this);
            buttonOptions.setMarker(markerOptions);
        } else {
            buttonOptions.setMarkerVisibility(false);
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

        return l;
    }

    public static DrawPolylineOptions drawPolylineOptionsFor(DrawPolylineState state) {
        DrawPolylineOptions options = DrawPolylineOptions.create();
        PathOptions polylinePathOptions = JsonUtils.safeEval(state.vectorStyleJson);
        options.setShapeOptions((PolylineOptions) polylinePathOptions);

        if (state.allowIntersection != null) {
            options.setAllowIntersection(state.allowIntersection);
        }
        if (state.guidelineDistance != null) {
            options.setGuidelineDistance(state.guidelineDistance);
        }
        if (state.metric != null) {
            options.setMetric(state.metric);
        }
        if (state.zIndexOffset != null) {
            options.setZIndexOffset(state.zIndexOffset);
        }
        if (state.repeatMode != null) {
            options.setRepeatMode(state.repeatMode);
        }
        return options;
    }

    public static DrawPolygonOptions drawPolygonOptionsFor(DrawPolygonState state) {
        DrawPolygonOptions options = DrawPolygonOptions.create();
        PathOptions pathOptions = JsonUtils.safeEval(state.vectorStyleJson);
        options.setShapeOptions((PolylineOptions)pathOptions);

        if (state.showArea != null) {
            options.setShowArea(state.showArea);
        }
        if (state.allowIntersection != null) {
            options.setAllowIntersection(state.allowIntersection);
        }
        if (state.guidelineDistance != null) {
            options.setGuidelineDistance(state.guidelineDistance);
        }
        if (state.metric != null) {
            options.setMetric(state.metric);
        }
        if (state.zIndexOffset != null) {
            options.setZIndexOffset(state.zIndexOffset);
        }
        if (state.repeatMode != null) {
            options.setRepeatMode(state.repeatMode);
        }
        return options;
    }

    public static DrawRectangleOptions drawRectangleOptionsFor(DrawRectangleState state) {
        DrawRectangleOptions options = DrawRectangleOptions.create();
        PathOptions pathOptions = JsonUtils.safeEval(state.vectorStyleJson);
        options.setShapeOptions(pathOptions);

        if (state.repeatMode != null) {
            options.setRepeatMode(state.repeatMode);
        }
        return options;
    }

    public static DrawCircleOptions drawCircleOptionsFor(DrawCircleState state) {
        DrawCircleOptions options = DrawCircleOptions.create();
        PathOptions pathOptions = JsonUtils.safeEval(state.vectorStyleJson);
        options.setShapeOptions(pathOptions);

        if (state.repeatMode != null) {
            options.setRepeatMode(state.repeatMode);
        }
        return options;
    }

    public static DrawMarkerOptions drawMarkerOptionsFor(DrawMarkerState state, AbstractControlConnector c) {
        DrawMarkerOptions options = DrawMarkerOptions.create();
        URLReference urlReference = c.getState().resources.get("markerDrawIcon");
        if (urlReference != null) {
            IconOptions iconOptions = IconOptions.create();
            iconOptions.setIconUrl(urlReference.getURL());

            if (state.iconSize != null) {
                iconOptions.setIconSize(Point.create(
                        state.iconSize.getLat(),
                        state.iconSize.getLon()));
            }
            if (state.iconAnchor != null) {
                iconOptions.setIconAnchor(Point.create(
                        state.iconAnchor.getLat(),
                        state.iconAnchor.getLon()));
            }
            Icon icon = Icon.create(iconOptions);
            options.setIcon(icon);
        }
        if (state.zIndexOffset != null) {
            options.setZIndexOffset(state.zIndexOffset);
        }
        if (state.repeatMode != null) {
            options.setRepeatMode(state.repeatMode);
        }
        return options;
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
