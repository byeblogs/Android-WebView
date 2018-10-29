package id.tokped.bye.hello.model;

import android.graphics.drawable.Drawable;

/**
 * Created by webster on 9/7/16.
 */
public class PortalModel {

    private String portal_name;
    private String portal_init;
    private String portal_image;
    private int type;

    public String getPortal_name() {
        return portal_name;
    }

    public void setPortal_name(String portal_name) {
        this.portal_name = portal_name;
    }

    public String getPortal_init() {
        return portal_init;
    }

    public void setPortal_init(String portal_init) {
        this.portal_init = portal_init;
    }

    public String getPortal_image() {
        return portal_image;
    }

    public void setPortal_image(String portal_image) {
        this.portal_image = portal_image;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}