package co.kr.itforone.peertalk.contact_pkg;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import co.kr.itforone.peertalk.R;
import co.kr.itforone.peertalk.databinding.ActivityNumlistBinding;

public class ListActivity extends AppCompatActivity {

    private ActivityNumlistBinding activityListBinding;
    String tv_total ="";
    ContactListAdapter contactListAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        activityListBinding = DataBindingUtil.setContentView(this, R.layout.activity_numlist);

        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        String[] projection = new String[]{
                ContactsContract.CommonDataKinds.Phone.NUMBER,
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.PHOTO_THUMBNAIL_URI,
                ContactsContract.CommonDataKinds.Phone._ID,
        };

        String [] selectionArgs = null;
        String sortOrder = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME+" COLLATE LOCALIZED ASC";
        ArrayList<itemModel> list = new ArrayList<itemModel>();
        ArrayList<String> list_names = new ArrayList<String>();
        ArrayList<String> list_numbers = new ArrayList<String>();

        String before_name="", before_number="";
        Cursor cursor = getApplicationContext().getContentResolver().query(uri,projection,null,selectionArgs,sortOrder);
        if(cursor.moveToFirst()){
            do {
                String name, number, struri;
                number = cursor.getString(0);
                name = cursor.getString(1);
                struri = cursor.getString(2);

                if(number!=null && !number.isEmpty())
                    number = number.replace("-","");

                if(before_number.equals(number) && before_name.equals(name)){
                    continue;
                }

                before_number = number;
                before_name = name;

                list.add(new itemModel(number,name,struri));
                list_names.add(name);
                list_numbers.add(number);
                /*tv_total+= cursor.getString(0);
                tv_total+= "\n";
                tv_total+= cursor.getString(1);
                tv_total+= "\n";
                tv_total+= cursor.getString(2);
                tv_total+= "\n";*/
                Log.d("cursor_1",number);
                Log.d("cursor_2",name);



               // Log.d("cursor_3",struri);

            }while (cursor.moveToNext());
        }
        else{
            Toast.makeText(this, "fail", Toast.LENGTH_SHORT).show();
        }



        LinearLayoutManager manager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        activityListBinding.listview.setLayoutManager(manager);
        contactListAdapter = new ContactListAdapter(list);
        activityListBinding.listview.setAdapter(contactListAdapter);

    }

    public void chkall(View view){

        contactListAdapter.selectAll();

    }
    public void list_submit (View view){

        ArrayList<itemModel> checkedlist = contactListAdapter.getslclist();

        ArrayList<String> namelist = new ArrayList<String>();
        ArrayList<String> numberlist = new ArrayList<String>();

        for (itemModel item:checkedlist) {

            namelist.add(item.getName());
            numberlist.add(item.getNumber());

        }

        String temp_names = TextUtils.join("|", namelist);
        String temp_numbers = TextUtils.join("|", numberlist);

        Intent intent = new Intent();
        intent.putExtra("size", checkedlist.size());
        intent.putExtra("names", temp_names);
        intent.putExtra("numbers", temp_numbers);

        setResult(RESULT_OK,intent);
        finish();
    }

}