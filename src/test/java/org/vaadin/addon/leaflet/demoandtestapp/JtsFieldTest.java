package org.vaadin.addon.leaflet.demoandtestapp;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Date;

import com.vaadin.data.ValidationException;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.ui.*;
import org.vaadin.addon.leaflet.util.LineStringField;
import org.vaadin.addon.leaflet.util.LinearRingField;
import org.vaadin.addon.leaflet.util.PointField;

import com.vaadin.data.Binder;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vividsolutions.jts.geom.LineString;
import com.vividsolutions.jts.geom.LinearRing;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.Polygon;
import org.vaadin.addonhelpers.AbstractTest;

public class JtsFieldTest extends AbstractTest {

	public static class JtsPojo {
		private String name;
		private LocalDate date;
		private Point point;
		private LineString lineString;
		private LinearRing linearRing;
		private Polygon polygon;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public LocalDate getDate() {
			return date;
		}

		public void setDate(LocalDate date) {
			this.date = date;
		}

		public Point getPoint() {
			return point;
		}

		public void setPoint(Point point) {
			this.point = point;
		}

		public LineString getLineString() {
			return lineString;
		}

		public void setLineString(LineString lineString) {
			this.lineString = lineString;
		}

		public Polygon getPolygon() {
			return polygon;
		}

		public void setPolygon(Polygon polygon) {
			this.polygon = polygon;
		}

		@Override
		public String toString() {
			return "JtsPojo [\n name=" + name + ",\n date=" + date
					+ ",\n point=" + point + ",\n lineString=" + lineString
					+ ",\n linearRing=" + linearRing + ",\n polygon=" + polygon
					+ "\n]";
		}

		public LinearRing getLinearRing() {
			return linearRing;
		}

		public void setLinearRing(LinearRing linearRing) {
			this.linearRing = linearRing;
		}
	}

	@Override
	public String getDescription() {
		return "A test for JTS Field implementations";
	}

	private JtsPojo pojo = new JtsPojo();
	private Label display = new Label();

	private TextField name = new TextField("Name");
	private DateField date = new DateField("Date");
	private PointField point = new PointField("Point");
	private LineStringField lineString = new LineStringField("LineStringField");
	private LinearRingField linearRing = new LinearRingField("LinearRingField");

	// private Polygon polygon;

	@Override
	public Component getTestComponent() {
		content.setMargin(true);

		display.setContentMode(ContentMode.PREFORMATTED);
		display.setCaption("Pojo state:");
		display.setValue(pojo.toString());

		HorizontalLayout horizontalLayout = new HorizontalLayout();
		horizontalLayout.setSizeFull();

		VerticalLayout editorform = new VerticalLayout();
		editorform.setSizeFull();

		editorform.setSpacing(true);
		editorform.setCaption("Edit JTS pojo:");

		TabSheet jtsFields = new TabSheet(point, lineString, linearRing);
		jtsFields.setCaption("JTS fiels:");
		jtsFields.setSizeFull();
		editorform.addComponents(new HorizontalLayout(name, date), jtsFields
		// ,polygon
				);
		editorform.setExpandRatio(jtsFields, 1);

		// TODO switch to helper in Vaadin when available http://dev.vaadin.com/ticket/13068
		final Binder<JtsPojo> beanBinder = new Binder<JtsPojo>(
				JtsPojo.class);
		beanBinder.readBean(pojo);
		beanBinder.bindInstanceFields(this);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.addComponent(new Button("Save", new ClickListener() {

			@Override
			public void buttonClick(ClickEvent event) {
				try {
					beanBinder.writeBean(pojo);
					display.setValue(pojo.toString());
				} catch (ValidationException e) {
					System.err.println("Validation errors:" + Arrays.toString(e.getBeanValidationErrors().toArray()));
				}
			}
		}));

		CheckBox roCheckBox = new CheckBox("Read only", false);
		roCheckBox.addValueChangeListener(event -> beanBinder.setReadOnly(event.getValue()));
		buttonLayout.addComponent(roCheckBox);

		editorform.addComponent(buttonLayout);

		horizontalLayout.addComponents(editorform, display);
		horizontalLayout.setExpandRatio(editorform, 1);
		horizontalLayout.setExpandRatio(display, 1);

		return horizontalLayout;
	}

}
