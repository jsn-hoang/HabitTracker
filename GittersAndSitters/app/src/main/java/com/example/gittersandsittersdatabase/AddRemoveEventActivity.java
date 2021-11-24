package com.example.gittersandsittersdatabase;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.w3c.dom.Text;


import java.io.File;
import java.text.DateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * This Activity is responsible for creating, editing, or deleting an Event.
 * This Activity has two "modes": newEvent, and editEvent
 * newEvent mode starts when the the user navigates to this Activity from HabitActivity
 * editEvent mode starts when the the user navigates to this Activity from EventHistoryActivity
 */

//TODO Fragments for habitEventPhoto and habitEventLocation

public class AddRemoveEventActivity extends AppCompatActivity {

    // Declare variables for referencing
    public static final int PERMISSIONS_REQUEST_CODE_FINE_LOCATION = 1;
    public static final int PERMISSIONS_REQUEST_CODE_CAMERA = 0;
    public static final int RESULT_LOAD_IMAGE = 1;
    public static final int RESULT_DELETE = 2;
    User user;
    Habit habit;                   // The parent Habit of the HabitEvent
    HabitEvent habitEvent;
    Calendar habitEventDate;
    Location habitEventLocation = null;
    File habitEventPhoto = null;
    boolean isNewHabitEvent;
    int habitListIndex;            // index position of the Habit in the User's habitList
    int habitEventListIndex;       // index position of the HabitEvent in the Habit's habitEventList
    ImageView imageView;
    ImageButton eventPhotoButton; // TODO delete if not used outside onCreate

    // location
    private FusedLocationProviderClient fusedLocationClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_habit_event);

        // get user
        user = (User) getIntent().getSerializableExtra("user");
        // get the Habit that is/will be the parent of this HabitEvent
        habit = (Habit) getIntent().getSerializableExtra("habit");
        // get the index position of the parentHabit in habitList
        habitListIndex = user.getUserHabitPosition(habit);

        // position intent is only available for an existing HabitEvent
        if (getIntent().hasExtra("position")) {
            isNewHabitEvent = false;
            habitEventListIndex = getIntent().getExtras().getInt("position");
            // get the HabitEvent to be edited
            habitEvent = habit.getHabitEvent(habitEventListIndex);
        }
        // else this is a new HabitEvent
        else isNewHabitEvent = true;


        // Declare variables for xml object referencing
        EditText habitEventNameEditText = findViewById(R.id.event_name_editText);
        EditText habitEventCommentEditText = findViewById(R.id.event_comment_editText);
        final Button deleteButton = findViewById(R.id.delete_event_button);
        final Button addButton = findViewById(R.id.add_event_button);
        final Button cancelButton = findViewById(R.id.cancel_event_button);
        final Button locationButton = findViewById(R.id.event_location_button);
        final TextView header = findViewById(R.id.add_edit_event_title_text);
        final TextView eventDateText = findViewById(R.id.event_date_text);
        final Button eventLocationButton = findViewById(R.id.event_location_button);
        final ImageButton eventPhotoButton = findViewById(R.id.event_photo_button);
        final ImageView imageView = findViewById(R.id.imageView);


        // setup location services
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up activity layout
        activityLayoutSetup(isNewHabitEvent, header, addButton, deleteButton);

        // Set HabitEvent date and date TextView
        setHabitEventDateAndField(eventDateText);

        // Set up remaining fields for existing HabitEvent
        if (!isNewHabitEvent) {

            // Set name and comment fields
            habitEventNameEditText.setText(habitEvent.getEventName());
            habitEventCommentEditText.setText(habitEvent.getEventComment());

            //TODO set the location and photo fields
            Location habitEventLocation = habitEvent.getEventLocation();
            File  habitEventPhoto = habitEvent.getEventPhoto();

        }

        // Listener for image button
        // requests image permissions
        eventPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get permissions if not granted already
                if (ContextCompat.checkSelfPermission(AddRemoveEventActivity.this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(AddRemoveEventActivity.this, new String[] {Manifest.permission.CAMERA}, PERMISSIONS_REQUEST_CODE_CAMERA);
                }

                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, PERMISSIONS_REQUEST_CODE_CAMERA);
            }
        });

        // Listener for location button
        locationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchLocation();
            }
        });

        // This Listener is responsible for the logic when clicking the "OK" button
        addButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                // Retrieve user inputted data
                String habitEventName = habitEventNameEditText.getText().toString();
                String habitEventComment = habitEventCommentEditText.getText().toString();

                //TODO: get the user inputted location and photo fields

                // Note: habitEventDate is already done


                if (isNewHabitEvent) {
                    // Create a new HabitEvent
                    HabitEvent newHabitEvent = new HabitEvent(habitEventName, habit.getHabitName(),
                            habitEventDate, habitEventComment);

                    // Add the new HabitEvent to the Habit's habitEventList
                    habit.addHabitEvent(newHabitEvent);

                }
                else { // else edit the existing HabitEvent
                    habitEvent.setEventName(habitEventName);
                    habitEvent.setEventLocation(habitEventLocation);
                    habitEvent.setEventComment(habitEventComment);
                    habitEvent.setEventPhoto(habitEventPhoto);
                    // Overwrite the edited HabitEvent
                    habit.setHabitEvent(habitEventListIndex, habitEvent);
                }

                // Overwrite the edited user Habit
                user.setUserHabit(habitListIndex, habit);


                // Navigate back to launcher Activity (HabitActivity or HabitEventActivity)
                Intent intent = new Intent();
                intent.putExtra("user", user);
                setResult(RESULT_OK, intent);
                finish();
            }
        });


        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // remove the habitEvent from the corresponding Habit
                habit.deleteHabitEvent(habitEvent);
                // overwrite the edited habit
                user.setUserHabit(habitListIndex, habit);

                // Navigate back to MainActivity
                Intent intent = new Intent();
                intent.putExtra("user", user);
                setResult(RESULT_DELETE, intent);
                finish();
            }
        });

    }

    private void fetchLocation() {
        if (ContextCompat.checkSelfPermission(
                AddRemoveEventActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
                    fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                // Got last known location. In some rare situations this can be null.
                                if (location != null) {
//                                if (location != null) {
                                    // Logic to handle location object
                                    Double userLat = location.getLatitude();
                                    Double userLong = location.getLongitude();

                                    // give coordinates to mapsActivity
                                    Intent intent = new Intent(AddRemoveEventActivity.this, MapsActivity.class);
                                    intent.putExtra("LONGITUDE", userLong);
                                    intent.putExtra("LATITUDE", userLat);
                                    startActivity(intent);
                                }
                            }
                        });


        } else if (ActivityCompat.shouldShowRequestPermissionRationale(AddRemoveEventActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // In an educational UI, explain to the user why your app requires this
            // permission for a specific feature to behave as expected. In this UI,
            // include a "cancel" or "no thanks" button that allows the user to
            // continue using your app without granting the permission.
//            showInContextUI(...);
            new AlertDialog.Builder(this)
                    .setTitle("Required Location Permission")
                    .setMessage("You need location permission to access the map")
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            ActivityCompat.requestPermissions(AddRemoveEventActivity.this,
                                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                                    PERMISSIONS_REQUEST_CODE_FINE_LOCATION);
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .create()
                    .show();
        } else {
            // You can directly ask for the permission.
            ActivityCompat.requestPermissions(AddRemoveEventActivity.this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    PERMISSIONS_REQUEST_CODE_FINE_LOCATION);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSIONS_REQUEST_CODE_FINE_LOCATION) {
            if (grantResults.length>0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // permission granted
                Toast.makeText(this, "Permission Granted", Toast.LENGTH_SHORT).show();
            } else {
                // permission not granted
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PERMISSIONS_REQUEST_CODE_CAMERA) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
        }
    }

    /**
     * This method sets the Activity and button text to the appropriate titles
     * given whether the user is creating a new habit, or editing and existing one.
     * @param isNewActivityMode - Boolean indicating whether user is creating a new event
     * @param header        - A TextView object that displays the Title of the activity
     * @param addButton     - Button for creating or updating a event
     * @param deleteButton  - Button for deleting an existing event
     */
    private void activityLayoutSetup(boolean isNewActivityMode,
                                     TextView header, Button addButton, Button deleteButton) {

        // get the parent Habit name (to be displayed in header)
        String habitName = habit.getHabitName();

        if (isNewActivityMode){
            // Make activity layout correspond to mode ADD
            header.setText("Add a New " + habitName + " Event");
            // deleteButton disappears, add button says CREATE
            deleteButton.setVisibility(View.GONE);
            addButton.setText("CREATE");
        }
        else {  // Make activity layout correspond to mode EDIT
            header.setText("Edit " + habitName + " Event");
            // add button says UPDATE
            addButton.setText("UPDATE");
        }
    }

    /**
     * This method initializes a TextView object to a particular date
     * For an existing HabitEvent, the Textview object is set to the existing date
     * For a new HabitEvent, the Textview object is set to today's date
     * @param eventDateText - TextView object that will be set to the HabitEvent's start date
     */
    public void setHabitEventDateAndField(TextView eventDateText) {

        // Create Calendar object
        Calendar c = Calendar.getInstance();

        // for new HabitEvent set c to today's date
        if (isNewHabitEvent) {
            // Get today's date
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // set c to today's date;
            c.set(Calendar.YEAR, year);
            c.set(Calendar.MONTH, month);
            c.set(Calendar.DAY_OF_MONTH, day);

        }
        else { // for existing HabitEvent, set c to existing HabitEvent date
            c = habitEvent.getEventDate();
        }
        // Assign c to habitEventDate
        habitEventDate = c;
        // Convert Calendar object to String
        String dateString = DateFormat.getDateInstance().format(c.getTime());
        // Set String representation of date to eventDateText
        eventDateText.setText(dateString);
    }
}

