package dev.sagar.smsblocker.ux.activities;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.crashlytics.android.Crashlytics;

import java.util.List;

import dev.sagar.smsblocker.R;
import dev.sagar.smsblocker.tech.beans.SMS;
import dev.sagar.smsblocker.tech.utils.AnalyticsUtil;
import dev.sagar.smsblocker.tech.utils.InboxUtil;
import dev.sagar.smsblocker.tech.utils.LogUtil;
import dev.sagar.smsblocker.ux.adapters.RVStarredSMSAdapter;
import io.fabric.sdk.android.Fabric;

public class StarredSMSActivity extends AppCompatActivity implements RVStarredSMSAdapter.Callback{

    //Log Initiate
    private LogUtil log = new LogUtil(this.getClass().getName());

    //Java Android
    private View parentHolder;
    private RecyclerView rvStarredSMS;
    private RVStarredSMSAdapter adapter;
    private LinearLayoutManager layoutManager;
    private Paint p = new Paint();

    //Java Core
    private List<SMS> smses;
    private InboxUtil inboxUtil;


    private void init(){
        final String methodName =  "init()";
        log.justEntered(methodName);

        parentHolder = findViewById(android.R.id.content);
        rvStarredSMS = findViewById(R.id.rv_starred_sms);
        inboxUtil = new InboxUtil(this);

        log.returning(methodName);
    }

    private void getData(){
        final String methodName =  "getData()";
        log.justEntered(methodName);

        smses = inboxUtil.getSavedSMSes(InboxUtil.SORT_DESC);

        log.returning(methodName);
    }

    private void addListeners(){
        final String methodName =  "addListeners()";
        log.justEntered(methodName);

        log.returning(methodName);
    }

    private void process(){
        final String methodName =  "process()";
        log.justEntered(methodName);
        adapter = new RVStarredSMSAdapter(this, smses, this);


        RVDragCallback dragCallback = new RVDragCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        new ItemTouchHelper(dragCallback).attachToRecyclerView(rvStarredSMS);

        layoutManager = new LinearLayoutManager(this);
        rvStarredSMS.setLayoutManager(layoutManager);
        rvStarredSMS.setAdapter(adapter);
        log.returning(methodName);
    }



    class RVDragCallback extends ItemTouchHelper.SimpleCallback {

        public RVDragCallback(int dragDirs, int swipeDirs) {
            super(dragDirs, swipeDirs);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final int position = viewHolder.getAdapterPosition();
            final SMS sms = adapter.itemRemoved(position);

            Snackbar.make(parentHolder, "SMS Unstarred", Snackbar.LENGTH_LONG)
                    .setAction(R.string.label_undo, new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            adapter.restore(position, sms);
                            int topVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                            int lastVisiblePosition = layoutManager.findLastVisibleItemPosition();
                            if(position < topVisiblePosition || position > lastVisiblePosition ) {
                                rvStarredSMS.scrollToPosition(position);
                            }
                        }
                    })
            .show();
        }

        @Override
        public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
            /*Bitmap icon;
            if(actionState == ItemTouchHelper.ACTION_STATE_SWIPE){

                View itemView = viewHolder.itemView;
                float height = (float) itemView.getBottom() - (float) itemView.getTop();
                float width = 25;

                if(dX > 0){
                    p.setColor(Color.parseColor("#388E3C"));
                    RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX,(float) itemView.getBottom());
                    c.drawRect(background,p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_star_white_24dp);
                    RectF icon_dest = new RectF((float) itemView.getLeft() + width ,(float) 50,(float) itemView.getLeft()+ 2*width,(float)50 );
                    //c.drawBitmap(icon,null,icon_dest,p);
                    drawText("Unstar", c, icon_dest, p);
                } else {
                    p.setColor(Color.parseColor("#D32F2F"));
                    RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(),(float) itemView.getRight(), (float) itemView.getBottom());
                    c.drawRect(background,p);
                    icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_star_white_24dp);
                    RectF icon_dest = new RectF((float) itemView.getRight() - 2*width ,(float) itemView.getTop() + width,(float) itemView.getRight() - width,(float)itemView.getBottom() - width);
                    c.drawBitmap(icon,null,icon_dest,p);
                    drawText("Unstar", c, icon_dest, p);
                }
            }*/
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        }

        private void drawText(String text, Canvas c, RectF button, Paint p) {
            float textSize = 60;
            p.setColor(Color.WHITE);
            p.setAntiAlias(true);
            p.setTextSize(textSize);

            float textWidth = p.measureText(text);
            c.drawText(text, button.centerX()-(textWidth/2), button.centerY()+(textSize/2), p);
        }
    }

    //--- AppCompatActivity Overrides Starts ---
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final String methodName =  "onCreate(Bundle)";
        log.justEntered(methodName);

        setContentView(R.layout.activity_starred_sms);
        AnalyticsUtil.start(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Window window = this.getWindow();
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        init();
        getData();
        process();
        addListeners();
    }
    //--- AppCompatActivity Overrides Ends ---

}
