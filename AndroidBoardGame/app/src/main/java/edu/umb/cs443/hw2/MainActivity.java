package edu.umb.cs443.hw2;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class MainActivity extends Activity {

    GridView gridView;

    private static int w = 5, curx, cury;
    private Random r = new Random();
    // Initialize the player's position
    private int initialPosition = 0;
    // Create new 5x5 grid
    static String[] numbers = new String[w*w];


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        gridView = (GridView) findViewById(R.id.gridView1);

        for (int i = 0; i < numbers.length; i++) {

            numbers[i] = " ";
        }

        curx = r.nextInt(w);
        cury = r.nextInt(w);

        initialPosition = cury * w + curx;
        numbers[initialPosition] = "0";


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.list_item, numbers);

        gridView.setAdapter(adapter);
        //init();
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View v,
                                    int position, long id) {
                ;
                Toast.makeText(getApplicationContext(),
                        (CharSequence) (new Integer(position).toString()),
                        Toast.LENGTH_SHORT).show();
                new PerformAsyncTask().execute(initialPosition, (new Integer(position)));
                initialPosition = position;
            }
        });



    }

    /*

     Initialize class PerformAsyncTask to extend AsyncTask
     to help with the proper use of the UI thread.

     This class with help with background running aspect of game app

     */
    private class PerformAsyncTask extends AsyncTask<Integer, Integer, Void> {
        int status;

        @Override
        protected  void onPreExecute() {

            Thread thread = new Thread(treasure);
            thread.start();
            super.onPreExecute();

        }

        // Method to perform in the background
        @Override
        protected Void doInBackground(Integer... parameters) {

            int x1 = parameters[0] % w;
            int y1 = (parameters[0] - x1) / w;
            int x2 = parameters[1] % w;
            int y2 = (parameters[1] - x2) / w;

            while ((x1 != x2)) {

                if (x1 < x2) {
                    x1++;
                }

                if (x1 > x2) {
                    x1--;
                }

                numbers[parameters[0]] = " ";
                parameters[0] = (y1 * w) + x1;

                if (numbers[parameters[0]] == "X") {
                    score++;
                }

                numbers[parameters[0]] = "0";
                publishProgress();

                try {

                    Thread.sleep(100);
                } catch (Exception e) {

                }
            }

            while (y1 != y2) {

                if (y1 < y2) {
                    y1++;
                }

                if (y1 > y2) {
                    y1--;
                }

                numbers[parameters[0]] = " ";
                parameters[0] = (y1 * w) + x1;

                if (numbers[parameters[0]] == "X") {
                    score++;
                }

                numbers[parameters[0]] = "0";
                publishProgress();
                try {
                    Thread.sleep(100);

                } catch (Exception e) {

                }
            }

            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            // Update UI in background while task are performing
            super.onProgressUpdate(values);
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(Void result) {
            // After task Executed update final UI
            super.onPostExecute(result);
            ((ArrayAdapter) gridView.getAdapter()).notifyDataSetChanged();
        }
    }

    // Determine how many X's will be on board
    int xCount = 0;
    int score = 0;
    private Runnable treasure = new Runnable() {
        // Delay
        private static final int DELAY = 3000;
        public void run() {

            try {

                while (true) {

                    for (int i = 0; i < numbers.length; i++) {

                        if (numbers[i] == "X") {

                            xCount++;
                        }
                    }

                    if (xCount < 4) {

                        int temp = xCount;
                        for (int i = temp; i < 4; i++) {

                            // Example from base
                            curx = r.nextInt(w);
                            cury = r.nextInt(w);

                            if (numbers[cury * w + curx] == " ") {
                                numbers[cury * w + curx] = "X";
                            }
                        }
                        xCount = 4;
                    }
                    Thread.sleep(DELAY);
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    };

    void init(){

        for(int i = 0; i < numbers.length; i++) numbers[i] = " ";
        curx=r.nextInt(w);
        cury=r.nextInt(w);
        numbers[cury * w + curx] = "O";
        score = 0;
        ((ArrayAdapter)gridView.getAdapter()).notifyDataSetChanged();
    }

    // Get position to obtain player's position
    int getPosition() {

        for (int i = 0; i < numbers.length; i++) {

            if (numbers[i] == "0") {

                Toast.makeText(getApplicationContext(), (CharSequence) ("Position: " + new Integer(i).toString()), Toast.LENGTH_SHORT).show();
                return i;
            }
        }
        return 0;
    }

    public void reset(View view){
        init();
    }
    public void quit(View view) {
        finish();
        System.exit(0);
    }
    
}
