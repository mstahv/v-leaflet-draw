package org.vaadin.addon.leaflet.draw.client;

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

        DrawPolylineState polylineState = getState().polylineState;
        DrawPolylineOptions polylineOptions = DrawPolylineOptions.create();
        if (polylineState == null) {
            buttonOptions.setPolylineVisibility(false);
        } else {
            if (polylineState.allowIntersection != null) {
                polylineOptions.setAllowIntersection(polylineState.allowIntersection);
            }
            if (polylineState.drawErrorState != null) {
                DrawError drawError = DrawError.create();
                if (polylineState.drawErrorState.color != null) {
                    drawError.setColor(polylineState.drawErrorState.color);
                }
                if (polylineState.drawErrorState.timeout != null) {
                    drawError.setTimeout(polylineState.drawErrorState.timeout);
                }
                polylineOptions.setDrawError(drawError);
            }
            if (polylineState.guidelineDistance != null) {
                polylineOptions.setGuidelineDistance(polylineState.guidelineDistance);
            }
            if (polylineState.polylineShapeState != null) {
                PolylineOptions polylinePathOptions = (PolylineOptions) createPathOptions(polylineState.polylineShapeState);
                polylineOptions.setShapeOptions(polylinePathOptions);
            }
            if (polylineState.metric != null) {
                polylineOptions.setMetric(polylineState.metric);
            }
            if (polylineState.zIndexOffset != null) {
                polylineOptions.setZIndexOffset(polylineState.zIndexOffset);
            }
            if (polylineState.repeatMode != null) {
                polylineOptions.setRepeatMode(polylineState.repeatMode);
            }
            buttonOptions.setPolyline(polylineOptions);
        }

        DrawPolygonState polygonState = getState().polygonState;
        DrawPolygonOptions polygonOptions = DrawPolygonOptions.create();
        if (polygonState == null) {
            buttonOptions.setPolygonVisibility(false);
        } else {
            if (polygonState.allowIntersection != null) {
                polygonOptions.setAllowIntersection(polygonState.allowIntersection);
            }
            if (polygonState.drawErrorState != null) {
                DrawError drawError = DrawError.create();
                if (polygonState.drawErrorState.color != null) {
                    drawError.setColor(polygonState.drawErrorState.color);
                }
                if (polygonState.drawErrorState.timeout != null) {
                    drawError.setTimeout(polygonState.drawErrorState.timeout);
                }
                polygonOptions.setDrawError(drawError);
            }
            if (polygonState.guidelineDistance != null) {
                polygonOptions.setGuidelineDistance(polygonState.guidelineDistance);
            }
            if (polygonState.polylineShapeState != null) {
                PolylineOptions polylinePathOptions = (PolylineOptions) createPathOptions(polygonState.polylineShapeState);
                polygonOptions.setShapeOptions(polylinePathOptions);
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
            if (polygonState.showArea != null) {
                polygonOptions.setShowArea(polygonState.showArea);
            }
            buttonOptions.setPolygon(polygonOptions);
        }

        DrawRectangleState rectangleState = getState().rectangleState;
        DrawRectangleOptions rectangleOptions = DrawRectangleOptions.create();
        if (rectangleState == null) {
            buttonOptions.setRectangleVisibility(false);
        } else {
            if (rectangleState.shapeState != null) {
                PathOptions rectanglePathOptions = createPathOptions(rectangleState.shapeState);
                rectangleOptions.setShapeOptions(rectanglePathOptions);
            }
            if (rectangleState.repeatMode != null) {
                rectangleOptions.setRepeatMode(rectangleState.repeatMode);
            }
            buttonOptions.setRectangle(rectangleOptions);
        }

        DrawCircleState circleState = getState().circleState;
        DrawCircleOptions circleOptions = DrawCircleOptions.create();
        if (circleState == null) {
            buttonOptions.setCircleVisibility(false);
        } else {
            if (circleState.shapeState != null) {
                PathOptions circlePathOptions = createPathOptions(circleState.shapeState);
                circleOptions.setShapeOptions(circlePathOptions);
            }
            if (circleState.repeatMode != null) {
                circleOptions.setRepeatMode(circleState.repeatMode);
            }
            buttonOptions.setCircle(circleOptions);
        }

        DrawMarkerState markerState = getState().markerState;
        DrawMarkerOptions markerOptions = DrawMarkerOptions.create();
        if (markerState == null) {
            buttonOptions.setMarkerVisibility(false);
        } else {
            DrawIconState drawIconState = markerState.iconState;
            if (drawIconState != null) {
                IconOptions iconOptions = IconOptions.create();
                if (drawIconState.iconUrl != null) {
                    iconOptions.setIconUrl(drawIconState.iconUrl);
                }
                if (drawIconState.iconSize != null) {
                    iconOptions.setIconSize(Point.create(
                            drawIconState.iconSize.getLat(),
                            drawIconState.iconSize.getLon()));
                }
                if (drawIconState.iconAnchor != null) {
                    iconOptions.setIconAnchor(Point.create(
                            drawIconState.iconAnchor.getLat(),
                            drawIconState.iconAnchor.getLon()));
                }
                Icon icon = Icon.create(iconOptions);
                markerOptions.setIcon(icon);
            }
            if (markerState.zIndexOffset != null) {
                markerOptions.setZIndexOffset(markerState.zIndexOffset);
            }
            if (markerState.repeatMode != null) {
                markerOptions.setRepeatMode(markerState.repeatMode);
            }
            buttonOptions.setMarker(markerOptions);
        }


        /*if (getState().drawVisibleButtons != null) {
            buttonOptions.setVisibleButtons(getState().drawVisibleButtons);
        }*/
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

    protected PathOptions createPathOptions(DrawShapeState state) {
        PathOptions options = PathOptions.create();
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
        if (state.fillRule != null) {
            //TODO: add fill rule to PathOptions
        }
        if (state.dashArray != null) {
            options.setDashArray(state.dashArray);
        }
        if (state.lineCap != null) {
            options.setLineCap(state.lineCap);
        }
        if (state.lineJoin != null) {
            options.setLineJoin(state.lineJoin);
        }
        if (state.clickable != null) {
            options.setClickable(state.clickable);
        }
        if (state.pointerEvents != null) {
            options.setPointerEvents(state.pointerEvents);
        }
        if (state.className != null) {
            options.setClassName(state.className);
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
