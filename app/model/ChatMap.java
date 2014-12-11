package model;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashMap;

/**
 * Created by Mani on 12/9/2014.
 * <p>
 * Created and overrided in order to execute any specific process upon the connection and disconnection of the client.
 */
public class ChatMap<String, EventSource> extends HashMap {
    private PropertyChangeSupport cSupport = new PropertyChangeSupport(this);


    public void addPropertyChangeListener(PropertyChangeListener cListener) {
        cSupport.addPropertyChangeListener(cListener);
    }

    @Override
    public Object remove(Object key) {

        cSupport.firePropertyChange("mapContents", null, null);
        return super.remove(key);
    }

    @Override
    public Object put(Object key, Object value) {
        cSupport.firePropertyChange("mapContents", null, null);
        return super.put(key, value);
    }

}
