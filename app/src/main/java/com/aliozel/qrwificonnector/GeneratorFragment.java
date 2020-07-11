package com.aliozel.qrwificonnector;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class GeneratorFragment extends Fragment {
    private Button generate;
    private Button pdfGenerate;
    private ImageView qrImage;
    private EditText et_wifissid;
    private EditText et_wifipassword;
    private TextView tv_wep;
    private TextView tv_wpa;
    private Switch sw_network_type;
    private int isWPA = 1;
    private Bitmap qrbitmap;
    private boolean isQrCreated = false;

    private ConstraintLayout rootView;
    private ScrollView scrollView;

    private static final int PERMISSION_WRITE_REQUEST_CODE = 1000;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.generator_layout, container, false);
        generate = view.findViewById(R.id.btn_generate);
        pdfGenerate = view.findViewById(R.id.btn_pdf);
        qrImage = view.findViewById(R.id.iv_qr);
        et_wifissid = view.findViewById(R.id.et_ssid);
        et_wifipassword = view.findViewById(R.id.et_password);
        sw_network_type = view.findViewById(R.id.sw_network_type);
        tv_wep = view.findViewById(R.id.tv_wep);
        tv_wpa = view.findViewById(R.id.tv_wpa);


        scrollView = view.findViewById(R.id.scrollview);
        rootView = view.findViewById(R.id.scroll_cl);
        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Rect r = new Rect();
                rootView.getWindowVisibleDisplayFrame(r);
                int screenHeight = rootView.getRootView().getHeight();
                int keypadHeight = screenHeight - r.bottom;
                if (keypadHeight > screenHeight * 0.15) {
                    scrollView.post(new Runnable() {
                        public void run() {
                            scrollView.scrollTo(0, scrollView.getBottom());
                        }
                    });
                }
            }
        });

        if (sw_network_type.isChecked()) {
            System.out.println("WEP Seçildi");
            isWPA = 0;
            tv_wep.setTypeface(null, Typeface.BOLD);
            tv_wep.setTextColor(getResources().getColor(R.color.limedspruce));
            tv_wpa.setTypeface(null, Typeface.NORMAL);
            tv_wpa.setTextColor(getResources().getColor(R.color.alto));
        } else {
            System.out.println("WPA Seçildi");
            isWPA = 1;
            tv_wep.setTypeface(null, Typeface.NORMAL);
            tv_wep.setTextColor(getResources().getColor(R.color.alto));
            tv_wpa.setTypeface(null, Typeface.BOLD);
            tv_wpa.setTextColor(getResources().getColor(R.color.limedspruce));
        }

        sw_network_type.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sw_network_type.isChecked()) {
                    System.out.println("WEP Seçildi");
                    isWPA = 0;
                    tv_wep.setTypeface(null, Typeface.BOLD);
                    tv_wep.setTextColor(getResources().getColor(R.color.limedspruce));
                    tv_wpa.setTypeface(null, Typeface.NORMAL);
                    tv_wpa.setTextColor(getResources().getColor(R.color.alto));
                } else {
                    System.out.println("WPA Seçildi");
                    isWPA = 1;
                    tv_wep.setTypeface(null, Typeface.NORMAL);
                    tv_wep.setTextColor(getResources().getColor(R.color.alto));
                    tv_wpa.setTypeface(null, Typeface.BOLD);
                    tv_wpa.setTextColor(getResources().getColor(R.color.limedspruce));
                }
            }
        });


        if (!isQrCreated) {
            pdfGenerate.setVisibility(View.GONE);
        }


        generate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String qrText = String.valueOf(et_wifissid.getText()) + "/appto/" + String.valueOf(et_wifipassword.getText() + "/appto/" + isWPA + "/appto/apptoxin"); // Whatever you need to encode in the QR code

                MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
                try {
                    BitMatrix bitMatrix = multiFormatWriter.encode(qrText, BarcodeFormat.QR_CODE, 300, 300);
                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                    qrbitmap = barcodeEncoder.createBitmap(bitMatrix);
                    qrImage.setImageBitmap(qrbitmap);
                    generate.setText(getResources().getString(R.string.string_button_createqr_again));
                    isQrCreated = true;
                    pdfGenerate.setVisibility(View.VISIBLE);
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
                    //Find the currently focused view, so we can grab the correct window token from it.
                    View view4keyboard = getActivity().getCurrentFocus();
                    //If no view currently has focus, create a new one, just so we can grab a window token from it
                    if (view4keyboard == null) {
                        view4keyboard = new View(getActivity());
                    }
                    imm.hideSoftInputFromWindow(view4keyboard.getWindowToken(), 0);

                } catch (WriterException e) {
                    e.printStackTrace();
                }


            }
        });

        pdfGenerate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_WRITE_REQUEST_CODE);
                    return;
                } else {
                    PdfDocument pdfDocument = new PdfDocument();
                    PdfDocument.PageInfo pi = new PdfDocument.PageInfo.Builder(qrbitmap.getWidth(), qrbitmap.getHeight(), 1).create();

                    PdfDocument.Page page = pdfDocument.startPage(pi);

                    Canvas canvas = page.getCanvas();
                    Paint paint = new Paint();
                    paint.setColor(Color.parseColor("#FFFFFF"));
                    canvas.drawPaint(paint);

                    qrbitmap = Bitmap.createScaledBitmap(qrbitmap, qrbitmap.getWidth(), qrbitmap.getHeight(), true);
                    paint.setColor(Color.BLUE);
                    canvas.drawBitmap(qrbitmap, 0, 0, null);

                    pdfDocument.finishPage(page);

                    File root = new File(Environment.getExternalStorageDirectory(), "/QRWifiConnector");
                    if (!root.exists()) {
                        root.mkdir();
                    }

                    String createdFilename = String.valueOf(et_wifissid.getText()) + ".pdf";

                    File file = new File(root, createdFilename);
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(file);
                        pdfDocument.writeTo(fileOutputStream);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    pdfDocument.close();

                    final Uri data = FileProvider.getUriForFile(view.getContext(), "com.aliozel.qrwificonnector.provider", file);
                    view.getContext().grantUriPermission(view.getContext().getPackageName(), data, Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    final Intent intent = new Intent(Intent.ACTION_VIEW)
                            .setDataAndType(data, "application/pdf")
                            .addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    Intent intentChooser = Intent.createChooser(intent, "Open File");
                    view.getContext().startActivity(intentChooser);
                }
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle(getResources().getString(R.string.string_header_generateqr));
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case PERMISSION_WRITE_REQUEST_CODE: {
            }
            break;
        }
    }
}
