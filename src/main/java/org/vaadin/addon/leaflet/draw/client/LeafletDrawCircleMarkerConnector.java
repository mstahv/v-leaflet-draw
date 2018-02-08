package org.vaadin.addon.leaflet.draw.client;

import com.google.gwt.core.client.Scheduler;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import com.vaadin.shared.ui.Connect;
import org.peimari.gleaflet.client.CircleMarker;
import org.peimari.gleaflet.client.draw.*;
import org.vaadin.addon.leaflet.client.U;
import org.vaadin.addon.leaflet.draw.LDrawCircleMarker;

import com.google.gwt.core.client.Scheduler.ScheduledCommand;

@Connect(LDrawCircleMarker.class)
public class LeafletDrawCircleMarkerConnector extends AbstractLeafletDrawFeatureConnector {

    private LeafletDrawCircleMarkerServerRcp rpc = RpcProxy.create(
            LeafletDrawCircleMarkerServerRcp.class, this);

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {

            @Override
            public void execute() {

                drawFeature = instantiateDrawFeature();
                listenerRegistration = getMap().addLayerCreatedListener(new LayerCreatedListener() {
                    @Override
                    public void onCreate(LayerCreatedEvent event) {
                        CircleMarker layer = (CircleMarker) event.getLayer();
                        rpc.circleMarkerAdded(U.toPoint(layer.getLatLng()), layer.getRadius());
                        getMap().removeListener(listenerRegistration);
                        listenerRegistration = null;
                    }
                });

                drawFeature.enable();
            }
        });

    }

    protected DrawFeature instantiateDrawFeature() {
        return DrawCircleMarker.create(getMap(), DrawCircleMarkerOptions.create());
    }
}
