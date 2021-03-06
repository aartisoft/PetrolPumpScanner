package com.reward.scanner;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.reward.scanner.api_client.Api_Call;
import com.reward.scanner.api_client.Base_Url;
import com.reward.scanner.api_client.RxApiClient;
import com.reward.scanner.model.SuccessModel;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;
import retrofit2.adapter.rxjava2.HttpException;

public class AddCustomerActivity extends AppCompatActivity {
    ImageView iv_back;
    TextView tv_add;
    EditText EtQrData;
    String QrData,Qr_id;
    EditText et_customer_name,et_mobile,et_vehical_no,et_reward_card_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);

        iv_back=findViewById(R.id.iv_back);
        tv_add=findViewById(R.id.tv_add_customer);
        EtQrData=findViewById(R.id.et_qr_data);

        et_customer_name=findViewById(R.id.et_customer_name);
        et_mobile=findViewById(R.id.et_mobile);
        et_vehical_no=findViewById(R.id.et_vehical_no);
        et_reward_card_no=findViewById(R.id.et_reward_card_no);

        if (getIntent()!=null){
            QrData=getIntent().getStringExtra("Qr_data");
            EtQrData.setText(QrData);

            if (Connectivity.isConnected(AddCustomerActivity.this)){
                UploadQR(QrData);
            }else {
                Toast.makeText(AddCustomerActivity.this, "Please check Internet", Toast.LENGTH_SHORT).show();
            }
        }


        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        tv_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Customer_name= et_customer_name.getText().toString();
                String Customer_mobile= et_mobile.getText().toString();
                String Customer_vehical= et_vehical_no.getText().toString();
                String Customer_reward_card= et_reward_card_no.getText().toString();

                if (!Customer_name.isEmpty() && !Customer_mobile.isEmpty() &&
                        !Customer_vehical.isEmpty() && !Customer_reward_card.isEmpty()){
                    if (Qr_id!=null && !Qr_id.isEmpty()){
                        if (Connectivity.isConnected(AddCustomerActivity.this)){
                            AddCustomer(Customer_name,Customer_mobile,Customer_vehical,Customer_reward_card);
                        }else {
                            Toast.makeText(AddCustomerActivity.this, "Please check Internet", Toast.LENGTH_SHORT).show();
                        }
                    }else {
                        Toast.makeText(AddCustomerActivity.this, "Qr id not found, Please scan QR again", Toast.LENGTH_SHORT).show();
                    }


                }else {
                    Toast.makeText(AddCustomerActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    @SuppressLint("CheckResult")
    private void UploadQR(String qr_text) {
        final ProgressDialog progressDialog = new ProgressDialog(AddCustomerActivity.this, R.style.MyGravity);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.setCancelable(false);
        progressDialog.show();

        Api_Call apiInterface = RxApiClient.getClient(Base_Url.BaseUrl).create(Api_Call.class);

        apiInterface.SaveQrCode(qr_text, "")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SuccessModel>() {
                    @Override
                    public void onNext(SuccessModel response) {
                        //Handle logic
                        try {
                            progressDialog.dismiss();
                            Log.e("result_my_test", "" + response.getMessage());
                            //Toast.makeText(EmailSignupActivity.this, "" + response.getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.getSuccess().equals(1)) {
                                //  Log.e("result_my_test", "" + response.getDeliveryagentInfo().getDeliveryagentId());
                            Qr_id= String.valueOf(response.getQrcode_id());

                            } else {
                                Toast.makeText(AddCustomerActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        //Handle error
                        progressDialog.dismiss();
                        Log.e("mr_product_error", e.toString());

                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).code();
                            switch (code) {
                                case 403:
                                    break;
                                case 404:
                                    //Toast.makeText(EmailSignupActivity.this, R.string.email_already_use, Toast.LENGTH_SHORT).show();
                                    break;
                                case 409:
                                    break;
                                default:
                                    // Toast.makeText(EmailSignupActivity.this, R.string.network_failure, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } else {
                            if (TextUtils.isEmpty(e.getMessage())) {
                                // Toast.makeText(EmailSignupActivity.this, R.string.network_failure, Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(EmailSignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                        progressDialog.dismiss();
                    }
                });
    }

    @SuppressLint("CheckResult")
    private void AddCustomer(String customer_name, String customer_mobile, String customer_vehical, String customer_reward_card) {
        final ProgressDialog progressDialog = new ProgressDialog(AddCustomerActivity.this, R.style.MyGravity);
        progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        progressDialog.show();

        Api_Call apiInterface = RxApiClient.getClient(Base_Url.BaseUrl).create(Api_Call.class);

        apiInterface.AddNewCustomer(customer_name, customer_mobile,customer_vehical,customer_reward_card,Qr_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<SuccessModel>() {
                    @Override
                    public void onNext(SuccessModel response) {
                        //Handle logic
                        try {
                            progressDialog.dismiss();
                            Log.e("result_my_test", "" + response.getMessage());
                            //Toast.makeText(EmailSignupActivity.this, "" + response.getMessage(), Toast.LENGTH_SHORT).show();
                            if (response.getSuccess().equals(1)) {
                              //  Log.e("result_my_test", "" + response.getDeliveryagentInfo().getDeliveryagentId());
                                Toast.makeText(AddCustomerActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                                Intent intent =new Intent(AddCustomerActivity.this,ThanksActivity.class);
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(AddCustomerActivity.this, response.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                        } catch (Exception e) {
                            progressDialog.dismiss();
                        }

                    }

                    @Override
                    public void onError(Throwable e) {
                        //Handle error
                        progressDialog.dismiss();
                        Log.e("mr_product_error", e.toString());

                        if (e instanceof HttpException) {
                            int code = ((HttpException) e).code();
                            switch (code) {
                                case 403:
                                    break;
                                case 404:
                                    //Toast.makeText(EmailSignupActivity.this, R.string.email_already_use, Toast.LENGTH_SHORT).show();
                                    break;
                                case 409:
                                    break;
                                default:
                                    // Toast.makeText(EmailSignupActivity.this, R.string.network_failure, Toast.LENGTH_SHORT).show();
                                    break;
                            }
                        } else {
                            if (TextUtils.isEmpty(e.getMessage())) {
                                // Toast.makeText(EmailSignupActivity.this, R.string.network_failure, Toast.LENGTH_SHORT).show();
                            } else {
                                //Toast.makeText(EmailSignupActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }

                    @Override
                    public void onComplete() {
                        progressDialog.dismiss();
                    }
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}