package org.vaadin.addon.leaflet.draw.client;

import com.google.gwt.core.client.Scheduler;
import com.vaadin.client.ServerConnector;
import com.vaadin.client.communication.RpcProxy;
import com.vaadin.client.communication.StateChangeEvent;
import org.peimari.gleaflet.client.draw.DrawFeature;
import org.peimari.gleaflet.client.draw.DrawPolygon;
import org.peimari.gleaflet.client.draw.DrawPolygonOptions;
import org.vaadin.addon.leaflet.draw.LDrawPolygon;

import com.vaadin.shared.ui.Connect;
import org.peimari.gleaflet.client.LatLng;
import org.peimari.gleaflet.client.Polygon;
import org.peimari.gleaflet.client.draw.LayerCreatedEvent;
import org.peimari.gleaflet.client.draw.LayerCreatedListener;
import org.vaadin.addon.leaflet.client.U;

@Connect(LDrawPolygon.class)
public class LeafletDrawPolygonConnector extends AbstractLeafletDrawFeatureConnector {

    private LeafletDrawPolylineServerRcp rpc = RpcProxy.create(
			LeafletDrawPolylineServerRcp.class, this);
	
	@Override
	protected void extend(final ServerConnector target) {
	}

	@Override
	public void onStateChanged(StateChangeEvent stateChangeEvent) {
		Scheduler.get().scheduleDeferred(new Scheduler.ScheduledCommand() {


			@Override
			public void execute() {
				drawFeature = instantiateDrawFeature();
				listenerRegistration = getMap().addLayerCreatedListener(new LayerCreatedListener() {
					
					@Override
					public void onCreate(LayerCreatedEvent event) {
						Polygon layer = (Polygon) event.getLayer();
                        final LatLng[] latLngs = layer.getExteriorRing();
						rpc.polylineAdded(U.toPointArray(latLngs));
						getMap().removeListener(listenerRegistration);
						listenerRegistration = null;
					}
				});

				drawFeature.enable();
			}

		});
	}
	
	protected DrawFeature instantiateDrawFeature() {
        return DrawPolygon.create(getMap(), DrawPolygonOptions.create());
	}

}
