package coyamo.ui.recyclertreeview.bean;


import coyamo.ui.recyclertreeview.R;
import tellh.com.recyclertreeview_lib.LayoutItemType;

/**
 * Created by tlh on 2016/10/1 :)
 */

public class Dir implements LayoutItemType {
    public String dirName;
    public String path;

    public Dir(String dirName, String path) {
        this.dirName = dirName;
        this.path = path;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_dir;
    }
}
