package com.commonsware.android.constants;


import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Max on 20.03.16.
 */
public class SearchFragment extends Fragment {
    private DatabaseHelper mDbHelper;
    private EditText etSearched;
    private Button btnSearch;
    private TextView tvFound;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.search, container, false);
        etSearched = (EditText) view.findViewById(R.id.etSearch);
        btnSearch = (Button) view.findViewById(R.id.btnSearch);
        tvFound = (TextView) view.findViewById(R.id.tvFound);
        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View _view) {
                find(etSearched.getText().toString());
            }
        });
        setHasOptionsMenu(true);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDbHelper = new DatabaseHelper(getActivity().getApplicationContext());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.actions, menu);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.add) {
            getFragmentManager().beginTransaction()
                    .replace(android.R.id.content,
                            new GravityFragment()).commit();
            return(true);
        }

        return(super.onOptionsItemSelected(item));
    }

    private void find(String s) {
        if (TextUtils.isEmpty(s)) {
            return;
        }
        String criteria = "%" + s + "%";
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT title, value FROM gravity WHERE title LIKE '" + criteria + "'", null);
        StringBuilder result = new StringBuilder();

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    String title = cursor.getString(cursor.getColumnIndex(DatabaseHelper.TITLE));
                    float value = cursor.getFloat(cursor.getColumnIndex(DatabaseHelper.VALUE));
                    result.append(title).append("  ").append(value).append("\n");
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        tvFound.setText(result);
    }
}
