package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.View;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

public class CategoryActivity extends AppCompatActivity {
    private final int refrigerationCategoryFragment = 1;
    private final int freezeCategoryFragment = 2;

    private ImageView refrigerationImage;
    private ImageView freezeImage;
    private TextView refrigeratorIdTextView;

    private String userUid; // RefrigeratorActivity에서 전달된 냉장고 Uid
    private String refrigeratorId; // RefrigeratorActivity에서 전달된 냉장고 id
    private String type;

    private CategoryViewModel viewModel;

    private AddCategoryDialog addCategoryDialog;

    private SharedPreferences categoryPreferences;
    private String SharedPrefFile = "com.example.android.MyApplication3";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_category);

        /* ---------- 초기화 ---------- */
        refrigerationImage = findViewById(R.id.refrigerationImage);
        freezeImage = findViewById(R.id.freezeImage);
        type = "냉장"; // 냉장 프래그먼트로 시작하므로 "냉장"으로 초기화
        refrigeratorIdTextView = findViewById(R.id.refrigeratorIdTextView);

        /* ---------- Intent 받아와서 uid, id 초기화 ---------- */
        Intent intent = getIntent();
        userUid = intent.getStringExtra("userUid"); // 냉장고 Uid
        refrigeratorId = intent.getStringExtra("refrigeratorID"); // 냉장고 id

        /* ---------- uid, id가 null이면 저장소에서 값 가져오기 ---------- */
        categoryPreferences = getSharedPreferences(SharedPrefFile, MODE_PRIVATE);
        if(userUid == null || refrigeratorId == null) {
            userUid = categoryPreferences.getString("userUid", null);
            refrigeratorId = categoryPreferences.getString("ID", null);
        }
        refrigeratorIdTextView.setText(refrigeratorId);

        /* ---------- View Model ---------- */
        viewModel = new ViewModelProvider(this).get(CategoryViewModel.class);
        viewModel.setUserUid(userUid);
        viewModel.setRefrigeratorId(refrigeratorId);

        // 리싸이클러뷰, 어댑터는 각 프래그먼트에서 생성
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor preferencesEditor = categoryPreferences.edit();

        preferencesEditor.putString("userUid", userUid);
        preferencesEditor.putString("ID", refrigeratorId);

        preferencesEditor.apply();
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        ((Activity)view.getContext()).getMenuInflater().inflate(R.menu.category_context_menu, contextMenu);
        Log.e("MainActivity", "onCreateContextMenu()");
    }

//    @Override
//    public boolean onContextItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case R.id.delete: // 카테고리 삭제하기
//                viewModel.deleteItem(viewModel.longClickPosition);
//                break;
//        }
//        return true;
//    }

    public String getUserUid() { return userUid; }
    public String getRefrigeratorId() { return refrigeratorId; }
    public CategoryViewModel getViewModel() { return viewModel; }

    // 인자로 받은 fragment로 이동하는 함수
    public void changeFragment(int fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (fragment) {
            case 1: // RefrigerationCategoryFragment 호출
                RefrigerationCategoryFragment refrigerationCategoryFrag = new RefrigerationCategoryFragment();
                transaction.replace(R.id.categoryFragmentContainerView, refrigerationCategoryFrag);
                transaction.commit();
                break;

            case 2: // FreezeCategoryFragment 호출
                FreezeCategoryFragment freezeCategoryFrag = new FreezeCategoryFragment();
                transaction.replace(R.id.categoryFragmentContainerView, freezeCategoryFrag);
                transaction.commit();
                break;
        }
    }

    // 카테고리 클릭 -> ItemActivity 이동 (해당 카테고리에 들어있는 아이템들을 보여줌)
    public void categoryClick(View v) {
        TextView textView = (TextView) v;
        Log.e("CategoryActivity", (String)textView.getText() + " Click");
        Log.e("CategoryActivity", "type = " + type);

        Intent intent = new Intent(this, ItemActivity.class);
        intent.putExtra("userUid", userUid);
        intent.putExtra("ID", refrigeratorId);
        intent.putExtra("category", (String)textView.getText()); // intent에 클릭한 카테고리 이름 넣어주기
        intent.putExtra("type", type);

        startActivity(intent); // 액티비티 이동
    }

    // "냉장" 클릭 -> refrigerationCategoryFragment로 전환, image 바꾸기
    public void refrigerationImageClick(View v) {
        Log.e("CategoryActivity", "냉장 버튼 Click");
        type = "냉장";

        refrigerationImage.setImageResource(R.drawable.selected_refrigeration_btn);
        freezeImage.setImageResource(R.drawable.unselected_freeze_btn);

        changeFragment(refrigerationCategoryFragment);
    }

    // "냉동" 클릭 -> freezeCategoryFragment로 전환, image 바꾸기
    public void freezeImageClick(View v) {
        Log.e("CategoryActivity", "냉동 버튼 Click");
        type = "냉동";

        refrigerationImage.setImageResource(R.drawable.unselected_refrigeration_btn);
        freezeImage.setImageResource(R.drawable.selected_freeze_btn);

        changeFragment(freezeCategoryFragment);
    }

    // 냉장 카테고리 추가하는 버튼을 누르면 실행되는 함수
   public void addRefrigerationCategoryBtnClick(View v) {
        addCategoryDialog = new AddCategoryDialog(CategoryActivity.this, viewModel, "냉장");
        addCategoryDialog.show();
    }

    // 냉동 카테고리 추가하는 버튼을 누르면 실행되는 함수
    public void addFreezeCategoryBtnClick(View v) {
        addCategoryDialog = new AddCategoryDialog(CategoryActivity.this, viewModel, "냉동");
        addCategoryDialog.show();
    }

    // "설정" 아이콘 클릭 -> SettinActivity 이동
    public void settingIconClick(View v) {
        Intent intent = new Intent(this, SettingActivity.class);
        startActivity(intent);
    }

    // <- 아이콘 클릭 -> RefrigeratorActivity 이동
    public void toRefrigeratorActivity(View v) {
        Intent intent = new Intent(this, RefrigeratorActivity.class);
        intent.putExtra("userUid", userUid);
        startActivity(intent);
    }
}