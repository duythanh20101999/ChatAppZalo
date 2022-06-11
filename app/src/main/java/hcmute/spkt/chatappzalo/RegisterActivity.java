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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.util.HashMap;

public class RegisterActivity extends AppCompatActivity {
    //các biến lấy dữ liệu từ màn hình đăng ký
    MaterialEditText et_username, et_password, et_email;
    //nút register trên màn hình đăng ký
    Button registerbtn;
    //Toolbar cho màn hình đăng ký có nút trở về
    Toolbar toolbar;
    //Các biến để gán dữ liệu từ 3 biến trên
    String username, email, password;
    //biến này dùng để xử lý tài khoản khi nhập đúng dữ liệu
    FirebaseAuth mAuth;
    //biến này dùng để gán dữ liệu vào firebase
    DatabaseReference reference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //set toolbar cho màn hình đăng ký
        toolbar = findViewById(R.id.toolbarregis);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Đăng ký");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //lấy dữ liệu từ form đăng ký
        et_username = findViewById(R.id.reg_username);
        et_email = findViewById(R.id.reg_email);
        et_password = findViewById(R.id.reg_password);
        registerbtn = findViewById(R.id.register_Account_btn);

        mAuth = FirebaseAuth.getInstance();

        //xử lý sự kiện khi nhấn nút register
        registerbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = et_email.getText().toString();
                password = et_password.getText().toString();
                username = et_username.getText().toString();

                //kiểm tra nếu nhập sai hoặc không nhập dữ liệu sẽ bắt lỗi
                if (TextUtils.isEmpty(email)) {
                    et_email.setError("Vui lòng nhập email!");
                } else if (TextUtils.isEmpty(username)) {
                    et_username.setError("Vui lòng nhập username!");
                } else if (TextUtils.isEmpty(password)) {
                    et_password.setError("Vui lòng nhập password!");
                }else if(password.length()<6){
                    et_password.setError("Mật khẩu có ít nhất 6 ký tự");
                }
                else {
                    //nếu nhập đúng, chuyển đến hàm registerUser để tạo tài khoản
                    registerUser(username, password, email);
                }

            }
        });

    }

    //hàm tạo tài khoản từ dữ liệu nhập vào
    private void registerUser(final String username, String password, final String email) {

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {

                    FirebaseUser user = mAuth.getCurrentUser();

                    //chỉ đường đến child cần dùng trong firebase
                    reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

                    //Nếu user có dữ liệu sẽ tiếp tục xử lý
                    if (user!=null) {

                        //hashMap sẽ lấy dữ liệu tạo ra cặp <Key, value> để đưa lên firebase
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("username", username);
                        hashMap.put("email", email);
                        hashMap.put("id", user.getUid());
                        hashMap.put("imageURL","default");
                        hashMap.put("status", "offline");

                        //set value vào firebase
                        reference.setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                                if (task.isSuccessful()) {
                                    //Nếu tạo tài khoản thành công sẽ đưa ra thông báo và chuyển về màn hình start
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thành công!", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(RegisterActivity.this,
                                            StartActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK ));

                                }
                                else {
                                    Toast.makeText(RegisterActivity.this, "Đăng ký thất bại!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                }
            }
        });

    }

}