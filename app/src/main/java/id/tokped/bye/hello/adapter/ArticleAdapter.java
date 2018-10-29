package id.tokped.bye.hello.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import id.tokped.bye.hello.PortalWebview;
import id.tokped.bye.hello.R;
import id.tokped.bye.hello.config.Rocket;
import id.tokped.bye.hello.helper.RoundedImageView;
import id.tokped.bye.hello.model.ArticleModel;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

public class ArticleAdapter extends RecyclerView.Adapter<ArticleAdapter.ViewHolder> {
    private List<ArticleModel> itemsData;
    private Context context;

    public ArticleAdapter(Context ctx, List<ArticleModel> itemsData) {
        this.itemsData = itemsData;
        this.context = ctx;
    }

    @Override
    public ArticleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.article_category_list_view, null));
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        if(itemsData.get(position).getCategory().equals("headlines")) {
            viewHolder.category.setVisibility(View.VISIBLE);
        } else if(itemsData.get(position).getCategory().equals("searching")){
            viewHolder.category.setVisibility(View.VISIBLE);
            viewHolder.category.setText("Keyword By : " + Html.fromHtml("<font color='#FFFFFF'>"+itemsData.get(position).getKeyword()+"</font>"));
        }

        viewHolder.title.setText(itemsData.get(position).getTitle());
        viewHolder.description.setText(itemsData.get(position).getDescription());
        if(itemsData.get(position).getAuthor() != null) {
            viewHolder.author.setText(itemsData.get(position).getAuthor());
        } else {
            viewHolder.author.setVisibility(View.GONE);
        }

        try {
            viewHolder.publish_date.setText(convertToNewFormat(itemsData.get(position).getPublishedAt()));
        } catch (ParseException e){
            e.printStackTrace();
        }

        viewHolder.image_photo.setScaleType(RoundedImageView.ScaleType.CENTER_CROP);
        viewHolder.image_photo.setFitsSystemWindows(true);
        new Rocket.AsyncTaskLoadImageCustomView(viewHolder.image_photo).execute(itemsData.get(position).getUrlToImage());

        viewHolder.relative_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(context, PortalWebview.class);
                i.putExtra("url_news", itemsData.get(position).getUrl());
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                Activity activity = (Activity) context;
                activity.startActivity(i);
                activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            }
        });

    }

    public static String convertToNewFormat(String dateStr) throws ParseException {
        TimeZone utc = TimeZone.getTimeZone("UTC");
        SimpleDateFormat sourceFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat destFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sourceFormat.setTimeZone(utc);
        Date convertedDate = sourceFormat.parse(dateStr);
        return destFormat.format(convertedDate);
    }


    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return itemsData.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView category;
        public TextView title;
        public TextView author;
        public TextView description;
        public TextView publish_date;
        public RoundedImageView image_photo;
        public RelativeLayout relative_layout;

        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            category = (TextView) itemLayoutView.findViewById(R.id.category);
            title = (TextView) itemLayoutView.findViewById(R.id.title);
            author = (TextView) itemLayoutView.findViewById(R.id.author);
            description = (TextView) itemLayoutView.findViewById(R.id.description);
            publish_date = (TextView) itemLayoutView.findViewById(R.id.publish_date);
            image_photo = (RoundedImageView) itemLayoutView.findViewById(R.id.image_photo);
            relative_layout = (RelativeLayout) itemLayoutView.findViewById(R.id.relative_layout);
        }
    }
}
