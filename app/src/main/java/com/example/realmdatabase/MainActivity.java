package com.example.realmdatabase;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import io.realm.Realm;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Realm realm;
    Button insert, delete, update, read;
    TextView show_data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        realm = Realm.getDefaultInstance();

        insert = findViewById(R.id.btn_insert);
        update = findViewById(R.id.btn_update);
        read = findViewById(R.id.btn_read);
        delete = findViewById(R.id.btn_delete);
        show_data = findViewById(R.id.show_data);

        insert.setOnClickListener(this);
        update.setOnClickListener(this);
        read.setOnClickListener(this);
        delete.setOnClickListener(this);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_insert:
                InsertData();
                break;
            case R.id.btn_read:
                ReadData();
                break;
            case R.id.btn_update:
                UpdateData();
                break;
            case R.id.btn_delete:
                DeleteData();
                break;
        }
    }

    private void DeleteData() {
        final AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.delete_dialog,null);
        al.setView(view);

        final EditText edt_id = view.findViewById(R.id.id);
        TextView tv_title = view.findViewById(R.id.title_dialog);
        Button btn_delete_dialog = view.findViewById(R.id.btn_delete_dialog);
        tv_title.setText("Delete User");
        btn_delete_dialog.setText("Delete");
        final AlertDialog alertDialog = al.show();

        btn_delete_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                long id = Long.parseLong(edt_id.getText().toString());
                User user = realm.where(User.class).equalTo("id", id).findFirst();
                if(user != null){
                    realm.executeTransaction(new Realm.Transaction() {
                        @Override
                        public void execute(Realm realm) {
                            user.deleteFromRealm();
                        }
                    });
                }
                else{
                    Toast.makeText(MainActivity.this, "User ID not found!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void UpdateData() {
        final AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.delete_dialog,null);
        al.setView(view);

        final EditText edt_id = view.findViewById(R.id.id);
        TextView tv_title = view.findViewById(R.id.title_dialog);
        Button btn_update_dialog = view.findViewById(R.id.btn_delete_dialog);
        tv_title.setText("Update User");
        btn_update_dialog.setText("Update");
        final AlertDialog alertDialog = al.show();

        btn_update_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                long id = Long.parseLong(edt_id.getText().toString());
                User user = realm.where(User.class).equalTo("id", id).findFirst();
                if(user != null)
                    ShowUpdateDialog(user);
                else
                    Toast.makeText(MainActivity.this, "User ID not found!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void ShowUpdateDialog(User user) {
        final AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.insert_dialog,null);
        al.setView(view);
        final EditText edt_name = view.findViewById(R.id.name);
        final EditText edt_age = view.findViewById(R.id.age);
        Button btn_save = view.findViewById(R.id.btn_save);
        edt_name.setText(user.getName());
        edt_age.setText(String.valueOf(user.getAge()));
        final AlertDialog alertDialog = al.show();
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        user.setName(edt_name.getText().toString());
                        user.setAge(Integer.parseInt(edt_age.getText().toString()));
                        realm.copyToRealmOrUpdate(user);
                    }
                });
            }
        });
    }

    private void ReadData() {
        List<User> userList = realm.where(User.class).findAll();
        show_data.setText("");
        for(int i = 0; i < userList.size(); i++){
            show_data.append("ID : " + userList.get(i).getId()
                    + "\tName : "+userList.get(i).getName()
                    +"\tAge : "+userList.get(i).getAge() + "\n");
        }
    }

    private void InsertData() {
        final AlertDialog.Builder al = new AlertDialog.Builder(MainActivity.this);
        View view = getLayoutInflater().inflate(R.layout.insert_dialog,null);
        al.setView(view);

        final EditText edt_name = view.findViewById(R.id.name);
        final EditText edt_age = view.findViewById(R.id.age);
        Button btn_save = view.findViewById(R.id.btn_save);
        final AlertDialog alertDialog = al.show();

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                User user = new User();

                Number current_id = realm.where(User.class).max("id");
                long nextId;
                if(current_id == null){
                    nextId = 1;
                }
                else{
                    nextId = current_id.intValue() + 1;
                }

                user.setId(nextId);
                user.setName(edt_name.getText().toString());
                user.setAge(Integer.parseInt(edt_age.getText().toString()));

                realm.executeTransaction(new Realm.Transaction() {
                    @Override
                    public void execute(Realm realm) {
                        realm.copyToRealm(user);
                    }
                });

            }
        });

    }
}