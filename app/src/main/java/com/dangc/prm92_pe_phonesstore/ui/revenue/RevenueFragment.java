package com.dangc.prm92_pe_phonesstore.ui.revenue;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView; // Import cho Spinner
import android.widget.ArrayAdapter; // Import cho Spinner
import android.widget.LinearLayout; // Import cho LinearLayout
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner; // Import cho Spinner
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.dangc.prm92_pe_phonesstore.R;
import com.dangc.prm92_pe_phonesstore.viewmodel.RevenueViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.DateFormatSymbols; // Import để lấy tên tháng
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RevenueFragment extends Fragment {

    private RevenueViewModel revenueViewModel;
    private TextView textViewTotalRevenue;
    private RadioGroup radioGroupFilter;
    private RadioButton radioTotal, radioDaily, radioMonthly, radioYearly;

    private TextInputLayout textFieldDailyDate;
    private TextInputEditText editTextDailyDate;

    private LinearLayout layoutMonthlyYearlyFilter;
    private Spinner spinnerMonth;
    private Spinner spinnerYear;

    private SimpleDateFormat dailyDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    private SimpleDateFormat monthlyDateFormat = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
    private SimpleDateFormat yearlyDateFormat = new SimpleDateFormat("yyyy", Locale.getDefault());

    private int selectedMonth;
    private int selectedYear;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_revenue, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        textViewTotalRevenue = view.findViewById(R.id.textViewTotalRevenue);
        radioGroupFilter = view.findViewById(R.id.radioGroupFilter);
        radioTotal = view.findViewById(R.id.radioTotal);
        radioDaily = view.findViewById(R.id.radioDaily);
        radioMonthly = view.findViewById(R.id.radioMonthly);
        radioYearly = view.findViewById(R.id.radioYearly);

        textFieldDailyDate = view.findViewById(R.id.textFieldDailyDate);
        editTextDailyDate = view.findViewById(R.id.editTextDailyDate);

        layoutMonthlyYearlyFilter = view.findViewById(R.id.layoutMonthlyYearlyFilter);
        spinnerMonth = view.findViewById(R.id.spinnerMonth);
        spinnerYear = view.findViewById(R.id.spinnerYear);

        revenueViewModel = new ViewModelProvider(requireActivity()).get(RevenueViewModel.class);

        requireActivity().setTitle("Revenue");

        setupMonthSpinner();
        setupYearSpinner();

        Calendar now = Calendar.getInstance();
        selectedMonth = now.get(Calendar.MONTH);
        spinnerMonth.setSelection(selectedMonth);
        selectedYear = now.get(Calendar.YEAR);
        spinnerYear.setSelection(findYearPosition(selectedYear));

        revenueViewModel.revenue.observe(getViewLifecycleOwner(), revenue -> {
            if (revenue != null) {
                textViewTotalRevenue.setText(String.format("$%.2f", revenue));
            } else {
                textViewTotalRevenue.setText("$0.00");
            }
        });

        radioGroupFilter.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.radioTotal) {
                revenueViewModel.setFilterType(RevenueViewModel.RevenueFilterType.TOTAL);
                textFieldDailyDate.setVisibility(View.GONE);
                layoutMonthlyYearlyFilter.setVisibility(View.GONE);
            } else if (checkedId == R.id.radioDaily) {
                revenueViewModel.setFilterType(RevenueViewModel.RevenueFilterType.DAILY);
                textFieldDailyDate.setVisibility(View.VISIBLE);
                layoutMonthlyYearlyFilter.setVisibility(View.GONE);
                if (editTextDailyDate.getText().toString().isEmpty()) {
                    editTextDailyDate.setText(dailyDateFormat.format(now.getTime()));
                }
            } else if (checkedId == R.id.radioMonthly) {
                revenueViewModel.setFilterType(RevenueViewModel.RevenueFilterType.MONTHLY);
                textFieldDailyDate.setVisibility(View.GONE);
                layoutMonthlyYearlyFilter.setVisibility(View.VISIBLE);
                spinnerMonth.setVisibility(View.VISIBLE);
            } else if (checkedId == R.id.radioYearly) {
                revenueViewModel.setFilterType(RevenueViewModel.RevenueFilterType.YEARLY);
                textFieldDailyDate.setVisibility(View.GONE);
                layoutMonthlyYearlyFilter.setVisibility(View.VISIBLE);
                spinnerMonth.setVisibility(View.GONE);
            }
            updateRevenueFilter();
        });

        editTextDailyDate.setOnClickListener(v -> showDatePicker());
        textFieldDailyDate.setEndIconOnClickListener(v -> showDatePicker());
        spinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedMonth = position;
                updateRevenueFilter();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        spinnerYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedYear = Integer.parseInt(parent.getItemAtPosition(position).toString());
                updateRevenueFilter();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {} // Bắt buộc phải có
        });
        radioTotal.setChecked(true);
        updateRevenueFilter();
    }

    private void setupMonthSpinner() {
        List<String> months = new ArrayList<>();
        String[] monthNames = new DateFormatSymbols(Locale.getDefault()).getMonths();
        for (int i = 0; i < 12; i++) {
            months.add(monthNames[i]);
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, months);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMonth.setAdapter(adapter);
    }

    private void setupYearSpinner() {
        List<String> years = new ArrayList<>();
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        for (int i = currentYear; i >= currentYear - 10; i--) {
            years.add(String.valueOf(i));
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(requireContext(),
                android.R.layout.simple_spinner_item, years);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerYear.setAdapter(adapter);
    }

    private int findYearPosition(int year) {
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) spinnerYear.getAdapter();
        for (int i = 0; i < adapter.getCount(); i++) {
            if (Integer.parseInt(adapter.getItem(i)) == year) {
                return i;
            }
        }
        return 0;
    }


    private void showDatePicker() {
        Calendar calendar = Calendar.getInstance();
        if (!editTextDailyDate.getText().toString().isEmpty()) {
            try {
                Date date = dailyDateFormat.parse(editTextDailyDate.getText().toString());
                if (date != null) {
                    calendar.setTime(date);
                }
            } catch (ParseException e) {
                e.printStackTrace();
                Toast.makeText(getContext(), "Invalid date format.", Toast.LENGTH_SHORT).show();
            }
        }

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                requireContext(),
                (view, year, month, dayOfMonth) -> {
                    Calendar selectedDate = Calendar.getInstance();
                    selectedDate.set(year, month, dayOfMonth);

                    editTextDailyDate.setText(dailyDateFormat.format(selectedDate.getTime()));
                    updateRevenueFilter();
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void updateRevenueFilter() {
        RevenueViewModel.RevenueFilterType currentType = revenueViewModel.getCurrentFilterType();
        String formattedDate = null;

        if (currentType == RevenueViewModel.RevenueFilterType.DAILY) {
            formattedDate = editTextDailyDate.getText().toString();
        } else if (currentType == RevenueViewModel.RevenueFilterType.MONTHLY) {
            formattedDate = String.format(Locale.getDefault(), "%d-%02d", selectedYear, selectedMonth + 1);
        } else if (currentType == RevenueViewModel.RevenueFilterType.YEARLY) {
            formattedDate = String.valueOf(selectedYear);
        }
        revenueViewModel.setFilterDate(formattedDate);
    }
}