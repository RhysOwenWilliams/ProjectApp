package project.projectapp.Account;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import project.projectapp.MainActivity;
import project.projectapp.R;

public class ProfileActivity extends AppCompatActivity {

    private final String CHANGE_USERNAME = "Change Username";
    private final String CHANGE_PASSWORD = "Change Password";

    private CircleImageView profileIcon, editImage;
    private TextView listText;
    private ListView options;
    private Toolbar toolbar;

    private String user;

    private ArrayList<String> listOfOptions;
    private ArrayAdapter<String> adapter;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_activity);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        profileIcon = findViewById(R.id.profile_details_profile_icon);
        editImage = findViewById(R.id.profile_details_edit_image);
        options = findViewById(R.id.profile_details_options);


        editImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editProfileImage();
            }
        });

        toolbar = findViewById(R.id.team_details_toolbar);
        setSupportActionBar(toolbar);

        // The code below sets the colour of the back button to white
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.getNavigationIcon()
                .setColorFilter(getResources().getColor(R.color.colorSplash),
                        PorterDuff.Mode.SRC_ATOP);

        setSelectableListItems();
        addOptionsToList();
        setUserDetails();
    }

    private void editProfileImage() {
        Toast.makeText(ProfileActivity.this, "Edit Image", Toast.LENGTH_SHORT).show();
    }

    /**
     * Contains the code for the list of selectable changes for a profile
     */
    private void setSelectableListItems() {
        listOfOptions = new ArrayList<>();
        adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listOfOptions){
            @Override
            public View getView(int position, View convertView, ViewGroup viewGroup){
                View view = super.getView(position, convertView, viewGroup);

                listText = view.findViewById(android.R.id.text1);
                listText.setTextColor(getResources().getColor(R.color.colorPrimary));

                return view;
            }
        };

        int[] colors = {0, 0xFFc1bfbe, 0};
        options.setDivider(new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors));
        options.setDividerHeight(1);

        options.setAdapter(adapter);
        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                Toast.makeText(ProfileActivity.this, item, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void addOptionsToList() {
        listOfOptions.add(CHANGE_USERNAME);
        listOfOptions.add(CHANGE_PASSWORD);
    }

    private void setUserDetails() {
        Glide.with(this).load(firebaseUser.getPhotoUrl()).into(profileIcon);
    }

    /**
     * Called when an option is selected, in this case the back button since it is the only option
     * @param item - our menu items, so the back button
     * @return - the parent class' onOptionsItemSelected method
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Default onBackPressed, returns to the previous activity
     */
    @Override
    public void onBackPressed() {
        if (getFragmentManager().getBackStackEntryCount() > 0) {
            getFragmentManager().popBackStack();
        } else {
            super.onBackPressed();
        }
    }
}