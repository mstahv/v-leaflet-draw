package org.vaadin.addon.leaflet.draw.client;

import com.google.gwt.core.client.EntryPoint;
import org.peimari.gleaflet.client.resources.LeafletDrawResourceInjector;

public class EagerDrawLoader implements EntryPoint {

	@Override
	public void onModuleLoad() {
		LeafletDrawResourceInjector.ensureInjected();
	}

}
