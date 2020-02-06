package uz.fti.ag.memoproject;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.scalified.fab.ActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import es.dmoral.toasty.Toasty;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.Adapter recyclerAdapter;
    RecyclerView.LayoutManager recyclerLayout;

    List<ListViewItem> items;
    ActionButton actionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To remove status bar:
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);
        //Ask two permissions at the beginning:
        int PERMISSION_ALL = 1;
        String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};

        if(!hasPermissions(this, PERMISSIONS)){
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }

        //for database
        final DatabaseHandler db = new DatabaseHandler(this);

        // And then find it within the content view:
        actionButton = (ActionButton) findViewById(R.id.action_button);
        // To set the button type:
        actionButton.setType(ActionButton.Type.DEFAULT);
        // To set button color for normal state:
        actionButton.setButtonColor(getResources().getColor(R.color.floatin_not_pressed));
        // To set button color for pressed state:
        actionButton.setButtonColorPressed(getResources().getColor(R.color.floatin_pressed));
        // To set button image:
        actionButton.setImageResource(R.drawable.fab_plus_icon);

        // To set show animation:
        actionButton.setShowAnimation(ActionButton.Animations.FADE_IN);

        //Initialize recycleView
        recyclerView = (RecyclerView)findViewById(R.id.listview1);
        recyclerView.setHasFixedSize(true);

        recyclerLayout = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayout);

        items = new ArrayList<>();



        recyclerAdapter = new MyAdapter(items);
        recyclerView.setAdapter(recyclerAdapter);



        List<ListViewItem> allMemos = db.getAllMemos();
        //Fetch data from database and add to items ArrayList:
        for (ListViewItem cn : allMemos) {

            items.add(new ListViewItem(cn.getID(),cn.getIcon(),cn.getTitle(),cn.getTime()));

        }

        recyclerAdapter.notifyDataSetChanged(); //update the recycleview

        //Touch listener for recycleview items
        recyclerView.addOnItemTouchListener(
                //on simple click
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {

                        ListViewItem getItem=items.get(position);//get clocked item position
                        Intent intent = new Intent(MainActivity.this, AddMemoActivity.class); //create intent
                        intent.putExtra("new",false); //boolean value to check whether we create new or update existing memo

                        //Get values from database:
                        intent.putExtra("image", db.getMemo(getItem.getID()).getIcon());
                        intent.putExtra("title", db.getMemo(getItem.getID()).getTitle());
                        intent.putExtra("time", db.getMemo(getItem.getID()).getTime());
                        intent.putExtra("id", db.getMemo(getItem.getID()).getID());
                        startActivity(intent);
                        finish();
                    }
                    //on long click
                    @Override public void onLongItemClick(View view, final int position) {
                        // Call alert dialog:
                        AlertDialog.Builder builder;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            builder = new AlertDialog.Builder(MainActivity.this);
                        } else {
                            builder = new AlertDialog.Builder(MainActivity.this);
                        }
                        builder.setTitle("Delete entry")
                                .setMessage("Are you sure you want to delete this entry?")
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        //First delete from database
                                        db.deleteMemo(items.get(position));
                                        //REMOVE: There are 3 steps to remove an item from a RecyclerView
                                        items.remove(position);
                                        recyclerAdapter.notifyItemRemoved(position);
                                        recyclerAdapter.notifyItemRangeChanged(position, items.size());
                                        Toasty.success(getApplicationContext(), "Delete successfull!", Toast.LENGTH_LONG, true).show();
                                    }
                                })
                                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // do nothing
                                    }
                                })
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                })
        );
        //Add floating button action:
        actionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(MainActivity.this, AddMemoActivity.class);
                intent.putExtra("new",true); //boolean value to check whether we create new or update existing memo
                startActivity(intent);
                finish();
            }
        });


    }
    // code for checking permissions
    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
        recyclerAdapter.notifyDataSetChanged();
    }

    /*
    * Part of code that handles "Back" button
    * If we click once, it will show warning message "Press again to exit"
    * If we click again, application will be closed
    * */
    private Boolean exit = false;
    @Override
    public void onBackPressed() {
        if (exit) {
            finish();
        } else {
            Toasty.custom(MainActivity.this, "Press again to exit", getResources().getDrawable(R.drawable.warning),
                    Color.BLACK, Toast.LENGTH_SHORT, true, true).show();
            exit = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    exit = false;
                }
            }, 3 * 1000);

        }

    }
}
