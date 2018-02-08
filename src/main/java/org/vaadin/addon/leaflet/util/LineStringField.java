package org.vaadin.addon.leaflet.util;

import org.vaadin.addon.leaflet.LPolyline;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureDrawnEvent;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureDrawnListener;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureModifiedEvent;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureModifiedListener;
import org.vaadin.addon.leaflet.draw.LDrawPolyline;
import org.vaadin.addon.leaflet.draw.LEditing;
import org.vaadin.addon.leaflet.shared.Bounds;
import org.vaadin.addon.leaflet.shared.Point;

import com.vividsolutions.jts.geom.LineString;

public class LineStringField extends AbstractJTSField<LineString> {

	private LPolyline lPolyline;

	public LineStringField() {
	}

	public LineStringField(String caption) {
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
						JTSUtil.toLineString(lPolyline)));
			}
		});
		map.zoomToExtent(new Bounds(lPolyline.getPoints()));
	}

	protected void prepareDrawing() {
		LDrawPolyline drawPolyline = new LDrawPolyline(map);
		drawPolyline.addFeatureDrawnListener(new FeatureDrawnListener() {

			@Override
			public void featureDrawn(FeatureDrawnEvent event) {
				setValue(getCrsTranslator().toModel(
						JTSUtil.toLineString((LPolyline) event
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
