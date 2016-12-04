package edu.calvin.kpb23students.calvindining.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Observable;
import java.util.Observer;

import edu.calvin.kpb23students.calvindining.JavaService;
import edu.calvin.kpb23students.calvindining.MyApplication;
import edu.calvin.kpb23students.calvindining.R;



/**
 * <p>
 *     This fragment shows the calendar
 * </p>
 */
public class Calendar extends Fragment {
    private JavaService javaService;
    private Observer javaServiceObserver;
    boolean getMealCount = true;

    public Calendar() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        RelativeLayout relativeLayout = (RelativeLayout) inflater.inflate(R.layout.fragment_calendar, container, false);
        // Edit Text of mealCount
        final TextView mealDifference = (TextView) relativeLayout.findViewById(R.id.mealDifference);
        final EditText mealCount = (EditText) relativeLayout.findViewById(R.id.mealCount);

        javaServiceObserver = new Observer() {
            @Override
            public void update(Observable o, Object arg) {
                // only load initially when loading view to avoid creating loops
                if (getMealCount && javaService.getUser() != null) {
                    mealCount.setText(Integer.toString(javaService.getUser().getMealCount()));
                    getMealCount = false;
                }
            }
        };

        javaService = MyApplication.getMyApplication().getJavaService();
        javaService.addObserver(javaServiceObserver);
        javaServiceObserver.update(null, null);



        mealCount.addTextChangedListener(new TextWatcher() {
            private int mealPastSet = 0;
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Do nothing
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (Math.abs(mealPastSet - getTextInt(mealCount)) != 1) {
                    mealDifference.setText("0");
                }
                mealPastSet = getTextInt(mealCount);
                javaService.setMeal(mealPastSet);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        // Button Add and Subtract http://stackoverflow.com/a/9838501/2948122
        Button addMeal = (Button) relativeLayout.findViewById(R.id.addMeal);
        addMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (initialMessage(mealCount, container)) {
                    makeToast(" + ADDED MEAL + ", container);
                    mealCount.setText(String.valueOf(getTextInt(mealCount) + 1)); // Change total meal
                    mealDifference.setText(String.valueOf(getTextInt(mealDifference) + 1)); // Change Meal Difference
                }
            }
        });
        Button subtractMeal = (Button) relativeLayout.findViewById(R.id.subtractMeal);
        subtractMeal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (initialMessage(mealCount, container)) {
                    makeToast("- SUBRACTED MEAL -", container);
                    mealCount.setText(String.valueOf(getTextInt(mealCount) - 1)); // Change total meal
                    mealDifference.setText(String.valueOf(getTextInt(mealDifference) - 1));
                }
            }
        });


        // Inflate the layout for this fragment
        return relativeLayout;
    }

    /**
     * Makes Toast message for subtract and add at the same space at the top of the screen
     * @param toastMessage
     * @param container
     */
    private void makeToast(String toastMessage, ViewGroup container) {
        Toast toast = Toast.makeText(container.getContext(), toastMessage, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.TOP,0,700);
        toast.show();
    }

    /**
     * Return string of meal count
     * @param text
     * @return
     */
    private int getTextInt(TextView text) {
        try {
            return Integer.parseInt(text.getText().toString());
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private boolean initialMessage(EditText mealCount, ViewGroup container) {
        if ((mealCount.getText().toString()).matches("")) {
            makeToast("Please Set Initial Meal", container);
            return false;
        }
        return true;
    }

    @Override
    public void onDestroy() {
        javaService.deleteObserver(javaServiceObserver);
        super.onDestroy();
    }
}
