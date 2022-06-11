package hcmute.spkt.chatappzalo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends AppCompatActivity {

    //nút login và register ở màn hình start
    Button login, register;

    FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        //gán 2 nút từ view.
        login = findViewById(R.id.loginBtn);
        register = findViewById(R.id.registerbtn);

        //bắt sự kiện click cho nút login để chuyển sang màn hình đăng nhập
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, LoginActivity.class));
            }
        });

        //bắt sự kiện click cho nút register để chuyển san màn hình đăng ký
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(StartActivity.this, RegisterActivity.class));
            }
        });
    }

    //hàm này dùng để kiểm tra nếu chưa đăng xuất, khi mở app sẽ chuyển thẳng đến màn hình chính
    @Override
    protected void onStart() {
        super.onStart();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if(user!= null){
            startActivity(new Intent(StartActivity.this, MainActivity.class));
        }
    }
}