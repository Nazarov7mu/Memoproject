package uz.fti.ag.memoproject;

/**
 * Created by Javokhir on 05.08.2017.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import org.w3c.dom.Text;

import java.io.File;
import java.util.List;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    List<ListViewItem> data;


    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public ImageView iconImage;
        public TextView titleStr;
        public TextView timeStr;
        public ViewHolder(View itemview) {
            super(itemview);
            iconImage = (ImageView)itemview.findViewById(R.id.imageView1);
            titleStr = (TextView)itemview.findViewById(R.id.textView1);
            timeStr = (TextView)itemview.findViewById(R.id.textView2);
        }
    }

    @Override
    public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).
                inflate(R.layout.listview_item, parent, false);

        ViewHolder vh = new ViewHolder(v);
        return vh;

    }

    public MyAdapter(List<ListViewItem> input) {
        data = input;
    }

    @Override
    public void onBindViewHolder(MyAdapter.ViewHolder holder, int position) {

        /*reduce size of bitmap otherwise it may cause out of memory error*/
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.RGB_565;
        options.inSampleSize = 4;
        Bitmap bitmap = BitmapFactory.decodeFile(data.get(position).getIcon(), options);

        holder.iconImage.setImageBitmap(bitmap);
        holder.titleStr.setText(data.get(position).getTitle());
        holder.timeStr.setText(data.get(position).getTime());

    }

    @Override
    public int getItemCount() {
        return data.size();
    }


}
