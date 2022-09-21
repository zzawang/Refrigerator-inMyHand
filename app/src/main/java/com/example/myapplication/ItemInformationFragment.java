package com.example.myapplication;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

/*
* ItemFragment에서 클릭한 Item의 정보(이름, 유통기한, 메모)를 보여주는 fragment, 정보를 수정할 수도 있다.
* 클릭한 아이템의 이름을 Intent로 받아오고, viewModel을 사용해서 찾고 수정한다
* ItemViewModel은 ItemActivity에서 생성하고 ItemFragment와 ItemInformationFragment가 공유한다.
 */

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ItemInformationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ItemInformationFragment extends Fragment {
    private String clickName;
    private Item item;
    private EditText nameEditText;
    private EditText expirationDateEditText;
    private EditText memoEditText;

    private ItemViewModel viewModel;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ItemInformationFragment() {

    }
    public ItemInformationFragment(String clickName) {
        this.clickName = clickName;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ItemInformationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ItemInformationFragment newInstance(String param1, String param2) {
        ItemInformationFragment fragment = new ItemInformationFragment();
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
        return inflater.inflate(R.layout.fragment_item_information, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /* ---------- View Model ---------- */
        viewModel = ((ItemActivity)getActivity()).getViewModel();

        /* ---------- 클릭한 item 가져와서 이름, 유통기한, 메모 초기화 ---------- */
        Log.e("ItemInformationFragment", "클릭한 item name = " + clickName);
        int index = viewModel.getItemIndex(clickName);
        item = viewModel.getItem(index);

        nameEditText = ((ItemActivity)getActivity()).findViewById(R.id.nameEditText);
        expirationDateEditText = ((ItemActivity)getActivity()).findViewById(R.id.expirationDateEditText);
        memoEditText = ((ItemActivity)getActivity()).findViewById(R.id.memoEditText);

        nameEditText.setText(item.getName());
        expirationDateEditText.setText(item.getExpirationDate());
        memoEditText.setText(item.getMemo());
    }
}