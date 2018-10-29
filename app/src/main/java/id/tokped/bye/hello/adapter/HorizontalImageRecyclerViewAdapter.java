package id.tokped.bye.hello.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import id.tokped.bye.hello.PortalDetails;
import id.tokped.bye.hello.R;
import id.tokped.bye.hello.config.Rocket;
import id.tokped.bye.hello.model.PortalModel;

import java.util.List;

public class HorizontalImageRecyclerViewAdapter extends RecyclerView.Adapter<HorizontalImageRecyclerViewAdapter.ViewHolder> {
    private List<PortalModel> value_list;
    private Context context;


    public HorizontalImageRecyclerViewAdapter(Context ctx, List<PortalModel> value_list) {
        this.value_list = value_list;
        this.context = ctx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View itemLayoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_horizontal_listview, null);

        return new ViewHolder(itemLayoutView);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {

        new Rocket.AsyncTaskLoadImagePortalView(viewHolder.portal_image).execute(value_list.get(position).getPortal_image());

        viewHolder.portal_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            Intent i = new Intent(context, PortalDetails.class);
                i.putExtra("portal_init", value_list.get(position).getPortal_init());
                i.putExtra("portal_image", value_list.get(position).getPortal_image());
                i.putExtra("portal_name", value_list.get(position).getPortal_name());
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            Activity activity = (Activity) context;
            activity.startActivity(i);
            activity.overridePendingTransition(R.anim.slide_in_up, R.anim.stay);
            }
        });
    }

    // Return the size of your itemsData (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return value_list.size();
    }

    // inner class to hold a reference to each item of RecyclerView
    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView portal_image;
        public ViewHolder(View itemLayoutView) {
            super(itemLayoutView);
            portal_image = (ImageView) itemLayoutView.findViewById(R.id.portal_image);
        }
    }
}
