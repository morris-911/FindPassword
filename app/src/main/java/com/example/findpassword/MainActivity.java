package com.example.findpassword;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.findpassword.Util.ViewUtil;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private TextView tv_password;
    private Button btn_forget;
    private Button btn_login;
    private EditText et_password;
    private EditText et_phone;
    private CheckBox ck_remember;
    private RadioButton rb_password;
    private RadioButton rb_verifycode;
    private ActivityResultLauncher<Intent> register;
    private String mPassword = "878787";
    private String verifycode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        RadioGroup rg_login = findViewById(R.id.rg_login);
        rg_login.setOnCheckedChangeListener(this);
        tv_password = findViewById(R.id.tv_password);
        btn_forget = findViewById(R.id.btn_forget);
        btn_login = findViewById(R.id.btn_login);
        btn_forget.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        et_password = findViewById(R.id.et_password);
        et_phone = findViewById(R.id.et_phone);
        et_phone.addTextChangedListener(new HideTextWatcher(et_phone, 10));
        et_password.addTextChangedListener(new HideTextWatcher(et_password, 6));
        rb_password = findViewById(R.id.rb_password);
        rb_verifycode = findViewById(R.id.rb_verifycode);

        register = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {
                Intent intent = result.getData();
                if(intent != null&& result.getResultCode() == MainActivity.RESULT_OK){
                    mPassword = intent.getStringExtra("new_password");
                }
            }
        });
    }

    //切換密碼和驗證碼
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {
        if(i == R.id.rb_password){
            tv_password.setText(getString(R.string.login_password));
            et_password.setHint(getString(R.string.input_password));
            btn_forget.setText(getString(R.string.forget_password));
            ck_remember.setVisibility(View.VISIBLE);
        }else{
            tv_password.setText(getString(R.string.verifycode2));
            et_password.setHint(getString(R.string.input_verifycode));
            btn_forget.setText(getString(R.string.get_verifycode));
            ck_remember.setVisibility(View.GONE);
        }
    }
    //判斷使用何種方式登入 切換忘記密碼或是獲取驗證碼
    @Override
    public void onClick(View view) {
        String phone = et_phone.getText().toString();
        if(phone.length() <10){
            Toast.makeText(this,"請輸入正確的手機號碼",Toast.LENGTH_SHORT).show();
            return;
        }
        if(view.getId() == R.id.btn_forget){
            if(rb_password.isChecked()){
                Intent intent = new Intent(this, LoginForgetActivity.class);
                intent.putExtra("phone",phone);
                register.launch(intent);
            }else if (rb_verifycode.isChecked()) {
                verifycode = String.format("%06d",new Random().nextInt(999999));
                AlertDialog.Builder builder = new AlertDialog.Builder((this));
                builder.setTitle("請記住驗證碼");
                builder.setMessage("手機號"+phone+"驗證碼:"+ verifycode +",請輸入驗證碼");
                builder.setPositiveButton("確認",null);
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        } else if (view.getId() == R.id.btn_login) {
            if(rb_password.isChecked()){
                if(!mPassword.equals(et_password.getText().toString())){
                    Toast.makeText(this,"請輸入正確的密碼",Toast.LENGTH_SHORT).show();
                    return;
                }
                loginSuccess();
            } else if (rb_verifycode.isChecked()) {
                if(!verifycode.equals(et_password.getText().toString())){
                    Toast.makeText(this,"請輸入正確的驗證碼",Toast.LENGTH_SHORT).show();
                    return;
                }
                loginSuccess();
            }
        }
    }

    //登入成功!!
    private void loginSuccess() {
        String desc = String.format("手機號碼:%s通過登入驗證,點擊確定返回驗證頁面",et_phone.getText().toString());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("驗證成功");
        builder.setMessage(desc);
        builder.setPositiveButton("確認", (dialogInterface, i) -> {
            finish();
        });
        builder.setNegativeButton("驗證其他號碼",null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    //自動隱藏鍵盤
    private class HideTextWatcher implements TextWatcher {
        private  EditText mView;
        private  int mMaxlength;
        public HideTextWatcher(EditText et, int maxlengh) {
            this.mView = et;
            this.mMaxlength = maxlengh;
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.toString().length() == mMaxlength){
                ViewUtil.hiadeOneInputMethod(MainActivity.this, mView);
            }

        }
    }
}