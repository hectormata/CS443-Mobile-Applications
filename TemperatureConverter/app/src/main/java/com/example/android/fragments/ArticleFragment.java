/*
 * Copyright (C) 2012 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.fragments;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ArticleFragment extends Fragment {
    final static String ARG_POSITION = "position";
    int mCurrentPosition = -1;
    boolean Celsius_to_Fahrenheit = false;
    double temperature;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
        Bundle savedInstanceState) {

        // If activity recreated (such as from screen rotate), restore
        // the previous article selection set by onSaveInstanceState().
        // This is primarily necessary when in the two-pane layout.
        if (savedInstanceState != null) {
            mCurrentPosition = savedInstanceState.getInt(ARG_POSITION);
        }

        // Inflate the layout for this fragment
        final View view;
        view = inflater.inflate(R.layout.news_articles, container, false);

        //LinearLayout linear = (LinearLayout) view.findViewById(R.id.linearLayout);

        final EditText input = (EditText) view.findViewById(R.id.edit_text);

        // Creates
        Button button = (Button) view.findViewById(R.id.Button);
        final TextView answer = (TextView) view.findViewById(R.id.textView);


        // Tell the User to input Temperature here
        button.setText("Convert");



        // Sets button to perform the concatenation of the result printing the result of the conversion
        button.setOnClickListener(new View.OnClickListener(){
            public void onClick(View v) {
                double temp = Double.parseDouble(String.valueOf(input.getText()));

                if (Celsius_to_Fahrenheit) {
                    answer.setText("Temperature " + temperature + " (C) is " + Celsius_to_Fahrenheit_Formula(temperature) + " (F)");

                }

                else {
                    answer.setText("Temperature " + temperature + " (F) is " + Fahrenheit_to_Celsius_Formula(temperature) + " (C)");
                }
            }
        });
        return view;
        // return inflater.inflate(R.layout.article_view, container, false);
    }

    // Formula to go Celsius to Fahrenheit
    public double Celsius_to_Fahrenheit_Formula(double Celsius) {

        double answer = (Celsius * 9) / (5 + 32);

        return answer;

    }

    // Formula to go Fahrenheit to Celsius
    public double Fahrenheit_to_Celsius_Formula(double Fahrenheit) {

        double total = (Fahrenheit - 32) * (5 / 9);

        return total;
    }

    public void updateArticleView(int position) {

        TextView article = (TextView) getActivity().findViewById(R.id.article);
        article.setText(Ipsum.Articles[position]);
        mCurrentPosition = position;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // Save the current article selection in case we need to recreate the fragment
        outState.putInt(ARG_POSITION, mCurrentPosition);
    }
}