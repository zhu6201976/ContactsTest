package com.zhu6201976.contactstest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * android 6.0之后查询系统联系人信息
 * 1.模拟器先创建几条联系人信息
 * 2.加入运行时权限
 */
public class MainActivity extends AppCompatActivity {

    private ArrayList<String> contactsList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 1.初始化控件,初始化空集合,绑定数据适配器
        ListView lv = (ListView) findViewById(R.id.lv);
        contactsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, contactsList);
        lv.setAdapter(adapter);

        // 2.检查自身权限
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) ==
                PackageManager.PERMISSION_GRANTED) {
            // 已经授权,直接读取系统联系人信息
            this.readContacts();
        } else {// 没有授权,请求用户授权
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_CONTACTS}, 1);
        }

    }

    // 请求用户授权结果
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // 请求通过,直接读取系统联系人
                    this.readContacts();
                } else {// 请求未通过
                    Toast.makeText(this, "you denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    /**
     * 读取系统联系人信息
     */
    private void readContacts() {
        // 通过内容解析者,查询所有系统联系人
        Cursor cursor = getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                null, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String displayName = cursor.getString(cursor.getColumnIndex(ContactsContract.
                        CommonDataKinds.Phone.DISPLAY_NAME));
                String number = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.
                        Phone.NUMBER));
                contactsList.add(displayName + "\n" + number);
            }
            cursor.close();
            // 刷新界面
            adapter.notifyDataSetChanged();
        }
    }
}
