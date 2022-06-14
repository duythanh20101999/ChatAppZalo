package hcmute.spkt.chatappzalo.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.spkt.chatappzalo.MessageActivity;
import hcmute.spkt.chatappzalo.Model.Chat;
import hcmute.spkt.chatappzalo.Model.Users;
import hcmute.spkt.chatappzalo.R;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.MyHolder> {
    Context context;
    //biến này dùng để lưu danh sánh các tài khoản
    List<Users> usersList;
    //
    boolean isChat, isOnline;
    //biến này dùng dể lưu id 1 tài khoản
    String friendid;
    //biến này dùng để lưu tin nhắn gần nhất
    String thelastmessage;
    FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<Users> usersList, boolean isChat, boolean isOnline) {
        this.context = context;
        this.usersList = usersList;
        this.isChat = isChat;
        this.isOnline = isOnline;
    }

    //set view cho holder
    @NonNull
    @Override
    public MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.layoutofusers, parent, false);
        return new MyHolder(view);
    }

    //lấy 1 user trong danh sách các tài khoản
    @Override
    public void onBindViewHolder(@NonNull MyHolder holder, int position) {

        //lấy user hiện tại trong danh sách
        Users users = usersList.get(position);


        friendid = users.getId();

        //set username lên toolbar
        holder.username.setText(users.getUsername());

        //set hình ảnh lên toolbar
        if(users.getImageURL().equals("default")){
            holder.imageView.setImageResource(R.drawable.user);

        }else{

            Glide.with(context).load(users.getImageURL()).into(holder.imageView);
        }

        //kiểm tra trạng thái online/offline
        if(isOnline){

            if(users.getStatus().equals("online")){
                holder.image_on.setVisibility(View.VISIBLE);
                holder.image_off.setVisibility(View.GONE);
            }else{
                holder.image_on.setVisibility(View.GONE);
                holder.image_off.setVisibility(View.VISIBLE);
            }
        } else {
            holder.image_on.setVisibility(View.GONE);
            holder.image_off.setVisibility(View.GONE);
        }

        //kiểm tra tin nhắn gần nhất
        if(isChat){
            LastMessage(users.getId(), holder.last_msg);
        } else {
            holder.last_msg.setVisibility(View.GONE);
        }

    }

    //hàm này trả về số lượng tài khoản
    @Override
    public int getItemCount() {
        return usersList.size();
    }

    //Class này dùng để định nghĩa trong my holder
    class MyHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        //các biến dùng để set lên màn hình
        TextView username, last_msg;
        CircleImageView imageView, image_on, image_off;


        //hiển thị dữ liệu từ firebase lên giao diện
        public MyHolder(@NonNull View itemView) {
            super(itemView);

            username = itemView.findViewById(R.id.username_userfrag);
            imageView = itemView.findViewById(R.id.image_user_userfrag);
            image_on = itemView.findViewById(R.id.image_online);
            image_off = itemView.findViewById(R.id.image_offline);
            last_msg = itemView.findViewById(R.id.lastMessage);
            itemView.setOnClickListener(this);
        }

        //xử lý sự kiện khi nhấn vào 1 item
        @Override
        public void onClick(View v) {
            Users users = usersList.get(getAdapterPosition());
            friendid = users.getId();
            Intent intent = new Intent(context, MessageActivity.class);
            intent.putExtra("friendid", friendid);
            context.startActivity(intent);


        }
    }
    //hàm lấy tin nhắn gần nhất
    private void LastMessage(String friendid, TextView las_msg){
        thelastmessage = "default";

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot ds: snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);
                    if(firebaseUser!=null && chat != null){
                        if(chat.getSender().equals(friendid) && chat.getReceiver().equals(firebaseUser.getUid()) ||
                                chat.getSender().equals(firebaseUser.getUid()) && chat.getReceiver().equals(friendid)){
                            thelastmessage = chat.getMessage();

                        }
                        if(chat.getSender().equals(friendid) && chat.getReceiver().equals(firebaseUser.getUid())){
                            if(chat.isIsseen()){
                                las_msg.setTypeface(Typeface.defaultFromStyle(Typeface.ITALIC));
                            } else {
                                las_msg.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
                            }
                        }
                    }
                }
                switch (thelastmessage){
                    case "default":
                        las_msg.setText("Không có tin nhắn");
                        break;

                    default:
                        las_msg.setText(thelastmessage);
                }

                thelastmessage = "default";
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
