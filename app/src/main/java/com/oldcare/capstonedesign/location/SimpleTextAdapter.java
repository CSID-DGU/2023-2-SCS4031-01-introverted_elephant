package com.oldcare.capstonedesign.location;

import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import com.oldcare.capstonedesign.R;

public class SimpleTextAdapter extends RecyclerView.Adapter<SimpleTextAdapter.SimpleTextViewHolder> implements SimpleTextItemTouchHelperCallback.ItemTouchListener {
    private List<String> simpleTextList;
    private OnStartDragViewHolderListener listener;

    public SimpleTextAdapter(List<String> simpleTextList, OnStartDragViewHolderListener listener) {
        this.simpleTextList = simpleTextList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public SimpleTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_simple_text, parent, false);
        return new SimpleTextViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SimpleTextViewHolder holder, int position) {
        holder.bind(simpleTextList.get(position));
    }

    @Override
    public int getItemCount() {
        if (simpleTextList != null) {
            return simpleTextList.size();
        }
        return 0;
    }

    @Override
    public boolean moveItem(int fromPosition, int toPosition) {
        String simpleText = simpleTextList.get(fromPosition);
        simpleTextList.remove(fromPosition);
        simpleTextList.add(toPosition, simpleText);
        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void removeItem(int position) {
        simpleTextList.remove(position);
        notifyItemRemoved(position);
    }

    public class SimpleTextViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener, GestureDetector.OnGestureListener {
        private TextView simpleText;
        private GestureDetector gestureDetector;

        public SimpleTextViewHolder(@NonNull View itemView) {
            super(itemView);
            simpleText = itemView.findViewById(R.id.simple_text);
            gestureDetector = new GestureDetector(itemView.getContext(), this);
            itemView.findViewById(R.id.drag_handle).setOnTouchListener(this);
        }

        public void bind(String city) {
            simpleText.setText(city);
        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            return gestureDetector.onTouchEvent(event);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            if (listener != null) {
                listener.onStartDragViewHolder(this);
                return true;
            }
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    public interface OnStartDragViewHolderListener {
        void onStartDragViewHolder(RecyclerView.ViewHolder viewHolder);
    }
}
