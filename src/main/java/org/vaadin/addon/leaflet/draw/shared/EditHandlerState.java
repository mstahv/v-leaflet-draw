package org.vaadin.addon.leaflet.draw.shared;

import com.vaadin.shared.communication.SharedState;

public class EditHandlerState extends SharedState {

    public boolean visible = true;
    public SelectedPathState selectedPathState = new SelectedPathState();
}
