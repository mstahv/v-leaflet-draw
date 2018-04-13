package org.vaadin.addon.leaflet.draw.client;

import com.google.gwt.core.client.JsonUtils;
import com.vaadin.shared.communication.URLReference;
import org.peimari.gleaflet.client.*;
import org.peimari.gleaflet.client.draw.*;
import org.peimari.gleaflet.client.draw.LayerType;
import org.peimari.gleaflet.client.resources.LeafletDrawResourceInjector;
import org.vaadin.addon.leaflet.client.*;
import org.vaadin.addon.leaflet.draw.LDraw;

import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;
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
        DrawControlButtonOptions drawOptions = DrawControlButtonOptions.create();
        DrawControlEditOptions editOptions = DrawControlEditOptions.create();

        final LeafletFeatureGroupConnector fgc = (LeafletFeatureGroupConnector) getState().featureGroup;
        FeatureGroup layerGroup = (FeatureGroup) fgc.getLayer();
        editOptions.setFeatureGroup(layerGroup);

        if (getState().editHandlerState.visible) {
            DrawControlEditHandlerOptions editHandlerOptions = editHandlerOptionsFor(getState().editHandlerState);
            editOptions.setEditHandler(editHandlerOptions);
        } else {
            editOptions.disableEditHandler();
        }

        if (getState().deleteHandlerState.visible) {
            DrawControlDeleteHandlerOptions deleteHandlerOptions = deleteHandlerOptionsFor(getState().deleteHandlerState);
            editOptions.setDeleteHandler(deleteHandlerOptions);
        } else {
            editOptions.disableDeleteHandler();
        }

        if (getState().drawPolylineState.visible) {
            DrawPolylineOptions polylineOptions = drawPolylineOptionsFor(getState().drawPolylineState);
            drawOptions.setPolyline(polylineOptions);
        } else {
            drawOptions.setPolylineVisibility(false);
        }

        if (getState().drawPolygonState.visible) {
            DrawPolygonOptions polygonOptions = drawPolygonOptionsFor(getState().drawPolygonState);
            drawOptions.setPolygon(polygonOptions);
        } else {
            drawOptions.setPolygonVisibility(false);
        }

        if (getState().drawRectangleState.visible) {
            DrawRectangleOptions rectangleOptions = drawRectangleOptionsFor(getState().drawRectangleState);
            drawOptions.setRectangle(rectangleOptions);
        } else {
            drawOptions.setRectangleVisibility(false);
        }

        if (getState().drawCircleState.visible) {
            DrawCircleOptions circleOptions = drawCircleOptionsFor(getState().drawCircleState);
            drawOptions.setCircle(circleOptions);
        } else {
            drawOptions.setCircleVisibility(false);
        }

        if (getState().drawMarkerState.visible) {
            DrawMarkerOptions markerOptions = drawMarkerOptionsFor(getState().drawMarkerState, this);
            drawOptions.setMarker(markerOptions);
        } else {
            drawOptions.setMarkerVisibility(false);
        }

        if (getState().drawCircleMarkerState.visible) {
            DrawCircleMarkerOptions circleMarkerOptions = drawCircleMarkerOptionsFor(getState().drawCircleMarkerState);
            drawOptions.setCircleMarker(circleMarkerOptions);
        } else {
            drawOptions.setCircleMarkerVisibility(false);
        }

        options.setDraw(drawOptions);
        options.setEdit(editOptions);

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
                    case circlemarker:
                        CircleMarker cm = (CircleMarker) event.getLayer();
                        rpc.circleMarkerDrawn(U.toPoint(cm.getLatLng()), cm.getRadius());
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
                        } else if (c instanceof LeafletCircleMarkerConnector) {
                            LeafletCircleMarkerConnector cmc = (LeafletCircleMarkerConnector) c;
                            CircleMarker circleMarker = (CircleMarker) cmc.getLayer();
                            rpc.circleMarkerModified(cmc,
                                    U.toPoint(circleMarker.getLatLng()),
                                    circleMarker.getRadius());
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

    public static DrawCircleMarkerOptions drawCircleMarkerOptionsFor(DrawHandlerState state) {
        return DrawCircleMarkerOptions.create();
    }

    public static DrawControlEditHandlerOptions editHandlerOptionsFor(EditHandlerState state) {
        DrawControlEditHandlerOptions options = DrawControlEditHandlerOptions.create();
        PathOptions pathOptions = JsonUtils.safeEval(state.selectedPathState.vectorStyleJson);
        SelectedPathOptions selectedPathOptions = pathOptions.cast();

        if (state.selectedPathState.maintainColor != null) {
            selectedPathOptions.setMaintainColor(state.selectedPathState.maintainColor);
        }
        options.setSelectedPathOptions(selectedPathOptions);

        return options;
    }

    public static DrawControlDeleteHandlerOptions deleteHandlerOptionsFor(DeleteHandlerState state) {
        return DrawControlDeleteHandlerOptions.create();
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
