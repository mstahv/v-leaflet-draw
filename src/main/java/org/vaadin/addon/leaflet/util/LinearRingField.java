package org.vaadin.addon.leaflet.util;

import org.vaadin.addon.leaflet.LPolygon;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureDrawnEvent;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureDrawnListener;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureModifiedEvent;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureModifiedListener;
import org.vaadin.addon.leaflet.draw.LDrawPolygon;
import org.vaadin.addon.leaflet.draw.LEditing;
import org.vaadin.addon.leaflet.shared.Bounds;
import org.vaadin.addon.leaflet.shared.Point;

import com.vividsolutions.jts.geom.LinearRing;

public class LinearRingField extends AbstractJTSField<LinearRing> {

	private LPolygon lPolygon;
        private LDrawPolygon drawPolyline;

	public LinearRingField() {
	}

	public LinearRingField(String caption) {
		this();
		setCaption(caption);
	}

	LEditing editing = null;

	protected void prepareEditing() {
		if (lPolygon == null) {
			lPolygon = new LPolygon();
			map.addLayer(lPolygon);
		}
		Point[] lPointArray = JTSUtil.toLeafletPointArray(getCrsTranslator()
				.toPresentation(getValue()));
		lPolygon.setPoints(lPointArray);
		editing = new LEditing(lPolygon);
		editing.addFeatureModifiedListener(new FeatureModifiedListener() {

			@Override
			public void featureModified(FeatureModifiedEvent event) {
				setValue(getCrsTranslator().toModel(
						JTSUtil.toLinearRing(lPolygon)));
			}
		});
		map.zoomToExtent(new Bounds(lPolygon.getPoints()));
	}

	protected void prepareDrawing() {
	   	if(drawPolyline != null) {
	   	   drawPolyline.remove();
	   	}
	   	if(lPolygon != null) {
	   	   map.removeLayer(lPolygon);
	   	   lPolygon = null;
	   	}
		drawPolyline = new LDrawPolygon(map);
		drawPolyline.addFeatureDrawnListener(new FeatureDrawnListener() {

			@Override
			public void featureDrawn(FeatureDrawnEvent event) {
				// TODO fill Vaadin bug report: exception from here has horrible
				// stack trace (non informative), even more horrible than the
				// usual that has some irrelevant stuff in front
				setValue(getCrsTranslator()
						.toModel(
								JTSUtil.toLinearRing((LPolygon) event
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
