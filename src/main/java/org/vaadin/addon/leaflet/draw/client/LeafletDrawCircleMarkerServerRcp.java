package org.vaadin.addon.leaflet.draw.client;

import com.vaadin.shared.communication.ServerRpc;
import org.vaadin.addon.leaflet.shared.Point;

public interface LeafletDrawCircleMarkerServerRcp extends ServerRpc {

    public void circleMarkerAdded(Point latLng, double radius);
}
