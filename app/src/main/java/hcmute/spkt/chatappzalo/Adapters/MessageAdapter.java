package hcmute.spkt.chatappzalo.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.spkt.chatappzalo.Model.Chat;
import hcmute.spkt.chatappzalo.R;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MyViewHolder> {

    Context context;
    //tạo 1 listchat để chứa tất cả tin nhắn của sender và receiver
    List<Chat> chatList;
    //hình ảnh để load lên trang chat
    String imageURL;

    public static final int MESSAGE_RIGHT = 0; //tin nhắn phía tài khoản
    public static final int MESSAGE_LEFT = 1; //tin nhắn phía đối phương

    public MessageAdapter(Context context, List<Chat> chatList, String imageURL) {
        this.context = context;
        this.chatList = chatList;
        this.imageURL = imageURL;
    }

    //set view cho holder
    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if(viewType == MESSAGE_RIGHT){
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right, parent, false);
            return new MyViewHolder(view);
        }else{
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left, parent, false);
            return new MyViewHolder(view);
        }
    }

    //hiển thị tin nhắn và avarta của receiver lên màn hình chat
    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Chat chat = chatList.get(position);
        holder.messagetext.setText(chat.getMessage());

        if(imageURL.equals("default")){
            holder.imageView.setImageResource(R.drawable.user);
        }else{
            Glide.with(context).load(imageURL).into(holder.imageView);
        }

        if(position == chatList.size() - 1){
            if(chat.isIsseen()){

                holder.seen.setText("seen");
            } else {
                holder.seen.setText("Delivered");
            }
        } else {
            holder.seen.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        return chatList.size();
    }

    //Class này dùng để định nghĩa trong my holder
    class MyViewHolder extends RecyclerView.ViewHolder{
        TextView messagetext, seen;
        CircleImageView imageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            messagetext = itemView.findViewById(R.id.show_message);
            imageView = itemView.findViewById(R.id.chat_image);
            seen = itemView.findViewById(R.id.text_Seen);
        }
    }

    //get view cho tin nhắn từ phía sender và receiver
    @Override
    public int getItemViewType(int position) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        if(chatList.get(position).getSender().equals(user.getUid())){
            return MESSAGE_RIGHT;
        }
        else {
            return MESSAGE_LEFT;
        }
    }
}
