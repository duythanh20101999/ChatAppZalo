package hcmute.spkt.chatappzalo;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.rengwuxian.materialedittext.MaterialEditText;

public class LoginActivity extends AppCompatActivity {
    //tạo 2 biến để lấy dữ liệu từ màn hình đăng nhập
    MaterialEditText et_email, et_password;
    //nút đăng nhập
    Button loginBtn;
    //toolbar cho màn hinh đăng nhập
    Toolbar toolbar;
    //2 biến để gán dữ liệu sau khi lấy dữ liệu thành công
    String email, password;
    //biến này dùng để xử lý tài khoản khi nhập đúng dữ liệu
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //set toolbar cho màn hình đăng nhập
        toolbar = findViewById(R.id.toolbarlogin);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Đăng nhập");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //lấy dữ liệu từ màn hình đăng nhập
        et_email = findViewById(R.id.log_email);
        et_password = findViewById(R.id.log_password);
        loginBtn = findViewById(R.id.login_account);

        mAuth=FirebaseAuth.getInstance();

        //xử lý sự kiện khi click nút đăng nhập
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email=et_email.getText().toString();
                password=et_password.getText().toString();

                if(TextUtils.isEmpty(email)){
                    et_email.setError("Vui lòng nhập email!");
                }else if(TextUtils.isEmpty(password)){
                    et_password.setError("Vui lòng nhập password!");
                }else{
                    LoginMeIn(email, password);
                }
            }
        });

    }

    //Hàm xử lý đăng nhập khi nhập đúng thông tin tài khoản
    private void LoginMeIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                    Toast.makeText(LoginActivity.this, "Đăng nhập thành công!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText(LoginActivity.this, "Đăng nhập thất bại!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}