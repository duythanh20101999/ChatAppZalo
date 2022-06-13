package hcmute.spkt.chatappzalo.Fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import hcmute.spkt.chatappzalo.Adapters.UserAdapter;
import hcmute.spkt.chatappzalo.Model.ChatList;
import hcmute.spkt.chatappzalo.Model.Users;
import hcmute.spkt.chatappzalo.R;

public class ChatsFragment extends Fragment {

    List<ChatList> userList; //danh sách chứa các danh sách chat
    List<Users> mUsers; //danh sách chứa các tài khoản

    //biến set view
    RecyclerView recyclerView;

    //các biến dùng khi kết nối với firebase
    UserAdapter mAdapter;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_chats, container, false);

        userList = new ArrayList<>();

        //set view
        recyclerView = view.findViewById(R.id.chat_recyclerview_chatfrag);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        //chỉ đường đến child cần dùng trong firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("ChatList")
                .child(firebaseUser.getUid());

        //thêm các danh sách chat vào danh sách chat của tài khoản
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                userList.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    ChatList chatList = ds.getValue(ChatList.class);

                    userList.add(chatList);
                }

                ChatListings();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return view;
    }


    //lấy danh sách các tài khoản có trong danh sách chat để đưa lên fragment
    private void ChatListings() {
        mUsers = new ArrayList<>();

        //chỉ đường đến mục cần dùng trong firebase
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Users");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mUsers.clear();

                for (DataSnapshot ds: snapshot.getChildren()){
                    Users users = ds.getValue(Users.class);

                    for (ChatList chatList: userList){
                        if(users.getId().equals(chatList.getId())){
                            mUsers.add(users);
                        }
                    }
                }

                mAdapter = new UserAdapter(getContext(), mUsers, true, true);
                recyclerView.setAdapter(mAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }
}