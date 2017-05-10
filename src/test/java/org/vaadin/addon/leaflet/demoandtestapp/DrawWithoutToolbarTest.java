package org.vaadin.addon.leaflet.demoandtestapp;

import com.vaadin.ui.*;
import org.vaadin.addon.leaflet.LFeatureGroup;
import org.vaadin.addon.leaflet.LLayerGroup;
import org.vaadin.addon.leaflet.LMap;
import org.vaadin.addon.leaflet.LTileLayer;
import org.vaadin.addon.leaflet.draw.AbstracLDrawFeature;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureDrawnEvent;
import org.vaadin.addon.leaflet.draw.LDraw.FeatureDrawnListener;
import org.vaadin.addon.leaflet.draw.LDrawCircle;
import org.vaadin.addon.leaflet.draw.LDrawMarker;
import org.vaadin.addon.leaflet.draw.LDrawPolygon;
import org.vaadin.addon.leaflet.draw.LDrawPolyline;
import org.vaadin.addon.leaflet.draw.LDrawRectangle;

import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import org.vaadin.addonhelpers.AbstractTest;

import java.util.Arrays;
import java.util.List;

public class DrawWithoutToolbarTest extends AbstractTest {

	@Override
	public String getDescription() {
		return "Test leaflet draw";
	}

	private LMap leafletMap;
	private LLayerGroup group;

	@Override
	public Component getTestComponent() {

		leafletMap = new LMap();
		leafletMap.setCenter(0, 0);
		leafletMap.setZoomLevel(5);
		leafletMap.addLayer(new LTileLayer(
				"http://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png"));

		group = new LFeatureGroup();

		leafletMap.addLayer(group);
		leafletMap.setSizeFull();

		HorizontalLayout testComponent = new HorizontalLayout();

		testComponent.addComponent(leafletMap);
		testComponent.addComponent(getControls());

		testComponent.setSizeFull();

		return testComponent;
	}

	private Component getControls() {

		List<Class> dataList = Arrays.asList(LDrawPolyline.class, LDrawPolygon.class, LDrawRectangle.class,
				LDrawMarker.class, LDrawCircle.class);
		final RadioButtonGroup<Class> radioButtonGroup = new RadioButtonGroup<>("Draw Shape");
		radioButtonGroup.setItems(dataList);
		radioButtonGroup.setItemCaptionGenerator(new ItemCaptionGenerator<Class>() {
			@Override
			public String apply(Class aClass) {
				return aClass.getSimpleName();
			}
		});

		Button button = new Button("Draw", new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				Class<? extends AbstracLDrawFeature> value = (Class<? extends AbstracLDrawFeature>) radioButtonGroup.getValue();
				try {
					AbstracLDrawFeature newInstance = value.newInstance();
					newInstance.addTo(leafletMap);
					newInstance.addFeatureDrawnListener(new FeatureDrawnListener() {

						@Override
						public void featureDrawn(FeatureDrawnEvent event) {
							group.addComponent(event.getDrawnFeature());
						}
					});
				} catch (InstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});

		VerticalLayout controlsLayout = new VerticalLayout(radioButtonGroup, button);
		controlsLayout.setSizeFull();

		return controlsLayout;
	}
}
