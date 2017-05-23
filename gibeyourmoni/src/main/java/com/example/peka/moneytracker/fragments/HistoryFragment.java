package com.example.peka.moneytracker.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peka.moneytracker.R;
import com.example.peka.moneytracker.models.Expense;
import com.example.peka.moneytracker.models.Tag;
import com.example.peka.moneytracker.utils.DbHelper;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.id.list;
import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class HistoryFragment extends Fragment {
    DbHelper mDbHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new DbHelper(getActivity());
        mDbHelper.onCreate(mDbHelper.getWritableDatabase());

        View view = inflater.inflate(R.layout.history_fragment, container, false);
        final ExpandableListView listView = (ExpandableListView) view.findViewById(R.id.listView);


        String name = "name";

        final List<Map<String, String>> groupData = new ArrayList<>();
        final List<List<Map<String, String>>> childData = new ArrayList<>();

        test(groupData, childData);

        final SimpleExpandableListAdapter adapter = new SimpleExpandableListAdapter(getActivity(), groupData,
                android.R.layout.simple_expandable_list_item_1,
                new String[]{name}, new int[]{android.R.id.text1},
                childData, android.R.layout.simple_expandable_list_item_2,
                new String[]{name}, new int[]{android.R.id.text1});

        listView.setAdapter(adapter);

        final Button plusButton = (Button) view.findViewById(R.id.plusButton);

        plusButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                LayoutInflater layoutInflater = (LayoutInflater) getActivity()
                        .getSystemService(LAYOUT_INFLATER_SERVICE);

                final View popupView = layoutInflater.inflate(R.layout.popup, null);

                final PopupWindow popupWindow = new PopupWindow(popupView,
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT,
                        true);

                popupWindow.setTouchable(true);
                popupWindow.setFocusable(true);


                final ListView tagListView = (ListView) popupView.findViewById(R.id.tagListView);


//                final List<Tag> tags = mDbHelper.selectTagsByExpense(mDbHelper.getWritableDatabase());
                final List<Tag> tagsInExpense = new ArrayList<>();

                final ArrayAdapter tagsInExpenseAdapter = new TagListAdapter(getActivity(), tagsInExpense);
                tagListView.setAdapter(tagsInExpenseAdapter);

                tagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {
                        final Tag item = (Tag) parent.getItemAtPosition(position);
                        view.animate().setDuration(2000).alpha(0)
                                .withEndAction(new Runnable() {
                                    @Override
                                    public void run() {
                                        tagsInExpense.remove(item);
                                        tagsInExpenseAdapter.notifyDataSetChanged();
                                        view.setAlpha(1);
                                    }
                                });
                    }

                });

                final List<Tag> allTags = mDbHelper.getTags(mDbHelper.getWritableDatabase());

                final ListView allTagListView = (ListView) popupView.findViewById(R.id.allTagListView);
                final Button newTag = (Button) popupView.findViewById(R.id.newTag);
                final EditText tagName = (EditText) popupView.findViewById(R.id.tagName);
                final ArrayAdapter allTagsAdapter = new TagListAdapter(getActivity(), allTags);
                allTagListView.setAdapter(allTagsAdapter);

                allTagListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, final View view,
                                            int position, long id) {
                        final Tag item = (Tag) parent.getItemAtPosition(position);
//                        view.animate().setDuration(2000).alpha(0)
//                                .withEndAction(new Runnable() {
//                                    @Override
//                                    public void run() {
                                        Toast.makeText(getActivity(), item.getName(), Toast.LENGTH_SHORT).show();
                                        tagsInExpense.add(item);
                                        tagsInExpenseAdapter.notifyDataSetChanged();
                        allTagListView.setVisibility(View.GONE);
                        tagName.setVisibility(View.GONE);
                        newTag.setVisibility(View.GONE);
//                                    }
//                                });
                    }

                });


                Button addTag = (Button) popupView.findViewById(R.id.addTag);


                addTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        allTagListView.setVisibility(View.VISIBLE);
                        newTag.setVisibility(View.VISIBLE);
                        tagName.setVisibility(View.VISIBLE);
                    }
                });

                newTag.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Tag tag = new Tag();
                        tag.setName(tagName.getText().toString());
                        mDbHelper.addTag(mDbHelper.getWritableDatabase(), tag);
                        tagName.setText("");
                        allTags.add(tag);
                        allTagsAdapter.notifyDataSetChanged();
                        allTagListView.setVisibility(View.GONE);
                        newTag.setVisibility(View.GONE);
                        tagName.setVisibility(View.GONE);
                    }
                });


                popupWindow.showAtLocation(popupView, Gravity.CENTER, 0, 0);

                Button addButton = (Button) popupView.findViewById(R.id.addButton);
                final TextView name = (TextView) popupView.findViewById(R.id.name);
                final TextView date = (TextView) popupView.findViewById(R.id.date);
                final TextView price = (TextView) popupView.findViewById(R.id.price);
                //todo dirty dirty
//                final DateTime time = new DateTime();
                final DateTime[] time = new DateTime[]{new DateTime()};
                date.setText(time[0].toString("dd-MM-YYYY"));

                final DatePickerDialog.OnDateSetListener dates = new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        time[0] = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);

                        date.setText(time[0].toString("dd-MM-YYYY"));
                    }

                };

                date.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        new DatePickerDialog(getContext(), dates, time[0].getYear(), time[0].getMonthOfYear() - 1,
                                time[0].getDayOfMonth()).show();
                    }
                });


                addButton.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (name.getText() != null && !name.getText().toString().equals("") &&
                                price.getText() != null && !price.getText().toString().equals("")) {
                            Expense expense = new Expense();
                            expense.setName(name.getText().toString());
                            expense.setPrice(Double.parseDouble(price.getText().toString()));
                            expense.setDate(time[0]);
                            mDbHelper.addExpense(mDbHelper.getWritableDatabase(), expense, tagsInExpense);
                            // TODO Auto-generated method stub
                            popupWindow.dismiss();

                            test(groupData, childData);
                            adapter.notifyDataSetChanged();
                        }
                    }
                });




                Button cancelButton = (Button) popupView.findViewById(R.id.cancelButton);

                cancelButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupWindow.dismiss();
                    }
                });
            }
        });

        return view;
    }

    @Override
    public void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    private void test(List<Map<String, String>> groupData, List<List<Map<String, String>>> childData) {
        List<Expense> list = mDbHelper.getExpenses(mDbHelper.getReadableDatabase());
        groupData.clear();
        childData.clear();
        String name = "name";

//        for(Map.Entry<DateTime, List<Expense>> entry : ExpenseUtils.splitByDates(list).entrySet()) {
//
//        }

        if (!list.isEmpty()) {
            DateTime firstDate = list.get(0).getDate();
            List<Map<String, String>> children = new ArrayList<>();
            Map<String, String> curChildMap;
            for (Expense expense : list) {
                List<Tag> tags = mDbHelper.selectTagsByExpense(mDbHelper.getWritableDatabase(), expense);
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
                StringBuilder tagsInLine = new StringBuilder();
                for (Tag tag : tags) {
                    tagsInLine.append(tag.getName());
                    tagsInLine.append(" ");
                }

                curChildMap.put(name, expense.getName() + " - " + expense.getPrice() + " TAGS:" + tagsInLine);
            }


            Map<String, String> curGroupMap = new HashMap<>();
            curGroupMap.put(name, firstDate.toString("dd-MM-YYYY"));
            groupData.add(curGroupMap);
            childData.add(children);
        }
    }
}
