# Memoproject
 
# Application name: " Just Do It. LATER "

Team name: "Advanced Geeks"

Team members:
-Javokhir Nazarov 
-Shukhrat Mirrakhimov 

Short description:
"Just Do It. LATER" (JDIL) application is simple memo app that uses SQLite database for storing memos of user. Memo text and date are stored in the database as String, while for image we stored String of absolute path to that image. We used bitmaps to put images to ImageViews. Every memo has its unique primary key ID in the database. All images are saved in the gallery in "just" folder.
To add new memo click on the floating button with PLUS icon. 
To delete memo long click on this memo. 
If user tries to save memo without image, text or date he will get notice of "WRONG INPUT".
To exit application click on "back" button twice(user will get warning message after first click). 

While designing application we tried to comply with all the principles of MATERIAL DESING  (https://material.io/guidelines/). We faced and solved problems with optimizing bitmaps to prevent memory leaks and runtime crashes. But anyway after adding a lot of memos, application works slowly, because of big number of displayed images in one screen. 

Used libraries:
-Floating Action Button Library for Android (https://github.com/Scalified/fab)
-Recycleview and cards (https://developer.android.com/training/material/lists-cards.html)
-Toasty (https://github.com/GrenderG/Toasty)
-ParaCamera (https://github.com/janishar/ParaCamera)

Also added: 
-We used custom styles for buttons (drawable/border.xml) and custom gradient background in material style (drawable/gradient_background.xml).
-Custom splash screen (layout/splash_screen.xml) with animation (res/anim/slide_in.xml & res/anim/slide_out.xml & layout/listview_item.xml).
-Custom application icon (minmap/ic_launcher.png).
-Custom toolbar in MainActivity (layout/toolbar.xml).
-We removed status bar programmically in two activities. 
-RecyclerItemClickListener.java for handling clicks on Recycleview Item. Solution provided by Jacob Tabak on stackoverflow (https://stackoverflow.com/a/26196831).
-Own class to handle all database CRUD(Create, Read, Update and Delete) operations (DatabaseHandler.java).
-Custom RecycleView and Adapter from practice lectures on Mobile Programming (ListViewItem.java & MyAdapter.java).
-Only portrait orientation (AndroidManifest.xml):
***With android:configChanges="orientation" you tell Android that you will be responsible of the changes of orientation.
***android:screenOrientation="portrait" you set the default orientation mode.

