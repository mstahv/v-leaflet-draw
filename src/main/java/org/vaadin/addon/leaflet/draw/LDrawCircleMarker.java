package org.vaadin.addon.leaflet.draw;

import org.vaadin.addon.leaflet.LCircleMarker;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureDrawnEvent;
import org.vaadin.addon.leaflet.draw.client.LeafletDrawCircleMarkerServerRcp;
import org.vaadin.addon.leaflet.shared.Point;

/**
 * Extension to initiate drawing of a Circle Marker on a map.
 */
public class LDrawCircleMarker extends AbstracLDrawFeature {

    public LDrawCircleMarker(LMap map) {
        super(map);
        registerRpc();
    }

    public LDrawCircleMarker() {
        registerRpc();
    }

    protected void registerRpc() {
        registerRpc(new LeafletDrawCircleMarkerServerRcp() {
            @Override
            public void circleMarkerAdded(Point latLng, double radius) {
                LCircleMarker circleMarker = new LCircleMarker(latLng, radius);
                fireEvent(new FeatureDrawnEvent(LDrawCircleMarker.this, circleMarker));
                remove();
            }
        });
    }

}
