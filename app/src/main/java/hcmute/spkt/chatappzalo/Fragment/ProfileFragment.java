package hcmute.spkt.chatappzalo.Fragment;

import static android.app.Activity.RESULT_OK;

import static com.firebase.ui.auth.AuthUI.getApplicationContext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import hcmute.spkt.chatappzalo.Model.Users;
import hcmute.spkt.chatappzalo.R;

public class ProfileFragment extends Fragment {

    //các biến dùng để liên kết với firebase
    DatabaseReference reference;
    FirebaseUser user;

    //biến dùng để set view
    CircleImageView imageView;

    //username
    TextView username;

    //2 biến dùng để chọn hình từ camera hoặc thư viện
    public static int CAMERA_CODE = 200;
    public static int GALLERY_CODE = 100;

    //biến lưu đường dẫn
    Uri imageUri = null;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Permissions();

        //set view
        imageView = view.findViewById(R.id.profile_frag_image);
        username = view.findViewById(R.id.username_profile_frag);

        user = FirebaseAuth.getInstance().getCurrentUser();

        //chỉ đường đến child cần dùng trong firebase
        reference = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

        //thêm dữ liệu vào firebase
        reference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users users = snapshot.getValue(Users.class);

                username.setText(users.getUsername());

                if(users.getImageURL().equals("default")){
                    imageView.setImageResource(R.drawable.user);
                } else {

                    Glide.with(getApplicationContext()).load(users.getImageURL()).into(imageView);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //xử lý sự kiện khi nhấn vào avatar ở profile
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeImage();
            }
        });

        return view;
    }

    //hàm thay đổi avatar
    private void changeImage() {
        String [] options = {"Camera", "Gallery"};

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());

        builder.setTitle("Chọn ảnh từ:");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {

                if (i == 0){
                    openCamera();
                }
                if (i==1){
                    openGallery();
                }

            }
        });

        builder.create().show();
    }

    //hàm mở thư viện ảnh trong điện thoại
    private void openGallery() {

        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_CODE);
    }

    //hàm mở camera trong điện thoại
    private void openCamera() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Temp Pick");
        values.put(MediaStore.Images.Media.TITLE, "Temp Desc");
        imageUri = getContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);


        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, CAMERA_CODE);
    }

    //hàm cho phép sử dụng camera và thư viện ảnh của điện thoại
    private void Permissions() {

        Dexter.withContext(getContext())
                .withPermissions(
                        Manifest.permission.CAMERA,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(new MultiplePermissionsListener() {
            @Override public void onPermissionsChecked(MultiplePermissionsReport report) {
            }
            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {

            }

        }).check();

    }

    //hàm đẩy hình ảnh lên firebase
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_CODE && resultCode == RESULT_OK) {

            imageUri = data.getData();

            String filepath = "Photos/" + "userprofile_" + user.getUid();

            StorageReference reference = FirebaseStorage.getInstance().getReference(filepath);
            reference.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String imageURL = uri.toString();

                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("imageURL", imageURL);
                            reference1.updateChildren(hashMap);


                        }
                    });
                }
            });
        }

        if (requestCode == CAMERA_CODE && resultCode == RESULT_OK) {

            Uri uri = imageUri;

            String filepath = "Photos/" + "userprofile_" + user.getUid();

            StorageReference reference = FirebaseStorage.getInstance().getReference(filepath);
            reference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                    Task<Uri> task = taskSnapshot.getMetadata().getReference().getDownloadUrl();

                    task.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {

                            String imageURL = uri.toString();

                            DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference("Users").child(user.getUid());

                            HashMap<String, Object> hashMap = new HashMap<>();
                            hashMap.put("imageURL", imageURL);
                            reference1.updateChildren(hashMap);


                        }
                    });


                }
            });

        }
    }
}