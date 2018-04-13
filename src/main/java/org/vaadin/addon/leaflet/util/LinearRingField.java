package org.vaadin.addon.leaflet.util;

import com.vividsolutions.jts.geom.LinearRing;
import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureDrawnEvent;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureDrawnListener;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureModifiedEvent;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureModifiedListener;
import org.vaadin.addon.leaflet.draw.LDrawPolyline;
import org.vaadin.addon.leaflet.draw.LEditing;
import org.vaadin.addon.leaflet.shared.Bounds;
import org.vaadin.addon.leaflet.shared.Point;

public class LinearRingField extends AbstractJTSField<LinearRing> {

	private LPolyline lPolyline;
	private LDrawPolyline lDrawPolyline;

	public LinearRingField() {
	}

	public LinearRingField(String caption) {
		this();
		setCaption(caption);
	}

	LEditing editing = null;

	protected void prepareEditing(boolean userOriginatedValueChange) {
		if (lPolyline == null) {
			lPolyline = new LPolyline();
			map.addLayer(lPolyline);
		}
		Point[] lPointArray = JTSUtil.toLeafletPointArray(getCrsTranslator()
				.toPresentation(getValue()));
		lPolyline.setPoints(lPointArray);
		editing = new LEditing(lPolyline);
		editing.addFeatureModifiedListener(new FeatureModifiedListener() {

			@Override
			public void featureModified(FeatureModifiedEvent event) {
				setValue(getCrsTranslator().toModel(
						JTSUtil.toLinearRing(lPolyline)));
			}
		});
		map.zoomToExtent(new Bounds(lPolyline.getPoints()));
	}

	protected void prepareDrawing() {
	   	if(lDrawPolyline != null) {
	   	   lDrawPolyline.remove();
	   	}
	   	if(lPolyline != null) {
	   	   map.removeLayer(lPolyline);
	   	   lPolyline = null;
	   	}
		lDrawPolyline = new LDrawPolyline();
		lDrawPolyline.addFeatureDrawnListener(new FeatureDrawnListener() {

			@Override
			public void featureDrawn(FeatureDrawnEvent event) {
				// TODO fill Vaadin bug report: exception from here has horrible
				// stack trace (non informative), even more horrible than the
				// usual that has some irrelevant stuff in front
				setValue(getCrsTranslator()
						.toModel(
								JTSUtil.toLinearRing((LPolyline) event
										.getDrawnFeature())));
			}
		});

	}

	@Override
	protected void prepareViewing() {
		if (editing != null) {
			editing.remove();
		}
	}

}
