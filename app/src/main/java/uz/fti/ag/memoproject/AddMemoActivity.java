package uz.fti.ag.memoproject;

import android.Manifest;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.mindorks.paracamera.Camera;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;

import es.dmoral.toasty.Toasty;


public class AddMemoActivity extends AppCompatActivity  {

    ImageButton take_a_photo;
    Button data_picker;
    EditText memoET;
    Button saveBTN;
    Button cancelBTN;

    private int year = 2017;/*default values*/
    private int month = 7;
    private int day = 5;

    private static final int CAMERA = 1;
    private static final int GALLERY = 2;


    String imagePath="";
    Bitmap myBitmap;//used for setting image and reducing quality of original image
    // Create global camera reference in an activity or fragment
    Camera camera;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //To remove status bar:
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.add_memo_activity);
        //Checking permissions
        if (ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED)
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.CAMERA}, 777);



        /*camera settings*/
        camera = new Camera.Builder()
                .resetToCorrectOrientation(true)// it will rotate the camera bitmap to the correct orientation from meta data
                .setTakePhotoRequestCode(1)
                .setDirectory("pics")
                .setName("ali_" + System.currentTimeMillis())
                .setImageFormat(Camera.IMAGE_JPEG)
                .setCompression(75)
                .setImageHeight(1000)// it will try to achieve this height as close as possible maintaining the aspect ratio;
                .build(this);


        //isStoragePermissionGranted();

        //getting current year, month and day*******************************************************
        Calendar myCalendar = Calendar.getInstance();
        year = myCalendar.get(Calendar.YEAR);
        month = myCalendar.get(Calendar.MONTH);
        day = myCalendar.get(Calendar.DAY_OF_MONTH);
        //******************************************************************************************


        //initialization part***********************************************************************
        take_a_photo = (ImageButton)findViewById(R.id.take_a_photo_icon);
        data_picker = (Button)findViewById(R.id.DataPickerXML);
        memoET = (EditText) findViewById(R.id.memoTextXML);
        saveBTN = (Button) findViewById(R.id.SaveButtonXML);
        cancelBTN=(Button) findViewById(R.id.CloseButtonXML);
        final DatabaseHandler db = new DatabaseHandler(this);

        //******************************************************************************************


        //listeners part for all ui components******************************************************
        take_a_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent,
                        "Select Picture"), SELECT_PICTURE);*/
                showPictureDialog();
            }
        });

        data_picker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(0);
            }
        });
        //******************************************************************************************

        final Intent intent = getIntent();
        final boolean fromnew=intent.getBooleanExtra("new",true);
        String title;
        String time;
        String savedImage;
        int id=123;
        if(fromnew)//if we came from ADD button
        {
            memoET.setText("");
            data_picker.setText("Choose Date");
        }
        else//if we want to UPDATE memo
        {
            title= intent.getStringExtra("title");
            time=intent.getStringExtra("time");
            id = intent.getIntExtra("id",123);
            savedImage = intent.getStringExtra("image");

            BitmapFactory.Options options = new BitmapFactory.Options();/*size reducing*/
            options.inPreferredConfig = Bitmap.Config.RGB_565;
            options.inSampleSize = 4;/*reduce four times original image*/
            myBitmap = BitmapFactory.decodeFile(savedImage, options);

            take_a_photo.setImageBitmap(myBitmap);
            imagePath=savedImage;
            memoET.setText(title);
            data_picker.setText(time);
            saveBTN.setText("update");
        }

        final int finalId = id;
        saveBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String memotext=memoET.getText().toString();
                String dateText= data_picker.getText().toString();
                if(imagePath.length()==0||memotext.length()==0||dateText.equals("Choose Date"))
                {
                    Toasty.error(getApplicationContext(),"Wrong input!!",Toast.LENGTH_LONG,true).show(); //Error toast
                }
                else{
                    if(fromnew)//if we came from "add new" button just add memo
                    {
                        db.addMemo(new ListViewItem(1,imagePath,memotext
                                ,dateText));
                        Toasty.info(getApplicationContext(), "New memo is saved", Toast.LENGTH_SHORT, true).show();
                    }
                    else//if we want to update memo
                    {
                        db.updateMemo(new ListViewItem(finalId,imagePath,memoET.getText().toString()
                                , data_picker.getText().toString()));
                        Toasty.info(getApplicationContext(), "Memo is updated", Toast.LENGTH_SHORT, true).show();
                    }

                    onBackPressed();
                }

            }
        });
        cancelBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        Intent intent1= new Intent(AddMemoActivity.this, MainActivity.class);
        startActivity(intent1);
        //Recycle bitmap to prevent memory leak
        if(myBitmap !=null && !myBitmap.isRecycled()){
            myBitmap.recycle();
            myBitmap = null;
        }
        finish();

    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        return new DatePickerDialog(this, datePickerListener, year, month, day);
    }
    private DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int selectedYear,
                              int selectedMonth, int selectedDay) {
            day = selectedDay;
            month = selectedMonth;
            year = selectedYear;
            data_picker.setText(selectedDay + " / " + (selectedMonth+1) + " / "
                    + selectedYear);
        }
    };

    private void showPictureDialog(){ //Dialog window to choose image
        final AlertDialog.Builder pictureDialog = new AlertDialog.Builder(this);
        pictureDialog.setTitle("Select");
        String[] pictureDialogItems = {
                "From gallery",
                "Take a photo" };
        pictureDialog.setItems(pictureDialogItems,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case 0:
                                choosePhotoFromGallary();
                                break;
                            case 1:
                                takePhotoFromCamera();
                                break;
                        }
                    }
                });
        pictureDialog.setNegativeButton("CLOSE",
                new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {


                        dialog.cancel();
                    }
                });
        pictureDialog.show();
    }

    public void choosePhotoFromGallary() {
        Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(galleryIntent, GALLERY);
    }


    private void takePhotoFromCamera() {

        // Call the camera takePicture method to open the existing camera
        try {
            camera.takePicture();
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //Save image into gallery
    public String saveImage(Bitmap myBitmap) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        myBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File wallpaperDirectory = new File(
                Environment.getExternalStorageDirectory() + "/" + "just" + "/");
        // have the object build the directory structure, if needed.
        if (!wallpaperDirectory.exists()) {
            wallpaperDirectory.mkdirs();
        }

        try {
            File f = new File(wallpaperDirectory, Calendar.getInstance()
                    .getTimeInMillis() + ".jpg");
            f.createNewFile();
            FileOutputStream fo = new FileOutputStream(f);
            fo.write(bytes.toByteArray());
            MediaScannerConnection.scanFile(this,
                    new String[]{f.getPath()},
                    new String[]{"image/jpeg"}, null);
            fo.close();
            //Log.d("TAG", "File Saved::--->" + f.getAbsolutePath());
            imagePath=f.getAbsolutePath();
            return f.getAbsolutePath();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        return "";
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GALLERY && resultCode == RESULT_OK) {
            if (data != null) {
                Uri contentURI = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), contentURI);
                    String path = saveImage(bitmap);
                    imagePath=path;
                    //Toast.makeText(AddMemoActivity.this, "Image Saved!", Toast.LENGTH_SHORT).show();
                    take_a_photo.setImageBitmap(bitmap);

                } catch (IOException e) {
                    //e.printStackTrace();
                    Toasty.error(AddMemoActivity.this, "Failed!", Toast.LENGTH_SHORT,true).show();
                }
            }

        } else if(requestCode == Camera.REQUEST_TAKE_PHOTO){
            Bitmap bitmap = camera.getCameraBitmap();
            if(bitmap != null) {
                saveImage(bitmap);
                take_a_photo.setImageBitmap(bitmap);
            }else{
                Toasty.warning(this.getApplicationContext(),"Picture not taken!",Toast.LENGTH_SHORT,true).show();
            }
        }
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                //Log.v(TAG,"Permission is granted");
                return true;
            } else {

                //Log.v(TAG,"Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            //Log.v(TAG,"Permission is granted");
            return true;
        }
    }

}