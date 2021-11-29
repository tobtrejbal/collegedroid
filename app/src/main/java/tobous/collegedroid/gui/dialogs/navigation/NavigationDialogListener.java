package tobous.collegedroid.gui.dialogs.navigation;

import tobous.collegedroid.functions.graphs.encapsulation.Vertex;
import tobous.collegedroid.functions.plans.encapsulation.Building;

/**
 * Created by Tob on 23. 12. 2015.
 */
public interface NavigationDialogListener {

    public void navigate(Building building, Vertex source, Vertex target);

}
