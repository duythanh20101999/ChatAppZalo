package hcmute.spkt.chatappzalo;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.spkt.chatappzalo.Adapters.MessageAdapter;
import hcmute.spkt.chatappzalo.Model.Chat;
import hcmute.spkt.chatappzalo.Model.Users;

public class MessageActivity extends AppCompatActivity {
    //id người nhận, tin nhắn, id người gửi
    String friendid, message, myid;

    //các biến dùng để set cho toolbar
    CircleImageView imageViewOnToolbar;
    TextView usernameOnToolbar;
    Toolbar toolbar;

    //biến lưu thông tin tài khoản từ firebase
    FirebaseUser firebaseUser;

    //các biến set giao diện
    EditText et_message;
    Button send;


    DatabaseReference reference;

    //danh sách các tin nhắn
    List<Chat> chatList;
    MessageAdapter messageAdapter;
    RecyclerView recyclerView;
    ValueEventListener seenlistener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        //Set toolbar
        toolbar = findViewById(R.id.toolbar_message);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageViewOnToolbar = findViewById(R.id.profile_image_toolbar_message);
        usernameOnToolbar = findViewById(R.id.username_ontoolbar_message);

        //set giao diện khi nhắn tin
        recyclerView = findViewById(R.id.recyclerview_messages);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(layoutManager);

        et_message = findViewById(R.id.edit_message_text);
        send = findViewById(R.id.send_messsage_btn);

        //lấy tài khoản đang đăng nhập
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        myid = firebaseUser.getUid();

        friendid = getIntent().getStringExtra("friendid"); //lấy id khi click vào item của người dùng đó
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //chỉ đường đến child cần dùng trong firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(friendid);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //lấy dữ liệu từ firebase gán vào đối tượng users chứa tất cả thông tin của tài khoản receiver
                Users users = snapshot.getValue(Users.class);
                //gán username của tài khoản lên toolbar
                usernameOnToolbar.setText(users.getUsername());
                //gán hình ảnh, nếu không có ảnh sẽ set ảnh mặc định
                if(users.getImageURL().equals("default")){
                    imageViewOnToolbar.setImageResource(R.drawable.user);
                }else{
                    Glide.with(getApplicationContext()).load(users.getImageURL()).into(imageViewOnToolbar);
                }

                //gọi hàm đọc tin nhắn để hiện thị tất cả tin nhắn
                readMessage(myid, friendid, users.getImageURL());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        seenMessage(friendid);

        //xử lý các sự kiện ở ô nhập tin nhắn
        et_message.addTextChangedListener(new TextWatcher() {
            //set bật/tắt nút gửi khi tin nhắn trống
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if(s.toString().length()>0){
                    send.setEnabled(true);
                }else{
                    send.setEnabled(false);
                }
            }

            //xử lý đoạn tin nhắn
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = et_message.getText().toString();
                if(!text.startsWith("")){
                    et_message.getText().insert(0,"");
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        //xử lý sự kiện khi bấm nút gửi
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                message = et_message.getText().toString();
                sendMessage(myid, friendid, message);
                et_message.setText(" ");
            }
        });


    }

    //Hàm set trang thái tin nhắn đã đọc
    private void seenMessage(String friendid){
        reference = FirebaseDatabase.getInstance().getReference("Chat");

        seenlistener = reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds: snapshot.getChildren()){
                    Chat chat = ds.getValue(Chat.class);

                    if(chat.getReceiver().equals(myid) && chat.getSender().equals(friendid)){
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("isseen", true);
                        ds.getRef().updateChildren(hashMap);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //hàm đọc tin nhắn
    private void readMessage(String myid, String friendid, String imageURL) {
        chatList = new ArrayList<>();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Chat");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                chatList.clear();

                for (DataSnapshot ds: snapshot.getChildren()) {

                    Chat chat = ds.getValue(Chat.class);

                    if (chat.getSender().equals(myid) && chat.getReceiver().equals(friendid) ||
                            chat.getSender().equals(friendid) && chat.getReceiver().equals(myid)) {

                        chatList.add(chat);
                    }

                    messageAdapter = new MessageAdapter(MessageActivity.this, chatList, imageURL);
                    recyclerView.setAdapter(messageAdapter);

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //hàm gửi tin nhắn
    private void sendMessage(String myid, String friendid, String message) {

        final DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("sender", myid);
        hashMap.put("receiver", friendid);
        hashMap.put("message", message);
        hashMap.put("isseen", false);

        reference.child("Chat").push().setValue(hashMap);

        final DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("ChatList").child(myid).child(friendid);
        reference1.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(!snapshot.exists()) {
                    reference1.child("id").setValue(friendid);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //hàm set trạng thái online/offline cho tài khoản
    private void Status (String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    //set online khi tài khoản được đăng nhập và đang sài app
    @Override
    protected void onResume() {
        super.onResume();
        Status("online");
    }
    //set offline khi thoát ứng dụng, và không hiển thị đã xem khi có tin nhắn đến
    @Override
    protected void onPause() {
        super.onPause();
        Status("offline");
        reference.removeEventListener(seenlistener);
    }
}