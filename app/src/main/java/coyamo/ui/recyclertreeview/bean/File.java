package coyamo.ui.recyclertreeview.bean;

import coyamo.ui.recyclertreeview.R;
import tellh.com.recyclertreeview_lib.LayoutItemType;

/**
 * Created by tlh on 2016/10/1 :)
 */

public class File implements LayoutItemType {
    public String fileName;
    public String path;

    public File(String fileName, String path) {
        this.fileName = fileName;
        this.path = path;
    }

    @Override
    public int getLayoutId() {
        return R.layout.item_file;
    }
}
