package org.vaadin.addon.leaflet.draw.shared;

import com.vaadin.shared.communication.SharedState;

public class DrawPolylineState extends SharedState {

    public Boolean allowIntersection;
    public DrawErrorState drawErrorState;
    public Integer guidelineDistance;
    public DrawPolylineShapeState polylineShapeState;
    public Boolean metric;
    public Integer zIndexOffset;
    public Boolean repeatMode;

}
