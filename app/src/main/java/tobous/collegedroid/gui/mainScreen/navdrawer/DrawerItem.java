package tobous.collegedroid.gui.mainScreen.navdrawer;

/**
 * Created by Tobous on 5. 2. 2015.
 */
public class DrawerItem {

    private int icon;
    private int type;
    private String title;

    private boolean isGroupHeader = false;

    public DrawerItem(String title) {
        this(-1, -1,title);
        isGroupHeader = true;
    }
    public DrawerItem(int icon, int type, String title) {
        super();
        this.icon = icon;
        this.type = type;
        this.title = title;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isGroupHeader() {
        return isGroupHeader;
    }

    public void setGroupHeader(boolean isGroupHeader) {
        this.isGroupHeader = isGroupHeader;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }
}
