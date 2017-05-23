package com.example.peka.moneytracker.fragments;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.peka.moneytracker.R;
import com.example.peka.moneytracker.models.Expense;
import com.example.peka.moneytracker.utils.DbHelper;
import com.example.peka.moneytracker.utils.ExpenseUtils;

import org.joda.time.DateTime;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.List;
import java.util.Map;

import lecho.lib.hellocharts.listener.ColumnChartOnValueSelectListener;
import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Column;
import lecho.lib.hellocharts.model.ColumnChartData;
import lecho.lib.hellocharts.model.SubcolumnValue;
import lecho.lib.hellocharts.util.ChartUtils;
import lecho.lib.hellocharts.view.ColumnChartView;

/**
 * Created by peka on 07.05.17.
 */

public class StatisticsFragment extends Fragment {
    private DbHelper mDbHelper;
    private ColumnChartView chart;
    private ColumnChartData data;
    private TextView startDate;
    private TextView endDate;

    private DateTime[] time = new DateTime[]{new DateTime().minusMonths(2), new DateTime()};

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new DbHelper(getActivity());

        View view = inflater.inflate(R.layout.statistics_fragment, container, false);

        setHasOptionsMenu(true);

        startDate = (TextView) view.findViewById(R.id.startDate);
        startDate.setText(time[0].toString("dd-MM-YYYY"));

        final DatePickerDialog.OnDateSetListener startDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                time[0] = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);

                startDate.setText(time[0].toString("dd-MM-YYYY"));
            }

        };

        startDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), startDateListener, time[0].getYear(), time[0].getMonthOfYear() - 1,
                        time[0].getDayOfMonth()).show();
            }
        });

        endDate = (TextView) view.findViewById(R.id.endDate);
        endDate.setText(time[1].toString("dd-MM-YYYY"));

        final DatePickerDialog.OnDateSetListener endDateListener = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                time[1] = new DateTime(year, monthOfYear + 1, dayOfMonth, 0, 0);

                endDate.setText(time[1].toString("dd-MM-YYYY"));
            }

        };

        endDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(getContext(), endDateListener, time[1].getYear(), time[1].getMonthOfYear() - 1,
                        time[1].getDayOfMonth()).show();
            }
        });

        chart = (ColumnChartView) view.findViewById(R.id.chart);
        chart.setOnValueTouchListener(new ValueTouchListener());

        prepareColumnData(time[0], time[1]);

        return view;
    }

    @Override
    public void onDestroy() {
        mDbHelper.close();
        super.onDestroy();
    }

    private void prepareColumnData(DateTime startDate, DateTime endDate) {
        // Chart looks the best when line data and column data have similar maximum viewports.
        List<Expense> expensesInDates = mDbHelper.selectInDates(mDbHelper.getWritableDatabase(), startDate, endDate);

        data = new ColumnChartData(expensesToColumnData(expensesInDates));

        Axis axisY = new Axis().setHasLines(true);
        axisY.setName("Moni");
        axisY.setValues(new ArrayList<AxisValue>(){{add(new AxisValue(100));}});
        Axis axisX = new Axis();
        axisX.setName("Day");

        List<AxisValue> xValues = new ArrayList<>();
        int number = 0;
        for (DateTime date : ExpenseUtils.splitByDates(expensesInDates).keySet()) {
            xValues.add(new AxisValue(number).setLabel(date.toString("dd.MM")));
            number++;
        }

        axisX.setValues(xValues);

        data.setAxisXBottom(axisX);
        data.setAxisYLeft(axisY);

        chart.setColumnChartData(data);
    }

    private ColumnChartData expensesToColumnData(List<Expense> expensesInDates) {
        List<Column> columns = new ArrayList<>();
        List<SubcolumnValue> values = new ArrayList<>();

        int color = 0;
        for (Map.Entry<DateTime, List<Expense>> entry : ExpenseUtils.splitByDates(expensesInDates).entrySet()) {
            for (Expense expense : entry.getValue()) {
                values.add(new SubcolumnValue((float) Math.log(expense.getPrice()), ChartUtils.COLORS[color]));
            }

            columns.add(new Column(values));
            color = color + 1 >= ChartUtils.COLORS.length ? 0 : color + 1;
            values = new ArrayList<>();
        }

        ColumnChartData columnChartData = new ColumnChartData(columns);

        return columnChartData;
    }

    private class ValueTouchListener implements ColumnChartOnValueSelectListener {

        @Override
        public void onValueDeselected() {
            // TODO Auto-generated method stub
        }

        @Override
        public void onValueSelected(int columnIndex, int subcolumnIndex, SubcolumnValue value) {
            Toast.makeText(getActivity(), "Selected column: " + new BigDecimal(
                    //todo WTF IS THAT?
                    Math.pow(Math.exp(1), value.getValue())).setScale(2, BigDecimal.ROUND_HALF_UP),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
