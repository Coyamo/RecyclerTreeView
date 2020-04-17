package coyamo.ui.recyclertreeview;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import coyamo.ui.recyclertreeview.bean.Dir;
import coyamo.ui.recyclertreeview.bean.File;
import coyamo.ui.recyclertreeview.viewbinder.DirectoryNodeBinder;
import coyamo.ui.recyclertreeview.viewbinder.FileNodeBinder;
import tellh.com.recyclertreeview_lib.LayoutItemType;
import tellh.com.recyclertreeview_lib.TreeNode;
import tellh.com.recyclertreeview_lib.TreeViewAdapter;

public class MainActivity extends AppCompatActivity {

    private RecyclerView rv;
    private
    TreeViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(MainActivity.this, "请求文件读写权限被拒绝！", Toast.LENGTH_LONG).show();
                        return;
                    }
                }
                initView();
                initFiles();
                break;
        }
    }

    private void sort(List<java.io.File> list) {
        Collections.sort(list);
    }

    private void addChild(TreeNode parent, List<java.io.File> files) {
        sort(files);
        for (java.io.File f : files) {
            if (f.isDirectory()) {
                TreeNode<Dir> dir = new TreeNode<>(new Dir(f.getName(), f.getAbsolutePath()));
                parent.addChild(dir);
            } else {
                TreeNode<File> file = new TreeNode<>(new File(f.getName(), f.getAbsolutePath()));
                parent.addChild(file);
            }
        }
    }

    private void initFiles() {
        List<TreeNode> nodes = new ArrayList<>();
        List<java.io.File> files = Arrays.asList(Environment.getExternalStorageDirectory().listFiles());
        sort(files);
        for (java.io.File f : files) {
            if (f.isDirectory()) {
                TreeNode file = new TreeNode(new Dir(f.getName(), f.getAbsolutePath()));
                addChild(file, Arrays.asList(f.listFiles()));
                nodes.add(file);
            } else {
                TreeNode file = new TreeNode(new File(f.getName(), f.getAbsolutePath()));
                nodes.add(file);
            }

        }

        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TreeViewAdapter(nodes, Arrays.asList(new FileNodeBinder(), new DirectoryNodeBinder()));
        // whether collapse child nodes when their parent node was close.
//        adapter.ifCollapseChildWhileCollapseParent(true);
        adapter.setOnTreeNodeListener(new TreeViewAdapter.OnTreeNodeListener() {
            @Override
            public boolean onClick(TreeNode node, RecyclerView.ViewHolder holder) {

                List<TreeNode> childs = node.getChildList();
                LayoutItemType type = node.getContent();

                if (!node.isExpand()) {
                    if (type instanceof Dir) {
                        //如果有新文件添加 这样可以就可以加载出来 而不是使用上一次的记录
                        //缺点 内部打开过的节点会是关闭状态.
                        childs.clear();
                        java.io.File f = new java.io.File(((Dir) type).path);
                        List<java.io.File> list = Arrays.asList(f.listFiles());
                        addChild(node, list);
                    }
                }
                if (type instanceof Dir) {
                    onToggle(!node.isExpand(), holder);
                }

                return false;
            }

            @Override
            public void onToggle(boolean isExpand, RecyclerView.ViewHolder holder) {
                DirectoryNodeBinder.ViewHolder dirViewHolder = (DirectoryNodeBinder.ViewHolder) holder;
                final ImageView ivArrow = dirViewHolder.getIvArrow();
                int rotateDegree = isExpand ? 90 : -90;
                ivArrow.animate().rotationBy(rotateDegree)
                        .start();
            }
        });
        rv.setAdapter(adapter);
    }

    private void initView() {
        rv = (RecyclerView) findViewById(R.id.rv);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.id_action_close_all:
                adapter.collapseAll();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
