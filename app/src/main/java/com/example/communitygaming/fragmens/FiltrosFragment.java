package com.example.communitygaming.fragmens;

import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.communitygaming.Activitys.FilterActivity;
import com.example.communitygaming.Activitys.HomeActivity;
import com.example.communitygaming.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link FiltrosFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FiltrosFragment extends Fragment {

    View mView;
    CardView cardViewPlay;
    CardView cardViewXbox;
    CardView cardViewPc;
    CardView cardViewNintendo;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public FiltrosFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FiltrosFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static FiltrosFragment newInstance(String param1, String param2) {
        FiltrosFragment fragment = new FiltrosFragment();
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
        mView = inflater.inflate(R.layout.fragment_filtros, container, false);
        cardViewNintendo = mView.findViewById(R.id.cardViewNintendo);
        cardViewPc = mView.findViewById(R.id.cardViewPC);
        cardViewXbox = mView.findViewById(R.id.cardViewXbox);
        cardViewPlay = mView.findViewById(R.id.cardViewPlaystation);

        cardViewNintendo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("NINTENDO");
            }
        });

        cardViewPc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("PC");
            }
        });

        cardViewXbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("XBOX");
            }
        });

        cardViewPlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                goToFilterActivity("PLAYSTATION");
            }
        });
        return mView;

    }



    private void goToFilterActivity(String category) {
        Intent intent = new Intent(getContext(), FilterActivity.class);
        intent.putExtra("category", category);
        startActivity(intent);
    }
}