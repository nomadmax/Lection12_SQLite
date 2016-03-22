/***
  Copyright (c) 2008-2014 CommonsWare, LLC
  Licensed under the Apache License, Version 2.0 (the "License"); you may not
  use this file except in compliance with the License. You may obtain	a copy
  of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
  by applicable law or agreed to in writing, software distributed under the
  License is distributed on an "AS IS" BASIS,	WITHOUT	WARRANTIES OR CONDITIONS
  OF ANY KIND, either express or implied. See the License for the specific
  language governing permissions and limitations under the License.
	
  From _The Busy Coder's Guide to Android Development_
    https://commonsware.com/Android
 */

package com.commonsware.android.constants;

import android.app.AlertDialog;
import android.app.ListFragment;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;

public class GravityFragment extends ListFragment implements
    DialogInterface.OnClickListener {

  private DatabaseHelper mDatabaseHelper = null;
  private Cursor mCursor = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    
    setHasOptionsMenu(true);
    setRetainInstance(true);
  }

  @Override
  public void onViewCreated(View view, Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);

    SimpleCursorAdapter adapter=
        new SimpleCursorAdapter(getActivity(), R.layout.row,
                mCursor, new String[] {
            DatabaseHelper.TITLE,
            DatabaseHelper.VALUE },
            new int[] { R.id.title, R.id.value },
            0);

    setListAdapter(adapter);

    if (mCursor ==null) {
      mDatabaseHelper =new DatabaseHelper(getActivity());
      ((CursorAdapter)getListAdapter()).changeCursor(doQuery());
    }
  }


  @Override
  public void onDestroy() {

    ((CursorAdapter)getListAdapter()).getCursor().close();
    mDatabaseHelper.close();

    super.onDestroy();
  }

  @Override
  public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
    inflater.inflate(R.menu.actions, menu);

    super.onCreateOptionsMenu(menu, inflater);
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    if (item.getItemId()==R.id.add) {
      add();
      return(true);
    }
      if (item.getItemId()==R.id.search) {
              getFragmentManager().beginTransaction()
                      .replace(android.R.id.content,
                              new SearchFragment()).commit();
      return(true);
    }

    return(super.onOptionsItemSelected(item));
  }

  private void add() {
    LayoutInflater inflater=getActivity().getLayoutInflater();
    View addView=inflater.inflate(R.layout.add_edit, null);
    AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());

    builder.setTitle(R.string.add_title).setView(addView)
           .setPositiveButton(R.string.ok, this)
           .setNegativeButton(R.string.cancel, null).show();
  }

  public void onClick(DialogInterface di, int whichButton) {
    ContentValues values=new ContentValues(2);
    AlertDialog dlg=(AlertDialog)di;
    EditText title=(EditText)dlg.findViewById(R.id.title);
    EditText value=(EditText)dlg.findViewById(R.id.value);

    values.put(DatabaseHelper.TITLE, title.getText().toString());
    values.put(DatabaseHelper.VALUE, value.getText().toString());

    mDatabaseHelper.getWritableDatabase().insert(DatabaseHelper.TABLE, DatabaseHelper.TITLE, values);
      ((CursorAdapter)getListAdapter()).changeCursor(doQuery());
  }

  private Cursor doQuery() {
    Cursor result=
            mDatabaseHelper.getReadableDatabase()
                    .query(DatabaseHelper.TABLE,
                            new String[] {"ROWID AS _id",
                                    DatabaseHelper.TITLE,
                                    DatabaseHelper.VALUE},
                            null, null, null, null, DatabaseHelper.TITLE);
    return(result);

  }
}
