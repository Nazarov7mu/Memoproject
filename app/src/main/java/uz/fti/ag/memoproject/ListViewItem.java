package uz.fti.ag.memoproject;

/**
 * Created by Javokhir on 05.08.2017.
 */
import android.graphics.drawable.Drawable;

public class ListViewItem {

    private String iconDrawable ;
    private String titleStr ;
    private String timeStr ;
    private int _id;


    public  ListViewItem(){
        //empty constructor
    }
    //constructor
    public ListViewItem(int _id, String iconDrawable, String titleStr, String timeStr) {
        this._id=_id;
        this.iconDrawable = iconDrawable;
        this.titleStr = titleStr;
        this.timeStr = timeStr;
    }
    //setters
    public void setIcon(String icon) {
        iconDrawable = icon ;
    }
    public void setTitle(String title) {
        titleStr = title ;
    }
    public void setTime(String time) {
        timeStr = time ;
    }
    public void setID(int id) { this._id = id; }

    //getters
    public String getIcon() {
        return this.iconDrawable ;
    }
    public String getTitle() {
        return this.titleStr ;
    }
    public String getTime() {
        return this.timeStr ;
    }
    public int getID(){ return this._id; }


}
