package com.taptm.shurikus.realmtutorial;

import android.app.Dialog;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.taptm.shurikus.realmtutorial.model.Teacher;

import io.realm.Realm;
import io.realm.RealmResults;

public class MainActivity extends AppCompatActivity {

    private Realm mRealm;
    private TeacherAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog();
            }
        });

        mRealm = Realm.getDefaultInstance();

        mAdapter = new TeacherAdapter(loadTeacher());
        ListView listView = (ListView) findViewById(R.id.list_teachers);
        listView.setAdapter(mAdapter);
    }

    private void showDialog(){
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.dialog_add_teacher);
        dialog.setTitle(R.string.text_add_new_teacher);

        Button cancelButton = (Button) dialog.findViewById(R.id.button_cancel);
        Button okButton = (Button) dialog.findViewById(R.id.button_ok);
        final EditText nameText = (EditText) dialog.findViewById(R.id.text_name);
        final EditText surnameText = (EditText) dialog.findViewById(R.id.text_surname);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = nameText.getText().toString();
                String surname = surnameText.getText().toString();

                if(name.equals("")
                        && surname.equals("")){
                    Toast.makeText(getApplicationContext(), R.string.msg_fill_all_fiekds,Toast.LENGTH_SHORT).show();
                    return;
                }

                saveTeacher(name, surname);
                dialog.dismiss();

            }
        });
        dialog.show();
    }

    private void saveTeacher(final String name, final String surname){
        mRealm.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                Number currentIdNum = realm.where(Teacher.class).max("id");
                int nextId;
                if(currentIdNum == null) {
                    nextId = 1;
                } else {
                    nextId = currentIdNum.intValue() + 1;
                }

                Teacher teacher = mRealm.createObject(Teacher.class, nextId);
                teacher.setName(name);
                teacher.setSurname(surname);

                mAdapter.replaceData(loadTeacher());
            }
        });
    }

    private RealmResults<Teacher> loadTeacher(){
        RealmResults<Teacher> results = mRealm.where(Teacher.class).findAll();
        return  results;
    }


    private static class TeacherAdapter extends BaseAdapter{

        private RealmResults<Teacher> mTeacherResult;

        public TeacherAdapter(RealmResults<Teacher> mTeacherResult) {
            this.mTeacherResult = mTeacherResult;
        }

        public void replaceData(RealmResults<Teacher> teachers) {
            mTeacherResult = teachers;
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mTeacherResult.size();
        }

        @Override
        public Object getItem(int position) {
            return mTeacherResult.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            View rowView = view;
            if(rowView == null){
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.item_teacher, parent, false);
            }

            final Teacher teacher = mTeacherResult.get(position);

            TextView textName = (TextView) rowView.findViewById(R.id.text_name);
            textName.setText(teacher.getName());

            TextView textSurname = (TextView) rowView.findViewById(R.id.text_surname);
            textSurname.setText(teacher.getSurname());

            return rowView;
        }
    }

}
