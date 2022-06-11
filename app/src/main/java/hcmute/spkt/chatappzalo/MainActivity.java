package hcmute.spkt.chatappzalo;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.spkt.chatappzalo.Fragment.ChatsFragment;
import hcmute.spkt.chatappzalo.Fragment.ProfileFragment;
import hcmute.spkt.chatappzalo.Fragment.UsersFragment;
import hcmute.spkt.chatappzalo.Model.Users;

public class MainActivity extends AppCompatActivity {

    //Biến này dùng để kiểm tra tài khoản
    FirebaseAuth mAuth;
    //biến này dùng để set toolbar
    Toolbar toolbar;
    //tạo 2 biến để lấy hình ảnh và username
    CircleImageView imageView;
    TextView username;

    //biến lấy dữ liệu từ database
    DatabaseReference reference;
    FirebaseUser firebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //lấy tài khoản gán cho biến mAuth
        mAuth = FirebaseAuth.getInstance();
        //gán hình ảnh và username từ thanh toolbar cho 2 biến
        imageView=findViewById(R.id.profile_image);
        username=findViewById(R.id.username_onmainactivity);

        //Set toolbar
        toolbar = findViewById(R.id.toolbarmain);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //gán tablayout và viewPager trên màn hình main cho 2 biến
        TabLayout tabLayout = findViewById(R.id.tablayout);
        ViewPager viewPager = findViewById(R.id.viewPager);

        //tạo viewPageAdapter để chứa các fragment
        ViewPageAdapter viewPageAdapter = new ViewPageAdapter(getSupportFragmentManager());

        //có 3 fragment tương ứng với 3 class và 3 title
<<<<<<< HEAD
        viewPageAdapter.addFragment(new ChatsFragment(), "Chats");
        viewPageAdapter.addFragment(new UsersFragment(), "Users");
        viewPageAdapter.addFragment(new ProfileFragment(), "Profile");
=======
        viewPageAdapter.addFragment(new ChatsFragment(), "Tin nhắn");
        viewPageAdapter.addFragment(new UsersFragment(), "Người dùng");
        viewPageAdapter.addFragment(new ProfileFragment(), "Cá nhân");
>>>>>>> 37d8fbf (Initial commit)

        //set view pager và tablayout
        viewPager.setAdapter(viewPageAdapter);
        tabLayout.setupWithViewPager(viewPager);

        //lấy tài khoản đã đăng nhập
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //kết nối firebase
        reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //lấy dữ liệu từ firebase gán vào đối tượng users chứa tất cả thông tin của tài khoản hiện tại
                Users users = snapshot.getValue(Users.class);

                //gán username của tài khoản lên toolbar
                username.setText(users.getUsername()); //gán username từ tài khoản lên toolbar

                //gán hình ảnh, nếu không có ảnh sẽ set ảnh mặc định
                if(users.getImageURL().equals("default")){
                    imageView.setImageResource(R.drawable.user);
                }else{
                    Glide.with(getApplicationContext()).load(users.getImageURL()).into(imageView);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    //class này dùng để tạo các fragment
    class ViewPageAdapter extends FragmentPagerAdapter{
        ArrayList<Fragment> fragments;
        ArrayList<String> titles;

        public ViewPageAdapter(FragmentManager fm) {
            super(fm);
            this.fragments = new ArrayList<>();
            this.titles = new ArrayList<>();
        }

        @Override
        public Fragment getItem(int position) {
            return fragments.get(position);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
        public void addFragment(Fragment fragment, String title){
            fragments.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

    //hàm này dùng để lấy thanh menu có nút đăng xuất
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    //hàm này dùng để xử lý sự kiện click vào đăng xuất trên thanh menu
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.logout){
            mAuth.signOut();
            //sau khi đăng xuất sẽ trở về lại màn hình start
            finish();
            return true;
        }
        return false;
    }

    //khi đăng nhập thành công, nếu ấn nút back trên điện thoại thì hàm này sẽ giúp thoát ứng dụng
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }

    //hàm này tương tự như bên MessageActivity
    private void Status (String status){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("status", status);

        reference.updateChildren(hashMap);
    }

    //hàm này tương tự như bên MessageActivity
    @Override
    protected void onResume() {
        super.onResume();
        Status("online");
    }

    //hàm này tương tự như bên MessageActivity
    @Override
    protected void onPause() {
        super.onPause();
        Status("offline");
    }
}