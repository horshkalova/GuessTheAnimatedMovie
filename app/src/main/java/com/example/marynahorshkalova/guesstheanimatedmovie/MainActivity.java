package com.example.marynahorshkalova.guesstheanimatedmovie;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> moviesURLs = new ArrayList<String>();
    ArrayList<String> moviesNames = new ArrayList<String>();

    int chosenMovie = 0;

    // random number between 0 and 3 (number of buttons)
    int locationOfTheCorrectAnswer = 0;

    String[] answers = new String[4];

    ImageView imageView;
    Button button0;
    Button button1;
    Button button2;
    Button button3;

    public void movieChosen(View view) {

        if (view.getTag().toString().equals(Integer.toString(locationOfTheCorrectAnswer))) {

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();

        } else {

            Toast.makeText(getApplicationContext(), "Wrong! It was " +
                    moviesNames.get(chosenMovie), Toast.LENGTH_SHORT).show();
        }

        createNewQuestion();
    }


    public class ImageDownloader extends AsyncTask<String, Void, Bitmap> {


        @Override
        protected Bitmap doInBackground(String... urls) {

            try {

                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();

                Bitmap myBitmap = BitmapFactory.decodeStream(inputStream);

                return myBitmap;


            } catch (MalformedURLException e) {

                e.printStackTrace();

            } catch (IOException e) {

                e.printStackTrace();
            }

            return null;
        }
    }

    public class DownloadTask extends AsyncTask<String, Void, String> {

        // downloading web content

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try {

                url = new URL(urls[0]);

                urlConnection = (HttpURLConnection) url.openConnection();

                InputStream in = urlConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while (data != -1) {

                    char current = (char) data;

                    result += current;

                    data = reader.read();
                }

                return result;

            } catch (Exception e) {

                e.printStackTrace();

            }
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        // download web content
        DownloadTask task = new DownloadTask();
        String result = null;

        try {
            result = task.execute("http://www.movie-film-review.com/devTop100.asp?no=100&type=32").get();

            // image urls downloader
            Pattern p = Pattern.compile("<img src=\"../files/images/filmimages/(.*?)\" alt=");
            Matcher m = p.matcher(result);

            while (m.find()) {

                moviesURLs.add("http://www.movie-film-review.com/files/images/filmimages/" + m.group(1));

            }

            // converting arrayList to array for printing to logs
            //System.out.println(Arrays.toString(moviesURLs.toArray()));

            // names downloader
            p = Pattern.compile("]</span> <a href=\"devFilm.asp?(.*?)</a>");
            m = p.matcher(result);

            while (m.find()) {

                moviesNames.add(m.group(1).substring(11));
            }

            moviesNames.remove(15);

        } catch (Exception e) {

            e.printStackTrace();
        }

        createNewQuestion();

    }

    public void createNewQuestion() {

        Random random = new Random();

        // movie which name's index == URL's index
        chosenMovie = random.nextInt(moviesURLs.size());

        ImageDownloader imageTask = new ImageDownloader();

        Bitmap movieImage = null;

        try {

            // get random image
            movieImage = imageTask.execute(moviesURLs.get(chosenMovie)).get();

            // set this image to imageView
            imageView.setImageBitmap(movieImage);

            locationOfTheCorrectAnswer = random.nextInt(4);

            int incorrectAnswerLocation;

            for (int i = 0; i < 4; i++) {

                if (i == locationOfTheCorrectAnswer) {

                    answers[i] = moviesNames.get(chosenMovie);

                } else {

                    incorrectAnswerLocation = random.nextInt(moviesURLs.size());

                    while (incorrectAnswerLocation == chosenMovie) {

                        incorrectAnswerLocation = random.nextInt(moviesURLs.size());
                    }

                    answers[i] = moviesNames.get(incorrectAnswerLocation);
                }

            }

            button0.setText(answers[0]);
            button1.setText(answers[1]);
            button2.setText(answers[2]);
            button3.setText(answers[3]);

        } catch (Exception e) {

            e.printStackTrace();

        }

    }
}
