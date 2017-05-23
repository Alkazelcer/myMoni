package com.example.peka.moneytracker.activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.PopupWindow;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;

import com.example.peka.moneytracker.R;
import com.example.peka.moneytracker.models.Expense;
import com.example.peka.moneytracker.utils.DbHelper;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    DbHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new DbHelper(getBaseContext());



        setContentView(R.layout.history_fragment);
        final ExpandableListView listView = (ExpandableListView) findViewById(R.id.listView);
        final List<Expense> list = new ArrayList<>();

        for (Expense entry : mDbHelper.getExpenses(mDbHelper.getReadableDatabase())) {
            list.add(entry);
        }

        String name = "name";

        List<Map<String, String>> groupData = new ArrayList<>();
        List<List<Map<String, String>>> childData = new ArrayList<>();


        DateTime firstDate = list.isEmpty() ? null : list.get(0).getDate();
        List<Map<String, String>> children = new ArrayList<>();
        Map<String, String> curChildMap;
        for (Expense expense : list) {
            if (firstDate.withTimeAtStartOfDay().compareTo(expense.getDate().withTimeAtStartOfDay()) > 0) {
                Map<String, String> curGroupMap = new HashMap<>();
                curGroupMap.put(name, firstDate.toString("dd-MM-YYYY"));
                groupData.add(curGroupMap);
                firstDate = expense.getDate();
                childData.add(children);
                children = new ArrayList<>();
            }
            curChildMap = new HashMap<>();
                children.add(curChildMap);
                curChildMap.put(name, expense.getName() + " - " + expense.getPrice());
        }

        Map<String, String> curGroupMap = new HashMap<>();
        curGroupMap.put(name, firstDate.toString("dd-MM-YYYY"));
        groupData.add(curGroupMap);
        childData.add(children);

        SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(this, groupData,
                android.R.layout.simple_expandable_list_item_1,
                new String[] { name }, new int[] { android.R.id.text1 },
                childData, android.R.layout.simple_expandable_list_item_2,
                new String[] { name }, new int[] { android.R.id.text1 });
        listView.setAdapter(adapter);

        final Button btn = (Button) findViewById(R.id.addButton);


        btn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getBaseContext()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);

                final View popupView = layoutInflater.inflate(R.layout.popup, null);

                final PopupWindow popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT,
                        true);

                popupWindow.setTouchable(true);
                popupWindow.setFocusable(true);

                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                Button btnDismiss = (Button) popupView.findViewById(R.id.addButton);
                final TextView name = (TextView) popupView.findViewById(R.id.name);
                final TextView date = (TextView) popupView.findViewById(R.id.date);
                final TextView price = (TextView) popupView.findViewById(R.id.price);
                final DateTime time = new DateTime();
                date.setText(time.toString("dd-MM-YYYY"));


                btnDismiss.setOnClickListener(new Button.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (name.getText() != null && !name.getText().toString().equals("") &&
                                price.getText() != null && !price.getText().toString().equals("")) {
                            Expense expense = new Expense();
                            expense.setName(name.getText().toString());
                            expense.setPrice(Double.parseDouble(price.getText().toString()));
                            expense.setDate(time);
//                            mDbHelper.addExpense(mDbHelper.getWritableDatabase(), expense);
                            // TODO Auto-generated method stub
                            popupWindow.dismiss();

                            refresh(list);
                            listView.refreshDrawableState();
                        }
                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    private void refresh(List<Expense> list) {
        list.clear();

        for (Expense entry : mDbHelper.getExpenses(mDbHelper.getReadableDatabase())) {
            list.add(entry);
        }
    }
}
