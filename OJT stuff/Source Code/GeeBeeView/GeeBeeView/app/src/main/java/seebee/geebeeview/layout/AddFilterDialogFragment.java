package seebee.geebeeview.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import seebee.geebeeview.R;

/**
 * AddFilterDialogFragment class is used by the DataVisualizationActivity
 * for the creation of the add filter dialog
 *
 * @author Stephanie Dy
 * @since 8/19/2017
 */

public class AddFilterDialogFragment extends DialogFragment {

    public static final String TAG = "AddFilterDialog";
    AddFilterDialogListener mListener;
    private Spinner spAgeFilter, spGenderFilter, spGradeLevelFilter;
    private EditText etFilter;
    private String ageValue;
    private String ageEquator;
    private String genderValue;
    private String gradeLevelValue;
    private ArrayList<String> gradeLevels;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // get layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        // inflate and set layout for the dialog
        View view = inflater.inflate(R.layout.dialog_add_filter, null);
        // pass null because it is a dialog
        builder.setView(view)
                .setTitle(R.string.add_filter)
                // Add action buttons
                .setPositiveButton(R.string.add, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(etFilter.getText().toString().isEmpty() && genderValue.contains("N/A")) {
                            etFilter.setError("This field is required!");
                        } else {
                            ageValue = etFilter.getText().toString();
                            // Send the positive button event back to the host activity
                            mListener.onDialogPositiveClick(AddFilterDialogFragment.this);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        // connect layout content with class
        spAgeFilter = (Spinner) view.findViewById(R.id.sp_age_filter);
        spGenderFilter = (Spinner) view.findViewById(R.id.sp_gender_filter);
        spGradeLevelFilter = (Spinner) view.findViewById(R.id.sp_grade_level_filter);

        etFilter = (EditText) view.findViewById(R.id.et_filter);
        // set filterEquator to what is pointed by spinner
        ArrayAdapter<String> spAgeAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                R.layout.custom_spinner, getResources().getStringArray(R.array.age_filter_array));
        spAgeFilter.setAdapter(spAgeAdapter);
        spAgeFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ageEquator = getResources().getStringArray(R.array.age_filter_array)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                ageEquator = getResources().getStringArray(R.array.age_filter_array)[0];
            }
        });

        ArrayAdapter<String> spGenderAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                R.layout.custom_spinner, getResources().getStringArray(R.array.gender_filter_array));
        spGenderFilter.setAdapter(spGenderAdapter);
        spGenderFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                genderValue = getResources().getStringArray(R.array.gender_filter_array)[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                genderValue = getResources().getStringArray(R.array.gender_filter_array)[0];
            }
        });
        /* prepare grade level spinner */
        final List<String> gradeLevelList = new ArrayList<>();
        for(int i = 0; i < gradeLevels.size(); i++) {
            gradeLevelList.add(gradeLevels.get(i));
        }
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(getActivity().getBaseContext(),
                R.layout.custom_spinner, gradeLevelList);
        spGradeLevelFilter.setAdapter(spinnerAdapter);

        spGradeLevelFilter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                gradeLevelValue = gradeLevels.get(position);
                //Log.i(TAG, "grade level value = "+gradeLevelValue);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                gradeLevelValue = gradeLevels.get(0);
            }
        });

        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (AddFilterDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    public String getAgeValue() {
        return ageValue;
    }

    public String getAgeEquator() {
        return ageEquator;
    }

    public String getGenderValue() {
        return genderValue;
    }

    public String getGradeLevelValue() {
        return gradeLevelValue;
    }

    public void setGradeLevels(ArrayList<String> gradeLevels) {
        this.gradeLevels = gradeLevels;
    }

    // interface which is inherited when this dialog is implemented
    public interface AddFilterDialogListener {
        void onDialogPositiveClick(AddFilterDialogFragment dialog);
        //void onDialogNegativeClick(DialogFragment dialog);
    }

}
