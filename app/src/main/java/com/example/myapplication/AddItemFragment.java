package com.example.myapplication;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link AddItemFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddItemFragment extends Fragment {
    private final int itemFragment = 1;

    private AddItemDialog addItemDialog;
    private ImageView addItemBtn;
    private ImageView returnBtn;
    private ImageView shootItemImageView;
    private ImageView shootExpirationDateBtn;

    private ItemViewModel viewModel;
    private ArrayList<Item> allItems;

    static final int REQUEST_CODE = 2;
    private TextRecognizer recognizer;
    private Uri uri;
    private Bitmap bitmap;
    private InputImage image;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddItemFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddItemFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddItemFragment newInstance(String param1, String param2) {
        AddItemFragment fragment = new AddItemFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_item, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = ((ItemActivity)getActivity()).getViewModel();
        allItems = ((ItemActivity)getActivity()).getAllItems();
        addItemBtn = getView().findViewById(R.id.addItemBtn);
        returnBtn = getView().findViewById(R.id.returnBtn);
        shootItemImageView = getView().findViewById(R.id.shootItemImageView);
        shootExpirationDateBtn = getView().findViewById(R.id.shootExpirationDateImageView);
        recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        addItemBtn.setOnClickListener(this::addItemBtnClick);
        returnBtn.setOnClickListener(this::returnBtnClick);
        shootExpirationDateBtn.setOnClickListener(this::shootExpirationDateBtnClick);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE:
                uri = data.getData();
                try {
                    InputStream in = getActivity().getContentResolver().openInputStream(uri);
                    bitmap = BitmapFactory.decodeStream(in);
                    image = InputImage.fromBitmap(bitmap, 0);
                    textRecognition(recognizer);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
        }
    }

    // "추가하기" 버튼 클릭 -> 추가하겠냐는 다이얼로그 생성
    public void addItemBtnClick(View v) {
        EditText newNameEditText = getView().findViewById(R.id.newNameEditText);
        EditText newExpirationDateEditText = getView().findViewById(R.id.newExpirationDateEditText);
        EditText newMemoEditText = getView().findViewById(R.id.newMemoEditText);

        String newName = newNameEditText.getText().toString();
        String newExpirationDate = newExpirationDateEditText.getText().toString();
        String newMemo = newMemoEditText.getText().toString();

        addItemDialog = new AddItemDialog(getContext(), viewModel, allItems, newName, newExpirationDate, newMemo);
        addItemDialog.show();
    }

    // 유통기한 "촬영" 버튼 클릭
    public void shootExpirationDateBtnClick(View v) {
        // 사진 촬영하고
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
        startActivityForResult(intent, REQUEST_CODE);
    }
    public void textRecognition(TextRecognizer recognizer) {
        Task<Text> result = recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<Text>() {
                    @Override
                    public void onSuccess(Text text) {
                        String resultText = text.getText();
                        Log.e("AddItemFragment", "인식한 텍스트 " + resultText);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("AddItemFragment", "텍스트 인식 실패");
                    }
                });
    }

    // "돌아가기" 버튼 클릭 -> ItemFragment로 이동
    public void returnBtnClick(View v) {
        ((ItemActivity)getActivity()).changeFragment(itemFragment);
    }
}