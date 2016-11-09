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
            DrawPolygonHandlerOptions polygonOptions = createPolygonOptions(polygonState);
            buttonOptions.setPolygonHandlerOptions(polygonOptions);
        }

        if (getState().polylineHandlerState != null) {
            LeafletDrawPolylineHandlerState polylineState = getState().polylineHandlerState;
            DrawPolylineHandlerOptions polylineOptions = createPolylineOptions(polylineState);
            buttonOptions.setPolylineHandlerOptions(polylineOptions);
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

        getMap().addDrawStartListener(new DrawStartListener() {
            @Override
            public void onDrawStart(DrawStartEvent event) {
                rpc.drawStart(org.vaadin.addon.leaflet.draw.shared.LayerType.valueOf(event.getRawLayerType()));
            }
        });

        getMap().addDrawStopListener(new DrawStopListener() {
            @Override
            public void onDrawStop(DrawStopEvent event) {
                rpc.drawStop(org.vaadin.addon.leaflet.draw.shared.LayerType.valueOf(event.getRawLayerType()));
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

    protected DrawPolygonHandlerOptions createPolygonOptions(LeafletDrawPolygonHandlerState state) {
        DrawPolygonHandlerOptions options = createPolylineOptions(state).cast();
        if (state.showArea != null) {
            options.setShowArea(state.showArea);
        }
        return options;
    }

    protected DrawPolylineHandlerOptions createPolylineOptions(LeafletDrawPolylineHandlerState state) {
        DrawPolylineHandlerOptions options = DrawPolylineHandlerOptions.create();
        ShapeOptions shapeOptions = createShapeOptions(state);
        options.setShapeOptions(shapeOptions);

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

    protected ShapeOptions createShapeOptions(AbstractLeafletDrawVectorHandlerState state) {
        ShapeOptions options = ShapeOptions.create();
        if (state.stroke != null) {
            options.setStroke(state.stroke);
        }
        if (state.color != null) {
            options.setColor(state.color);
        }
        if (state.weight != null) {
            options.setWeight(state.weight);
        }
        if (state.opacity != null) {
            options.setOpacity(state.opacity);
        }
        if (state.fill != null) {
            options.setFill(state.fill);
        }
        if (state.fillColor != null) {
            options.setFillColor(state.fillColor);
        }
        if (state.fillOpacity != null) {
            options.setFillOpacity(state.fillOpacity);
        }
        if (state.dashArray != null) {
            options.setDashArray(state.dashArray);
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
