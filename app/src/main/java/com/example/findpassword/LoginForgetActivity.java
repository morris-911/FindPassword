package com.example.findpassword;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.findpassword.Util.ViewUtil;

import java.util.Random;

public class LoginForgetActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText et_password_first;
    private EditText et_password_second;
    private EditText et_verifycode;
    private String mphone;
    private Button btn_verifycode;
    private Button btn_confirm;
    private String verifycode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login_forget);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        et_password_first = findViewById(R.id.et_password_first);
        et_password_second = findViewById(R.id.et_password_second);
        et_verifycode = findViewById(R.id.et_verifycode);
        et_password_first.addTextChangedListener(new HideTextWatcher(et_password_first, 6));
        et_password_second.addTextChangedListener(new HideTextWatcher(et_password_second, 6));
        et_verifycode.addTextChangedListener(new HideTextWatcher(et_verifycode, 6));
        btn_verifycode = findViewById(R.id.btn_get_verifycode);
        btn_confirm = findViewById(R.id.btn_confirm);
        btn_verifycode.setOnClickListener(this);
        btn_confirm.setOnClickListener(this);

        mphone = getIntent().getStringExtra("phone");

    }

    @Override
    public void onClick(View view) {
        if(view.getId() == R.id.btn_get_verifycode){
            verifycode = String.format("%06d",new Random().nextInt(999999));
            AlertDialog.Builder builder = new AlertDialog.Builder((this));
            builder.setTitle("請記住驗證碼");
            builder.setMessage("手機號"+mphone+"驗證碼:"+ verifycode +",請輸入驗證碼");
            builder.setPositiveButton("確認",null);
            AlertDialog dialog = builder.create();
            dialog.show();
        } else if (view.getId() == R.id.btn_confirm) {
            if(et_password_first.getText().toString().length() < 6){
                Toast.makeText(this,"新密碼必須要六位數",Toast.LENGTH_SHORT).show();
                return;
            }if(!et_password_first.getText().toString().equals(et_password_second.getText().toString())){
                Toast.makeText(this,"請再次確認新密碼",Toast.LENGTH_SHORT).show();
                return;
            }if(!verifycode.equals(et_verifycode.getText().toString())){
                Toast.makeText(this,"請確認驗證碼是否正確",Toast.LENGTH_SHORT).show();
            }else{
                Intent intent = new Intent();
                intent.putExtra("new_password",et_password_first.getText().toString());
                setResult(Activity.RESULT_OK,intent);
                NewPasswordSuccess();
            }
        }

    }

    private class HideTextWatcher implements TextWatcher {
        private  EditText mView;
        private  int mMaxlength;
        public HideTextWatcher(EditText et, int i) {
            this.mView = et;
            this.mMaxlength = i;
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
                ViewUtil.hiadeOneInputMethod(LoginForgetActivity.this, mView);
            }
        }
    }
    private void NewPasswordSuccess() {
        String desc = String.format("手機號碼:%s,您的新密碼已經設定好了,點擊確定返回驗證頁面",mphone);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("新密碼設定成功");
        builder.setMessage(desc);
        builder.setPositiveButton("確認", (dialogInterface, i) -> {
            finish();
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}