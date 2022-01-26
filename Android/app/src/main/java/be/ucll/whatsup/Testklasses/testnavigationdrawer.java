package be.ucll.whatsup.Testklasses;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toolbar;

import com.google.android.material.navigation.NavigationView;

import be.ucll.whatsup.Chat.MakeGroupChat;
import be.ucll.whatsup.Contacts.ContactList;
import be.ucll.whatsup.R;
import be.ucll.whatsup.SettingsMenu;

public class testnavigationdrawer extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testnavigationdrawer);

        drawerLayout = findViewById(R.id.nav_view);
        NavigationView navigationView = findViewById(R.id.navigation_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Intent intent;

        switch (item.getItemId()){
            case R.id.nav_settings:
                intent = new Intent(this, SettingsMenu.class);
                startActivity(intent);
                break;
            case R.id.nav_contacts:
                intent = new Intent(this, ContactList.class);
                startActivity(intent);
                break;
            case R.id.nav_group:
                intent = new Intent(this, MakeGroupChat.class);
                startActivity(intent);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);

        return true;
    }
}