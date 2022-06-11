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
import hcmute.spkt.chatappzalo.Model.Users;
import hcmute.spkt.chatappzalo.R;

public class UsersFragment extends Fragment {

    RecyclerView recyclerView;
    List<Users> usersList; //danh sách tài khoản
    UserAdapter mAdapter;
    FirebaseUser firebaseUser;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        displayusers();

        return view;
    }

    //hàm hiển thị danh sách user lên fragment
    private void displayusers() {
        usersList = new ArrayList<>();
        //chỉ đường đến child cần dùng trong firebase
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users");
        //thêm dữ liệu vào firebase
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                usersList.clear();

                for(DataSnapshot ds: snapshot.getChildren()){
                    Users users = ds.getValue(Users.class);
                    firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

                    if(!users.getId().equals(firebaseUser.getUid())){
                        usersList.add(users);
                    }

                    mAdapter = new UserAdapter(getContext(), usersList, false);
                    recyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}